package model;

import java.util.*;

public class Scheduler {
    private List<Node> _graph;
    private int _numberOfProcessor;
    private List<Processor> _processors;

    public Scheduler(List<Node> graph, int numberOfProcessor){
        _graph = graph;
        _numberOfProcessor = numberOfProcessor;
        _processors = new ArrayList<>();
        for (int i = 0; i < numberOfProcessor; i++){
            _processors.add(new Processor(i));
        }
    }

    /**
     * schedules the nodes in the list to processors using greedy algorithm
     */
    public void schedule(){
        List<Node> processedNodes = new ArrayList<>();

        //schedules all nodes in list
        for (Node currentNode:_graph){
            //checks if nodes parents have been fulfilled
            if(processedNodes.containsAll(currentNode.getParents().keySet())){
                greedySchedule(currentNode);
                processedNodes.add(currentNode);
            }
        }
    }

    /**
     * greedy algorithm to find the current best Processor to allocating the currentNode
     * @param currentNode the node to be allocated to a processor
     */
    private void greedySchedule(Node currentNode){
        Processor bestProcessor = _processors.get(0);
        int bestStartTime = 0;

        //Iterates through each processor to find the earliest time the node can start processing in that processor
        for (int i = 0; i < _numberOfProcessor; i++) {
            Processor currentProcessor = _processors.get(i);

            //Find the current time able to start on current processor ignoring other processors
            int currentAbleToStart = StartTime(currentNode, currentProcessor);

            //Finds the earliest possible time considering other processors
            currentAbleToStart = compareWithOtherProcessors(currentProcessor, currentNode, currentAbleToStart);

            //assigns the earliest time to start on first processor
            if (i == 0){
                bestStartTime = currentAbleToStart;

            //Compare time of current best starting time to the earliest starting time on tis processor
            }else if(currentAbleToStart < bestStartTime){
                bestStartTime = currentAbleToStart;
                bestProcessor = currentProcessor;
            }
        }
        //updates the processor to add node to the schedule
        bestProcessor.addNode(bestStartTime, currentNode);
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

    /**
     * Return a list of scheduled processors
     */
    public List<Processor> getSchedule() {
        return _processors;
    }
}
