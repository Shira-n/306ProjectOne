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
}
