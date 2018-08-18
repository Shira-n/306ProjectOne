package model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParallelRunnable implements Runnable{
    List<Node> _graph;
    List<Processor> _processors;

    private State _optimalState;

    public ParallelRunnable(List<Node> graph, List<Processor> processors){
        _graph = graph;
        _processors = processors;
    }

    public void run() {
        _optimalState = bbOptimalSchedule();
    }

    public State getOptimalState(){
        return _optimalState;
    }

    /**
     * Branch and Bound algorithm. Recursively explore all the possible schedule and find the optimal schedule.
     */
    private State bbOptimalSchedule(){
        State optimalState = new State();

        Set<Node> freeToSchedule = new State(_processors).rebuild(_graph, _processors);

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
                    for (Processor processor :  _processors) {
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
                            bbOptimalSchedule();
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
            for (Processor processor : _processors){
                max = Math.max(max, processor.getCurrentAbleToStart());
            }
            if (max < optimalState.getMaxWeight()){
                optimalState = new State(_processors);
                //optimalState.print();
            }
        }
        return optimalState;
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


}
