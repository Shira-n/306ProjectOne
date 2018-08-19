package model.scheduler;

import model.Node;
import model.state.AbstractState;
import model.state.State;
import model.Processor;

import java.util.*;

/**
 * AbstractScheduler class that implements Branch and Bound algorithm and guarantees to find an optimal schedule
 */
public class OptimalScheduler extends AbstractScheduler {
    private List<Node> _graph;
    private List<Processor> _processors;
    private State _optimalState;
    private Set<Node> _freeToSchedule;


    public OptimalScheduler(List<Node> graph, int numberOfProcessor) {
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

    /*
        Methods that call schedule and return the final schedule.
     */
    /**
     * Return a list of scheduled processors. Used in Basic Milestone
     */
    @Override
    public AbstractState getSchedule(){
        schedule();
        //update GUI state to complete if there is visualisation
        if(_controller != null) {
            System.out.println("IN COMPLETE");
            _controller.completed(_optimalState);
        }
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
            //Prune:
            //When there are two equivalent Nodes, only schedule one of them.
            Set<Node> uniqueNodes = new HashSet<>();
            for (Node node : freeToSchedule) {
                if (!equivalentNode(node, uniqueNodes)){
                    uniqueNodes.add(node);

                    //Prune:
                    //Processor Normalisation
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
            if (max < _optimalState.getMaxWeight()){
                _optimalState = new State(_processors);

                if (_controller != null) {
                    _controller.update(_optimalState.translate(),_optimalState.getMaxWeight());
                }
            }
        }
        return optimalState;
    }


    /**
     * for test
     */
    public List<Node> getGraph() {
        return _graph;
    }


    /*
        A*. A Star implementation.

    /**
     * Return the optimal state from Branch and Bound algorithm.

     public State getOptimalSchedule(){
     ASchedule();
     return _optimalState;
     }*
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
<<<<<<< HEAD

        for (Node node : freeToSchedule) {
            //Calculate Nodes that become free because of scheduling this Node
            Set<Node> newFreeToSchedule = node.ifSchedule();
            newFreeToSchedule.addAll(freeToSchedule);
            newFreeToSchedule.remove(node);
            for (Processor processor : _processors) {
                int startTime = Math.max(processor.getCurrentAbleToStart(), influencedByParents(processor, node));
=======
        //Set<Node> nodesToIgnore = internalOrderingCheck(freeToSchedule);
        for (Node node : freeToSchedule) {
            // Check if node is okay to schedule
            //if (!(nodesToIgnore.contains(node))) {
            //int bottomeWeight = Integer.MAX_VALUE;

            //Calculate Nodes that become free because of scheduling this Node
            Set<Node> newFreeToSchedule = node.ifSchedule();
            newFreeToSchedule.addAll(freeToSchedule);
            newFreeToSchedule.remove(node);
                System.out.print( "\nScheduling Node " + node.getId() + ", current free Nodes are:");
                for (Node n : newFreeToSchedule){
                    System.out.print(" " + n.getId());
                }

            for (Processor processor : _processors) {

                //System.out.println("\nNow try to schedule it on P" + processor.getID());
                int startTime = Math.max(processor.getCurrentAbleToStart(), infulencedByParents(processor, node));

                node.schedule(processor, startTime);
                processor.addNodeAt(node, startTime);

                //Record this State
                State state = new State(_processors, newFreeToSchedule);
<<<<<<< HEAD
                newStates.add(state);
                unscheduleNode(node);
            }
=======
                //state.print();
                //if (state.getBottomWeight() < bottomeWeight){
                //bottomeWeight = state.getBottomWeight();
                newStates.add(state);
                //System.out.println("this state is added to list of states to return");
                //}
                unscheduleNode(node);
            }
            //}
>>>>>>> ad68d27ee22c9d3b783063c71e89904168736a6a
        }
        return newStates;
    }
  */

    /*
        Getter & Setter methods for testing
     */

}
