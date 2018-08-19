package controller;


import application.Main;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import model.scheduler.AbstractScheduler;
import model.state.AbstractState;
import org.apache.commons.math3.analysis.function.Abs;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;


/**
 * Controller class for the Main. Initialise and monitors all components in the Window.
 */
public class Controller{

    private SingleGraph _graph;

    private ColourManager _colourMgr;

    private GraphViewer _viewer;

    private List<model.Node> _nodes;

    private GUITimer _timer;

    private Tile _tile;

    private FutureTask _futureTask;

    private AbstractState _optimalState;

    private Future<AbstractState> _result;

    private AlgorithmThread _algoThread;

    private ChartData _data1 = new ChartData("D1",0,Tile.LIGHT_GREEN);
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
    private Button _start;

    @FXML
    private Button _gantt;

    /**
     * Method to initialise all components in JavaFX window
     */
    @FXML
    public void initialize() {
        //get raw input nodes and timer instance from GUIEntry
        _nodes = GUIEntry.getNodes();
        _timer = GUIEntry.getTimer();
        //connect timer to this controller
        _timer.setController(this);

        GUIEntry.setController(this);

        initColour();

        initLabels();

        initGraph();

        initDataPane();
    }


    /**
     * Create a colour manager instance which generates colour for each of the processors
     */
    private void initColour() {
        _colourMgr = new ColourManager(GUIEntry.getNumProcessor());
    }



    /**
     * Initialise the Info labels in the GUI
     */
    private void initLabels() {
        //only show Gatt chart after computation is finished
        _ganttPane.setVisible(false);
        _gantt.setDisable(true);
        _gantt.setTextFill(javafx.scene.paint.Paint.valueOf("#423222"));

        //set labels to values corresponding to the current computation graph
        _status.setText("Not Started");
        _currentBestTime.setText("NA");
        _numNode.setText(GUIEntry.getNumNode() + "");
        _numProcessor.setText(GUIEntry.getNumProcessor() + "");
        _isParallel.setText(GUIEntry.getParallelised() + "");
    }


    /**
     * Initialise the node graph to display the initial state of the graph.
     */
    private void initGraph() {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        //get the Single Graph instance of the input graph
        _graph = GUIEntry.getGraph();

        _viewer = new GraphViewer(_graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD,_colourMgr);

        //set graph background colour
        _graph.addAttribute("ui.stylesheet", "graph {\n" +
                "fill-mode: gradient-vertical;\n" +
                "fill-color:  #405d60, #202033;\n" +
                "padding: 20px;\n" +
                "}");
        _viewer.enableAutoLayout();

        //set graph to show in existing Javafx window
        ViewPanel viewPanel = _viewer.addDefaultView(false);
        viewPanel.setBackground(Color.blue);
        viewPanel.setMinimumSize(new Dimension(900, 500));
        viewPanel.setOpaque(false);
        viewPanel.setBackground(Color.black);

        //in order to display graph, it is wrapped in a SwingNode object
        SwingUtilities.invokeLater(() -> {
            _swingNode.setContent(viewPanel);
        });
        _swingNode.setLayoutX(25);
        _swingNode.setLayoutY(35);
    }

    /**
     * Data pane contains the CPU chart.
     */
    private void initDataPane() {
        //create a SystemInfoVisualisation object that monitor system's CPU usage and periodically update controller with new info on another thread
        SystemInfoVisualisation info = new SystemInfoVisualisation(this);

        //create CPU chart also styling the colours and size of the chart
        _tile = TileBuilder.create()
                .skinType(Tile.SkinType.SMOOTH_AREA_CHART)
                .maxHeight(200)
                .minWidth(300)
                .unit("%")
                .backgroundColor(javafx.scene.paint.Color.color(0,0,0,0))
                .chartData(_data1,_data2,_data3,_data4)
                .animated(true)
                .value(20)
                .title("CPU Usage")
                .build();

        //add the CPU chart to the pane
        _dataPane.getChildren().add(_tile);
        //set position of CPU chart on the pane
        _tile.setLayoutX(25);

        //start collecting CPU info on another thread
        info.run();
    }

    /**
     * Called by algorithm to update GUI to show newly calculated current optimal schedule.
     * @param updatedState
     */
    public synchronized void update(Map<String,String[]> updatedState) {
        //this is running on JavaFx Thread now
        Platform.runLater(() -> {
            for (String nodeID : updatedState.keySet()) {

                //update graph with new start time and the processor on which the node is allocated
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
                //update the graph visualisation using the new info
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
     * Method is called to display a Gantt chart at the end of computation.
     */
    private void drawGanttChart() {
        Platform.runLater(() -> {
            System.out.println("drawGanttChart running on: "+Thread.currentThread().getName());
            GanttChart chart = new GanttChart(_optimalState, Integer.parseInt(_numProcessor.getText()), _nodes, _colourMgr);
            _ganttPane.getChildren().add(chart.createGraph());
            _ganttPane.setBackground(Background.EMPTY);
            //_ganttPane.setVisible(true);
        });

    }


    /**
     * Method called to update timer label
     * @param count
     */
    public synchronized  void setTimer(int count) {
        //calculations so that time displays in the right format
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

    /**
     * Called by algorithm when computation is complete
     */
    public synchronized void completed(AbstractState optimalState) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _status.setText("Completed");
                _gantt.setDisable(false);
                _gantt.setTextFill(javafx.scene.paint.Paint.valueOf("#FFFFFF"));
                _timer.stopTimer();
            }
        });

            _optimalState = optimalState;

            //write the optimal state out to a file
            Main.writeResult(optimalState);

            drawGanttChart();


    }

    /**
     * Update the CPU chart by specifying four new data values for the chart.
     * @param cpu1
     * @param cpu2
     * @param cpu3
     * @param cpu4
     */
    public void updateCPU(double cpu1, double cpu2, double cpu3, double cpu4) {
        Platform.runLater(() -> {
            _data1.setValue(cpu1);
            _data2.setValue(cpu2);
            _data3.setValue(cpu3);
            _data4.setValue(cpu4);
        });
    }

    /**
     * Method that handles action when user clicks start button. Action includes run algorhtm on a new thread, start
     * timer, and change appropriate labels
     */
    @FXML
    public void handlePressStart(ActionEvent event) {
        //disable start button
        _start.setDisable(true);

        _start.setTextFill(javafx.scene.paint.Paint.valueOf("#423222"));
        //set status label to computing
        _status.setText("Computing");

        //start timer
        _timer.startTimer();

        //set gantt chart to invisible, only displays after computation finishes
        _ganttPane.setVisible(false);


        Controller controller = this;
        //get the scheduler instance that does the algorithm computation
        AbstractScheduler scheduler = GUIEntry.getScheduler();

        /*
        //run the algorithm using Callable which returns an optimal State
        Callable task = new Callable() {
            @Override
            public State call() throws ScriptException {
                System.out.print("IN THREAD");
                scheduler.setController(controller);
                return scheduler.getSchedule();
            }
        };

        ExecutorService service = Executors.newFixedThreadPool(2);
        _result = service.submit(task);
        */
        _algoThread = new AlgorithmThread(scheduler,controller);
        _algoThread.start();
    }


    /**
     * Called when user presses exit on the top right corner
     * @param event
     */
    @FXML
    public void handlePressQuit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }


    @FXML
    public void handlePressGantt(ActionEvent event) {
        if(_graphPane.isVisible()) {
            _ganttPane.setVisible(true);
            _graphPane.setVisible(false);
        }
        else {
            _ganttPane.setVisible(false);
            _graphPane.setVisible(true);
        }
    }


}

