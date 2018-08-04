package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represent a node/task in the input graph. Containing the time this task consumes, references to its parent tasks
 * that this task depends on, references to its children tasks that depend on this task, and communication costs of
 * connected nodes.
 */
public class Node {
    private Map<Node, Integer> _parents;
    private Map<Node, Integer> _children;

    private String _id;

    private int _weight;
    private int _unsortedParents;

    private int _startTime;


    public Node(int weight, String id){
        _parents = new HashMap<>();
        _children = new HashMap<>();
        _weight = weight;
        _id = id;
    }

    /**
     * Return the weight of this Node.
     */
    public int getWeight(){return _weight;}
    
    /**
     * Return the id of this Node.
     */
    public String getId(){return _id;}

    
    /**
     * @return the starting time of the node in the processor
     */
    public int getStartTime(){return _startTime;}

    /**
     * @return the parents of this node
     */
    public Map<Node, Integer> getParents(){return _parents;}

    /**
     * @return the children of this node
     */
    public Map<Node, Integer> getChildren(){return _children;}

    /**
     * @param startTime sets the starting time of the node in the processor
     */
    public void setStartTime(int startTime){_startTime = startTime;}

    /**
     * Add a parent node to this Node with the communication weight between these two Nodes.
     */
    public void addParent(Node parent, int pathWeight){
        _parents.put(parent, pathWeight);
        _unsortedParents++;
    }

    /**
     * Add a child node to this Node with the communication weight between these two Nodes.
     */
    public void addChild(Node child, int pathWeight) {
        _children.put(child, pathWeight);
    }

    public void sortOneParent(){
        _unsortedParents--;
    }

    public boolean parentsSorted(){
        return _unsortedParents == 0;
    }


    /**
     * Return true if the input Node is a parent node of this Node. False otherwise.
     */
    public boolean isParent(Node node){ return _parents.keySet().contains(node); }

    /**
     * Return true if the input Node is a child node of this Node. False otherwise.
     */
    public boolean isChild(Node node){ return _children.keySet().contains(node); }

    /**
     * Return the communication weight to this child node.
     * Return -1 if the input is not a child node to this Node.
     */
    public int getPathWeightToChild(Node child){
        if (_children.containsKey(child)){
            return _children.get(child);
        }else{
            return -1;
        }
    }
}
