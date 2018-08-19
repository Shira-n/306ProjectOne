package model;

import java.util.*;

public class State {
    int _max;
    int _bottomWeight;
    Map<Integer, String> _stateStringRep = new HashMap<>();
    Map<String, Node> _nodeMap = new HashMap<>();
    Set<Node> _freeToSchedule = new HashSet<>();

    public State(List<Processor> schedule, Set<Node> freeToStart){
        _max = 0;
        _bottomWeight = 0;

        //Keep references of current free to schedule Nodes
        //System.out.print("Free Nodes at this state are: ");
        for (Node n : freeToStart){
            _freeToSchedule.add(n);
            //System.out.print(" " + n.getId());
        }
        //System.out.println(" ");
        for (Processor p : schedule){
            //Store the schedule on a processor by a String
            _stateStringRep.put(p.getID(), p.toString());
            _max = Math.max(_max, p.getCurrentAbleToStart());
            _bottomWeight = Math.max(_bottomWeight, p.getBottomWeight());
            //Keep references of Nodes scheduled in the current schedule
            for (Node n : p.getCurrentSchedule().values()){
                _nodeMap.put(n.getId(), n);
            }
        }
    }

    //Empty State constructor
    public State(){
        _max = Integer.MAX_VALUE;
        _bottomWeight = Integer.MAX_VALUE;
    }

    //Old constructor
    public State(List<Processor> schedule){
        _max = 0;
        _bottomWeight = 0;
        //Store the schedule on a processor by a String
        for (Processor p : schedule){
            //Keep references of Node scheduled in the current schedule
            for (Node n : p.getCurrentSchedule().values()){
                _nodeMap.put(n.getId(), n);
            }
            _stateStringRep.put(p.getID(), p.toString());
            _max = Math.max(_max, p.getCurrentAbleToStart());
            _bottomWeight = Math.max(_bottomWeight, p.getBottomWeight());
        }
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
    public Set<Node> rebuild(List<Node> graph, List<Processor> processors){
        Map<String, String[]> translation = translate();
        Map<String, Processor> processorMap = new HashMap<>();

        //System.out.println("Step 1: Try to clean current state");
        //Clear all the schedule in the input state.
        for (Processor p : processors){
            p.clear();
            processorMap.put(Integer.toString(p.getID()), p);
            //System.out.println("P" + p.getID() + " put in map: size = " + p.getCurrentSchedule().size());
        }
        for (Node n : graph) {
            n.reset();
            //System.out.println("Node" + n.getId() + ": start time= " + n.getStartTime());
        }

        Processor processor;
        int startTime;

        //Reschedule the input to this State
        for (Node n : _nodeMap.values()){
            //System.out.println("Rescheduling Node "+ n.getId());
            processor =  processorMap.get(translation.get(n.getId())[0]);
            //System.out.println("It was at P"+ translation.get(n.getId())[0]);
            startTime = Integer.parseInt(translation.get(n.getId())[1]);
            n.schedule(processor, startTime);
            processor.addNodeAt(n, startTime);
            //System.out.println("It was scheduled to start at "+ startTime);
            //System.out.println("Newer P start time "+ processor.getCurrentAbleToStart());
        }

        return  _freeToSchedule;
    }



    /*
        Helper methods for testing & debugging
     */

    //Used in debugging
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
