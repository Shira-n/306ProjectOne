package controller;

import javafx.application.Application;
import model.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the class that Main class calls when client requests visualisation. This class creates
 * an GUIMain object ad stores appropriate fields that will be used by the GUI .
 */
public class GUIEntry implements Runnable{
    private List<Node> _nodes;
    private String _filename;
    private int _numProcessor;
    private boolean _parallelised;
    private static SingleGraph _graph = new SingleGraph("graph");

    public GUIEntry(List<Node> nodes, String filename, int numProcessor, boolean parallelised) {
        _nodes = nodes;
        _filename = filename;
        _numProcessor = numProcessor;
        _parallelised = parallelised;
        createGraph();
    }

    /**
     * Calls GUIEntry class which starts the visualisation.
     */
    @Override
    public void run() {
        Application.launch(GUIMain.class);
    }


    public static SingleGraph getGraph() {
        return _graph;
    }
    public String getFilename() {
        return _filename;
    }

    public int getNumProcessor() {
        return _numProcessor;
    }

    public boolean getParallelised() {
        return _parallelised;
    }


    private void createGraph() {


        _graph.addNode("A");
        _graph.addNode("B");
        _graph.addEdge("a","A","B");

        /*
        for(Node n: _nodes) {
            _graph.addNode(n.getId().toString());
            Set<Node> children  = n.getChildren().keySet();
            for (Node c: children) {
                _graph.addEdge("a",n.getId().toString(),c.getId().toString());
            }
        }
        */
    }

}

