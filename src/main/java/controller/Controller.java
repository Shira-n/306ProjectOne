package controller;


import application.Main;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import model.State;
import model.scheduler.Scheduler;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;


import javax.management.ObjectName;
import javax.script.ScriptException;
import javax.swing.*;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


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

    private Gauge _gauge;

    private Tile _tile;

    private FutureTask _futureTask;

    private ChartData _data1 = new ChartData("D1",0,Tile.GRAY);
    private ChartData _data2 = new ChartData("D2",20,Tile.LIGHT_GREEN);
    private ChartData _data3 = new ChartData("D3",0,Tile.LIGHT_GREEN);
    private ChartData _data4 = new ChartData("D4",40,Tile.LIGHT_GREEN);

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

    public void updateCPU(double cpu1, double cpu2, double cpu3, double cpu4) {
        Platform.runLater(() -> {
            _data1.setValue(cpu1);
            _data2.setValue(cpu2);
            _data3.setValue(cpu3);
            _data4.setValue(cpu4);
        });
    }


    private void initDataPane() {
        SystemInfoVisualisation info = new SystemInfoVisualisation(this);


        _tile = TileBuilder.create()
                .skinType(Tile.SkinType.SMOOTH_AREA_CHART)
                .maxHeight(180)
                .minWidth(300)
                .unit("%")
                .chartData(_data1,_data2,_data3,_data4)
                .animated(true)
                .value(20)
                .title("CPU Usage")
                .barColor(javafx.scene.paint.Color.rgb(255,255,255))
                .backgroundColor(javafx.scene.paint.Color.rgb(0,0,0,0))
                .build();

        _dataPane.getChildren().add(_tile);

        _tile.setPadding(new Insets(20,0,0,30));

        info.run();
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

        Scheduler scheduler = GUIEntry.getScheduler();
        Callable task = new Callable() {
            @Override
            public State call() throws ScriptException {
                System.out.print("IN THREAD");
                scheduler.setController(controller);
                return scheduler.getSchedule();
            }
        };
        _futureTask = new FutureTask<State>(task);
        Thread algorithmThread = new Thread(_futureTask);
        algorithmThread.start();
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
    public synchronized void update(Map<String,String[]> updatedState,int weight) {
        //this is running on JavaFx Thread now
        Platform.runLater(() -> {
            int finishTime = 0;

            for (model.Node node : _nodes) {

                int startTime = Integer.parseInt(updatedState.get(node.getId())[1]);

                if (finishTime < startTime + node.getWeight()) {
                    finishTime = startTime + node.getWeight();
                }

            }
            _currentBestTime.setText(finishTime+" sec");

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


    public synchronized void updateFrequency(String nodeID) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Node node = _graph.getNode(nodeID);
                int previousCount = node.getAttribute("numAllocation");
                node.removeAttribute("numAllocation");
                node.addAttribute("numAllocation",(previousCount+1));
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        _viewer.updateNodeFrequency(node);
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
        /*
        _graph.addAttribute("ui.stylesheet","graph {\n" +
                "fill-color: rgba(0,0,0,0);\n}");
                */
        //_graph.addAttribute("ui.stylesheet", "node { fill-color: rgba(255,0,0,255); }");

        _viewer.enableAutoLayout();
        ViewPanel viewPanel = _viewer.addDefaultView(false);
        //viewPanel.setBackground(Color.blue);
        viewPanel.setMinimumSize(new Dimension(900, 500));
        viewPanel.setOpaque(false);
        //viewPanel.setBackground(Color.black);
        SwingUtilities.invokeLater(() -> {
            _swingNode.setContent(viewPanel);
        });
        _swingNode.setLayoutX(25);
        _swingNode.setLayoutY(35);


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
        });

    }

    public synchronized  void setTimer(int count) {

        Platform.runLater(() -> {
            String minZeroPlaceholder ="";
            String secZeroPlaceholder = "";
            String msZeroPlaceholder = "";
            long min = count / 60000;
            long sec = (count - min * 60000) / 10;
            long ms = count - min * 60000 - sec * 10;
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

    public synchronized void completed(int weight) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _status.setText("Completed");

                drawGanttChart();

            }
        });
        try {
            State state = (State)_futureTask.get();
            Main.writeResult(state);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }catch (ExecutionException ex) {
            ex.printStackTrace();
        }

    }

    @FXML
    public void handlePressDisplayGanttChart(ActionEvent event){
        _ganttPane.setVisible(true);
    }

    @FXML
    public void handlePressQuit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }


}

