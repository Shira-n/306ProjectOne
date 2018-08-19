package model.scheduler;

import controller.Controller;
import model.*;

import java.util.*;

public class ParallelScheduler implements Scheduler{
    private List<List<Node>> _graphs;
    private List<List<Processor>> _processors;
    private Map<Integer, ParallelThread> _threads;
    private List<ParallelThread> _freeThreads;

    private State _optimalState;
    private Set<Node> _freeToSchedule;


    public ParallelScheduler(Map<Integer, ParallelThread> threads, List<List<Node>> graphs, int numberOfProcessor) {
        // 0 ... n-2 : threads, n-1 : main thread
        _graphs = graphs;
        _threads = threads;
        _freeThreads = new ArrayList<>();
        for (ParallelThread t : threads.values()){
            _freeThreads.add(t);
        }

        //Set up processors
        _processors = new ArrayList<>();
        for(int i = 0; i < _graphs.size(); i++) {
            List<Processor> processors = new ArrayList<>();
            for (int j = 0; j < numberOfProcessor; j++) {
                processors.add(new Processor(j));
            }
            _processors.add(processors);
        }

        //Pre-process graphs
        Map<String, Node> shadow = new HashMap<>();
        for(Node node : _graphs.get(0)){
            shadow.put(node.getId(), node);
        }

        _freeToSchedule = new HashSet<>();
        for (Node node : shadow.values()) {
            //Find entry points
            if (node.getParents().size() <= 0) {
                _freeToSchedule.add(node);
                calcBottomWeight(node);
            }
            //Calculate equivalent nodes
            for (Node parent : node.getParents().keySet()) {
                for (Node sibling : parent.getChildren().keySet()) {
                    if (!node.equals(sibling) && node.isEquivalent(sibling)) {
                        node.addEquivalentNode(sibling);
                    }
                }
            }
        }

        //Copy result to other graphs
        for (List<Node> graph : _graphs){
            for (Node node : graph){
                node.setBottomWeight((shadow.get(node.getId()).getBottomWeight()));
                for (Node sibling : shadow.get(node.getId()).getEquivalentNodes()){
                    node.setEquivalentNodes(sibling);
                }
            }
        }

        _optimalState = new State();
    }

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


    /*
        Methods that call schedule and return the final schedule
     */
    /**
     * Return a list of scheduled processors. Used in Basic Milestone
     */
    public State getSchedule() {
        schedule();
        return _optimalState;
    }

    /*
        Schedule methods
     */

    /**
     * Schedules the Nodes in the list to Processors using greedy algorithm
     */
    public void schedule() {
        parallelCompute(_freeToSchedule);

        // = bbOptimalSchedule(_freeToSchedule);
        System.out.println("\nMax Weight: "+ _optimalState.getMaxWeight());
    }


    private void test(Set<Node> freeToSchedule){
        //
        while (!freeToSchedule.isEmpty()){


        }
    }


    private void parallelCompute(Set<Node> freeToSchedule){
        for (Node node : freeToSchedule){
            if (_freeThreads.size() > 0){
                ParallelThread thread = _freeThreads.get(0);
                _freeThreads.remove(thread);
                State state = new State(_processors.get(_processors.size() - 1));
                Runnable r = new ParallelRunnable(_graphs.get(thread.getParallelId()), _processors.get(thread.getParallelId()))/* {

                    @Override
                    public void run() {
                        State optimal = bbOptimalSchedule(state, thread.getParallelId());
                    }

                }*/
                        ;
                thread.setRunnable(r);
                thread.start();
            }

        }
    }

    /**
     * Branch and Bound algorithm. Recursively explore all the possible schedule and find the optimal schedule.
     */
    private State bbOptimalSchedule(State state, int threadId){
        State optimalState = new State();

        Set<Node> freeToSchedule = state.rebuild(_graphs.get(threadId), _processors.get(threadId));

        //If there is still a Node to schedule
        if (freeToSchedule.size() > 0){
            // Get Nodes to ignore when internal order is arbitrary
            Set<Node> visitedNodes = new HashSet<>();
            for (Node node : freeToSchedule) {
                boolean repeated = false;
                for (Node visited : visitedNodes){
                    if (node.isEquivalent(visited)){
                        repeated = true;
                        break;
                    }
                }
                // Check if node is okay to schedule
                if (!repeated) {
                    for (Processor processor :  _processors.get(threadId)) {
                        //Calculate the earliest Start time of this Node on this Processor.
                        int startTime = Math.max(processor.getCurrentAbleToStart(), infulencedByParents(processor, node));
                        //Prune:
                        //Check the minimum potential total weight of schedules after this step.
                        //If it is greater than the current optimal schedule's weight, skip it
                        //Otherwise, schedule this Node on this Processor and continue investigating.
                        if (node.getBottomWeight() + startTime <= _optimalState.getMaxWeight()) {
                            //Schedule this Node on this Processor. Get a set of Nodes that became free because of this step.
                            Set<Node> newFreeToSchedule = node.schedule(processor, startTime);
                            processor.addNodeAt(node, startTime);
                            //Include every Nodes in the original free Node set except for this scheduled Node.
                            newFreeToSchedule.addAll(freeToSchedule);
                            newFreeToSchedule.remove(node);
                            //Recursively investigating
                            bbOptimalSchedule(new State(_processors.get(threadId)), threadId);
                            //Un-schedule this Node to allow it being scheduled on next Processor.
                            unscheduleNode(node);
                        }
                    }
                }
                visitedNodes.add(node);
            }
        }else{ //If all the Nodes have been scheduled
            int max = 0;
            //Calculate the current schedule's weight and compare with the current optimal schedule.
            for (Processor processor : _processors.get(threadId)){
                max = Math.max(max, processor.getCurrentAbleToStart());
            }
            if (max < optimalState.getMaxWeight()){
                optimalState = new State(_processors.get(threadId));
                //optimalState.print();
            }
        }
        return optimalState;
    }


    /*
        Schedule Helpers
     */
    private void scheduleNode(Processor processor, Node node, int startTime){
        node.schedule(processor, startTime);
        processor.addNodeAt(node, startTime);
    }

    private void unscheduleNode(Node node){
        if (node.getProcessor() != null) {
            node.getProcessor().removeNodeAt(node.getStartTime());
        }
        node.unSchedule();
    }

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


    @Override
    public void setController(Controller controller) {

    }
}
