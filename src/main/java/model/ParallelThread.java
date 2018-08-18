package model;

import java.util.List;
import java.util.Map;

public class ParallelThread extends Thread{
    private int _parallelId;
    private Map<String, Node> _graph;
    private Map<Integer, Processor> _processors;

    private Runnable _parallelRunnable;

    //Parameters: assigned Node list and Processor list
    public ParallelThread(int id, List<Node> graph, List<Processor> processors) {
        _parallelId = id;
        for (Node node : graph){
            _graph.put(node.getId(), node);
        }
        for (Processor processor : processors){
            _processors.put(processor.getID(), processor);
        }
    }

    public int getParallelId() {
        return _parallelId;
    }

    public void setRunnable(Runnable runnable){
        _parallelRunnable = runnable;
    }

    @Override
    public void run(){
        _parallelRunnable.run();
    }



}
