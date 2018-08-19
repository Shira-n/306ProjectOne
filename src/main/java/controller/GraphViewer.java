package controller;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;

/**
 *
 * This is the customised view class for the node graph. It is called to update the status of the node graph.
 * It is initialised and managed by Controller.
 * It initialise the viewer instance that contains the graph.
 * It customises the style and beahviour of the graph by extending Viewer
 * It updates the states and styles of the nodes, edges and sprites when called by Controller.
 *
 */
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

    /**
     * This is called when initialising the graph. It initialises all nodes to the state where they have not been allocated
     * to a processor and also sets the basic style of the nodes & edges.
     */
    public void initialiseLabels() {
        //initialises and stylise all the nodes in the graph, displays ID of each node
        for(Node node: _graph) {
            node.setAttribute("ui.label",
                     node.getId()+"");
            node.addAttribute("ui.style", "text-alignment: center;\n"
                    +"\tstroke-mode: plain; stroke-color:grey; stroke-width: 3px;"
                    + "\tfill-mode: plain; fill-color: rgb(255,255,255);\n"
                    + "\tsize: 30px, 30px;\n"
                    + "\ttext-size: 15px; text-color: black;\n");
        }
        //initialise and stylises all edges in the graph, displays communication cost of each edge
        for(int i =0; i< _graph.getEdgeCount(); i++) {
            Edge edge = _graph.getEdge(i);
            edge.addAttribute("ui.style", "fill-mode: plain; fill-color: grey;\n"
                    + "\ttext-size: 15px; text-color: white;\n"
                    + "\ttext-alignment: along;\n");
            edge.addAttribute("ui.label",edge.getAttribute("weight")+"");
        }

    }

    /**
     * Updates a specific node when allocated to a new processor.
     * Updates node to the colour corresponding to the processor it is allocated to.
     * Adds sprite to the node which displays the processor ID and start time of the node.
     *
     * Called by Controller #update().
     * @param node
     */
    public void updateNodeColour(Node node) {
        //get processor colour
        String processorColour = _colourMgr.getColor(Integer.parseInt(node.getAttribute("processor")));
        //update node colour
        node.removeAttribute("ui.style");
        node.addAttribute("ui.style", "text-alignment: center;\n"
                +"\tstroke-mode: plain; stroke-color: " + processorColour + "; stroke-width: 3px;"
                + "\tfill-mode: plain; fill-color: " + processorColour +";\n"
                + "\tsize: 30px, 30px;\n"
                + "\ttext-size: 20px; text-color: black;\n");

        //add sprite which displays extra info
        Sprite sprite = _mgr.addSprite("Node" + node.getId());
        sprite.addAttribute("ui.label", "P" + node.getAttribute("processor")
                + "   Start: " + node.getAttribute("startTime"));
        sprite.addAttribute("ui.style","text-alignment: center;\n"
                + "\ttext-background-mode: rounded-box;\n"
                + "\ttext-alignment: under;\n"
                + "\tfill-mode: plain; fill-color: rgba(0,0,0,0);\n"
                + "\tpadding: 2px\n;"
                + "\ttext-background-color: rgba(255,255,255,120);\n"
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

}
