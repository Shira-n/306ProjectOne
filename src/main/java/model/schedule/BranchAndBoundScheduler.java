package model.schedule;

import model.Node;
import model.Processor;
import model.State;

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





    /**
     * Return a list of scheduled processors
     */
    public List<Processor> getSchedule() {
        schedule();
        return _processors;
    }

    public Map<String, Node> getScheduledNodes() {
        schedule();
        Map<String, Node> schedule = new HashMap<>();
        for (Node n : _graph) {
            schedule.put(n.getId(), n);
        }
        return schedule;
    }

    public State getOptimalSchedule(){
        schedule();
        return _optimalState;
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Schedules the Nodes in the list to Processors using greedy algorithm
     */
    public void schedule() {
        scheduleNode(_processors.get(0), _graph.get(0), 0);
        bbOptimalSchedule(1);
        //_optimalState.print();
        System.out.println("Weight: "+_optimalState.getMaxWeight());
    }

    private void bbOptimalSchedule(int pointer){
        if (pointer < _graph.size()){
            Node node = _graph.get(pointer);
            int startTime;
            for (Processor processor : _processors){
                unscheduleAfter(pointer);
                startTime = Math.max(processor.getCurrentAbleToStart(), infulencedByParents(processor, node));
                scheduleNode(processor, node, startTime);
                pointer++;
                bbOptimalSchedule(pointer);
                pointer--;
            }
        }else{
            State schedule = new State(_processors);
            if (schedule.getMaxWeight() < _optimalState.getMaxWeight()){
                _optimalState = schedule;
            }
        }
    }

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

    /**
     * for test
     */
    public List<Node> getGraph() {
        return _graph;
    }
}
