package testModel;
/*
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.*;

import org.junit.Test;
import model.DotFileAdapter;
import model.Node;
import model.Processor;
public class TestDFAWriteOutput{

	private List<Node> _graph = new ArrayList<>();
	private List<Processor> _schedule = new ArrayList<>();
	
	@Before
	public void setup() {
		// add input path to Nodes_7_OutTree
		//DotFileAdapter _dfa = new DotFileAdapter();
		
		_schedule.add(new Processor(1));
		_schedule.add(new Processor(2));
		_schedule.add(new Processor(3));
		_schedule.add(new Processor(4));
		
		
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
        
        _schedule.get(1).addNode(n0.getStartTime(), n0);
        _schedule.get(1).addNode(n1.getStartTime(), n1);
        _schedule.get(2).addNode(n2.getStartTime(), n2);
        _schedule.get(3).addNode(n3.getStartTime(), n3);
        _schedule.get(4).addNode(n4.getStartTime(), n4);
        _schedule.get(2).addNode(n5.getStartTime(), n5);
        _schedule.get(3).addNode(n6.getStartTime(), n6);
	}
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
*/