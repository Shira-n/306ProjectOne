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
                     node.getAttribute("id")+"");
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
        String processorColour = _colourMgr.getColor(Integer.parseInt(node.getAttribute("processor"))-1);
        node.removeAttribute("ui.style");
        node.addAttribute("ui.style", "text-alignment: center;\n"
                +"\tstroke-mode: plain; stroke-color:" + processorColour + "; stroke-width: 3px;"
                + "\tfill-mode: plain; fill-color: rgba(0,0,0,0);\n"

                + "\tsize: 30px, 30px;\n"
                + "\ttext-size: 13px; text-color: white;\n");

    }

    public void setNodeBorderColour() {

    }

    public void setEdgeColour() {

    }

    public void updateNodeAttribute() {

    }



}
