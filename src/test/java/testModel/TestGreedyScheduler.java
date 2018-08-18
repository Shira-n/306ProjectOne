/*
package testModel;

import model.scheduler.GreedyScheduler;
import model.Node;
import model.Processor;
import org.junit.*;

import java.util.*;

public class TestGreedyScheduler {

    private List<Node> _graph = new ArrayList<>();
    /*
    private List<Node> _simpleGraph = new ArrayList<>();
    private List<Node> _simpleGraph1 = new ArrayList<>();
    private List<Node> _simpleGraph2 = new ArrayList<>();
    private List<Node> _simpleGraph3 = new ArrayList<>();
    private List<Node> _simpleGraph4 = new ArrayList<>();
    *

    @Before
    public void initialise() {
        Node n0 = new Node(5, "0");
        Node n1 = new Node(6, "1");
        n0.addChild(n1, 15);
        n1.addParent(n0, 15);

        Node n2 = new Node(5, "2");
        n0.addChild(n2, 11);
        n2.addParent(n0, 11);

        Node n3 = new Node(6, "3");
        n0.addChild(n3, 11);
        n3.addParent(n0, 11);

        Node n4 = new Node(4, "4");
        n1.addChild(n4, 19);
        n4.addParent(n1, 19);

        Node n5 = new Node(7, "5");
        n1.addChild(n5, 4);
        n5.addParent(n1, 4);

        Node n6 = new Node(7, "6");
        n1.addChild(n6, 21);
        n6.addParent(n1, 21);

        _graph.add(n0);
        _graph.add(n1);
        _graph.add(n2);
        _graph.add(n3);
        _graph.add(n4);
        _graph.add(n5);
        _graph.add(n6);

        /*
        _simpleGraph.add(n0);

        _simpleGraph1.add(n0);
        _simpleGraph1.add(n1);

        _simpleGraph2.add(n0);
        _simpleGraph2.add(n1);
        _simpleGraph2.add(n4);

        _simpleGraph3.add(n0);
        _simpleGraph3.add(n1);
        _simpleGraph3.add(n2);

        _simpleGraph4.add(n0);
        _simpleGraph4.add(n1);
        _simpleGraph4.add(n2);
        _simpleGraph4.add(n3);
        *
    }

    @Test
    public void testScheduleNewOutputFormat(){
        GreedyScheduler greedyScheduler = new GreedyScheduler(_graph, 2);

        Map<String, Node> test = greedyScheduler.getScheduledNodes();

        for (String s : test.keySet()){
            System.out.println("Id: " + s + " is scheduled at P" + test.get(s).getProcessor().getID() + " at time " +
            test.get(s).getStartTime());
        }

        System.out.println("\nCompare\n");

        /*
        assertEquals(0, _simpleGraph.get(0).getStartTime());
        assertEquals(5, schedule.get(0).getCurrentAbleToStart());
        assertSame(schedule.get(0).getCurrentSchedule().size(), 1);
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_simpleGraph.get(0)));

        assertEquals(0, schedule.get(1).getCurrentAbleToStart());
        assertSame(schedule.get(1).getCurrentSchedule().size(), 0);
        assertTrue(schedule.get(1).getCurrentSchedule().values().isEmpty());
         *
    }

    @Test
    public void testSchedule(){
        GreedyScheduler greedyScheduler = new GreedyScheduler(_graph, 2);
        List<Processor> schedule = greedyScheduler.getSchedule();
        for (Processor p : schedule){
            System.out.println("Processor: " + p.getID());
            for (int i : p.getCurrentSchedule().keySet()) {
                System.out.println("At " + i + " scheduled Node" + p.getCurrentSchedule().get(i).getId());
            }
        }
    }

    /*
    @Test
    public void testScheduleTwoNodesWithDependency(){

        GreedyScheduler scheduler = new GreedyScheduler(_simpleGraph1, 2);
        scheduler.schedule();
        List<Processor> schedule = scheduler.getSchedule();
        assertEquals(0, _simpleGraph1.get(0).getStartTime());
        assertEquals(5 , _simpleGraph1.get(1).getStartTime());

        assertEquals(11, schedule.get(0).getCurrentAbleToStart());
        assertSame(2, schedule.get(0).getCurrentSchedule().size());
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_simpleGraph1.get(0)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_simpleGraph1.get(1)));

        assertEquals(0, schedule.get(1).getCurrentAbleToStart());
        assertSame(0, schedule.get(1).getCurrentSchedule().size());
        assertTrue(schedule.get(1).getCurrentSchedule().values().isEmpty());
    }

    @Test
    public void testScheduleThreeNodesWithDependency(){

        GreedyScheduler scheduler = new GreedyScheduler(_simpleGraph2, 2);
        scheduler.schedule();
        List<Processor> schedule = scheduler.getSchedule();
        assertEquals(0, _simpleGraph2.get(0).getStartTime());
        assertEquals(5 , _simpleGraph2.get(1).getStartTime());
        assertEquals(11 , _simpleGraph2.get(2).getStartTime());

        assertEquals(15, schedule.get(0).getCurrentAbleToStart());
        assertSame(3, schedule.get(0).getCurrentSchedule().size());
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_simpleGraph2.get(0)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_simpleGraph2.get(1)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_simpleGraph2.get(2)));

        assertEquals(0, schedule.get(1).getCurrentAbleToStart());
        assertSame(0, schedule.get(1).getCurrentSchedule().size());
        assertTrue(schedule.get(1).getCurrentSchedule().values().isEmpty());
    }

    @Test
    public void addParentTest() {
        try {
            GreedyScheduler s = new GreedyScheduler(_graph, 1);
            for (Node n : s.getGraph()){
                System.out.println(n.getId());
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            fail("You did something wrong! Check error msg!");
        }
    }

    public void testScheduleThreeNodesWithOneParent(){

        GreedyScheduler scheduler = new GreedyScheduler(_simpleGraph3, 2);
        scheduler.schedule();
        List<Processor> schedule = scheduler.getSchedule();
        assertEquals(0, _simpleGraph3.get(0).getStartTime());
        assertEquals(5 , _simpleGraph3.get(1).getStartTime());
        assertEquals(11 , _simpleGraph3.get(2).getStartTime());

        assertEquals(16, schedule.get(0).getCurrentAbleToStart());
        assertSame(3, schedule.get(0).getCurrentSchedule().size());
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_simpleGraph3.get(0)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_simpleGraph3.get(1)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_simpleGraph3.get(2)));

        assertEquals(0, schedule.get(1).getCurrentAbleToStart());
        assertSame(0, schedule.get(1).getCurrentSchedule().size());
        assertTrue(schedule.get(1).getCurrentSchedule().values().isEmpty());
    }

    @Test
    public void testScheduleFourNodesWithOneParent(){

        GreedyScheduler scheduler = new GreedyScheduler(_simpleGraph4, 2);
        scheduler.schedule();
        List<Processor> schedule = scheduler.getSchedule();
        assertEquals(0, _simpleGraph4.get(0).getStartTime());
        assertEquals(5 , _simpleGraph4.get(1).getStartTime());
        assertEquals(11 , _simpleGraph4.get(2).getStartTime());
        assertEquals(15 , _simpleGraph4.get(3).getStartTime());

        assertEquals(16, schedule.get(0).getCurrentAbleToStart());
        assertSame(3, schedule.get(0).getCurrentSchedule().size());
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_simpleGraph4.get(0)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_simpleGraph4.get(1)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_simpleGraph4.get(2)));

        assertEquals(21, schedule.get(1).getCurrentAbleToStart());
        assertSame(1, schedule.get(1).getCurrentSchedule().size());
        assertTrue(schedule.get(1).getCurrentSchedule().values().contains(_simpleGraph4.get(3)));
    }

    @Test
    public void testScheduleFullNodes7OutTree(){

        GreedyScheduler scheduler = new GreedyScheduler(_graph, 2);
        scheduler.schedule();
        List<Processor> schedule = scheduler.getSchedule();
        assertEquals(0, _graph.get(0).getStartTime());
        assertEquals(5 , _graph.get(1).getStartTime());
        assertEquals(11 , _graph.get(2).getStartTime());
        assertEquals(15 , _graph.get(3).getStartTime());
        assertEquals(16 , _graph.get(4).getStartTime());
        assertEquals(20 , _graph.get(5).getStartTime());
        assertEquals(27 , _graph.get(6).getStartTime());

        assertEquals(34, schedule.get(0).getCurrentAbleToStart());
        assertSame(6, schedule.get(0).getCurrentSchedule().size());
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(0)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(1)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(2)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(4)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(5)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(6)));

        assertEquals(21, schedule.get(1).getCurrentAbleToStart());
        assertSame(1, schedule.get(1).getCurrentSchedule().size());
        assertTrue(schedule.get(1).getCurrentSchedule().values().contains(_graph.get(3)));
    }

    @Test
    public void testSchedule4ProcessorsFullNodes7OutTree(){

        GreedyScheduler scheduler = new GreedyScheduler(_graph, 4);
        scheduler.schedule();
        List<Processor> schedule = scheduler.getSchedule();
        assertEquals(0, _graph.get(0).getStartTime());
        assertEquals(5 , _graph.get(1).getStartTime());
        assertEquals(11 , _graph.get(2).getStartTime());
        assertEquals(15 , _graph.get(3).getStartTime());
        assertEquals(16 , _graph.get(4).getStartTime());
        assertEquals(15 , _graph.get(5).getStartTime());
        assertEquals(20 , _graph.get(6).getStartTime());

        assertEquals(27, schedule.get(0).getCurrentAbleToStart());
        assertSame(5, schedule.get(0).getCurrentSchedule().size());
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(0)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(1)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(2)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(4)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(6)));

        assertEquals(21, schedule.get(1).getCurrentAbleToStart());
        assertSame(1, schedule.get(1).getCurrentSchedule().size());
        assertTrue(schedule.get(1).getCurrentSchedule().values().contains(_graph.get(3)));

        assertEquals(22, schedule.get(2).getCurrentAbleToStart());
        assertSame(1, schedule.get(2).getCurrentSchedule().size());
        assertTrue(schedule.get(2).getCurrentSchedule().values().contains(_graph.get(5)));
    }


}
*/