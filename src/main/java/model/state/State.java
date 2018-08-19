package model.state;

import model.Node;
import model.Processor;
import model.state.AbstractState;

import java.util.*;

public class State extends AbstractState {
    private Map<String, Node> _nodeMap = new HashMap<>();
    private Set<Node> _freeToSchedule = new HashSet<>();

    public State(){ super(); }

    public State(List<Processor> schedule, Set<Node> freeToStart){
        _max = 0;
        _bottomWeight = 0;

        //Keep references of current free to schedule Nodes
        for (Node n : freeToStart){
            _freeToSchedule.add(n);
        }
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

    //Old constructor used in basic milestone
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

    /**
     * Rebuild the input state to the state represented by this State object. Return the set of Nodes that are
     * free to schedule in the current state.
     */
    public Set<Node> rebuild(List<Node> graph, List<Processor> processors){
        Map<String, String[]> translation = translate();
        Map<String, Processor> processorMap = new HashMap<>();

        //Clear all the schedule in the input state.
        for (Processor p : processors){
            p.clear();
            processorMap.put(Integer.toString(p.getID()), p);
        }
        for (Node n : graph) {
            n.reset();
        }

        //Reschedule the input to this State
        Processor processor;
        int startTime;
        for (Node n : _nodeMap.values()){
            processor =  processorMap.get(translation.get(n.getId())[0]);
            startTime = Integer.parseInt(translation.get(n.getId())[1]);
            n.schedule(processor, startTime);
            processor.addNodeAt(n, startTime);
        }

        return  _freeToSchedule;
    }
}
