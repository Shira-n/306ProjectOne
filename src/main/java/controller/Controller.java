package controller;

import java.awt.*;
import java.util.Iterator;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import org.graphstream.graph.implementations.*;

import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;


import javax.swing.*;


/**
 * Controller class for the MainWindow. Initialise all components on the pane.
 */
public class Controller{


    private SingleGraph _graph;

    @FXML
    private SwingNode _swingNode;

    @FXML
    private AnchorPane _anchor;


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

        _graph = new SingleGraph("graph");
        _graph.addNode("A");
        _graph.addNode("B");

        _graph.addAttribute("ui.stylesheet", "graph { fill-color: rgb(0,0,0); }");

        Viewer viewer = new Viewer (_graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();
        ViewPanel viewPanel = viewer.addDefaultView(false);
        viewPanel.setMinimumSize(new Dimension(800,500));
        SwingUtilities.invokeLater(() -> {
            _swingNode.setContent(viewPanel);
        });

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

