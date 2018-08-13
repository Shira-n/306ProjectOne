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
        System.out.println(" ");

        scheduleNode(_processors.get(0), _graph.get(0), 0);
        bbOptimalSchedule(1);

        System.out.println("\n\nFinally the optimal schedule is");
       _optimalState.print();

        System.out.println("Weight: "+_optimalState.getMaxWeight());
    }

    private State _optimalState = new State();
    private List<Node> _freeToSchdule = new ArrayList<>();
    private List<Node> _scheduled = new ArrayList<>();
    private List<Node> _canNotSchedule = new ArrayList<>();


    private void bbOptimalSchedule(int pointer){
        if (pointer < _graph.size()){
            System.out.println("\nScheduling Node"+ _graph.get(pointer).getId());

            Node node = _graph.get(pointer);
            int startTime;
            for (Processor processor : _processors){
                System.out.println("\nScheduling "+ _graph.get(pointer).getId() +" at P" + processor.getID());
                System.out.println("\nFirst unschedule nodes after "+ _graph.get(pointer).getId());
                unscheduleAfter(pointer);

                startTime = Math.max(processor.getCurrentAbleToStart(), infulencedByParents(processor, node));
                System.out.println("\nAfter that, scheduled " + node.getId() + " at P" + processor.getID() + " at time " + startTime);
                scheduleNode(processor, node, startTime);

                pointer++;
                System.out.println("\nGo down a level");
                bbOptimalSchedule(pointer);
                System.out.println("\nGo back a level");
                pointer--;
            }
        }else{
            State schedule = new State(_processors);
            System.out.println(schedule.getMaxWeight() + " vs current optimal " + _optimalState.getMaxWeight());
            if (schedule.getMaxWeight() < _optimalState.getMaxWeight()){
                System.out.println("Switched!");
                _optimalState = schedule;
                System.out.println("Now the optimal schedule is ");
                _optimalState.print();
            }
        }
    }



    private void scheduleNode(Processor processor, Node node, int startTime){
        node.schedule(processor, startTime);
        processor.addNodeAt(node, startTime);
    }

    private void unscheduleNode(Node node){
        if (node.getProcessor() != null) {
            System.out.println("Node " + node.getId() + " 's processor is not null");
            node.getProcessor().removeNodeAt(node.getStartTime());
        }
        node.unSchedule();
    }

    private void unscheduleAfter(int pointer){
        for (int i = pointer; i < _graph.size(); i++){
            System.out.println("Clear schedule for " + _graph.get(i).getId());
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
