package main.java.model;

import java.util.ArrayList;
import java.util.List;

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

    }

    /**
     * Return a list of scheduled processors
     */
    public List<Processor> getSchedule() {
        return _processors;
    }
}
