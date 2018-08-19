package model.scheduler;

import model.Node;
import model.State;
import model.Processor;

import java.util.*;

/**
 * AbstractScheduler class that implements Branch and Bound algorithm and guarantees to find an optimal schedule
 */
public class OptimalAbstractScheduler extends AbstractScheduler {
    private List<Node> _graph;
    private List<Processor> _processors;
    private State _optimalState;
    private Set<Node> _freeToSchedule;

    public OptimalAbstractScheduler(List<Node> graph, int numberOfProcessor) {
        _graph = graph;
        _freeToSchedule = findEntries(graph);
        //Calc bottom weight
        for (Node node : _freeToSchedule){
            calcBottomWeight(node);
        }

        //Calc equivalent nodes
        for(Node node : _graph){
            for (Node parent : node.getParents().keySet()){
                for (Node sibling : parent.getChildren().keySet()){
                    if (node.equals(sibling) && internalOrderingCheck(node, sibling) && node.isEquivalent(sibling)){
                        node.addEquivalentNode(sibling);
                        sibling.addEquivalentNode(node);
                    }
                }
            }
        }
        _processors = new ArrayList<>();
        for (int i = 0; i < numberOfProcessor; i++) {
            _processors.add(new Processor(i));
        }
        _optimalState = new State();
    }

    /**
     * Find the entry points of a graph, that is, Nodes that initially do not have a parent.
     */
    private Set<Node> findEntries(List<Node> graph){
        Set<Node> entries = new HashSet<>();
        for (Node n : graph) {
            if (n.getParents().size() <= 0) {
                entries.add(n);
            }
        }
        return  entries;
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
        Methods that call schedule and return the final schedule.
     */
    /**
     * Return a list of scheduled processors. Used in Basic Milestone
     */
    public State getSchedule(){
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
        //Manually schedule the first Node on the first Processor
        bbOptimalSchedule(_freeToSchedule);
        System.out.println("\nMax Weight: "+ _optimalState.getMaxWeight());
    }

    /**
     * Branch and Bound algorithm. Recursively explore all the possible schedule and find the optimal schedule.
     */
    private State bbOptimalSchedule(Set<Node> freeToSchedule){
        State optimalState = new State();

        //If there is still a Node to schedule
        if (freeToSchedule.size() > 0){
            // Get Nodes to ignore when internal order is arbitrary
            Set<Node> uniqueNodes = new HashSet<>();
            for (Node node : freeToSchedule) {
                if (!equivalentNode(node, uniqueNodes)){
                    uniqueNodes.add(node);

                    Set<Processor> uniqueProcessors = new HashSet<>();
                    for (Processor processor : _processors) {

                        //Calculate the earliest Start time of this Node on this Processor.
                        int startTime = Math.max(processor.getCurrentAbleToStart(), influencedByParents(processor, node));
                        if (!equivalentProcessor(processor, uniqueProcessors, node, startTime)) {
                            uniqueProcessors.add(processor);
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
            for (Processor processor : _processors){
                max = Math.max(max, processor.getCurrentAbleToStart());
            }
            if (max < optimalState.getMaxWeight()){
                _optimalState = new State(_processors);
                //optimalState.print();
            }
        }
        return _optimalState;
    }

    /*
        A*. A Star implementation.

   public State getOptimalSchedule(){
        ASchedule();
        return _optimalState;
    }

    private void ASchedule(){
        for (State s : getNewStates(_freeToSchedule)){
            _Optimal_stateQueue.add(s);
        }
        AStarSchedule(_Optimal_stateQueue);
    }

    private PriorityQueue<State> _Optimal_stateQueue = new PriorityQueue<State>(10, (s1, s2) -> {
        if (s1.getMaxWeight() + s1.getBottomWeight() > s2.getMaxWeight() + s2.getBottomWeight()){
            return 1;
        }else{
            return -1;
        }
    });

    private void AStarSchedule(PriorityQueue<State> stateQueue){
        State state;
        Set<Node> freeToSchedule;
        while (stateQueue.size() > 1){
            state = stateQueue.peek();
            freeToSchedule = state.rebuild(_graph, _processors);
            if (freeToSchedule.size() < 1){
                _optimalState = state;
                return;
            }
            stateQueue.remove(state);
            for (State s : getNewStates(freeToSchedule)){
                stateQueue.add(s);
            }
            if (Runtime.getRuntime().freeMemory() < 600_000_00L){
                break;
            }
        }
        System.out.println(stateQueue.size());
        System.out.println("Switched to B&B");
        State bbOptimal;
        for (State s : stateQueue){
            freeToSchedule = s.rebuild(_graph, _processors);
            bbOptimal = bbOptimalSchedule(freeToSchedule);
            if (_optimalState.getMaxWeight() > bbOptimal.getMaxWeight()){
                _optimalState = bbOptimal;
            }
        }
    }

    **
     * Given a set of Nodes that are free to schedule in the current state, calculate the possible states that can be
     * generated from this state.
     *
    private List<State> getNewStates(Set<Node> freeToSchedule){
        List<State> newStates = new ArrayList<>();

        for (Node node : freeToSchedule) {
            //Calculate Nodes that become free because of scheduling this Node
            Set<Node> newFreeToSchedule = node.ifSchedule();
            newFreeToSchedule.addAll(freeToSchedule);
            newFreeToSchedule.remove(node);
            for (Processor processor : _processors) {
                int startTime = Math.max(processor.getCurrentAbleToStart(), influencedByParents(processor, node));
                node.schedule(processor, startTime);
                processor.addNodeAt(node, startTime);

                //Record this State
                State state = new State(_processors, newFreeToSchedule);
                newStates.add(state);
                unscheduleNode(node);
            }
        }
        return newStates;
    }
  */





    /*
        Schedule Helpers
     */
    private void scheduleNode(Processor processor, Node node, int startTime){
        node.schedule(processor, startTime);
        processor.addNodeAt(node, startTime);
    }



    /*
     * Calculate the earliest start time of the input Node on the input Processor, only considering the schedule
     * of the input Node's parents.

    private int influencedByParents(Processor target, Node n) {
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


     * Return true if two nodes are exchangable, false otherwise.
     *
    private boolean internalOrderingCheck(Node node, Node visited){
        if (node.getWeight() != visited.getWeight()){
            return false;
        }
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
            anotherStartTime = Math.max(uniqueProcessor.getCurrentAbleToStart(), influencedByParents(uniqueProcessor, node));
            if (anotherStartTime == startTime && processor.toString().equals(uniqueProcessor.toString())){
                return true;
            }
        }
        return false;
    }

      private void unscheduleNode(Node node){
        if (node.getProcessor() != null) {
            node.getProcessor().removeNodeAt(node.getStartTime());
        }
        node.unSchedule();
    }
  */
    /*
        Getter & Setter methods for testing
     */

    /**
     * for test
     */
    public List<Node> getGraph() {
        return _graph;
    }
}
