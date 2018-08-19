package model.scheduler;

import model.Node;
import model.Processor;
import model.State;

import java.util.*;

/**
 * AbstractScheduler class implements Greedy algorithm that guarantees to find a valid schedule. Used in Basic Milestone.
 */
public class GreedyAbstractScheduler extends AbstractScheduler {
    private List<Node> _graph;
    private List<Processor> _processors;

    public GreedyAbstractScheduler(List<Node> graph, int numberOfProcessor){
        _graph = topologicalSort(graph);
        _processors = new ArrayList<>();
        for (int i = 0; i < numberOfProcessor; i++){
            _processors.add(new Processor(i));
        }
    }

    /**
     * Schedules the Nodes in the list to Processors using greedy algorithm
     */
    public void schedule(){
        //Schedules all Nodes in list. As the list has already be topologically sorted, we can just schedule all
        //the Nodes one by one.
        for (Node currentNode : _graph){
            simplifiedGreedySchedule(currentNode);
        }
    }

    /**
     * A simplified greedy scheduling method. Schedule the input Node to the Processor such that has the earliest
     * start time.
     */
    private void simplifiedGreedySchedule(Node node){
        Processor bestProcessor = _processors.get(0);
        int bestStartTime = Integer.MAX_VALUE;
        int currentAbleToStart = 0;

        for (Processor p : _processors){
            //Calculate the earliest possible start time on this processor.
            currentAbleToStart = Math.max(p.getCurrentAbleToStart(), influencedByParents(p, node));
            //Set this processor as a candidate if the start time on it is earlier than the current best.
            if (currentAbleToStart < bestStartTime) {
                bestStartTime = currentAbleToStart;
                bestProcessor = p;
            }
        }
        //Update the processor to add node to the schedule.
        bestProcessor.addNodeAt( node, bestStartTime);
    }

    /**
     * Topological sort the input list of Nodes according to their dependencies. Returns a sorted list.
     */
    private List<Node> topologicalSort(List<Node> graph){
        //Find the start nodes in the graph
        List<Node> startNodes = new ArrayList<>();
        for (Node n : graph){
            if (n.parentsSorted()){
                startNodes.add(n);
            }
        }
        //Recursively sort the rest of nodes
        return recursiveSort(startNodes);
    }

    /**
     * Recursive BFS method conduct topological sorting on input Nodes and their children.
     * @param startNodes a list of Nodes that either have no parent or all its parents have been sorted.
     * @return a list of Nodes that contain the input node and its children in sorted topological order.
     */
    private List<Node> recursiveSort(List<Node> startNodes){
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
        if (newStartNodes.size() < 1) { //When there is no more child to sort, return the input list of Nodes.
            return sorted;
        }else{ //If there are still children, recursively sort them
            sorted.addAll(recursiveSort(newStartNodes));
            return sorted;
        }
    }

    /**
     * for test
     */
    public List<Node> getGraph() {
        return _graph;
    }

    /**
     * Return a list of scheduled processors
     */
    public State getSchedule() {
        schedule();
        return new State(_processors);
    }

    /**
     *  Used in Basic milestone. Return the schedule in a way that is easier for file writing
     */
    public Map<String, Node> getScheduledNodes(){
        schedule();
        Map<String, Node> schedule = new HashMap<>();
        for (Node n : _graph){
            schedule.put(n.getId(), n);
        }
        return schedule;
    }

}
