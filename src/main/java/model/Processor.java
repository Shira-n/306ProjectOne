package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a processor in the algorithm. Contains processor's current assigned tasks and path, and processor's
 * current weight. Could call Processor to add tasks to a processor, or get processor's assigned tasks.
 */
public class Processor {

    private int _pid;
    private int _currentAbleToStart;
    private boolean _isEmpty;

    private Map<Integer, Node> _currentSchedule;


    /**
     * Constructor of Processor. Default processor's path weight is set to 0.
     * @param pid the id of the processor instance to be created
     */
    public Processor(int pid){
        _pid = pid + 1;
        _currentAbleToStart = 0;
        _currentSchedule = new HashMap<>();
        _isEmpty = true;
    }

    /**
     * Add a new node/task to the Processor instance, also updates processor's total path weight.
     */
    public void addNode(int start, Node node){
        _currentSchedule.put(start, node);
        _currentAbleToStart = start + node.getWeight();
        _isEmpty = false;
        //System.out.println("P"+ this.getID() +" added at time " + start);

        //Add the start time and the scheduled processor to the node
        node.setStartTime(start);
        node.setProcessor(this);
    }

    public void removeNode(int start){
        //System.out.println("P"+ this.getID() +" removed at time " + start);
        _currentSchedule.remove(start);
        if (_currentSchedule.values().size() < 1){
            _isEmpty = true;
        }
    }


    public boolean isEmpty(){
        return _isEmpty;
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





    /*
    public boolean equals(Processor p, Node n){
        for (Node parent : n.getParents().keySet()){
            if ()
        }
    }
    */

    @Override
    public String toString(){
        String schedule = " ";
        for (int startTime : _currentSchedule.keySet()){
            schedule = schedule + startTime + "," + _currentSchedule.get(startTime).getId() + ";";
        }
        return schedule;
    }
}
