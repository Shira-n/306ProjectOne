package controller;



import java.util.Timer;
import java.util.TimerTask;


/**
 * A Timer class that is used by the GUI.
 *
 * It updates the controller class timer label periodically.
 *
 * Instance of this class is created by GUIEntry.
 *
 */
public class GUITimer extends Thread{
    private int _counter;
    private Controller _controller;
    private Timer _timer;

    public void setController(Controller controller) {
        _controller = controller;
    }

    /**
     * Stops timer. called by controller #completed()
     */
    public void stopTimer() {
        _timer.cancel();
    }

    /**
     * Starts timer. clled by controller #handlePressStart()
     */
    public void startTimer(){
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                _counter++;//increments the counter
                if (_controller != null) {
                    _controller.setTimer(_counter);
                }
            }
        };

        _timer = new Timer("MyTimer");//create a new Timer

        _timer.scheduleAtFixedRate(timerTask, 0, 10);
    }
}
