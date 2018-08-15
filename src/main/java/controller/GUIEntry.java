package controller;

import javafx.application.Application;
import model.Node;
import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the class that Main class calls when client requests visualisation. This class creates
 * an GUIMain object ad stores appropriate fields that will be used by the GUI .
 */
public class GUIEntry implements Runnable{
    private static List<Node> _nodes;
    private static String _filename;
    private static int _numProcessor;
    private static boolean _parallelised;
    private static SingleGraph _graph = new SingleGraph("graph");
    private static Controller _controller;

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
    public static String getFilename() {
        return _filename;
    }

    public static int getNumProcessor() {
        return _numProcessor;
    }

    public static boolean getParallelised() {
        return _parallelised;
    }

    //TODO
    public static int getNumNode() { return /*_nodes.size()*/3;}


    private void createGraph() {


        org.graphstream.graph.Node A = _graph.addNode("A");
        A.addAttribute("id","A");
        _graph.addNode("B");
        _graph.addNode("C");
        Edge e = _graph.addEdge("a","A","B");
        e.addAttribute("weight","32");

        /*
        for(Node n: _nodes) {
            org.graphstream.graph.Node node = _graph.addNode(n.getId());
            node.addAttribute("weight",String.valueOf(n.getWeight()));
            node.addAttribute("processor","null"));
            node.addAttribute("startTime","null");

            Set<Node> children  = n.getChildren().keySet();
            for (Node c: children) {
                String id = n.getId() + c.getId();
                Edge edge = _graph.addEdge(id,n.getId(),c.getId());
                edge.addAttribute("weight", String.valueOf(n.getPathWeightToChild(c)));
            }
        }
        */

    }

    public static Controller getController() {
        return _controller;
    }

    public static void setController(Controller controller) {
        _controller = controller;
    }

}

