package controller;

import javafx.application.Application;
import model.Node;

import java.util.List;

/**
 * This is the class that Main class calls when client requests visualisation. This class creates
 * an GUIMain object ad stores appropriate fields that will be used by the GUI .
 */
public class GUIEntry implements Runnable{
    private List<Node> _graph;
    private String _filename;
    private int _numProcessor;
    private boolean _parallelised;

    public GUIEntry(List<Node> graph, String filename, int numProcessor, boolean parallelised) {
        _graph = graph;
        _filename = filename;
        _numProcessor = numProcessor;
        _parallelised = parallelised;
    }

    /**
     * Calls GUIEntry class which starts the visualisation.
     */
    @Override
    public void run() {
        Application.launch(GUIMain.class);
    }

    public List<Node> getGraph() {
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


    private void cerateGraph() {

    }
}

