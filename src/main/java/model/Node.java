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

    private Processor _processor;
    private int _processorID;
    private int _startTime;

    public Node(int weight, String id){
        _id = id;
        _weight = weight;
        _parents = new HashMap<>();
        _children = new HashMap<>();
    }

    /**
     * Return the weight of this Node.
     */
    public int getWeight(){return _weight;}
    
    /**
     * Return the id of this Node.
     */
    public String getId(){return _id;}


    /*
        Getter & Setter of Schedule related fields
     */

    public Processor getProcessor(){ return _processor; }

    public void setProcessor(Processor p){ _processor = p; }

    /**
     * Return the start time of the Node scheduled in the processor
     */
    public int getStartTime(){return _startTime;}

    /**
     * Set the start time of the Node scheduled in the processor
     */
    public void setStartTime(int startTime){_startTime = startTime;}


    public void setSchedule(int p, int t){
        _processorID = p;
        _startTime = t;
    }

    public void unSchedule(){
        _processorID = -1;
        _startTime = -1;
    }



    /*
        Parents/Children
     */

    /**
     * Add a parent Node to this Node with the communication weight between these two Nodes.
     */
    public void addParent(Node parent, int pathWeight){
        _parents.put(parent, pathWeight);
        _unsortedParents++;
    }

    /**
     * Add a child Node to this Node with the communication weight between these two Nodes.
     */
    public void addChild(Node child, int pathWeight) {
        _children.put(child, pathWeight);
    }

    /**
     * Return the parents of this Node together with the communication costs.
     */
    public Map<Node, Integer> getParents(){return _parents;}

    /**
     * Return the children of this Node together with the communication costs.
     */
    public Map<Node, Integer> getChildren(){return _children;}


    /**
     * Return true if the input Node is a parent node of this Node. False otherwise.
     */
    public boolean isParent(Node node){ return _parents.keySet().contains(node); }

    /**
     * Return true if the input Node is a child node of this Node. False otherwise.
     */
    public boolean isChild(Node node){ return _children.keySet().contains(node); }

    /**
     * Return the communication weight to this child Node.
     * Return -1 if the input is not a child Node to this Node.
     */
    public int getPathWeightToChild(Node child){
        if (_children.containsKey(child)){
            return _children.get(child);
        }else{
            return -1;
        }
    }

    /**
     * Return the communication weight to this parent Node.
     * Return -1 if the input is not a parent Node to this Node.
     */
    public int getPathWeightToParent(Node parent){
        if (_parents.containsKey(parent)){
            return _parents.get(parent);
        }else{
            return -1;
        }
    }

    /*
        Helper methods for topological sorting
     */
    public void sortOneParent(){
        _unsortedParents--;
    }

    public boolean parentsSorted(){
        return _unsortedParents == 0;
    }

}
