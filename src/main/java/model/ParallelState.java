package model;

import java.util.*;

public class ParallelState extends State {
    int _max;
    int _bottomWeight;
    Map<Integer, String> _stateStringRep = new HashMap<>();
    Set<String> _scheduledNodeId = new HashSet<>();
    Set<String> _freeToScheduleId = new HashSet<>();


    public ParallelState(List<Processor> schedule, Set<String> freeToStart){
        _max = 0;
        _bottomWeight = 0;
        if (freeToStart != null){
            for (String id : freeToStart){
                _freeToScheduleId.add(id);
            }
        }
        for (Processor p : schedule){
            //Store the schedule on a processor by a String
            _stateStringRep.put(p.getID(), p.toString());
            _max = Math.max(_max, p.getCurrentAbleToStart());
            _bottomWeight = Math.max(_bottomWeight, p.getBottomWeight());
            //Keep references of Nodes scheduled in the current schedule
            for (Node n : p.getCurrentSchedule().values()){
                _scheduledNodeId.add(n.getId());
            }
        }
    }

    //Empty State constructor
    public ParallelState(){
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


    /**
     * Rebuild the input state to the state represented by this State object. Return the set of Nodes that are
     * free to schedule in the current state.
     */
    public Set<String> rebuild(Map<String, Node> graph, List<Processor> processors){
            Map<String, String[]> translation = translate();
            Map<String, Processor> processorMap = new HashMap<>();

            //Clear all the schedule in the input state.
            for (Processor p : processors){
                p.clear();
                processorMap.put(Integer.toString(p.getID()), p);
            }
            for (Node n : graph.values()) {
                n.reset();
            }

            Processor processor;
            int startTime;

            //Reschedule the input to this State
            for (String nodeId : _scheduledNodeId){
                processor =  processorMap.get(translation.get(nodeId)[0]);
                startTime = Integer.parseInt(translation.get(nodeId)[1]);
                graph.get(nodeId).schedule(processor, startTime);
                processor.addNodeAt(graph.get(nodeId), startTime);
            }
            return  _freeToScheduleId;
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
