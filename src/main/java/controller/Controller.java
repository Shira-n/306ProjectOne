package controller;

import java.util.Iterator;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
<<<<<<< HEAD
import org.graphstream.ui.swingViewer.ViewPanel;
=======
>>>>>>> 16240289c315dc16d7782e95737cbd710a0b386e
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import scala.App;


import javax.swing.*;

import static javafx.application.Application.launch;


public class Controller extends Application {


    @FXML
    private SwingNode _swingNode;

    @FXML
    private Pane _board;

    @FXML
    private AnchorPane _anchor;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1280, 800));

        //new GraphExplorer();

        primaryStage.show();
    }

    @FXML
    public void initialize() {
        SwingNode node = new SwingNode();
        createAndSetSwingContent(node);
        _board.getChildren().add(node);
    }

    public static void main(String args[]) {
        launch(args);
    }

    public void explore(Node source) {
        Iterator<? extends Node> k = source.getBreadthFirstIterator();

        while (k.hasNext()) {
            Node next = k.next();
            next.setAttribute("ui.class", "marked");
            sleep();
        }
    }

    protected void sleep() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
    }


    private void createAndSetSwingContent(SwingNode swingNode) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        SingleGraph graph = new SingleGraph("Graph"); //GraphStream Class
        //SwingNode graphViewer = new SwingNode(); //JavaFX Component to display Swing elements

        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

        graph.addNode("A");

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


    protected String styleSheet =
            "node {" +
                    "	fill-color: black;" +
                    "}" +
                    "node.marked {" +
                    "	fill-color: red;" +
                    "}";

}

