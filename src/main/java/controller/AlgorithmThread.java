package controller;

import model.scheduler.AbstractScheduler;
import model.state.AbstractState;


/**
 * This class supports multithreading that runs the algorothm on another thread when visualisation is present. The thread
 * is not a part of the parallelisation but just a thread used between GUI and the algorithm.
 *
 * It is initiated and handled by the Controller class. (see Controller #handlePressStart)
 *
 */
public class AlgorithmThread extends Thread {
    private AbstractScheduler _scheduler;
    private Controller _controller;
    private AbstractState _state;

    public AlgorithmThread(AbstractScheduler scheduler, Controller controller) {
        super();
        _scheduler = scheduler;
        _controller = controller;
    }

    /**
     * Override run method to start the algorithm
     */
    @Override
    public void run() {
        _scheduler.setController(_controller);
        _state =  _scheduler.getSchedule();
    }

    /**
     * Getter method to get the optimal state calculated from the algorithm. T
     *
     * It is called from Controller #setCompleted to obtain result from the algorithm thread
     * @return optimal state
     */
    public AbstractState getOptimalState() {
        return _state;
    }
}

