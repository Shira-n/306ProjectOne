package model;

import java.util.*;

public class BranchAndBoundScheduler {
    private List<Node> _graph;
    private List<Processor> _processors;
    private State _optimalState;

    public BranchAndBoundScheduler(List<Node> graph, int numberOfProcessor) {
        _graph = topologicalSort(graph);
        _processors = new ArrayList<>();
        for (int i = 0; i < numberOfProcessor; i++) {
            _processors.add(new Processor(i));
        }
        _optimalState = new State();
    }



    /*
        Topological Sort
     */

    /**
     * Topological sort the input list of Nodes according to their dependencies. Returns a sorted list.
     */
    private List<Node> topologicalSort(List<Node> graph) {
        //Find the start nodes in the graph
        List<Node> startNodes = new ArrayList<>();
        for (Node n : graph) {
            if (n.parentsSorted()) {
                startNodes.add(n);
            }
        }
        //Recursively sort the rest of nodes
        return recursiveSort(startNodes);
    }

    /**
     * Recursive BFS method conduct topological sorting on input Nodes and their children.
     *
     * @param startNodes a list of Nodes that either have no parent or all its parents have been sorted.
     * @return a list of Nodes that contain the input node and its children in sorted topological order.
     */
    private List<Node> recursiveSort(List<Node> startNodes) {
        List<Node> sorted = new ArrayList<>();
        List<Node> newStartNodes = new ArrayList<>();
        for (Node n : startNodes) {
            //Add the input list of Nodes to sorted List.
            sorted.add(n);
            //Explore input Nodes' children and check if there is any child node has all its parents sorted.
            if (n.getChildren().keySet().size() > 0) {
                for (Node child : n.getChildren().keySet()) {
                    child.sortOneParent();
                    if (child.parentsSorted()) {
                        newStartNodes.add(child);
                    }
                }
            }
        }
        //When there is no more child to sort, return the input list of Nodes.
        if (newStartNodes.size() < 1) {
            return sorted;
            //If there are still children, recursively sort them
        } else {
            sorted.addAll(recursiveSort(newStartNodes));
            return sorted;
        }
    }



    /*
        Methods that call schedule and return the final schedule in different forms.
     */

    /**
     * Return a list of scheduled processors. Used in Basic Milestone
     */
    public List<Processor> getSchedule() {
        schedule();
        return _processors;
    }

    /**
     * Return a list of scheduled nodes. Used in Basic Milestone
     */
    public Map<String, Node> getScheduledNodes() {
        schedule();
        Map<String, Node> schedule = new HashMap<>();
        for (Node n : _graph) {
            schedule.put(n.getId(), n);
        }
        return schedule;
    }

    /**
     * Return the optimal state from Branch and Bound algorithm.
     */
    public State getOptimalSchedule(){
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
        scheduleNode(_processors.get(0), _graph.get(0), 0);
        //Start Branch and Bound schedule from the second Node.
        bbOptimalSchedule(1);
        System.out.println("Weight: "+_optimalState.getMaxWeight());
    }

    /**
     * Branch and Bound algorithm. Recursively explore all the possible schedule and find the optimal schedule.
     */
    private void bbOptimalSchedule(int pointer){
        //If there is still a Node to schedule
        if (pointer < _graph.size()){
            Node node = _graph.get(pointer);
            int startTime;
            //Iterate through all the processors to try out all the possible schedule
            for (Processor processor : _processors){
                //Need to un-schedule all the Node after and including this Node. Otherwise other schedule will affect
                //the earliest start time of this Node.
                unscheduleAfter(pointer);
                //Calculate the earliest possible start time for this Node on this Processor.
                startTime = Math.max(processor.getCurrentAbleToStart(), infulencedByParents(processor, node));
                //Schedule this Node on this Processor.
                scheduleNode(processor, node, startTime);
                //Go down to the next Node.
                pointer++;
                //Recursively schedule the rest of the Nodes.
                bbOptimalSchedule(pointer);
                //Go back a level and try to schedule this Node on the next processor.
                pointer--;
            }
        }else{ //If all the Nodes have been scheduled
            State schedule = new State(_processors);
            //Calculate the current schedule's weight and compare with the current optimal schedule.
            if (schedule.getMaxWeight() < _optimalState.getMaxWeight()){
                _optimalState = schedule;
            }
        }
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

    private void unscheduleAfter(int pointer){
        for (int i = pointer; i < _graph.size(); i++){
            unscheduleNode(_graph.get(i));
        }
    }

    private void unscheduleNode(Processor processor, Node node){
        node.unSchedule();
        processor.removeNodeAt(node.getStartTime());
    }

    /**
     * Calculate the earliest start time of the input Node on the input Processor, only considering the schedule
     * of the input Node's parents.
     */
    protected int infulencedByParents(Processor target, Node n) {
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
