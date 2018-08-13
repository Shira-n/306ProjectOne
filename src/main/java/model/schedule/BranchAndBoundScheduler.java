package model.schedule;

import model.Node;
import model.Processor;
import model.State;

import java.util.*;

public class BranchAndBoundScheduler {
    private List<Node> _graph;
    private List<Processor> _processors;

    private State _optimal;

    public BranchAndBoundScheduler(List<Node> graph, int numberOfProcessor) {
        _graph = topologicalSort(graph);
        _processors = new ArrayList<>();
        for (int i = 0; i < numberOfProcessor; i++) {
            _processors.add(new Processor(i));
        }
        _optimal = new State();
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




    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Schedules the Nodes in the list to Processors using greedy algorithm
     */
    public void schedule() {
        //Schedules all Nodes in list. As the list has already be topologically sorted, we can just schedule all
        //the Nodes one by one.

        System.out.println("Sorted list: ");
        for (int i = 0; i < _graph.size(); i++){
            System.out.print(_graph.get(i).getId() + " ");
        }
        _graph.get(0).schedule(_processors.get(0), 0);
        bbOptimalSchedule(1);

       _optimalState.print();
    }

    private State _optimalState = new State();
    private List<Node> _freeToSchdule = new ArrayList<>();
    private List<Node> _scheduled = new ArrayList<>();
    private List<Node> _canNotSchedule = new ArrayList<>();


    private void bbOptimalSchedule(int pointer){
        if (pointer < _graph.size()){
            System.out.println("Scheduling "+ _graph.get(pointer).getId());
            unscheduleAfter(pointer);

            Node n = _graph.get(pointer);
            int startTime;
            for (Processor p : _processors){
                startTime = Math.max(p.getCurrentAbleToStart(), infulencedByParents(p, n));
                n.schedule(p, startTime);
                System.out.println("Scheduled " + n.getId() + " at P" + p.getID() + " at time " + startTime);
                pointer++;
                bbOptimalSchedule(pointer);
            }
        }else{
            State schedule = new State(_processors);
            System.out.println(schedule.getMaxWeight() + " vs current optimal " + _optimalState.getMaxWeight());
            if (schedule.getMaxWeight() < _optimalState.getMaxWeight()){
                System.out.println("Switched!");
                _optimalState = schedule;
            }
        }
    }





    private void unscheduleAfter(int pointer){
        for (int i = pointer; i <= _graph.size(); i++){
            //if (_graph.get(i).getProcessor() == null) {
                System.out.println("Unscheduling " + _graph.get(i).getId());
                _graph.get(i).unSchedule();
            //}else{
                //System.out.println("Not unscheduling " + _graph.get(i).getId());
            //}
        }
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
                //System.out.println("influenced by parent on this p, parent is "+parent.getId()+  " limit is "+ limit);
            } else {
                //System.out.println("currentlimit: " +limit);
                //int i = parent.getStartTime() + parent.getWeight() + n.getPathWeightToParent(parent);
                //System.out.println("other parents: " + i);
                limit = Math.max(limit, parent.getStartTime() + parent.getWeight() + n.getPathWeightToParent(parent));
                //System.out.println("influenced by parent not on this p, parent is "+parent.getId()+ " changed limit tp "+ limit);
            }
        }
        //System.out.println("max limit: " + limit);
        return limit;
    }









    /**
     * for test
     */
    public List<Node> getGraph() {
        return _graph;
    }
}
