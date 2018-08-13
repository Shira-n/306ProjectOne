package model;

import java.util.ArrayList;
import java.util.List;

public class State {
    List<Node> _scheduled;
    int _max;

    List<String> _state = new ArrayList<>();

    public State(List<Processor> schedule){
        _max = 0;
        for (Processor p : schedule){
            _state.add(p.toString());
            _max = Math.max(_max, p.getCurrentAbleToStart());
            System.out.println("currentmax " + _max);
        }
    }

    public State(){
        _max = Integer.MAX_VALUE;
    }

    public int getMaxWeight(){
        return _max;
    }

    public void print(){
        for (String s : _state){
            System.out.println("At this P: " + s);
        }
    }
}
