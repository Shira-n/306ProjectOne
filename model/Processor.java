package model;

//@TODO @Suying

import java.util.List;

public class Processor {

    private int _pid;
    private int _currentTotalWeight;

    private List<Node> _currentPath;

    public Processor(int pid){
        _pid = pid;
        _currentTotalWeight = 0;
    }

    public void addNode(Node node){
        _currentPath.add(node);
        _currentTotalWeight += node.getWeight();
    }

    public Node getLastNode(){
        return _currentPath.get(_currentPath.size()-1);
    }
}