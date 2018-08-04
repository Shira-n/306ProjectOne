package testModel;

import model.Node;
import model.Scheduler;
import org.junit.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestScheduler {

    private List<Node> _graph = new ArrayList<>();
    @Before
    public void initialise() {
        Node n0 = new Node("0", 5);
        Node n1 = new Node("1", 6);
        n0.addChild(n1, 15);
        n1.addParent(n0, 15);

        Node n2 = new Node("2", 5);
        n0.addChild(n2, 11);
        n2.addParent(n0, 11);

        Node n3 = new Node("3", 6);
        n0.addChild(n3, 11);
        n3.addParent(n0, 11);

        Node n4 = new Node("4", 4);
        n1.addChild(n4, 19);
        n4.addParent(n1, 19);

        Node n5 = new Node("5", 7);
        n1.addChild(n5, 4);
        n5.addParent(n1, 4);

        Node n6 = new Node("6", 7);
        n1.addChild(n6, 21);
        n6.addParent(n1, 21);

        _graph.add(n0);
        _graph.add(n1);
        _graph.add(n2);
        _graph.add(n3);
        _graph.add(n4);
        _graph.add(n5);
        _graph.add(n6);
    }

    @Test
    public void addParentTest() {
        try {
            Scheduler s = new Scheduler(_graph, 1);
            for (Node n : s.getGraph()){
                System.out.println(n.getId());
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            fail("You did something wrong! Check error msg!");
        }
    }

}
