package controller;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import model.BranchAndBoundScheduler;
import model.State;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.*;

import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;


import javax.swing.*;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;


/**
 * Controller class for the MainWindow. Initialise all components on the pane.
 */
public class Controller {


    private SingleGraph _graph;

    private Timeline _timeline;

    private int _timeSeconds;

    private ColourManager _colourMgr;

    private GraphViewer _viewer;

    private List<model.Node> _nodes;

    private GUITimer _timer;

    @FXML
    private Pane _graphPane;

    @FXML
    private Pane _buttonPane;

    @FXML
    private Pane _ganttPane;

    @FXML
    private ToggleButton _toggle;

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







    }


    /**
    *
    */
    @FXML
    public void handlePressStart(ActionEvent event) {
        Controller controller = this;
        Thread thread = new Thread() {
            public void run() {
                System.out.print("IN THREAD");
                BranchAndBoundScheduler scheduler = new BranchAndBoundScheduler(GUIEntry.getNodes(), GUIEntry.getNumProcessor(), controller,_timer);
                model.State optimalSchedule = scheduler.getOptimalSchedule();
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
        System.out.println("UPDATE");
        for (String nodeID: updatedState.keySet()) {
            String[] nodeInfo = updatedState.get(nodeID);
            Node node = _graph.getNode(nodeID);
            System.out.println(node.getAttribute("startTime")+"");
            if (node.hasAttribute("startTime")) {
                node.removeAttribute("startTime");
            }

            System.out.println(node.getId()+  " Processor: " + node.getAttribute("processor"));
            if (node.hasAttribute("processor")) {
                node.removeAttribute("processor");
            }
            node.addAttribute("startTime", nodeInfo[1]);
            node.addAttribute("processor", nodeInfo[0]);
            if(node.getAttribute("processor")==null){
                System.out.println("hi");
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    _viewer.updateNodeColour(node);
                }
            });
        }
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
        _toggle.setVisible(false);

        //set labels to values corresponding to the current computation graph
        _status.setText("Computing");
        _currentBestTime.setText("NA");
        _numNode.setText(GUIEntry.getNumNode() + "");
        _numProcessor.setText(GUIEntry.getNumProcessor() + "");
        _isParallel.setText(GUIEntry.getParallelised() + "");

    }


    public synchronized  void setTimer(int count) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _time.setText(count+"");
            }
        });

    }

    public synchronized void completed(int maxWeight) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                _status.setText("Completed");
            }
        });
    }

    @FXML
    public void handlePressQuit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

}

