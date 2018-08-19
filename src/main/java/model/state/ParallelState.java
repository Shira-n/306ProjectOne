package model.state;

import model.Node;
import model.Processor;

import java.util.*;

public class ParallelState extends AbstractState {
    Set<String> _scheduledNodeId = new HashSet<>();
    Set<String> _freeToScheduleId = new HashSet<>();

    public ParallelState(){ super(); }

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
}
