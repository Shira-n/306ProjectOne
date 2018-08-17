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
    final double YAxisStart = 20;
    final double YAxisEnd = 460;
    final double XAxisStart = 80;
    final double XAxisEnd = 680;

    double _totalHeight;
    double _totalWidth;

    double _horizontalUnit;
    double _verticalUnit;

    final State _optimalState;
    int _numProcessors;
    List<Node> _nodes;



    public GanttChart(State optimalState, int numProcessor, List<Node> nodes){
        _optimalState = optimalState;
        _numProcessors = numProcessor;
        _nodes = nodes;
    }

    public Group create(){
        Group root = new Group();
        int finishTime = 0;
        Color lineColor = Color.BLACK;
        int numberOfProcessors = _numProcessors;

        Map<String, String[]> schedule = _optimalState.translate();

        for (Node node : _nodes) {
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
        root.getChildren().addAll(verticalLine, horizontalLine);

        _totalHeight = YAxisEnd - YAxisStart;
        _totalWidth = XAxisEnd - XAxisStart;

        _horizontalUnit = _totalWidth / (double) finishTime;
        _verticalUnit = _totalHeight / (double) (1 + 3 * _numProcessors);

        //draw vertical lines for viewing time on chart
        for (int i = 0; i <= finishTime; i++) {
            Line _line = new Line(i * _horizontalUnit + XAxisStart, YAxisEnd, i * _horizontalUnit + XAxisStart, YAxisEnd + 5);
            if (i % 5 != 0) {
                _line.setStrokeWidth(0.5);
            }
            Label _label = new Label(Integer.toString(i));

            double width = (fontSize(_label))[1];

            _label.setLayoutX(i * _horizontalUnit + XAxisStart - width / 2);
            _label.setLayoutY(YAxisEnd + 5);

            Line line5 = new Line(i * _horizontalUnit + XAxisStart, YAxisStart, i * _horizontalUnit + XAxisStart, YAxisEnd);
            line5.getStrokeDashArray().addAll(2d);
            line5.setStrokeWidth(0.2);

            root.getChildren().addAll(_line, _label, line5);
        }

        //draw nodes and labels
        for (Node node : _nodes) {
            int processor = Integer.parseInt(schedule.get(node.getId())[0]);
            int startTime = Integer.parseInt(schedule.get(node.getId())[1]);
            //double startHeight = VerticalUnit+3*(i-1)*VerticalUnit+YAxisStart;
            double startHeight = _verticalUnit+3*(processor-1)*_verticalUnit+YAxisStart;
            Rectangle rectangle = new Rectangle((startTime*_horizontalUnit)+XAxisStart, startHeight, node.getWeight()*_horizontalUnit, 2*_verticalUnit);
            rectangle.setStroke(Color.WHITE);

            Label label = new Label(node.getId());
            double height = (fontSize(label))[0];
            double width = (fontSize(label))[1];

            label.setLayoutX((startTime*_horizontalUnit)+XAxisStart+(_horizontalUnit*node.getWeight())/2-width/2);
            label.setLayoutY(startHeight+0.5*_verticalUnit+height+height/3);
            label.setTextFill(Color.WHITE);

            if(startTime!=0) {
                Label alabel = new Label(Integer.toString(startTime));
                height = (fontSize(alabel))[0];
                width = (fontSize(alabel))[1];
                alabel.setLayoutX(startTime * _horizontalUnit + XAxisStart - width / 2);
                alabel.setLayoutY(startHeight - height);
                root.getChildren().add(alabel);
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
                blabel.setLayoutX((startTime + node.getWeight()) * _horizontalUnit + XAxisStart - width / 2);
                blabel.setLayoutY(startHeight - height);
                root.getChildren().add(blabel);
            }

            root.getChildren().addAll(rectangle, label);
        }

        //draw labels for processors
        for (int i = 0; i < numberOfProcessors; i++) {
            double startHeight = _verticalUnit+3*(i)*_verticalUnit+YAxisStart;

            Label label = new Label("Processor " + (Integer.toString(i+1)));

            double height = (fontSize(label))[0];

            label.setLayoutX(XAxisStart-70);
            label.setLayoutY(startHeight+0.5*_verticalUnit+height+height/3);

            root.getChildren().add(label);
        }
        return root;
    }

    private double[] fontSize(Label label) {
        Text theText = new Text(label.getText());
        theText.setFont(label.getFont());
        double[] size = {theText.getBoundsInLocal().getHeight(), theText.getBoundsInLocal().getWidth()};
        return size;
    }
}
