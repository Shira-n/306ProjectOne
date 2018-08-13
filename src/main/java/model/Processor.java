package model;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a processor in the algorithm. Contains processor's current assigned tasks and path, and processor's
 * current weight. Could call Processor to add tasks to a processor, or get processor's assigned tasks.
 */
public class Processor {

    private int _pid;
    private int _currentAbleToStart;

    private TreeMap<Integer, Node> _currentSchedule;


    /**
     * Constructor of Processor. Default processor's path weight is set to 0.
     * @param pid the id of the processor instance to be created
     */
    public Processor(int pid){
        _pid = pid + 1;
        _currentAbleToStart = 0;
        _currentSchedule = new TreeMap<>();
    }

    /**
     * Add a new node/task to the Processor instance, also updates processor's total path weight.
     */
    public void addNodeAt(Node node, int start){
        _currentSchedule.put(start, node);
        _currentAbleToStart = start + node.getWeight();
        System.out.println("P"+ this.getID() +": added a node at time " + start);
    }

    public void removeNodeAt(int start){
        _currentSchedule.remove(start);
        System.out.println("P"+ this.getID() +" removed Node" + _currentSchedule.get(start) + " at time " + start);

        if(_currentSchedule.size() > 0) {

            System.out.println("");
            System.out.println(_currentSchedule.lastEntry().getValue().getId());
            System.out.println("");
            Node currentLast = _currentSchedule.lastEntry().getValue();
            _currentAbleToStart = currentLast.getStartTime() + currentLast.getWeight();
            System.out.println("recalc start time of the processor to" + _currentAbleToStart);
        }else {
            _currentAbleToStart = 0;
        }
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
