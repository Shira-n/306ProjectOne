package model;

//@TODO @Suying

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a processor in the algorithm. Contains processor's current assigned tasks and path, and processor's
 * current weight. Could call Processor to add tasks to a processor, or get processor's assigned tasks.
 */
public class Processor {

    private int _pid;
    private int _currentAbleToStart;

    private Map<Integer, Node> _currentSchedule;

    /**
     * Constructor of Processor. Default processor's path weight is set to 0.
     * @param pid the id of the processor instance to be created
     */
    public Processor(int pid){
        _pid = pid;
        _currentAbleToStart = 0;
        _currentSchedule = new HashMap<>();
    }

    /**
     * Add a new node/task to the Processor instance, also updates processor's total path weight.
     * @param node
     */
    public void addNode(int start, Node node){
        _currentSchedule.put(start, node);
        _currentAbleToStart = start + node.getWeight();

        //adds the start time of the processing to the node
        node.setStartTime(start);
    }

    public Map<Integer, Node> getCurrentSchedule() {
        return _currentSchedule;
    }

    public int getCurrentAbleToStart(){
        return _currentAbleToStart;
    }
    
    public int getID() {
        return _pid;
    }
}
