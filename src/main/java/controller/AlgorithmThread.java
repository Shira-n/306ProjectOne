package controller;

import model.State;
import model.scheduler.Scheduler;

public class AlgorithmThread extends Thread {
    private Scheduler _scheduler;
    private Controller _controller;
    private model.State _state;

    public AlgorithmThread(Scheduler scheduler, Controller controller) {
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

    public model.State getOptimalState() {
        return _state;
    }
}

