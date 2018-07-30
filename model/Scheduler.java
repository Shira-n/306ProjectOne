package model;

import java.util.List;

public class Scheduler {
    private List<Node> _graph;
    private int _numberOfProcessor;

    public Scheduler(List<Node> graph, int numberOfProcessor){
        _graph = graph;
        _numberOfProcessor=numberOfProcessor;
    }

}
