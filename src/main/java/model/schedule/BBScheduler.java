package model.schedule;

import model.Node;
import model.Processor;
import model.State;

import java.util.ArrayList;
import java.util.List;

public class BBScheduler extends Scheduler {
    List<State> _states;
    State _optimal;

    public BBScheduler(List<Node> graph, int numberOfProcessor) {
        super(graph, numberOfProcessor);
        _states = new ArrayList<>();
        _optimal = new State();
    }



    private void bbOptimalSchedule(){
        List<Node> unScheduled = _graph;
        List<Node> scheduled = new ArrayList<>();

        int startTime = 0;
        while (!unScheduled.isEmpty()){
            Node n = unScheduled.get(0);
            for (Processor p : _processors){
                startTime = Math.max(p.getCurrentAbleToStart(), infulencedByParents(p, n));
                n.setSchedule(p.getID(), startTime);
                //TODO Arraylist should use different method (should use queue or stack)
                unScheduled.remove(0);
                scheduled.add(n);
                State s = new State(scheduled);
                if (s.getMaxCost() < _optimal.getMaxCost()){
                    _optimal = s;
                }else{

                }
            }
        }

    }

}
