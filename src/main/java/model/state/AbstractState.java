package model.state;

import model.Processor;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractState {
    protected int _max;
    protected int _bottomWeight;
    protected Map<Integer, String> _stateStringRep = new HashMap<>();

    public AbstractState(){
        _max = Integer.MAX_VALUE;
        _bottomWeight = Integer.MAX_VALUE;
    }

    public int getMaxWeight(){ return _max; }

    public int getBottomWeight(){ return _bottomWeight; }

    /**
     * Translate a Processor's schedule from a String to a form that is easier for DotFileAdapter.
     * The schedule is translated to the following format { NodeID, [ProcessorID, StartTime] }
     * The schedule information of a Node can be found using the Node's ID, and the first String
     * in the String array is the ID of the Processor that the Node is scheduled on, the second
     * String in the String array is the start time of the Node.
     */
    public Map<String, String[]> translate(){
        Map<String, String[]> translation = new HashMap<>();
        String processor, node, start;
        for (Integer i : _stateStringRep.keySet()){
            processor = Integer.toString(i);
            if (_stateStringRep.get(i).trim().length() > 0) {
                for (String s : _stateStringRep.get(i).trim().split(Processor.EXTERNAL_SPLIT)) {
                    String[] pair = new String[2];
                    start = s.split(Processor.INTERNAL_SPLIT)[0];
                    node = s.split(Processor.INTERNAL_SPLIT)[1];
                    pair[0] = processor;
                    pair[1] = start;
                    translation.put(node, pair);
                }
            }
        }
        return translation;
    }

    /*
        Helper methods for testing & debugging
     */
    //Print the current schedule of this State
    public void print(){
        for (Integer i : _stateStringRep.keySet()){
            if (_stateStringRep.get(i).length() > 0) {
                System.out.println("At P" + i +": " + _stateStringRep.get(i));
            }else{
                System.out.println("At P" + i +": no schedule");
            }
        }
    }
}
