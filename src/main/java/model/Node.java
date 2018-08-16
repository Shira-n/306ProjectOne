package model;

import java.util.*;

/**
 * Represent a node/task in the input graph. Containing the time this task consumes, references to its parent tasks
 * that this task depends on, references to its children tasks that depend on this task, and communication costs of
 * connected nodes.
 */
public class Node {
    private Map<Node, Integer> _parents;
    private Map<Node, Integer> _children;
    private List<Node> _equivalentNodes;

    private String _id;
    private int _weight;
    private int _bottomWeight;

    private int _unsortedParents;

    private Processor _processor;
    private int _startTime;

    public Node(int weight, String id){
        _id = id;
        _weight = weight;
        _parents = new HashMap<>();
        _children = new HashMap<>();
        _equivalentNodes = new ArrayList<>();
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

    public void setBottomWeight(int bottomLevel){
        _bottomWeight = bottomLevel;
    }

    public int getBottomWeight(){
        return _bottomWeight;
    }
///////////////////////////////////////////////////////////////////need to be deleted
//NOTE: Can be used in testing. No longer used in scheduling
    public void setProcessor(Processor p){ _processor = p; }

    public void setStartTime(int startTime){_startTime = startTime;}
///////////////////////////////////////////////////////////////////need to be deleted

    public Set<Node> schedule(Processor processor, int startTime){
        _processor = processor;
        _startTime = startTime;
        Set<Node> freeChildren = new HashSet<>();
        for (Node child : _children.keySet()){
            child.sortOneParent();
            if (child.parentsSorted()){
                freeChildren.add(child);
            }
        }
        return freeChildren;
    }

    /**
     * Return a set of Nodes that will become free if this Node is scheduled
     */
    public Set<Node> ifSchedule(){
        Set<Node> freeChildren = new HashSet<>();
        for (Node child : _children.keySet()){
            child.sortOneParent();
            if (child.parentsSorted()){
                freeChildren.add(child);
            }
            child.unsortOneParent();
        }
        return freeChildren;
    }


    public void unSchedule(){
        _processor = null;
        _startTime = Integer.MAX_VALUE;
        //Set<Node> notFreeChildren = new HashSet<>();
        for (Node child : _children.keySet()){
            child.unsortOneParent();
            //if (!child.parentsSorted()){
            //    notFreeChildren.add(child);
            //}
        }
        //return notFreeChildren;
    }



    public int getStartTime(){return _startTime;}

    public Processor getProcessor(){ return _processor; }

    public int getProcessorId(){
        return  _processor.getID();
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


    public List<Node> getEquivalentNodes(){
        return _equivalentNodes;
    }

    public boolean isEquivalent(Node node){
        return _equivalentNodes.contains(node);
    }

    public void addEquivalentNode(Node node){
        _equivalentNodes.add(node);
        node.addEquivalentNode(this);
    }


    /*
        Helper methods
     */
    public void sortOneParent(){
        _unsortedParents--;
    }

    public void unsortOneParent(){
        _unsortedParents++;
    }

    public boolean parentsSorted(){
        return _unsortedParents == 0;
    }

    public void reset(){
        _processor = null;
        _startTime = Integer.MAX_VALUE;
        _unsortedParents = _parents.keySet().size();
    }

}
