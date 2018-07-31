package model;

//@TODO @Suying

import java.util.List;

/**
 * Represents a processor in the algorithm. Contains processor's current assigned tasks and path, and processor's
 * current weight. Could call Processor to add tasks to a processor, or get processor's assigned tasks.
 */
public class Processor {

    private int _pid;
    private int _currentTotalWeight;

    private List<Node> _currentPath;

    /**
     * Constructor of Processor. Default processor's path weight is set to 0.
     * @param pid the id of the processor instance to be created
     */
    public Processor(int pid){
        _pid = pid;
        _currentTotalWeight = 0;
    }

    /**
     * Add a new node/task to the Processor instance, also updates processor's total path weight.
     * @param node
     */
    public void addNode(Node node){
        _currentPath.add(node);
        _currentTotalWeight += node.getWeight();
    }

    /**
     * Returns the last node of processor's current path. 
     * @return
     */
    public Node getLastNode(){
        return _currentPath.get(_currentPath.size()-1);
    }
}