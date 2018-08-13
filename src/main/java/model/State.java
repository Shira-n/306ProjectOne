package model;

import java.util.*;

public class State {
    int _max;
    Map<Integer, String> _state = new HashMap<>();

    public State(List<Processor> schedule){
        _max = 0;
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

    /*
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
     * Format { NodeID, [ProcessorID, StartTime] }
     */
    public Map<String, String[]> translate(){
        Map<String, String[]> translation = new HashMap<>();
        String processor, node, start;
        for (Integer i : _state.keySet()){
            processor = Integer.toString(i);
            for (String s : _state.get(i).trim().split(Processor.EXTERNAL_SPLIT)){
                String[] pair = new String[2];
                start = s.split(Processor.INTERNAL_SPLIT)[0];
                node = s.split(Processor.INTERNAL_SPLIT)[1];
                pair[0] = processor;
                pair[1] = start;
                translation.put(node, pair);
            }
        }
        return translation;
    }
}
