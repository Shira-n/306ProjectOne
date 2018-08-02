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

public class TestSchedulerSeriesParallel {

    private List<Node> _graph = new ArrayList<>();

    @Before
    public void initialise() {
        Node n0 = new Node(10);
        Node n2 = new Node(6);
        n0.addChild(n2, 51);
        n2.addParent(n0, 51);

        Node n3 = new Node(7);
        n0.addChild(n3, 22);
        n3.addParent(n0, 22);

        Node n4 = new Node(5);
        n0.addChild(n4, 44);
        n4.addParent(n0, 44);

        Node n1 = new Node(7);

        Node n6 = new Node(2);
        n2.addChild(n6, 59);
        n6.addParent(n2, 59);

        Node n7 = new Node(2);
        n2.addChild(n7, 15);
        n7.addParent(n2, 15);

        Node n8 = new Node(7);
        n2.addChild(n8, 59);
        n8.addParent(n2, 59);

        n3.addChild(n1, 59);
        n1.addParent(n3, 59);

        n4.addChild(n1, 66);
        n1.addParent(n4, 66);

        Node n5 = new Node(9);
        n5.addChild(n1, 37);
        n1.addParent(n5, 37);

        n6.addChild(n5, 22);
        n5.addParent(n6, 22);

        n7.addChild(n5, 59);
        n5.addParent(n7, 59);

        n8.addChild(n5, 59);
        n5.addParent(n8, 59);

        _graph.add(n0);
        _graph.add(n2);
        _graph.add(n3);
        _graph.add(n4);
        _graph.add(n8);
        _graph.add(n7);
        _graph.add(n6);
        _graph.add(n5);
        _graph.add(n1);

    }

    @Test
    public void testOneProcessor(){

        Scheduler scheduler = new Scheduler(_graph, 1);
        scheduler.schedule();
        List<Processor> schedule = scheduler.getSchedule();
        assertEquals(0, _graph.get(0).getStartTime());
        assertEquals(10 , _graph.get(1).getStartTime());
        assertEquals(16 , _graph.get(2).getStartTime());
        assertEquals(23 , _graph.get(3).getStartTime());
        assertEquals(28 , _graph.get(4).getStartTime());
        assertEquals(35 , _graph.get(5).getStartTime());
        assertEquals(37 , _graph.get(6).getStartTime());
        assertEquals(39 , _graph.get(7).getStartTime());
        assertEquals(48 , _graph.get(8).getStartTime());

        assertEquals(55, schedule.get(0).getCurrentAbleToStart());
        assertSame(9, schedule.get(0).getCurrentSchedule().size());
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(0)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(1)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(2)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(3)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(4)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(5)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(6)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(7)));
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(8)));

    }

    @Test
    public void testTwoProcessor(){

        Scheduler scheduler = new Scheduler(_graph, 2);
        scheduler.schedule();
        List<Processor> schedule = scheduler.getSchedule();
        assertEquals(0, _graph.get(0).getStartTime());
        assertEquals(10 , _graph.get(1).getStartTime());
        assertEquals(16 , _graph.get(2).getStartTime());
        assertEquals(23 , _graph.get(3).getStartTime());
        assertEquals(28 , _graph.get(4).getStartTime());
        assertEquals(31 , _graph.get(5).getStartTime());
        assertEquals(35 , _graph.get(6).getStartTime());
        assertEquals(92 , _graph.get(7).getStartTime());
        assertEquals(101 , _graph.get(8).getStartTime());

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
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(8)));

    }

    @Test
    public void testThreeProcessor(){

        Scheduler scheduler = new Scheduler(_graph, 3);
        scheduler.schedule();
        List<Processor> schedule = scheduler.getSchedule();
        assertEquals(0, _graph.get(0).getStartTime());
        assertEquals(10 , _graph.get(1).getStartTime());
        assertEquals(16 , _graph.get(2).getStartTime());
        assertEquals(23 , _graph.get(3).getStartTime());
        assertEquals(28 , _graph.get(4).getStartTime());
        assertEquals(31 , _graph.get(5).getStartTime());
        assertEquals(35 , _graph.get(6).getStartTime());
        assertEquals(92 , _graph.get(7).getStartTime());
        assertEquals(101 , _graph.get(8).getStartTime());

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
        assertTrue(schedule.get(0).getCurrentSchedule().values().contains(_graph.get(8)));

    }
}
