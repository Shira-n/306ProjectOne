package controller;

import com.sun.management.OperatingSystemMXBean;
import javafx.application.Platform;
import javafx.scene.layout.Pane;


import java.lang.management.ManagementFactory;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This is a model class in terms of MVC pattern that monitors and stores all the info related to the operating system
 * environment.
 *
 * It is initialised by Controller, and it calls Controller #updateCPU periodically
 */
public class SystemInfoVisualisation extends Thread {
    private Pane _pane;
    private Timer _timer;
    private OperatingSystemMXBean _bean;
    private Controller _controller;

    // 4 data values of CPU is stored for the CPU chart
    private double cpu1 = 0;
    private double cpu2 = 0;
    private double cpu3 = 0;
    private double cpu4 = 0;

    public SystemInfoVisualisation(Controller controller) {
        super();
        _controller = controller;
        _timer = new Timer();
        _bean = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);
}

    /**
     * Updates the CPU periodically, calls Controller #updateCPU()
     */
    @Override
    public void run() {
        _timer.scheduleAtFixedRate(new TimerTask() {
                                      @Override
                                      public void run() {
                                          Platform.runLater(() ->
                                          {
                                              double newcpu = _bean.getProcessCpuLoad() * 100;
                                              cpu1 = cpu2;
                                              cpu2 = cpu3;
                                              cpu3 = cpu4;
                                              cpu4 = newcpu;
                                              _controller.updateCPU(cpu1,cpu2,cpu3,cpu4);
                                          });
                                      }
                                  },
                10, 100);
    }

    /**
     * called to stop monitoring CPU
     */
    public void stopTimer() {
        _timer.cancel();
    }


}
