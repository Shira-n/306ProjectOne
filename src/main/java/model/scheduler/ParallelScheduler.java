package model.scheduler;

import model.*;
import model.state.AbstractState;
import model.state.ParallelState;

import java.util.*;
import java.util.concurrent.*;

public class ParallelScheduler extends AbstractScheduler {
    private final int MAIN_THREAD_ID = 0;
    private List<Map<String, Node>> _graphs;
    private List<List<Processor>> _processors;

    private ParallelState _optimalState;
    private Set<String> _freeToSchedule;

    private ExecutorService _executorService;
    private int _threadCount;

    public ParallelScheduler(int numberOfThreads, List<Map<String, Node>> graphs, int numberOfProcessor) {
        _threadCount = numberOfThreads;
        _graphs = graphs;
        _executorService = Executors.newFixedThreadPool(_threadCount);

        //Set up N processors for each thread
        _processors = new ArrayList<>();
        for(int i = 0; i < _graphs.size(); i++) {
            List<Processor> processors = new ArrayList<>();
            for (int j = 0; j < numberOfProcessor; j++) {
                processors.add(new Processor(j));
            }
            _processors.add(processors);
        }

        //Pre-process graphs, use the first graph map as a shadow copy (used for later replication).
        Map<String, Node> shadow = _graphs.get(MAIN_THREAD_ID);

        _freeToSchedule = new HashSet<>();
        for (Node node : shadow.values()) {
            //Find entry points
            if (node.getParents().size() <= 0) {
                _freeToSchedule.add(node.getId());
                calcBottomWeight(node);
            }
            //Calculate equivalent nodes
            for (Node parent : node.getParents().keySet()) {
                for (Node sibling : parent.getChildren().keySet()) {
                    //Add equivalent relationship between two nodes when they are different nodes, have not been added,
                    //and are equivalent to each other.
                    if (node.getId() != sibling.getId() && !node.isEquivalent(sibling) && internalOrderingCheck(node, sibling)){
                        node.addEquivalentNode(sibling);
                        sibling.addEquivalentNode(node);
                    }
                }
            }
        }

        //Copy result to other graphs
        for (int i = 1; i < _graphs.size(); i++){
            for (Node node : shadow.values()){
                //Copy bottom weight
                _graphs.get(i).get(node.getId()).setBottomWeight(node.getBottomWeight());
                //Copy equivalent Nodes
                for (Node sibling : node.getEquivalentNodes()){
                    Node trueSibling = _graphs.get(i).get(sibling.getId());
                    Node trueNode = _graphs.get(i).get(node.getId());
                    if (!trueNode.isEquivalent(sibling)) {
                        trueNode.addEquivalentNode(trueSibling);
                    }
                }
            }
        }

        _optimalState = new ParallelState();
    }



    /*
        Schedule methods
     */
    @Override
    public AbstractState getSchedule() {
        try {
            paraSchedule(_freeToSchedule);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        _executorService.shutdown();

        if (_controller != null) {
            _controller.completed(_optimalState);
        }
        return  _optimalState;
    }

    private void paraSchedule(Set<String> freeNodeId) throws InterruptedException, ExecutionException {
        List<Processor> processors = _processors.get(MAIN_THREAD_ID);
        Set<Node> freeToSchedule = getNodes(freeNodeId, MAIN_THREAD_ID);

        List<ParallelState> states = new ArrayList<>();
        //When not all the possible schedules have been explored
        if (!freeToSchedule.isEmpty()) {

            //Create tasks for every thread
            if (states.size() < _threadCount) {
                Set<Node> uniqueNodes = new HashSet<>();

                for (Node node : freeToSchedule) {
                    //Check equivalent Nodes
                    if (!equivalentNode(node, uniqueNodes)) {
                        uniqueNodes.add(node);

                        Set <Processor> uniqueProcessors = new HashSet<>();
                        for (Processor processor : processors){
                            int startTime = Math.max(processor.getCurrentAbleToStart(), influencedByParents(processor, node));
                            if (!equivalentProcessor(processor, uniqueProcessors, node, startTime)){
                                uniqueProcessors.add(processor);

                                if (node.getBottomWeight() + startTime <= _optimalState.getMaxWeight()) {
                                    //Schedule this Node on this Processor. Get a set of Nodes that became free because of this step.
                                    Set<Node> newFreeToSchedule = node.schedule(processor, startTime);
                                    processor.addNodeAt(node, startTime);
                                    //Include every Nodes in the original free Node set except for this scheduled Node.
                                    newFreeToSchedule.addAll(freeToSchedule);
                                    newFreeToSchedule.remove(node);

                                    ParallelState newState = new ParallelState(processors, getNodesId(newFreeToSchedule));
                                    states.add(newState);

                                    unscheduleNode(node);

                                    //Start to send task for parallelisation
                                    if (states.size() == _threadCount ){
                                        List<Callable<Void>> callables = new ArrayList<>();

                                        Stack<Integer> threadId = new Stack<>();
                                        for (int i = 1; i <= states.size(); i++){
                                            threadId.push(i);
                                        }
                                        for (ParallelState state : states){
                                            Callable<Void> callable = () -> {
                                                int id = threadId.pop();

                                                List<Processor> paraProcessors = _processors.get(id);
                                                Map<String, Node> paraGraph = _graphs.get(id);
                                                Set<Node> paraFreeToSchedule = getNodes(state.rebuild(paraGraph, paraProcessors), id);

                                                branchAndBoundScheduleParallel(paraProcessors, paraFreeToSchedule);
                                                return null;
                                            };
                                            callables.add(callable);
                                        }
                                        _executorService.invokeAll(callables);
                                        states.clear();
                                    }
                                }
                            }
                        }
                    }
                }
                for (ParallelState state: states){
                    Set<String> temp  = state.rebuild(_graphs.get(MAIN_THREAD_ID), _processors.get(MAIN_THREAD_ID));
                    paraSchedule(temp);
                }
            }
        }else{
            for (ParallelState state : states){
                if (_optimalState.getMaxWeight() > state.getMaxWeight()){
                    _optimalState = state;
                }
            }
        }
    }


    /**
     * Branch and Bound algorithm. Recursively explore all the possible schedule and find the optimal schedule.
     */
    private void branchAndBoundScheduleParallel(List<Processor> processors, Set<Node> freeToSchedule) {
        ParallelState optimalState = new ParallelState();

        if (freeToSchedule.size() > 0){
            // Get Nodes to ignore when internal order is arbitrary
            Set<Node> uniqueNodes = new HashSet<>();
            for (Node node : freeToSchedule) {
                if (!equivalentNode(node, uniqueNodes)){
                    uniqueNodes.add(node);

                    Set<Processor> uniqueProcessors = new HashSet<>();
                    for (Processor processor : processors) {
                        //Calculate the earliest Start time of this Node on this Processor.
                        int startTime = Math.max(processor.getCurrentAbleToStart(), influencedByParents(processor, node));
                        if (!equivalentProcessor(processor, uniqueProcessors, node, startTime)) {
                            uniqueProcessors.add(processor);
                            //Prune:
                            //Check the minimum potential total weight of schedules after this step.
                            //If it is greater than the current optimal schedule's weight, skip it
                            //Otherwise, schedule this Node on this Processor and continue investigating.
                            if (node.getBottomWeight() + startTime < getOptimal().getMaxWeight()) {
                                //Schedule this Node on this Processor. Get a set of Nodes that became free because of this step.
                                Set<Node> newFreeToSchedule = node.schedule(processor, startTime);
                                processor.addNodeAt(node, startTime);
                                //Include every Nodes in the original free Node set except for this scheduled Node.
                                newFreeToSchedule.addAll(freeToSchedule);
                                newFreeToSchedule.remove(node);
                                //Recursively investigating
                                branchAndBoundScheduleParallel(processors, newFreeToSchedule);
                                //Un-schedule this Node to allow it being scheduled on next Processor.
                                unscheduleNode(node);
                            }
                        }
                    }
                }
            }
        }else{ //If all the Nodes have been scheduled
            int max = 0;
            //Calculate the current schedule's weight and compare with the current optimal schedule.
            for (Processor processor : processors){
                max = Math.max(max, processor.getCurrentAbleToStart());
            }
            if (max < optimalState.getMaxWeight()){
                setOptimal(new ParallelState(processors, null));
            }
        }
    }

    /*
        Helper methods
     */
    private Set<Node> getNodes(Set<String> id, int threadId){
        Set<Node> nodes = new HashSet<>();
        for (String s : id){
            nodes.add(_graphs.get(threadId).get(s));
        }
        return nodes;
    }

    private Set<String> getNodesId(Set<Node> nodes){
        Set<String> id = new HashSet<>();
        for (Node n: nodes){
            id.add(n.getId());
        }
        return  id;
    }

    private synchronized ParallelState getOptimal(){
        return _optimalState;
    }

    private synchronized void setOptimal(ParallelState optimal) {
        if (_controller != null) {
            _controller.update(optimal.translate(),optimal.getMaxWeight());
        }
        _optimalState = optimal;
    }
}
