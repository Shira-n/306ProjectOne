package controller;


import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.graphstream.graph.implementations.*;

import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;


import javax.swing.*;
import java.awt.*;


/**
 * Controller class for the MainWindow. Initialise all components on the pane.
 */
public class Controller{



    private SingleGraph _graph;


    @FXML
    private Pane _graphPane;

    @FXML
    private Pane _buttonPane;

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

        initGraph();

        initLabel();
    }

    /**
     * Initialise the node graph to display the initial state of the graph.
     */
    private void initGraph() {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        _graph = GUIEntry.getGraph();


        _graph.addAttribute("ui.stylesheet", "graph { fill-color: rgba(0,0,0,255); }");
        _graph.addAttribute("ui.stylesheet", "node { fill-color: rgba(255,0,0,255); }");
        Viewer viewer = new Viewer (_graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        ViewPanel viewPanel = viewer.addDefaultView(false);
        viewPanel.setMinimumSize(new Dimension(600,500));
        SwingUtilities.invokeLater(() -> {
            _swingNode.setContent(viewPanel);
        });
        _swingNode.setLayoutX(25);
        _swingNode.setLayoutY(55);


    }

    private void initLabel() {

    }
    /*
    private void createAndSetSwingContent(SwingNode swingNode) {
         //GraphStream Class
        //SwingNode graphViewer = new SwingNode(); //JavaFX Component to display Swing elements


        JPanel panel = new JPanel();
        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        ViewPanel viewPanel = viewer.addDefaultView(false);   // false indicates "no JFrame".
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(viewPanel);
        });

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(swingNode);
        stackPane.setPrefHeight(481);
        stackPane.setPrefWidth(738);
        _anchor.getChildren().add(swingNode);
    }
    */
}

