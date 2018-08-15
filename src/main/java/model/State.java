package model;

import java.util.*;

public class State {
    int _max;
    Map<Integer, String> _state = new HashMap<>();

    public State(List<Processor> schedule){
        _max = 0;
        //Store the schedule on a processor by a String
        for (Processor p : schedule){
            _state.put(p.getID(), p.toString());
            _max = Math.max(_max, p.getCurrentAbleToStart());
        }
    }

    public State(){
        _max = Integer.MAX_VALUE;
    }

    public int getMaxWeight(){
        return _max;
    }

    /* Used in debugging
    public void print(){
        for (Integer i : _state.keySet()){
            if (s.length() > 0) {
                System.out.println("At this P: " + s);
            }else{
                System.out.println("At this P: no schedule");
            }
        }
    }
    */

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
        for (Integer i : _state.keySet()){
            processor = Integer.toString(i);
            if (_state.get(i).trim().length() > 0) {
                for (String s : _state.get(i).trim().split(Processor.EXTERNAL_SPLIT)) {
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


    /**
     * return in the format {processorID, [NodeId, StartTime]}
     * @return
     */
    public Map<String, String[]> translateByProcessor(){
        Map<String, String[]> translation = new HashMap<>();
        String processor, node, start;
        for (Integer i : _state.keySet()){
            processor = Integer.toString(i);
            if (_state.get(i).trim().length() > 0) {
                for (String s : _state.get(i).trim().split(Processor.EXTERNAL_SPLIT)) {
                    String[] pair = new String[2];
                    start = s.split(Processor.INTERNAL_SPLIT)[0];
                    node = s.split(Processor.INTERNAL_SPLIT)[1];
                    pair[0] = node;
                    pair[1] = start;
                    translation.put(processor, pair);
                }
            }
        }
        return translation;
    }
}
