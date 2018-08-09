package model;

import java.util.List;

public class State {
    List<Node> _scheduled;
    int _max;

    public State(List<Node> scheduled){
        _scheduled = scheduled;
        calcMax();
    }

    public State(){
        _max = Integer.MAX_VALUE;
    }

    public void calcMax(){
        int max = 0;
        for (Node n: _scheduled){
            max = Math.max(max, n.getStartTime()+n.getWeight());
        }
        _max = max;
    }

    public int getMaxCost(){
        return _max;
    }

}
