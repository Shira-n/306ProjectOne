package model;

import controller.Controller;

import java.util.*;

public class BranchAndBoundScheduler {
    private List<Node> _graph;
    private List<Processor> _processors;
    private State _optimalState;
    private Set<Node> _freeToSchedule;
    private Controller _controller;

    public BranchAndBoundScheduler(List<Node> graph, int numberOfProcessor) {
        this(graph,numberOfProcessor,null);
    }

    public BranchAndBoundScheduler(List<Node> graph, int numberOfProcessor, Controller controller) {
        //_graph = topologicalSort(graph);
        _graph = graph;
        _controller = controller;
        _freeToSchedule = findEntries(graph);
        for (Node node : _freeToSchedule){
            calcBottomWeight(node);
        }
        for (int i = 0 ;i < _graph.size(); i++){
            System.out.print(" " + _graph.get(i).getId());
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
     * @param node
     * @return
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
        bbOptimalSchedule(_freeToSchedule);
        System.out.println("\nMax Weight: "+_optimalState.getMaxWeight());
    }

    /**
     * Branch and Bound algorithm. Recursively explore all the possible schedule and find the optimal schedule.
     */
    private void bbOptimalSchedule(Set<Node> freeToSchedule){
        //If there is still a Node to schedule
        if (freeToSchedule.size() > 0){
            for (Node node : freeToSchedule) {
                for (Processor processor : _processors) {
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
                        bbOptimalSchedule(newFreeToSchedule);
                        //Un-schedule this Node to allow it being scheduled on next Processor.
                        unscheduleNode(node);
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
                //if there is visualisation
                //System.out.println(_controller);
                if (_controller != null) {
                    //System.out.println("Call update");
                    //calls the controller class to update GUI to display newly computed current optimal schedule.
                    _controller.update(_optimalState.translate());
                }
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
