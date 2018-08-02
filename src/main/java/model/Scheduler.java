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

    public void schedule(){
        List<Node> processedNodes = new ArrayList<>();
        for (int i = 0; i < _graph.size(); i++){
            Node currentNode = _graph.get(i);
            if (i == 0){
                _processors.get(0).addNode(0, currentNode);
            }else{
                if(processedNodes.containsAll(currentNode.getParents().keySet())){
                    scheduleNode(currentNode);
                    processedNodes.add(currentNode);
                }
            }
        }
    }

    private void scheduleNode(Node currentNode){
        Processor bestProcessor = _processors.get(0);
        int bestStartTime = 0;
        for (int i = 0; i < _numberOfProcessor; i++) {
            Processor currentProcessor = _processors.get(i);
            //Find the current time able to start on current processor
            int currentAbleToStart = earliestStartTime(currentNode.getParents().keySet(), currentProcessor.getCurrentSchedule().values(), currentProcessor);

            int delayStartTime = 0;
            //Compare with other processors to find earliest time possible to start on current processor
            for (Processor p : _processors){
                if (!p.equals(currentProcessor)) {
                    for (Node n : currentNode.getParents().keySet()) {
                        for (Node scheduleNodes : currentProcessor.getCurrentSchedule().values()) {
                            if (n.equals(scheduleNodes)) {
                                delayStartTime = n.getWeight() + n.getStartTime() + n.getPathWeightToChild(currentNode);
                                if (currentAbleToStart < delayStartTime){
                                    currentAbleToStart = delayStartTime;
                                }
                            }
                        }
                    }
                }
            }
            if (i == 0){
                bestStartTime = currentAbleToStart;
            }else if(currentAbleToStart < bestStartTime){
                bestStartTime = currentAbleToStart;
                bestProcessor = currentProcessor;
            }
        }
        bestProcessor.addNode(bestStartTime, currentNode);
    }

    private int earliestStartTime(Set<Node> parents, Collection<Node> _scheduleNodes, Processor processor){
        int currentAbleToStart = processor.getCurrentAbleToStart();
        for (Node n : parents){
            for(Node currentScheduleNode : _scheduleNodes){
                if (n.equals(currentScheduleNode)){
                    if (currentScheduleNode.getStartTime() + currentScheduleNode.getWeight() > currentAbleToStart){
                        currentAbleToStart = currentScheduleNode.getStartTime() + currentScheduleNode.getWeight();
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
