package controller;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import model.Node;
import model.State;

import java.util.List;
import java.util.Map;

public class GanttChart {
    final double _YAxisStart = 20;
    final double _YAxisEnd = 460;
    final double _XAxisStart = 80;
    final double _XAxisEnd = 680;

    final double _totalHeight;
    final double _totalWidth;

    double _horizontalUnit;
    double _verticalUnit;

    final int _numProcessors;
    final List<Node> _nodes;
    final Group _root;
    final Map<String, String[]> _schedule;

    final Color _lineColor = Color.WHITE;

    public GanttChart(State optimalState, int numProcessor, List<Node> nodes){
        _numProcessors = numProcessor;
        _nodes = nodes;
        _schedule = optimalState.translate();

        _totalHeight = _YAxisEnd - _YAxisStart;
        _totalWidth = _XAxisEnd - _XAxisStart;

        _root = new Group();
    }



    public Group createGraph(){
        calculateFinishTimeAndUnits();
        drawAxis();
        drawNodes();
        drawLabels();
        return _root;
    }

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

    private void drawVerticalLines(int finishTime){
        int incrementValue = finishTime/25;
        for (int i = 0; i <= finishTime; i = i + incrementValue) {

            double xValueStart = i * _horizontalUnit + _XAxisStart;
            double xValueEnd = i * _horizontalUnit + _XAxisStart;

            // draw lines on axis to indicate time
            Line axisLine = new Line(xValueStart, _YAxisEnd, xValueEnd, _YAxisEnd + 5);
            if (i % 5 != 0) {
                axisLine.setStrokeWidth(0.5);
            }
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
        //draw Axis
        Line verticalLine = new Line(_XAxisStart, _YAxisStart, _XAxisStart, _YAxisEnd);
        verticalLine.setStroke(_lineColor);

        Line horizontalLine = new Line(_XAxisStart, _YAxisEnd, _XAxisEnd, _YAxisEnd);
        horizontalLine.setStroke(_lineColor);

        _root.getChildren().addAll(verticalLine, horizontalLine);
    }

    public void drawNodes(){
        //draw nodes and labels
        for (Node node : _nodes) {
            int processor = Integer.parseInt(_schedule.get(node.getId())[0]);
            int startTime = Integer.parseInt(_schedule.get(node.getId())[1]);

            double startY = _verticalUnit+3*(processor-1)*_verticalUnit+ _YAxisStart;
            double startX = startTime*_horizontalUnit + _XAxisStart;
            double width = node.getWeight()*_horizontalUnit;

            Rectangle rectangle = new Rectangle(startX, startY, width, 2*_verticalUnit);
            rectangle.setStroke(_lineColor);

            Label label = new Label(node.getId());
            double fontHeight = (fontSize(label))[0];
            double fontWidth = (fontSize(label))[1];
            label.setLayoutX(startX + (width)/2 - fontWidth/2);
            label.setLayoutY(startY + 0.5*_verticalUnit + fontHeight + fontHeight/3);
            label.setTextFill(Color.WHITE);

            if(startTime!=0) {
                Label alabel = new Label(Integer.toString(startTime));
                fontHeight = (fontSize(alabel))[0];
                fontWidth = (fontSize(alabel))[1];
                alabel.setLayoutX(startX - fontWidth / 2);
                alabel.setLayoutY(startY - fontHeight);
                alabel.setTextFill(_lineColor);
                _root.getChildren().add(alabel);
            }

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

    private void drawLabels(){
        //draw labels for processors
        for (int i = 0; i < _numProcessors; i++) {
            double startHeight = _verticalUnit+3*(i)*_verticalUnit+ _YAxisStart;

            Label label = new Label("Processor " + (Integer.toString(i+1)));

            double height = (fontSize(label))[0];

            label.setLayoutX(_XAxisStart - 70);
            label.setLayoutY(startHeight+0.5*_verticalUnit+height+height/3);
            label.setTextFill(_lineColor);

            _root.getChildren().add(label);
        }
    }

    private double[] fontSize(Label label) {
        Text theText = new Text(label.getText());
        theText.setFont(label.getFont());
        double[] size = {theText.getBoundsInLocal().getHeight(), theText.getBoundsInLocal().getWidth()};
        return size;
    }
}
