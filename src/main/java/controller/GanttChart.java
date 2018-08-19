package controller;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import model.Node;
import model.state.AbstractState;

import java.util.List;
import java.util.Map;

public class GanttChart {
    final private double _YAxisStart = 20;
    final private double _YAxisEnd = 460;
    final private double _XAxisStart = 80;
    final private double _XAxisEnd = 890;

    final private double _totalHeight;
    final private double _totalWidth;

    private double _horizontalUnit;
    private double _verticalUnit;

    final private int _numProcessors;
    final private List<Node> _nodes;
    final private Group _root;
    final private Map<String, String[]> _schedule;

    final private Color _lineColor = Color.WHITE;

    final private ColourManager _colourMgr;

    /**
     * constructor to create the GanttChart and initialise variables
     * @param optimalState the final optimal schedule found
     * @param numProcessor the number of processors used for alocating
     * @param nodes nodes in the graph
     * @param colourMgr the manager for displaying colors on the nodes
     */
    public GanttChart(AbstractState optimalState, int numProcessor, List<Node> nodes, ColourManager colourMgr){
        System.out.println("constructor");
        _numProcessors = numProcessor;
        _nodes = nodes;
        _schedule = optimalState.translate();

        _totalHeight = _YAxisEnd - _YAxisStart;
        _totalWidth = _XAxisEnd - _XAxisStart;

        _colourMgr = colourMgr;

        //initialise root that will be returned for the graph
        _root = new Group();
    }

    /**
     * calls methods to create objects for the gantt chart
     * @return Group of objects for gantt chart including nodes labels and lines to be displayed
     */
    public Group createGraph(){
        calculateFinishTimeAndUnits();
        drawAxis();
        drawNodes();
        drawProcessorLabels();
        return _root;
    }

    /**
     * calculates the total finish time of the task and also the units required for calculating the weight per pixel
     */
    public void calculateFinishTimeAndUnits(){
        int finishTime = 0;

        for (Node node : _nodes) {

            int startTime = Integer.parseInt(_schedule.get(node.getId())[1]);

            if (finishTime < startTime + node.getWeight()) {
                finishTime = startTime + node.getWeight();
            }

        }

        _horizontalUnit = _totalWidth / (double) finishTime;
        _verticalUnit = _totalHeight / (double) (1 + 3 * _numProcessors);

        drawVerticalLines(finishTime);
    }

    /**
     * draws the labels and lines that run vertically from the x axis
     * @param finishTime the time that the entire task is finished used for calculating spread and gaps
     */
    private void drawVerticalLines(int finishTime){
        System.out.println("draw Vertical lines");
        int incrementValue = finishTime/25;
        if (incrementValue == 0){
            incrementValue = 1;
        }

        for (int i = 0; i <= finishTime; i = i + incrementValue) {
            double xValueStart = i * _horizontalUnit + _XAxisStart;
            double xValueEnd = i * _horizontalUnit + _XAxisStart;
            // draw lines on axis to indicate time in graph
            Line axisLine = new Line(xValueStart, _YAxisEnd, xValueEnd, _YAxisEnd + 5);
            axisLine.setStroke(_lineColor);
            //draw labels for axis
            Label numberLabel = new Label(Integer.toString(i));
            double width = (fontSize(numberLabel))[1];
            numberLabel.setLayoutX(i * _horizontalUnit + _XAxisStart - width / 2);
            numberLabel.setLayoutY(_YAxisEnd + 5);
            numberLabel.setTextFill(_lineColor);
            //draw dotted lines for axis
            Line dottedLine = new Line(xValueStart, _YAxisStart, xValueEnd, _YAxisEnd);
            dottedLine.getStrokeDashArray().addAll(2d);
            dottedLine.setStrokeWidth(0.2);
            dottedLine.setStroke(_lineColor);
            _root.getChildren().addAll(axisLine, numberLabel, dottedLine);
        }
    }

    /**
     * Draws Axis on chart
     */
    private void drawAxis(){
        System.out.println("draw Axis");
        //draw Axis
        Line verticalLine = new Line(_XAxisStart, _YAxisStart, _XAxisStart, _YAxisEnd);
        verticalLine.setStroke(_lineColor);

        Line horizontalLine = new Line(_XAxisStart, _YAxisEnd, _XAxisEnd, _YAxisEnd);
        horizontalLine.setStroke(_lineColor);

        _root.getChildren().addAll(verticalLine, horizontalLine);
    }

    /**
     * draws the tasks that are allocated
     */
    public void drawNodes(){
        //draw nodes and labels
        for (Node node : _nodes) {
            double fontHeight = positioning();
            int processor = Integer.parseInt(_schedule.get(node.getId())[0]);
            int startTime = Integer.parseInt(_schedule.get(node.getId())[1]);

            //calculates the starting positions of the node
            double startY = _verticalUnit+3*(processor-1)*_verticalUnit+ _YAxisStart;
            double startX = startTime*_horizontalUnit + _XAxisStart;
            double width = node.getWeight()*_horizontalUnit;

            //draw the nodes
            Rectangle rectangle = new Rectangle(startX, startY, width, 2*_verticalUnit);
            String colorString = _colourMgr.getColor(processor);

            //sets the color and outline of the node
            Color color = Color.web(colorString);
            rectangle.setFill(color);
            rectangle.setStroke(_lineColor);

            //adds tha name of the node as a label at the center of the node rectangle
            Label label = new Label(node.getId());
            if (_numProcessors<10){
                label.setStyle("-fx-font-size:16px;");
                fontHeight = 0;
            }
            double fontWidth = (fontSize(label))[1];
            label.setLayoutX(startX + (width)/2 - fontWidth/2);
            label.setLayoutY(startY + 0.5*_verticalUnit + fontHeight + fontHeight/3);

            //adds labels on top right of node to indicate start time
            if(startTime!=0) {
                Label alabel = new Label(Integer.toString(startTime));
                fontHeight = (fontSize(alabel))[0];
                fontWidth = (fontSize(alabel))[1];
                alabel.setLayoutX(startX - fontWidth / 2);
                alabel.setLayoutY(startY - fontHeight);
                alabel.setTextFill(_lineColor);
                _root.getChildren().add(alabel);
            }

            //adds labels on top left of node to indicate end time
            boolean repeat = false;
            for(String[] node1: _schedule.values()) {
                if (startTime + node.getWeight()==Integer.parseInt(node1[1])){
                    repeat = true;
                }
            }

            if(!repeat){
                Label blabel = new Label(Integer.toString(startTime + node.getWeight()));
                fontHeight = (fontSize(blabel))[0];
                fontWidth = (fontSize(blabel))[1];
                blabel.setLayoutX(startX + width - fontWidth / 2);
                blabel.setLayoutY(startY - fontHeight);
                blabel.setTextFill(_lineColor);
                _root.getChildren().add(blabel);
            }

            _root.getChildren().addAll(rectangle, label);
        }
    }

    /**
     * draws the labels for the processors
     */
    private void drawProcessorLabels(){
        double fontHeight = positioning();

        for (int i = 0; i < _numProcessors; i++) {
            double startHeight = _verticalUnit+3 * i * _verticalUnit + _YAxisStart;

            Label label = new Label("Processor " + (Integer.toString(i + 1)));

            label.setLayoutX(_XAxisStart - 80);
            label.setLayoutY(startHeight+0.5*_verticalUnit+fontHeight + fontHeight/_numProcessors);
            label.setTextFill(_lineColor);

            Circle circle = new Circle(_XAxisStart, startHeight,1);

            _root.getChildren().add(label);
            _root.getChildren().add(circle);
        }
    }

    private double positioning(){
        double fontHeight = 0;

        if (_numProcessors<6){
            fontHeight = (fontSize(new Label("Processor")))[0];
        }

        return fontHeight;
    }
    private double[] fontSize(Label label) {
        Text theText = new Text(label.getText());
        theText.setFont(label.getFont());
        double[] size = {theText.getBoundsInLocal().getHeight(), theText.getBoundsInLocal().getWidth()};
        return size;
    }

}
