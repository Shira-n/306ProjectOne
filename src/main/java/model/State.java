package model;

import java.util.ArrayList;
import java.util.List;

public class State {
    List<Node> _scheduled;
    int _max;

    List<String> _state = new ArrayList<>();

    public State(List<Processor> schedule){
        _max = 0;
        System.out.println("\nState: Create new State");
        System.out.println("Current schedule:");
        for (Processor p : schedule){
            _state.add(p.toString());
            _max = Math.max(_max, p.getCurrentAbleToStart());
            System.out.println( "P" + p.getID() +" has a weight of " + p.getCurrentAbleToStart());
        }
        System.out.println("so the maxWeight is " + _max);
    }

    public State(){
        _max = Integer.MAX_VALUE;
    }

    public int getMaxWeight(){
        return _max;
    }

    public void print(){
        for (String s : _state){
            if (s.length() > 0) {
                System.out.println("At this P: " + s);
            }else{
                System.out.println("At this P: no schedule");
            }
        }
    }
}
