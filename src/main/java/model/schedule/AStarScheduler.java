package model.schedule;

import model.Node;

import java.util.List;

public class AStarScheduler extends Scheduler {
    public AStarScheduler(List<Node> graph, int numberOfProcessor) {
        super(graph, numberOfProcessor);
    }


}
