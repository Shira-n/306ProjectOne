package model;

import java.util.*;

public class Scheduler {
    private List<Node> _graph;
    private int _numberOfProcessor;
    private List<Processor> _processors;

    public Scheduler(List<Node> graph, int numberOfProcessor){
        _graph = topoligicalSort(graph);
        _numberOfProcessor = numberOfProcessor;
        _processors = new ArrayList<>();
        for (int i = 0; i < numberOfProcessor; i++){
            _processors.add(new Processor(i));
        }
    }

    public void schedule(){
        List<Node> processedNodes = new ArrayList<>();
        for (int i = 0; i < _graph.size(); i++){
            if (i == 0){
                _processors.get(0).addNode(0,_graph.get(0));
            }else{
                if(dependenciesMet(processedNodes, _graph.get(i))){
                    earliestStartTime(_graph.get(i));
                }
            }
        }
    }

    private boolean dependenciesMet(List<Node> processedNodes, Node currentNode){
        if (processedNodes.containsAll(currentNode.getParents().keySet())){
            return true;
        }else {
            return false;
        }
    }

    private int earliestStartTime(Node currentNode){
        Processor bestProcessor = _processors.get(0);
        for (int i = 0; i < _numberOfProcessor; i++) {
            System.out.println("check");
        }

        return -1;
    }


    private List<Node> topoligicalSort(List<Node> graph){
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
        if (startNodes.size() < 1){
            return null;
        }else {
            List<Node> sortedQueue = new ArrayList<>();
            List<Node> newStartNodes = new ArrayList<>();
            for (Node n : startNodes) {
                sortedQueue.add(n);
                for (Node child : n.getChildren().keySet()) {
                    child.sortParent();
                    if (child.parentsSorted()) {
                        newStartNodes.add(child);
                    }
                }
            }
            sortedQueue.addAll(recursiveSort(newStartNodes));
            return sortedQueue;
        }
    }

    /**
     * Return a list of scheduled processors
     */
    public List<Processor> getSchedule() {
        return _processors;
    }
}
