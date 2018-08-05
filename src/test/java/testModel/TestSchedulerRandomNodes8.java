package testModel;

import model.Node;
import model.Processor;
import model.Scheduler;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TestSchedulerRandomNodes8 {

    private List<Node> _graph = new ArrayList<>();

    @Before
    public void initialise() {
        Node n0 = new Node(35,"0");
        Node n1 = new Node(88,"1");
        n0.addChild(n1, 3);
        n1.addParent(n0, 3);

        Node n2 = new Node(176, "2");
        n0.addChild(n2, 9);
        n2.addParent(n0, 9);

        Node n3 = new Node(159, "3");
        n0.addChild(n3, 7);
        n3.addParent(n0, 7);

        Node n4 = new Node(176, "4");
        n0.addChild(n4, 5);
        n4.addParent(n0, 5);

        Node n6 = new Node(141, "6");
        n0.addChild(n6, 4);
        n6.addParent(n0, 4);

        Node n7 = new Node(53, "7");
        n0.addChild(n7, 9);
        n7.addParent(n0, 9);

        n1.addChild(n4, 10);
        n4.addParent(n1, 10);

        n1.addChild(n7, 6);
        n7.addParent(n1, 6);

        n2.addChild(n4, 8);
        n4.addParent(n2, 8);

        Node n5 = new Node(141, "5");
        n2.addChild(n5, 6);
        n5.addParent(n2, 6);

        n2.addChild(n7, 3);
        n7.addParent(n2, 3);

        n3.addChild(n5, 5);
        n5.addParent(n3, 5);

        n3.addChild(n6, 8);
        n6.addParent(n3, 8);

        n4.addChild(n6, 2);
        n6.addParent(n4, 2);

        n5.addChild(n7, 4);
        n7.addParent(n5, 4);

        n6.addChild(n7, 8);
        n7.addParent(n6, 8);

        _graph.add(n0);
        _graph.add(n1);
        _graph.add(n2);
        _graph.add(n3);
        _graph.add(n4);
        _graph.add(n5);
        _graph.add(n6);
        _graph.add(n7);

    }

    @Test
    public void testOneProcessor(){
        System.out.println("\nOne Processor");

        Scheduler scheduler = new Scheduler(_graph, 1);
        List<Processor> schedule = scheduler.getSchedule();
        printSchedule(schedule);
        /*
        assertEquals(0, _graph.get(0).getStartTime());
        assertEquals(35 , _graph.get(1).getStartTime());
        assertEquals(123 , _graph.get(2).getStartTime());
        assertEquals(299 , _graph.get(3).getStartTime());
        assertEquals(458 , _graph.get(4).getStartTime());
        assertEquals(634 , _graph.get(5).getStartTime());
        assertEquals(775 , _graph.get(6).getStartTime());
        assertEquals(916 , _graph.get(7).getStartTime());

        assertEquals(969, schedule.get(0).getCurrentAbleToStart());
        assertSame(8, schedule.get(0).getCurrentSchedule().size());
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(0)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(1)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(2)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(3)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(4)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(5)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(6)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(7)));
        */
    }

    @Test
    public void testTwoProcessor(){
        System.out.println("\nTwo Processor");

        Scheduler scheduler = new Scheduler(_graph, 2);
        List<Processor> schedule = scheduler.getSchedule();
        printSchedule(schedule);
        /*
        assertEquals(0, _graph.get(0).getStartTime());
        assertEquals(35 , _graph.get(1).getStartTime());
        assertEquals(44 , _graph.get(2).getStartTime());
        assertEquals(123 , _graph.get(3).getStartTime());
        assertEquals(220 , _graph.get(4).getStartTime());
        assertEquals(282 , _graph.get(5).getStartTime());
        assertEquals(396 , _graph.get(6).getStartTime());
        assertEquals(537 , _graph.get(7).getStartTime());

        assertEquals(423, schedule.get(0).getCurrentAbleToStart());
        assertSame(4, schedule.get(0).getCurrentSchedule().size());

        assertEquals(590, schedule.get(1).getCurrentAbleToStart());
        assertSame(4, schedule.get(1).getCurrentSchedule().size());

        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(0)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(1)));
        assertTrue(schedule.get(1).getCurrentSchedule().values().contains(_graph.get(2)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(3)));
        assertTrue(schedule.get(1).getCurrentSchedule().values().contains(_graph.get(4)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(5)));
        assertTrue(schedule.get(1).getCurrentSchedule().values().contains(_graph.get(6)));
        assertTrue(schedule.get(1).getCurrentSchedule().values().contains(_graph.get(7)));

       */

    }

    @Test
    public void testThreeProcessor(){
        System.out.println("\nThree Processor");

        Scheduler scheduler = new Scheduler(_graph, 3);
        List<Processor> schedule = scheduler.getSchedule();
        printSchedule(schedule);
        /*
        assertEquals(0, _graph.get(0).getStartTime());
        assertEquals(35 , _graph.get(1).getStartTime());
        assertEquals(44 , _graph.get(2).getStartTime());
        assertEquals(42 , _graph.get(3).getStartTime());
        assertEquals(220 , _graph.get(4).getStartTime());
        assertEquals(226 , _graph.get(5).getStartTime());
        assertEquals(396 , _graph.get(6).getStartTime());
        assertEquals(537 , _graph.get(7).getStartTime());

        assertEquals(367, schedule.get(0).getCurrentAbleToStart());
        assertSame(3, schedule.get(0).getCurrentSchedule().size());

        assertEquals(590, schedule.get(1).getCurrentAbleToStart());
        assertSame(4, schedule.get(1).getCurrentSchedule().size());

        assertEquals(201, schedule.get(2).getCurrentAbleToStart());
        assertSame(1, schedule.get(2).getCurrentSchedule().size());

        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(0)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(1)));
        assertTrue(schedule.get(1).getCurrentSchedule().values().contains(_graph.get(2)));
        assertTrue(schedule.get(2).getCurrentSchedule().values().contains(_graph.get(3)));
        assertTrue(schedule.get(1).getCurrentSchedule().values().contains(_graph.get(4)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(5)));
        assertTrue(schedule.get(1).getCurrentSchedule().values().contains(_graph.get(6)));
        assertTrue(schedule.get(1).getCurrentSchedule().values().contains(_graph.get(7)));
        */

    }

    @Test
    public void testFourProcessor(){
        System.out.println("\nFourProcessor");
        Scheduler scheduler = new Scheduler(_graph, 4);
        List<Processor> schedule = scheduler.getSchedule();
        printSchedule(schedule);
        /*
        assertEquals(0, _graph.get(0).getStartTime());
        assertEquals(35 , _graph.get(1).getStartTime());
        assertEquals(44 , _graph.get(2).getStartTime());
        assertEquals(42 , _graph.get(3).getStartTime());

        assertEquals(28 , _graph.get(4).getStartTime());
        assertEquals(31 , _graph.get(5).getStartTime());
        assertEquals(35 , _graph.get(6).getStartTime());
        assertEquals(92 , _graph.get(7).getStartTime());

        assertEquals(108, schedule.get(0).getCurrentAbleToStart());
        assertSame(8, schedule.get(0).getCurrentSchedule().size());

        assertEquals(33, schedule.get(1).getCurrentAbleToStart());
        assertSame(1, schedule.get(1).getCurrentSchedule().size());

        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(0)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(1)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(2)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(3)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(4)));
        assertTrue(schedule.get(1).getCurrentSchedule().values().contains(_graph.get(5)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(6)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(7)));
        */
    }

    private void printSchedule(List<Processor> schedule){
        for (Processor p : schedule){
            System.out.println("Processor: " + p.getID());
            for (int i : p.getCurrentSchedule().keySet()) {
                System.out.println(i + " scheduled Node" + p.getCurrentSchedule().get(i).getId());
            }
        }
    }
}
