package model;

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
        List<Node> processedNodes = new ArrayList<>();
        for (int i=0;i<_graph.size();i++){
            if(i==0){
                _processors.get(0).addNode(_graph.get(0));
            }else{
                if(dependenciesMet(processedNodes, _graph.get(i))){
                    earliestStartTime(_graph.get(i));
                }
            }
        }
    }

    private boolean dependenciesMet(List<Node> processedNodes, Node currentNode){
        if(processedNodes.containsAll(currentNode._parents.keySet())){
            return true;
        }else {
            return false;
        }
    }

    private int earliestStartTime(Node currentNode){
        Processor bestProcessor = _processors.get(0);
        for (int i=0;i<_numberOfProcessor;i++) {

        }
        return -1;
    }

    /**
     * Return a list of scheduled processors
     */
    public List<Processor> getSchedule() {
        return _processors;
    }
}
