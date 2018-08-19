package controller;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;

public class GraphViewer extends Viewer {
    private Graph _graph;

    private ColourManager _colourMgr;

    private SpriteManager _mgr;

    public GraphViewer(Graph graph, Viewer.ThreadingModel threadingModel, ColourManager colourMgr) {
        super(graph, threadingModel);
        _graph = graph;
        _colourMgr = colourMgr;
        _mgr = new SpriteManager(_graph);
        
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        initialiseLabels();


    }


    public void initialiseLabels() {

        for(Node node: _graph) {
            node.setAttribute("ui.label",
                     node.getId()+"");
            node.addAttribute("ui.style", "text-alignment: center;\n"
                    +"\tstroke-mode: plain; stroke-color:grey; stroke-width: 3px;"
                    + "\tfill-mode: plain; fill-color: rgba(0,0,0,0);\n"
                    + "\tsize: 30px, 30px;\n"
                    + "\ttext-size: 15px; text-color: white;\n");
        }

        for(int i =0; i< _graph.getEdgeCount(); i++) {
            Edge edge = _graph.getEdge(i);
            edge.addAttribute("ui.style", "fill-mode: plain; fill-color: grey;\n"
                    + "\ttext-size: 15px; text-color: white;\n"
                    + "\ttext-alignment: along;\n");
            edge.addAttribute("ui.label",edge.getAttribute("weight")+"");
        }



        /*
        SpriteManager mgr = new SpriteManager(_graph);
        System.out.println("out");
        for(Node node: _graph) {
            System.out.println("here");
            Sprite sprite = mgr.addSprite("Schedule Node " + node.getId());
            sprite.addAttribute("ui.label", "P " + node.getAttribute("processor") + "\nWeight " + node.getAttribute("weight"));
            sprite.addAttribute("ui.style","text-alignment: center;\n"
                    + "\ttext-background-mode: rounded-box;\n"
                    + "\ttext-background-color: yellow;\n"
                    + "\ttext-size: 16px;\n");
            sprite.attachToNode(node.getAttribute("id"));

        }
        */

    }

    public void updateNodeColour(Node node) {
        //System.out.println("Processor " + node.getAttribute("processor"));
        String processorColour = _colourMgr.getColor(Integer.parseInt(node.getAttribute("processor")));
        node.removeAttribute("ui.style");
        node.addAttribute("ui.style", "text-alignment: center;\n"
                +"\tstroke-mode: plain; stroke-color: " + processorColour + "; stroke-width: 3px;"
                + "\tfill-mode: plain; fill-color: rgba(0,0,0,0);\n"

                + "\tsize: 30px, 30px;\n"
                + "\ttext-size: 20px; text-color: " + processorColour + ";\n");
        Sprite sprite = _mgr.addSprite("Node" + node.getId());
        sprite.addAttribute("ui.label", "P" + node.getAttribute("processor")
                + "   Start: " + node.getAttribute("startTime"));
        sprite.addAttribute("ui.style","text-alignment: center;\n"
                + "\ttext-background-mode: rounded-box;\n"
                + "\ttext-alignment: under;\n"
                + "\tfill-mode: plain; fill-color: rgba(0,0,0,0);\n"
                + "\ttext-size: 16px;\n");
        sprite.attachToNode(node.getId());

        /*
        for(int i =0; i< _graph.getEdgeCount(); i++) {
            Edge edge = _graph.getEdge(i);
            if (edge.getNode0().getAttribute("processor").equals(edge.getNode1().getAttribute("processor"))) {

                edge.removeAttribute("ui.style");
                edge.addAttribute("ui.style", "fill-mode: plain; fill-color: " + processorColour + ";\n"
                        + "\ttext-size: 15px; text-color: white;\n"
                        + "\ttext-alignment: along;\n");
            }
        }
        */
    }

    public void updateNodeFrequency(Node node) {
        System.out.println("frequency");
        String attribute = node.getAttribute("ui.style");
        node.removeAttribute("ui.style");
        String processorColour;
        if (!node.getAttribute("processor").equals("null")) {
            processorColour = _colourMgr.getColor(Integer.parseInt(node.getAttribute("processor")));
        }
        else {
            processorColour = "rgba(0,0,0,0)";
        }
        int frequency = node.getAttribute("numAllocation");
        int shadowWidth;
        if (frequency < 20) {
            shadowWidth = 3;
        }
        else if (frequency < 100) {
            shadowWidth = 5;
        }
        else if (frequency < 500) {
            shadowWidth = 7;
        }
        else {
            shadowWidth = 8;
        }
        node.addAttribute("ui.style", "text-alignment: center;\n"
                +"\tstroke-mode: plain; stroke-color: " + processorColour + "; stroke-width: 3px;"
                + "\tfill-mode: plain; fill-color: rgba(0,0,0,0);\n"

                + "\tsize: 30px, 30px;\n"
                + "\ttext-size: 20px; text-color: " + processorColour + ";\n"
                + "\tshadow-mode: gradient-radial;\n"
                + "\tshadow-color:white,rgba(255,255,255,0);\n"
                + "\tshadow-offset: 0;\n"
                + "\tshadow-width:"+ shadowWidth + "px;\n");
    }

    public void setNodeBorderColour() {

    }

    public void setEdgeColour() {

    }

    public void updateNodeAttribute() {

    }



}
