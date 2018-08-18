package controller;


import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import model.BranchAndBoundScheduler;
import model.State;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;


import javax.swing.*;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


/**
 * Controller class for the MainWindow. Initialise all components on the pane.
 */
public class Controller{


    private SingleGraph _graph;

    private Timeline _timeline;

    private int _timeSeconds;

    private ColourManager _colourMgr;

    private GraphViewer _viewer;

    private List<model.Node> _nodes;

    private GUITimer _timer;

    private State _optimalSchedule;
    @FXML
    private Pane _graphPane;

    @FXML
    private Pane _buttonPane;

    @FXML
    private Pane _ganttPane;

    @FXML
    private Pane _dataPane;


    @FXML
    private Label _time;

    @FXML
    private Label _status;

    @FXML
    private Label _currentBestTime;

    @FXML
    private Label _numNode;

    @FXML
    private Label _numProcessor;

    @FXML
    private Label _isParallel;

    @FXML
    private SwingNode _swingNode;

    @FXML
    public void initialize() {
        _nodes = GUIEntry.getNodes();
        _timer = GUIEntry.getTimer();
        _timer.setController(this);

        GUIEntry.setController(this);

        initColour();

        initLabels();

        initGraph();

        initDataPane();
    }


    private void initDataPane() {
        GaugeBuilder builder = GaugeBuilder.create().skinType(Gauge.SkinType.TILE_SPARK_LINE);
        Gauge gauge = GaugeBuilder.create()
                .skinType(Gauge.SkinType.TILE_SPARK_LINE)
                .animated(true)
                .build();
        _dataPane.getChildren().add(gauge);


        Sigar sigar = new Sigar();
        double cpu = 0;
        try {
            cpu = sigar.getCpuPerc().getCombined();
        }
        catch (SigarException e) {
            e.printStackTrace();
        }
        gauge.setValue(cpu);
    }

    /**
    *
    */
    @FXML
    public void handlePressStart(ActionEvent event) {
        _status.setText("Computing");
        _timer.startTimer();
        _ganttPane.setVisible(false);
        Controller controller = this;
        Thread thread = new Thread() {
            public void run() {
                System.out.print("IN THREAD");
                BranchAndBoundScheduler scheduler = new BranchAndBoundScheduler(GUIEntry.getNodes(), GUIEntry.getNumProcessor(), controller,_timer);
                model.State optimalSchedule = scheduler.getOptimalSchedule();

                //drawGanttChart(optimalSchedule);

                _optimalSchedule = optimalSchedule;

            }
        };
        thread.start();
    }

    /**
     * Create a colour manager instance which generates colour for each of the processors
     */
    private void initColour() {
        _colourMgr = new ColourManager(GUIEntry.getNumProcessor());
    }

    /**
     * Called by algorithm to update GUI to show newly calculated current optimal schedule.
     * @param updatedState
     */
    public synchronized void update(Map<String,String[]> updatedState) {
        System.out.println("update called");
        //this is running on JavaFx Thread now
        Platform.runLater(() -> {
            System.out.println("UPDATE");
            for (String nodeID : updatedState.keySet()) {
                String[] nodeInfo = updatedState.get(nodeID);
                Node node = _graph.getNode(nodeID);
                System.out.println(node.getAttribute("startTime") + "");
                if (node.hasAttribute("startTime")) {
                    node.removeAttribute("startTime");
                }

                System.out.println(node.getId() + " Processor: " + node.getAttribute("processor"));
                if (node.hasAttribute("processor")) {
                    node.removeAttribute("processor");
                }
                node.addAttribute("startTime", nodeInfo[1]);
                node.addAttribute("processor", nodeInfo[0]);
                if (node.getAttribute("processor") == null) {
                    System.out.println("hi");
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        _viewer.updateNodeColour(node);
                    }
                });
            }
        });
    }

    /**
     * Initialise the node graph to display the initial state of the graph.
     */
    private void initGraph() {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        _graph = GUIEntry.getGraph();

        //Viewer viewer = new Viewer(_graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        _viewer = new GraphViewer(_graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD,_colourMgr);

        _graph.addAttribute("ui.stylesheet", "graph {\n" +
                "fill-mode: gradient-vertical;\n" +
                "fill-color:  #405d60, #202033;\n" +
                "padding: 20px;\n" +
                "}");
        //_graph.addAttribute("ui.stylesheet", "node { fill-color: rgba(255,0,0,255); }");

        _viewer.enableAutoLayout();
        ViewPanel viewPanel = _viewer.addDefaultView(false);
        viewPanel.setBackground(Color.blue);
        viewPanel.setMinimumSize(new Dimension(700, 500));
        viewPanel.setOpaque(false);
        viewPanel.setBackground(Color.black);
        SwingUtilities.invokeLater(() -> {
            _swingNode.setContent(viewPanel);
        });
        _swingNode.setLayoutX(25);
        _swingNode.setLayoutY(55);


    }

    private void initLabels() {
        //only show Gatt chart after computation is finished
        _ganttPane.setVisible(false);

        //set labels to values corresponding to the current computation graph
        _status.setText("Not Started");
        _currentBestTime.setText("NA");
        _numNode.setText(GUIEntry.getNumNode() + "");
        _numProcessor.setText(GUIEntry.getNumProcessor() + "");
        _isParallel.setText(GUIEntry.getParallelised() + "");

    }


    private void drawGanttChart() {
        Platform.runLater(() -> {
            System.out.println("drawGanttChart running on: "+Thread.currentThread().getName());
            GanttChart chart = new GanttChart(_optimalSchedule, Integer.parseInt(_numProcessor.getText()), _nodes, _colourMgr);
            _ganttPane.getChildren().add(chart.createGraph());
            _ganttPane.setBackground(Background.EMPTY);
            _ganttPane.setVisible(true);
        });

    }

    public synchronized  void setTimer(int count) {

        Platform.runLater(() -> {
            String minZeroPlaceholder ="";
            String secZeroPlaceholder = "";
            String msZeroPlaceholder = "";
            long min = count / 60000;
            long sec = (count - min * 60000) / 1000;
            long ms = count - min * 60000 - sec * 1000;
            if (min<10){
                minZeroPlaceholder = "0";
            }
            if (sec<10){
                secZeroPlaceholder = "0";
            }
            if (ms<10){
                msZeroPlaceholder = "0";
            }
            _time.setText(minZeroPlaceholder + min + ":" + secZeroPlaceholder+sec + ":" + msZeroPlaceholder+ms);
        });

    }

    public synchronized void completed(int maxWeight) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _status.setText("Completed");
                //drawGanttChart();
            }
        });
    }

    @FXML
    public void handlePressQuit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

}

