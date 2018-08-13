package controller;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;

public class GraphViewer extends Viewer {

    public GraphViewer(Graph graph, Viewer.ThreadingModel threadingModel) {
        super(graph, threadingModel);
        
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
    }

    public void initialiseLabels() {

    }

    public void setNodColour() {

    }

    public void setNodeBorderColour() {

    }

    public void updateNodeAttribute() {

    }



}
