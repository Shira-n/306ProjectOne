package controller;


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
        _toggle.setVisible(false);

        //set labels to values corresponding to the current computation graph
        _status.setText("Computing");
        _currentBestTime.setText("NA");
        _numNode.setText(GUIEntry.getNumNode() + "");
        _numProcessor.setText(GUIEntry.getNumProcessor() + "");
        _isParallel.setText(GUIEntry.getParallelised() + "");

    }

    /*
    public void drawGanttChart(State optimalState) {
        final double YAxisStart = 20;
        final double YAxisEnd = 460;
        final double XAxisStart = 80;
        final double XAxisEnd = 680;


        int finishTime = 0;
        javafx.scene.paint.Color lineColor = javafx.scene.paint.Color.BLACK;
        int numberOfProcessors = Integer.parseInt(_numProcessor.getText());

        Map<String, String[]> schedule = optimalState.translate();


        for (model.Node node : _nodes) {
            int startTime = Integer.parseInt(schedule.get(node.getId())[1]);
            if (finishTime < startTime + node.getWeight()) {
                finishTime = startTime + node.getWeight();
            }
        }

        //draw Axis
        Line verticalLine = new Line(XAxisStart, YAxisStart, XAxisStart, YAxisEnd);
        verticalLine.setStroke(lineColor);
        Line horizontalLine = new Line(XAxisStart, YAxisEnd, XAxisEnd, YAxisEnd);
        horizontalLine.setStroke(lineColor);
        _ganttPane.getChildren().addAll(verticalLine, horizontalLine);

        double totalHeight = YAxisEnd - YAxisStart;
        double totalWidth = XAxisEnd - XAxisStart;

        double HorizontalUnit = totalWidth / (double) finishTime;
        double VerticalUnit = totalHeight / (double) (1 + 3 * numberOfProcessors);

        //draw vertical lines for viewing time on chart
        for (int i = 0; i <= finishTime; i++) {
            Line _line = new Line(i * HorizontalUnit + XAxisStart, YAxisEnd, i * HorizontalUnit + XAxisStart, YAxisEnd + 5);
            if (i % 5 != 0) {
                _line.setStrokeWidth(0.5);
            }
            Label _label = new Label(Integer.toString(i));

            double width = (fontSize(_label))[1];

            _label.setLayoutX(i * HorizontalUnit + XAxisStart - width / 2);
            _label.setLayoutY(YAxisEnd + 5);

            Line line5 = new Line(i * HorizontalUnit + XAxisStart, YAxisStart, i * HorizontalUnit + XAxisStart, YAxisEnd);
            line5.getStrokeDashArray().addAll(2d);
            line5.setStrokeWidth(0.2);

            _ganttPane.getChildren().addAll(_line, _label, line5);
        }

        //draw nodes and labels
        for (model.Node node : _nodes) {
            int processor = Integer.parseInt(schedule.get(node.getId())[0]);
            int startTime = Integer.parseInt(schedule.get(node.getId())[1]);
            //double startHeight = VerticalUnit+3*(i-1)*VerticalUnit+YAxisStart;
            double startHeight = VerticalUnit+3*(processor-1)*VerticalUnit+YAxisStart;
            javafx.scene.shape.Rectangle rectangle = new Rectangle((startTime*HorizontalUnit)+XAxisStart, startHeight, node.getWeight()*HorizontalUnit, 2*VerticalUnit);
            rectangle.setStroke(javafx.scene.paint.Color.WHITE);

            Label label = new Label(node.getId());
            double height = (fontSize(label))[0];
            double width = (fontSize(label))[1];

            label.setLayoutX((startTime*HorizontalUnit)+XAxisStart+(HorizontalUnit*node.getWeight())/2-width/2);
            label.setLayoutY(startHeight+0.5*VerticalUnit+height+height/3);
            label.setTextFill(javafx.scene.paint.Color.WHITE);

            if(startTime!=0) {
                Label alabel = new Label(Integer.toString(startTime));
                height = (fontSize(alabel))[0];
                width = (fontSize(alabel))[1];
                alabel.setLayoutX(startTime * HorizontalUnit + XAxisStart - width / 2);
                alabel.setLayoutY(startHeight - height);
                _ganttPane.getChildren().add(alabel);
            }
            boolean repeat = false;
            for(String[] node1: schedule.values()) {
                if (startTime + node.getWeight()==Integer.parseInt(node1[1])){
                    repeat=true;
                }
            }
            if(!repeat){
                Label blabel = new Label(Integer.toString(startTime + node.getWeight()));
                height = (fontSize(blabel))[0];
                width = (fontSize(blabel))[1];
                blabel.setLayoutX((startTime + node.getWeight()) * HorizontalUnit + XAxisStart - width / 2);
                blabel.setLayoutY(startHeight - height);
                _ganttPane.getChildren().add(blabel);
            }

            _ganttPane.getChildren().addAll(rectangle, label);
        }

        //draw labels for processors
        for (int i = 0; i < numberOfProcessors; i++) {
            double startHeight = VerticalUnit+3*(i)*VerticalUnit+YAxisStart;

            Label label = new Label("Processor " + (Integer.toString(i+1)));

            double height = (fontSize(label))[0];

            label.setLayoutX(XAxisStart-70);
            label.setLayoutY(startHeight+0.5*VerticalUnit+height+height/3);

             _ganttPane.getChildren().add(label);
        }

        //_ganttPane.setVisible(true);
    }

    private double[] fontSize(Label label) {
        Text theText = new Text(label.getText());
        theText.setFont(label.getFont());
        double[] size = {theText.getBoundsInLocal().getHeight(), theText.getBoundsInLocal().getWidth()};
        return size;

    public void drawGanttChart() {
        System.out.println("drawGanttChart running on: "+Thread.currentThread().getName());
        GanttChart chart = new GanttChart(_optimalSchedule, Integer.parseInt(_numProcessor.getText()), _nodes);
        _ganttPane.getChildren().add(chart.createGraph());
        _ganttPane.setBackground(Background.EMPTY);
        _ganttPane.setVisible(true);

    }
    */

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

