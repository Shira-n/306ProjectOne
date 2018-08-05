package controller;
import java.util.Iterator;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;

import static javafx.application.Application.launch;



public class Controller extends Application {

    @FXML
    private SwingNode _swingNode;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../view/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1280, 800));

        createAndSetSwingContent(_swingNode);

        //new GraphExplorer();
        primaryStage.show();
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
        try { Thread.sleep(1000); } catch (Exception e) {}
    }


    private void createAndSetSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                SingleGraph graph = new SingleGraph("Graph"); //GraphStream Class
                //SwingNode graphViewer = new SwingNode(); //JavaFX Component to display Swing elements

                graph.addAttribute("ui.stylesheet", styleSheet);
                graph.setAutoCreate(true);
                graph.setStrict(false);
                graph.display();

                graph.addEdge("AB", "A", "B");
                graph.addEdge("BC", "B", "C");
                graph.addEdge("CA", "C", "A");
                graph.addEdge("AD", "A", "D");
                graph.addEdge("DE", "D", "E");
                graph.addEdge("DF", "D", "F");
                graph.addEdge("EF", "E", "F");

                for (Node node : graph) {
                    node.addAttribute("ui.label", node.getId());
                }

                explore(graph.getNode("A"));

                JPanel panel = new JPanel();
                Viewer viewer = new Viewer(graph,Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
                View view = viewer.addDefaultView(false);   // false indicates "no JFrame".
                panel.add((JComponent) view);
                swingNode.setContent(panel);
                //_anchor.getChildren().add(graphViewer);

                //swingNode.setContent(new JButton("Click me!"));
            }
        });
    }

    protected String styleSheet =
            "node {" +
                    "	fill-color: black;" +
                    "}" +
                    "node.marked {" +
                    "	fill-color: red;" +
                    "}";

}

