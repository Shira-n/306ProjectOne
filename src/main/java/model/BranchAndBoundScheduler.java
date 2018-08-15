package model;

import java.util.*;

public class BranchAndBoundScheduler {
    private List<Node> _graph;
    private List<Processor> _processors;
    private State _optimalState;
    private Set<Node> _freeToSchedule;

    public BranchAndBoundScheduler(List<Node> graph, int numberOfProcessor) {
        //_graph = topologicalSort(graph);
        _graph = graph;
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
            // Get Nodes to ignore when internal order is arbitrary
            Set<Node> nodesToIgnore = internalOrderingCheck(freeToSchedule);
            for (Node node : freeToSchedule) {
                // Check if node is okay to schedule
               if (!(nodesToIgnore.contains(node))) {
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
            }
        }else{ //If all the Nodes have been scheduled
            int max = 0;
            //Calculate the current schedule's weight and compare with the current optimal schedule.
            for (Processor processor : _processors){
                max = Math.max(max, processor.getCurrentAbleToStart());
            }
            if (max < _optimalState.getMaxWeight()){
                _optimalState = new State(_processors);
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

    /**
     *
     * @param freeToSchedule the current iteration of nodes with sorted parents
     * @return Set<Node> of Nodes to ignore due to arbitrary internal ordering
     */
    protected Set<Node> internalOrderingCheck(Set<Node> freeToSchedule) {
        Set<Node> nodesToRemove = new HashSet<>();

        for (Iterator<Node> aIterator = freeToSchedule.iterator(); aIterator.hasNext(); ) {
            Node n1 = aIterator.next();
            for (Iterator<Node> bIterator = aIterator; bIterator.hasNext(); ) {
                Node n2 = bIterator.next();
                boolean sameDependencies = false;
                if (!n1.equals(n2)) {
                    sameDependencies = true;
                    Set<Node> n1parents = n1.getParents().keySet();
                    Set<Node> n2parents = n2.getParents().keySet();
                    Set<Node> n1children = n1.getChildren().keySet();
                    Set<Node> n2children = n2.getChildren().keySet();
                    // Check if they have the same set of parents
                    if (n1parents.equals(n2parents)) {
                        // Check if they have the same set of children
                        if (n1children.equals(n2children)) {
                            // Check if all incoming edges have same weight
                            for (Node p : n1parents) {
                                if (!(p.getPathWeightToChild(n1) == p.getPathWeightToChild(n2))) {
                                    sameDependencies = false;
                                }
                            }
                            // Check if all outgoing edges have same weight
                            for (Node p: n1children) {
                                if (!(n1.getPathWeightToParent(p) == n2.getPathWeightToParent(p))) {
                                    sameDependencies = false;
                                }
                            }

                        }
                    }
                }
                if (sameDependencies) {
                    if  (!(nodesToRemove.contains(n2))) {
                        nodesToRemove.add(n2);
                    }

                }
            }
        }



        return nodesToRemove;
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
