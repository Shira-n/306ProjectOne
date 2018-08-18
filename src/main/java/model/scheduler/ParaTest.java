package model.scheduler;

import model.Node;
import model.ParallelState;
import model.Processor;
import model.State;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParaTest implements Scheduler {
    private final int MAIN_THREAD_ID = 0;
    private List<Map<String, Node>> _graphs;
    private List<List<Processor>> _processors;

    private ParallelState _optimalState;
    private Set<String> _freeToSchedule;

    private ExecutorService _executorService;
    private int _threadCount;
    //private Stack<Integer> _freeThreadId;


    public ParaTest(int threads, List<Map<String, Node>> graphs, int numberOfProcessor) {
        //Set up N graphs for each thread
        _graphs = graphs;
        //Use N - 1 extra threads (main counts for one thread)
        _executorService = Executors.newFixedThreadPool(threads);

        _threadCount = numberOfProcessor;
        /*
        _freeThreadId = new Stack<>();
        for (int i = 0; i < threads; i++){
            _freeThreadId.push(i);
        }
        */

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
        Map<String, Node> shadow = _graphs.get(0);

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
                    if (!node.equals(sibling) && !node.isEquivalent(sibling) && internalOrderingCheck(node, sibling)){
                        node.addEquivalentNode(sibling);
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
                    _graphs.get(i).get(node.getId()).addEquivalentNode(_graphs.get(i).get(sibling.getId()));
                }
            }
        }

        _optimalState = new ParallelState();
    }



    /*
        Set up helper methods
     */
    /**
     * Recursively calculate Bottom Weight of the input Node. The bottom weight of a Node will be the sum of its
     * weight and the maximum bottom weight of its children.
     */
    private int calcBottomWeight(Node node){
        if (node.getChildren().size() > 0){
            int maxChileBtmWeight = 0;
            for (Node child: node.getChildren().keySet()){
                maxChileBtmWeight = Math.max(maxChileBtmWeight,calcBottomWeight(child));
            }
            node.setBottomWeight(maxChileBtmWeight + node.getWeight());
        }else{
            node.setBottomWeight(node.getWeight());
        }
        return node.getBottomWeight();
    }

    /**
     * Return true if two nodes are equivalent, false otherwise.
     * To be equivalent, two Nodes must have the same weight, the same parent set with the same communiciation cost
     * to each corresponding parent, and the same children set with the same communication cost to each corresponding
     * child.
     */
    private boolean internalOrderingCheck(Node node, Node visited){
        //The number of parents and children of them must be the same
        if (node.getParents().keySet().size() != visited.getParents().keySet().size()
                || node.getChildren().keySet().size() != visited.getChildren().keySet().size()){
            return false;
        }

        for (Node parent : node.getParents().keySet()){
            if (visited.getParents().keySet().contains(parent) &&
                    node.getPathWeightToParent(parent) == visited.getPathWeightToParent(parent)){
                ;
            }else{ //The communication cost for every parent has to be the same for both nodes
                return false;
            }
        }

        for (Node child : node.getChildren().keySet()){
            if (visited.getChildren().keySet().contains(child) &&
                    node.getPathWeightToChild(child) != visited.getPathWeightToParent(child)){
                ;
            }else{ //The communication cost for every child has to be the same for both nodes
                return false;
            }
        }
        return true;
    }



    /*
        Schedule methods
     */
    @Override
    public State getSchedule() {
        paraSchedule(_freeToSchedule);
        _executorService.shutdown();
        return  _optimalState;
    }

    private Set<Node> getNodes(Set<String> id, int threadId){
        Set<Node> nodes = new HashSet<>();
        for (String s : id){
            nodes.add(_graphs.get(threadId).get(id));
        }
        return nodes;
    }

    private ParallelState getChildState(Node node, State state){

    }



    private void paraSchedule(Set<String> freeToScheduleMain){
        if (!freeToScheduleMain.isEmpty()){

            ParallelState currentState = new ParallelState(_processors.get(MAIN_THREAD_ID), freeToScheduleMain);

            Map<Integer, Callable<ParallelState>> parallelOptimalStates = new HashMap<>();

            List<ParallelState> _startStates = new ArrayList<>();
            while (_startStates.size() < _threadCount){
                for (Node node : getNodes(freeToScheduleMain, 0)){
                    for (Processor processor : _processors.get(0)){

                    }
                }
            }

            //If there is a free thread
            while (!_freeThreadId.empty()){
                int threadId = _freeThreadId.pop();
                Callable<ParallelState> callable = () -> {
                    //Rebuild the assigned data to the input State
                    Set<String> freeToSchedule = currentState.rebuild(_graphs.get(threadId), _processors.get(threadId));


                    return bbOptimalSchedule(_processors.get(threadId), getNodes(freeToSchedule, threadId));
                };
                parallelOptimalStates.put(threadId, callable);


            }

            try {







                List<Future<ParallelState>> optimalStates = _executorService.invokeAll(parallelOptimalStates.values());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{




        }
    }


    /**
     * Branch and Bound algorithm. Recursively explore all the possible schedule and find the optimal schedule.
     */
    private ParallelState bbOptimalSchedule(List<Processor> processors, Set<Node> freeToSchedule) {
        ParallelState optimalState = new ParallelState();

        if (freeToSchedule.size() > 0) {
            Set<Node> uniqueNodes = new HashSet<>();
            for (Node node : freeToSchedule) {
                //Check equivalent Nodes
                boolean equivalentNode = false;
                for (Node visited : uniqueNodes) {
                    if (node.isEquivalent(visited)) {
                        equivalentNode = true;
                        break;
                    }
                }

                //Only process when the Node is not an equivalent Node to any scheduled Node
                if (!equivalentNode) {
                    uniqueNodes.add(node);

                    Set<Processor> uniqueProcessors = new HashSet<>();
                    boolean equivalentProcessor = false;
                    for (Processor processor : processors) {
                        //Calculate the earliest Start time of this Node on this Processor.
                        int startTime = Math.max(processor.getCurrentAbleToStart(), infulencedByParents(processor, node));
                        //Check equivalent Processors
                        int anotherStartTime;
                        for (Processor uniqueProcessor : uniqueProcessors){
                            //Calculate the earliest Start time of this Node on this Processor.
                            anotherStartTime = Math.max(processor.getCurrentAbleToStart(), infulencedByParents(processor, node));
                            if (anotherStartTime == startTime && processor.toString().equals(uniqueProcessor.toString())){
                                equivalentProcessor = true;
                                break;
                            }
                        }

                        if (!equivalentProcessor){
                            uniqueProcessors.add(processor);

                            //Prune:
                            //Check the minimum potential total weight of schedules after this step.
                            //If it is greater than the current optimal schedule's weight, skip it
                            //Otherwise, schedule this Node on this Processor and continue investigating.
                            if (node.getBottomWeight() + startTime <= _optimalState.getMaxWeight()) {
                                //Schedule this Node on this Processor. Get a set of Nodes that became free because of this step.
                                Set<Node> newFreeToSchedule = node.schedule(processor, startTime);
                                processor.addNodeAt(node, startTime);

                                //Current processor is unique so make a deep copy of current state to add to list of processors to check for normalization
                                uniqueProcessors.add(new Processor(processor));

                                //Include every Nodes in the original free Node set except for this scheduled Node.
                                newFreeToSchedule.addAll(freeToSchedule);
                                newFreeToSchedule.remove(node);
                                //Recursively investigating
                                bbOptimalSchedule(processors, newFreeToSchedule);
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
                _optimalState = new ParallelState(processors, null);
                //optimalState.print();
            }
        }
        return optimalState;
    }

















    /*
     * Branch and Bound algorithm. Recursively explore all the possible schedule and find the optimal schedule.

    private ParallelState bbOptimalSchedule(ParallelState state, int threadId) {
        ParallelState optimalState = new ParallelState();
        Set<String> freeToSchedule = state.rebuild(_graphs.get(threadId), _processors.get(threadId));

        if (freeToSchedule.size() > 0) {
            Set<Node> uniqueNodes = new HashSet<>();
            for (String nodeId : freeToSchedule) {
                //Check equivalent Nodes
                Node node = _graphs.get(threadId).get(nodeId);
                boolean equivalentNode = false;
                for (Node visited : uniqueNodes) {
                    if (node.isEquivalent(visited)) {
                        equivalentNode = true;
                        break;
                    }
                }

                //Only process when the Node is not an equivalent Node to any scheduled Node
                if (!equivalentNode) {
                    uniqueNodes.add(node);

                    Set<Processor> uniqueProcessors = new HashSet<>();
                    boolean equivalentProcessor = false;
                    for (Processor processor : _processors.get(threadId)) {
                        //Calculate the earliest Start time of this Node on this Processor.
                        int startTime = Math.max(processor.getCurrentAbleToStart(), infulencedByParents(processor, node));
                        //Check equivalent Processors
                        int anotherStartTime;
                        for (Processor uniqueProcessor : uniqueProcessors){
                            //Calculate the earliest Start time of this Node on this Processor.
                            anotherStartTime = Math.max(processor.getCurrentAbleToStart(), infulencedByParents(processor, node));
                            if (anotherStartTime == startTime && processor.toString().equals(uniqueProcessor.toString())){
                                equivalentProcessor = true;
                                break;
                            }
                        }

                        if (!equivalentProcessor){
                            uniqueProcessors.add(processor);

                            //Prune:
                            //Check the minimum potential total weight of schedules after this step.
                            //If it is greater than the current optimal schedule's weight, skip it
                            //Otherwise, schedule this Node on this Processor and continue investigating.
                            if (node.getBottomWeight() + startTime <= _optimalState.getMaxWeight()) {
                                //Schedule this Node on this Processor. Get a set of Nodes that became free because of this step.
                                Set<Node> newFreeToSchedule = node.schedule(processor, startTime);
                                processor.addNodeAt(node, startTime);

                                //Current processor is unique so make a deep copy of current state to add to list of processors to check for normalization
                                uniqueProcessors.add(new Processor(processor));

                                //Include every Nodes in the original free Node set except for this scheduled Node.
                                newFreeToSchedule.addAll(freeToSchedule);
                                newFreeToSchedule.remove(node);
                                //Recursively investigating
                                bbOptimalSchedule(newFreeToSchedule);
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
            for (Processor processor : _processors.get(threadId)){
                max = Math.max(max, processor.getCurrentAbleToStart());
            }
            if (max < optimalState.getMaxWeight()){
                _optimalState = new State(_processors.get(threadId), null);
                //optimalState.print();
            }
        }
        return optimalState;
    }
     */


    /*
        Scheduler helper methods
     */

    /**
     * Calculate the earliest start time of the input Node on the input Processor, only considering the schedule
     * of the input Node's parents.
     */
    private int infulencedByParents(Processor target, Node n) {
        int limit = 0;
        for (Node parent : n.getParents().keySet()) {
            if (parent.getProcessor().getID() == target.getID()) {
                limit = Math.max(limit, parent.getStartTime() + parent.getWeight());
            } else {
                limit = Math.max(limit, parent.getStartTime() + parent.getWeight() + n.getPathWeightToParent(parent));
            }
        }
        return limit;
    }

    private void unscheduleNode(Node node){
        if (node.getProcessor() != null) {
            node.getProcessor().removeNodeAt(node.getStartTime());
        }
        node.unSchedule();
    }

    private boolean equivalentNode(Node node, Set<Node> uniqueNodes){
        boolean equivalentNode = false;
        for (Node uniqueNode : uniqueNodes){
            if (uniqueNode.isEquivalent(node)){
                return true;
            }
        }
        return false;
    }

    private boolean equivalentProcessor(Processor processor, List<Processor> processors, Node node){
        int maxStart =

    }

}
