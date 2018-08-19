package model.scheduler;

import controller.Controller;
import model.state.AbstractState;
import model.Node;
import model.Processor;

import java.util.Set;

public abstract class AbstractScheduler {

    protected Controller _controller;

    public abstract AbstractState getSchedule();

    public void setController(Controller controller){
        _controller = controller;
    }

    /*
        Schedule related
     */
    protected void unscheduleNode(Node node){
        if (node.getProcessor() != null) {
            node.getProcessor().removeNodeAt(node.getStartTime());
        }
        node.unSchedule();
    }

    /**
     * Calculate the earliest start time of the input Node on the input Processor, only considering the schedule
     * of the input Node's parents.
     */
    protected int influencedByParents(Processor target, Node n) {
        int limit = 0;
        for (Node parent : n.getParents().keySet()) {
            if (parent.getProcessor().getID() == target.getID()) {
                limit = Math.max(limit, parent.getStartTime() + parent.getWeight());
            } else {
                limit = Math.max(limit, parent.getStartTime() + parent.getWeight() + n.getPathWeightToParent(parent));
            }
        }
        return limit;
    }


    /*
        Pruning related
     */
    /**
     * Recursively calculate Bottom Weight of the input Node. The bottom weight of a Node will be the sum of its
     * weight and the maximum bottom weight of its children.
     */
    protected int calcBottomWeight(Node node){
        if (node.getChildren().size() > 0){
            int maxChileBtmWeight = 0;
            for (Node child: node.getChildren().keySet()){
                maxChileBtmWeight = Math.max(maxChileBtmWeight,calcBottomWeight(child));
            }
            node.setBottomWeight(maxChileBtmWeight + node.getWeight());
        }else{
            node.setBottomWeight(node.getWeight());
        }
        return node.getBottomWeight();
    }

    /**
     * Return true if two nodes are exchangable, false otherwise.
     */
    protected boolean internalOrderingCheck(Node node, Node visited){
        if (node.getWeight() != visited.getWeight()){
            return false;
        }
        //The number of parents and children of them must be the same
        if (node.getParents().keySet().size() != visited.getParents().keySet().size()
                || node.getChildren().keySet().size() != visited.getChildren().keySet().size()){
            return false;
        }

        for (Node parent : node.getParents().keySet()){
            if (visited.getParents().keySet().contains(parent) &&
                    node.getPathWeightToParent(parent) == visited.getPathWeightToParent(parent)){
                ;
            }else{ //The communication cost for every parent has to be the same for both nodes
                return false;
            }
        }

        for (Node child : node.getChildren().keySet()){
            if (visited.getChildren().keySet().contains(child) &&
                    node.getPathWeightToChild(child) != visited.getPathWeightToParent(child)){
                ;
            }else{ //The communication cost for every child has to be the same for both nodes
                return false;
            }
        }
        return true;
    }

    protected boolean equivalentNode(Node node, Set<Node> uniqueNodes){
        for (Node uniqueNode : uniqueNodes){
            if (uniqueNode.isEquivalent(node)){
                return true;
            }
        }
        return false;
    }

    protected boolean equivalentProcessor(Processor processor, Set<Processor> uniqueProcessors, Node node, int startTime){
        int anotherStartTime;
        for (Processor uniqueProcessor : uniqueProcessors){
            //Calculate the earliest Start time of this Node on this Processor.
            anotherStartTime = Math.max(uniqueProcessor.getCurrentAbleToStart(), influencedByParents(uniqueProcessor, node));
            if (anotherStartTime == startTime && processor.toString().equals(uniqueProcessor.toString())){
                return true;
            }
        }
        return false;
    }
}
