package model;

import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a processor in the algorithm. Contains processor's current assigned tasks and path, and processor's
 * current weight. Could call Processor to add tasks to a processor, or get processor's assigned tasks.
 */
public class Processor {
    public final static String EXTERNAL_SPLIT = ";";
    public final static String INTERNAL_SPLIT = ",";

    private int _pid;
    private int _currentAbleToStart;
    private int _bottomWeight;

    private TreeMap<Integer, Node> _currentSchedule;

    /**
     * Constructor of Processor. Default processor's path weight is set to 0.
     * @param pid the id of the processor instance to be created
     */
    public Processor(int pid){
        _pid = pid + 1;
        _currentAbleToStart = 0;
        _currentSchedule = new TreeMap<>();
        _bottomWeight = 0;
    }

    /**
     * Add a new node/task to the Processor instance, also updates processor's total path weight.
     */
    public void addNodeAt(Node node, int start){
        _currentSchedule.put(start, node);
        _currentAbleToStart = Math.max(_currentAbleToStart, start + node.getWeight());
        _bottomWeight = Math.max(_bottomWeight, node.getBottomWeight());
    }

    public void removeNodeAt(int start){
        _currentSchedule.remove(start);
        if(_currentSchedule.size() > 0) {
            //Remove the Node.
            Node currentLast = _currentSchedule.lastEntry().getValue();
            //Recalculate the current able-to-start time.
            _currentAbleToStart = currentLast.getStartTime() + currentLast.getWeight();
        }else {
            //To avoid NullPointerException. 2 hours debugging just because of this _(:з」∠)_.
            _currentAbleToStart = 0;
        }
    }

    public Map<Integer, Node> getCurrentSchedule() {
        return _currentSchedule;
    }


    public int getID() {
        return _pid;
    }

    public int getCurrentAbleToStart(){
        return _currentAbleToStart;
    }

    public int getBottomWeight() {
        return _bottomWeight;
    }

    /*
    public int getIdleCostFunction(){
        int idle, totalIdle
        for (int i : _currentSchedule.keySet()){
            idle = _currentSchedule.get(i).getStartTime() +
            if (_currentSchedule.get(i))
        }
    }
    */


    /*
    public boolean equals(Processor p, Node n){
        for (Node parent : n.getParents().keySet()){
            if ()
        }
    }
    */


    public void clear(){
        _currentAbleToStart = 0;
        _currentSchedule = new TreeMap<>();
        _bottomWeight = 0;
    }

    /**
     * Override Object.toString(). Use a String to represent the current schedule on this Processor.
     */
    @Override
    public String toString(){
        String schedule = " ";
        for (int startTime : _currentSchedule.keySet()){
            schedule = schedule + startTime + INTERNAL_SPLIT + _currentSchedule.get(startTime).getId() + EXTERNAL_SPLIT;
        }
        return schedule;
    }
}
