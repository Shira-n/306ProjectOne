package controller;



import java.util.Timer;
import java.util.TimerTask;

public class GUITimer extends Thread{
    private int _counter;
    private Controller _controller;
    private Timer _timer;

    public GUITimer () {

    }

    public void setController(Controller controller) {
        _controller = controller;
    }

    public void stopTimer() {
        _timer.cancel();
    }

    public void startTimer(){
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                //System.out.println("TimerTask executing counter is: " + _counter);
                _counter++;//increments the counter
                if (_controller != null) {
                    _controller.setTimer(_counter);
                }
            }
        };

        _timer = new Timer("MyTimer");//create a new Timer

        _timer.scheduleAtFixedRate(timerTask, 0, 100);
    }
}
