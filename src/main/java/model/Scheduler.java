package model;

import application.Main;

import java.util.*;

public class Scheduler {
    private List<Node> _graph;
    private int _numberOfProcessor;
    private List<Processor> _processors;

    public Scheduler(List<Node> graph, int numberOfProcessor){
        _graph = topologicalSort(graph);
        _numberOfProcessor = numberOfProcessor;
        _processors = new ArrayList<>();
        for (int i = 0; i < numberOfProcessor; i++){
            _processors.add(new Processor(i));
        }
    }

    /**
     * Schedules the Nodes in the list to Processors using greedy algorithm
     */
    public void schedule(){
        List<Node> processedNodes = new ArrayList<>();

        //Schedules all Nodes in list. As the list has already be topologically sorted, we can just schedule all
        //the Nodes one by one.
        for (Node currentNode : _graph){
            //System.out.println("\n\nLook at Node" + currentNode.getId());
            simplifedSchedule(currentNode);
            //greedySchedule(currentNode);
        }
    }

    /**
     * A simplified greedy scheduling method. Schedule the input Node to the Processor such that has the earliest
     * start time.
     */
    private void simplifedSchedule(Node node){
        Processor bestProcessor = _processors.get(0);
        int bestStartTime = Integer.MAX_VALUE;
        int currentAbleToStart = 0;

        for (Processor p : _processors){
            //System.out.println("\nlook at Processor" + p.getID());
            //Calculate the earliest possible start time on this processor.
            currentAbleToStart = Math.max(p.getCurrentAbleToStart(), infulencedByParents(p, node));
            //System.out.println("Best Start Time at P" + p.getID() + " is " + currentAbleToStart);
            //Set this processor as a candidate if the start time on it is earlier than the current best.
            if (currentAbleToStart < bestStartTime) {
                bestStartTime = currentAbleToStart;
                bestProcessor = p;
            }
        }
        //Update the Node with the best processor.
        node.set_processor(bestProcessor);
        //Update the processor to add node to the schedule.
        bestProcessor.addNode(bestStartTime, node);
        //System.out.println("\nPut it on P" + bestProcessor.getID() + "with time " + bestStartTime);
    }

    /**
     * Calculate the earliest start time of the input Node on the input Processor, only considering the schedule
     * of the input Node's parents.
     */
    private int infulencedByParents(Processor target, Node n){
        int limit = 0;
        for (Node parent : n.getParents().keySet()){
            if (parent.getProcessor().getID() == target.getID()) {
                limit = Math.max(limit, parent.getStartTime() + parent.getWeight());
                //System.out.println("influenced by parent on this p, parent is "+parent.getId()+  " limit is "+ limit);
            }else{
                //System.out.println("currentlimit: " +limit);
                int i = parent.getStartTime() + parent.getWeight() + n.getPathWeightToParent(parent);
                //System.out.println("other parents: " + i);
                limit = Math.max(limit, parent.getStartTime() + parent.getWeight() + n.getPathWeightToParent(parent));
                //System.out.println("influenced by parent not on this p, parent is "+parent.getId()+ " changed limit tp "+ limit);
            }
        }
        //System.out.println("max limit: " + limit);
        return limit;
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

    private List<Node> recursiveSort(List<Node> startNodes){
        List<Node> sorted = new ArrayList<>();
        List<Node> newStartNodes = new ArrayList<>();
        for (Node n : startNodes) {
            sorted.add(n);
            if (n.getChildren().keySet().size() > 0) {
                for (Node child : n.getChildren().keySet()) {
                    child.sortOneParent();
                    if (child.parentsSorted()) {
                        newStartNodes.add(child);
                    }
                }
            }
        }
        if (newStartNodes.size() < 1) {
            return sorted;
        }else{
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
    public List<Processor> getSchedule() {
        schedule();
        return _processors;
    }



    /**
     * Greedy algorithm to find the current best Processor and the best start time to allocate the input Node
     */
    private void greedySchedule(Node node){
        Processor bestProcessor = _processors.get(0);
        int bestStartTime = Integer.MAX_VALUE;
        int currentAbleToStart = 0;

        //Iterates through each processor to find the earliest time the node can start processing in that processor
        for (int i = 0; i < _numberOfProcessor; i++) {
            Processor candidateProcessor = _processors.get(i);

            //Find the current time able to start on current processor ignoring other processors
            currentAbleToStart = StartTime(node, candidateProcessor);

            //Finds the earliest possible time considering other processors
            currentAbleToStart = compareWithOtherProcessors(candidateProcessor, node, currentAbleToStart);

            //Assigns the earliest time to start on first processor
            if (i == 0){
                bestStartTime = currentAbleToStart;

                //Compare time of current best starting time to the earliest starting time on tis processor
            }else if(currentAbleToStart < bestStartTime){
                bestStartTime = currentAbleToStart;
                bestProcessor = candidateProcessor;
            }
        }

        //Updates the processor to add node to the schedule
        node.set_processor(bestProcessor);
        bestProcessor.addNode(bestStartTime, node);
    }

    /**
     * StartTime method finds the earliest possible time that the schedule can start processing on the processor
     * This method ignores the delay required from other dependencies in other processors.
     * @param node the node that needs to find the earliest it can start processing on processor
     * @param processor the processor that the node needs to be scheduled to
     * @return the earliest time the node can start processing on the processor
     */
    private int StartTime(Node node, Processor processor) {
        int currentAbleToStart = processor.getCurrentAbleToStart();

        //find nodes that are parents of the current node
        for (Node scheduleNode : processor.getCurrentSchedule().values()) {
            if (node.isParent(scheduleNode)) {
                if (scheduleNode.getStartTime() + scheduleNode.getWeight() > currentAbleToStart) {
                    currentAbleToStart = scheduleNode.getStartTime() + scheduleNode.getWeight();
                }
            }
        }
        return currentAbleToStart;
    }

    /**
     * compareWithOtherProcessor method finds the earliest possible time that the schedule can start processing on the
     * processor given the delay required from other dependencies in other processors.
     * @param currentProcessor the processor that the node is being scheduled to
     * @param currentNode the node to be scheduled
     * @param currentAbleToStart the start time of the node on the currentProcessor without considering delays from other processors
     * @return the actual time the node can start processing on current processor considering delays
     */
    private int compareWithOtherProcessors(Processor currentProcessor, Node currentNode, int currentAbleToStart){
        int delayStartTime = 0;
        //Compare with other processors to find earliest time possible to start on current processor
        for (Processor p : _processors){
            if (!p.equals(currentProcessor)) {
                for (Node scheduleNodes : p.getCurrentSchedule().values()) {
                    if (currentNode.isParent(scheduleNodes)) {
                        delayStartTime = scheduleNodes.getWeight() + scheduleNodes.getStartTime() + scheduleNodes.getPathWeightToChild(currentNode);
                        if (currentAbleToStart < delayStartTime) {
                            currentAbleToStart = delayStartTime;
                        }
                    }
                }
            }
        }
        return currentAbleToStart;
    }

}
