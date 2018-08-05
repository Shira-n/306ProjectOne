package testModel;

import model.Node;
import model.Processor;
import model.Scheduler;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestSchedulerNode11 {
    private List<Node> _graph = new ArrayList<>();


    @Before
    public void initialise() {
        Node n0 = new Node(50, "0");
        Node n1 = new Node(70, "1");
        n0.addChild(n1, 9);
        n1.addParent(n0, 9);

        Node n2 = new Node(90, "2");
        n0.addChild(n2, 7);
        n2.addParent(n0, 7);

        Node n3 = new Node(100, "3");
        n0.addChild(n3, 4);
        n3.addParent(n0, 4);

        Node n4 = new Node(40, "4");
        n1.addChild(n4, 10);
        n4.addParent(n1, 10);

        Node n5 = new Node(20, "5");
        n1.addChild(n5, 7);
        n5.addParent(n1, 7);

        Node n6 = new Node(100, "6");
        n1.addChild(n6, 5);
        n6.addParent(n1, 5);

        Node n7 = new Node(80, "7");
        n2.addChild(n7, 5);
        n7.addParent(n2, 5);

        Node n8  = new Node(50, "8");
        n2.addChild(n8, 3);
        n8.addParent(n2, 3);

        Node n9  = new Node(20, "9");
        n2.addChild(n9, 10);
        n9.addParent(n2, 10);

        Node n10  = new Node(20, "10");
        n3.addChild(n10, 4);
        n10.addParent(n3, 4);

        _graph.add(n0);
        _graph.add(n1);
        _graph.add(n2);
        _graph.add(n3);
        _graph.add(n4);
        _graph.add(n5);
        _graph.add(n6);
        _graph.add(n7);
        _graph.add(n8);
        _graph.add(n9);
        _graph.add(n10);
    }
/*
    @Test
    public void testOneProcessor() {
        System.out.println("\nOneProcessor");
        Scheduler scheduler = new Scheduler(_graph, 1);
        List<Processor> schedule = scheduler.getSchedule();
        printSchedule(schedule);
    }
*/
    @Test
    public void testTwoProcessor() {
        System.out.println("\nTwoProcessors");
        Scheduler scheduler = new Scheduler(_graph, 2);
        List<Processor> schedule = scheduler.getSchedule();
        printSchedule(schedule);
    }
/*
    @Test
    public void testThreeProcessor() {
        System.out.println("\nThreeProcessors");
        Scheduler scheduler = new Scheduler(_graph, 3);
        List<Processor> schedule = scheduler.getSchedule();
        printSchedule(schedule);
    }
*/
    private void printSchedule(List<Processor> schedule){
        for (Processor p : schedule){
            System.out.println("Processor: " + p.getID());
            for (int i : p.getCurrentSchedule().keySet()) {
                System.out.println(i + " scheduled Node" + p.getCurrentSchedule().get(i).getId());
            }
        }
    }

}
