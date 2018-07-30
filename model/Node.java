package model;

import java.util.HashMap;
import java.util.Map;

public class Node {
    Map<Node, Integer> _parents;
    Map<Node, Integer> _children;

    private int _weight;

    public Node(int weight){
        _parents = new HashMap<>();
        _children = new HashMap<>();
        _weight = weight;
    }

    public void addParent(Node parent, int pathWeight){
        _parents.put(parent, pathWeight);
    }

    public void addChild(Node child, int pathWeight) {
        _children.put(child, pathWeight);
    }

    public boolean isParent(Node node){
        return _parents.keySet().contains(node);
    }

    public boolean isChild(Node node){
        return _children.keySet().contains(node);
    }

    public int getPathWeightToChild(Node child){
        if (_children.containsKey(child)){
            return _children.get(child);
        }else{
            return -1;
        }
    }
}
