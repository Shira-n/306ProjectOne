package model.scheduler;

import model.*;

import java.util.*;
import java.util.concurrent.*;

public class ParaTest implements Scheduler {
    private final int MAIN_THREAD_ID = 0;
    private List<Map<String, Node>> _graphs;
    private List<List<Processor>> _processors;

    private ParallelState _optimalState;
    private Set<String> _freeToSchedule;

    private ExecutorService _executorService;
    private int _threadCount;


    public ParaTest(int numberOfThreads, List<Map<String, Node>> graphs, int numberOfProcessor) {
        _threadCount = numberOfThreads;
        System.out.println("ParaTest: threadCount= "+ _threadCount);
        _graphs = graphs;
        System.out.println("ParaTest: graphs size= "+ _graphs.size());
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
        System.out.println("\nExaming node " + node.getId() + " and node "+ visited.getId());
        //The number of parents and children of them must be the same
        if (node.getParents().keySet().size() != visited.getParents().keySet().size()
                || node.getChildren().keySet().size() != visited.getChildren().keySet().size()){
            System.out.println("they have different number of parent/child: " + node.getParents().size()+" vs " + visited.getParents().size());
            return false;
        }

        for (Node parent : node.getParents().keySet()){
            if (visited.getParents().keySet().contains(parent) &&
                    node.getPathWeightToParent(parent) == visited.getPathWeightToParent(parent)){
                ;
            }else{ //The communication cost for every parent has to be the same for both nodes
                System.out.println("they have different cost to parent");
                return false;
            }
        }

        for (Node child : node.getChildren().keySet()){
            if (visited.getChildren().keySet().contains(child) &&
                    node.getPathWeightToChild(child) != visited.getPathWeightToParent(child)){
                ;
            }else{ //The communication cost for every child has to be the same for both nodes
                System.out.println("they have different cost to child");
                return false;
            }
        }
        System.out.println("they are identical");
        return true;
    }



    /*
        Schedule methods
     */
    @Override
    public State getSchedule() {
        try {
            paraSchedule(_freeToSchedule);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        _executorService.shutdown();
        return  _optimalState;
    }


    private void paraSchedule(Set<String> freeNodeId) throws InterruptedException, ExecutionException {
        List<Processor> processors = _processors.get(MAIN_THREAD_ID);
        Map<String, Node> graph = _graphs.get(MAIN_THREAD_ID);
        Set<Node> freeToSchedule = getNodes(freeNodeId, MAIN_THREAD_ID);

        //When not all the possible schedules have been explored
        if (!freeToSchedule.isEmpty()) {
            List<ParallelState> states = new ArrayList<>();

            //Create tasks for every thread
            if (states.size() < _threadCount) {
                Set<Node> uniqueNodes = new HashSet<>();

                for (Node node : freeToSchedule) {
                    //Check equivalent Nodes
                    if (!equivalentNode(node, uniqueNodes)) {
                        uniqueNodes.add(node);

                        Set <Processor> uniqueProcessors = new HashSet<>();
                        for (Processor processor : processors){
                            int startTime = Math.max(processor.getCurrentAbleToStart(), infulencedByParents(processor, node));
                            if (!equivalentProcessor(processor, uniqueProcessors, node, startTime)){
                                uniqueProcessors.add(processor);

                                if (node.getBottomWeight() + startTime <= _optimalState.getMaxWeight()) {
                                    //Schedule this Node on this Processor. Get a set of Nodes that became free because of this step.
                                    Set<Node> newFreeToSchedule = node.schedule(processor, startTime);
                                    processor.addNodeAt(node, startTime);
                                    //Include every Nodes in the original free Node set except for this scheduled Node.
                                    newFreeToSchedule.addAll(freeToSchedule);
                                    newFreeToSchedule.remove(node);

                                    states.add(new ParallelState(processors, getNodesId(newFreeToSchedule)));

                                    unscheduleNode(node);

                                    //Start to send task for parallelisation
                                    if (states.size() == _threadCount){
                                        List<Callable<ParallelState>> callables = new ArrayList<>();
                                        int thread = 0;
                                        for (ParallelState state : states){
                                            Callable<ParallelState> callable = () -> {
                                                return branchAndBoundScheduleParallel(thread, state);
                                            };

                                            callables.add(callable);
                                        }

                                        List<Future<ParallelState>> optimalSchedules =  _executorService.invokeAll(callables);
                                        for (Future<ParallelState> f: optimalSchedules){
                                            ParallelState optimalSchedule = f.get();
                                            if (_optimalState.getMaxWeight() > optimalSchedule.getMaxWeight()){
                                                _optimalState = optimalSchedule;
                                            }
                                        }
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
        }

    }

    /**
     * Branch and Bound algorithm. Recursively explore all the possible schedule and find the optimal schedule.
     */
    private ParallelState branchAndBoundScheduleParallel(int threadId, ParallelState state) {
        //Navigate to the corresponding lists and maps
        List<Processor> processors = _processors.get(threadId);
        Map<String, Node> graph = _graphs.get(threadId);
        Set<Node> freeToSchedule = getNodes(state.rebuild(graph, processors), threadId);

        ParallelState optimalState = new ParallelState();


        if (freeToSchedule.size() > 0) {
            Set<Node> uniqueNodes = new HashSet<>();
            for (Node node : freeToSchedule) {
                //Check equivalent Nodes
                if (!equivalentNode(node, uniqueNodes)){
                    uniqueNodes.add(node);

                    Set<Processor> uniqueProcessors = new HashSet<>();
                    for (Processor processor: _processors.get(threadId)){
                        int startTime = Math.max(processor.getCurrentAbleToStart(), infulencedByParents(processor, node));
                        //Check equivalent Processors
                        if (!equivalentProcessor(processor,uniqueProcessors, node, startTime)){
                            uniqueProcessors.add(processor);

                            if (node.getBottomWeight() + startTime <= optimalState.getMaxWeight()) {
                                //Schedule this Node on this Processor. Get a set of Nodes that became free because of this step.
                                Set<Node> newFreeToSchedule = node.schedule(processor, startTime);
                                processor.addNodeAt(node, startTime);
                                //Include every Nodes in the original free Node set except for this scheduled Node.
                                newFreeToSchedule.addAll(freeToSchedule);
                                newFreeToSchedule.remove(node);
                                //Recursively investigating
                                branchAndBoundScheduleParallel(threadId, new ParallelState(processors, getNodesId(newFreeToSchedule)));
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
                optimalState = new ParallelState(processors, null);
                //optimalState.print();
            }
        }
        return optimalState;
    }











    /**
     * Branch and Bound algorithm. Recursively explore all the possible schedule and find the optimal schedule.

    private ParallelState bbOptimalSchedule(List<Processor> processors, Set<Node> freeToSchedule) {
        ParallelState optimalState = new ParallelState();

        if (freeToSchedule.size() > 0) {
            Set<Node> uniqueNodes = new HashSet<>();
            for (Node node : freeToSchedule) {
                //Check equivalent Nodes
                if (!equivalentNode(node, uniqueNodes)){
                    uniqueNodes.add(node);


                    Set<Processor> uniqueProcessors = new HashSet<>();
                    for (Processor processor: processors){
                        int startTime = Math.max(processor.getCurrentAbleToStart(), infulencedByParents(processor, node));
                        //Check equivalent Processors
                        if (!equivalentProcessor(processor, processors, node, startTime)){
                            uniqueProcessors.add(processor);


                        }
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

 */

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
        for (Node uniqueNode : uniqueNodes){
            if (uniqueNode.isEquivalent(node)){
                return true;
            }
        }
        return false;
    }

    private boolean equivalentProcessor(Processor processor, Set<Processor> uniqueProcessors, Node node, int startTime){
        int anotherStartTime;
        for (Processor uniqueProcessor : uniqueProcessors){
            //Calculate the earliest Start time of this Node on this Processor.
            anotherStartTime = Math.max(uniqueProcessor.getCurrentAbleToStart(), infulencedByParents(uniqueProcessor, node));
            if (anotherStartTime == startTime && processor.toString().equals(uniqueProcessor.toString())){
                System.out.println("Node "+ node.getId() + " is the same when schedule at " +  startTime );
                System.out.println("P" + processor.getID() + ": " + processor);
                System.out.println("P" + uniqueProcessor.getID() + ": " +  uniqueProcessor);
                return true;
            }
        }
        return false;
    }

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

}
