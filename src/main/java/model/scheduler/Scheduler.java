package model.scheduler;

import controller.Controller;
import model.State;

public interface Scheduler {
    public State getSchedule();

    public void setController(Controller controller);
}
