package controller;

import model.scheduler.AbstractScheduler;
import model.state.AbstractState;

public class AlgorithmThread extends Thread {
    private AbstractScheduler _scheduler;
    private Controller _controller;
    private AbstractState _state;

    public AlgorithmThread(AbstractScheduler scheduler, Controller controller) {
        super();
        _scheduler = scheduler;
        _controller = controller;
    }

    @Override
    public void run() {
        System.out.print("IN THREAD");
        _scheduler.setController(_controller);
        _state =  _scheduler.getSchedule();
    }

    public AbstractState getOptimalState() {
        return _state;
    }
}

