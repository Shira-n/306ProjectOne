package testModel;

import model.Node;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.*;

public class NodeTest {

	private Node _baseNode;

	/* TO DO @Josh */
	@Before
	public void initialise() {
		_baseNode = new Node(1, "0");
	}

	/** 
	 * Tests getWeight() method returns the correct value 
	 */
	@Test
	public void getWeightTest() {
		assertEquals(_baseNode.getWeight(),1);
	}

	/**
	 *  Tests that addParent() successfully adds a parent to a Node
	 */
	@Test
	public void addParentTest() {
		/* Makes a node and uses addParent() method to add it as a parent to _baseNode */
		Node Parent = new Node(1,"0");
		try {
			_baseNode.addParent(Parent, 1);
			assertTrue(_baseNode.isParent(Parent)); 
		}
		catch(Exception ex) {
			fail("Should add 'Parent' as parent node to _baseNode");
		}
	}

	/**
	 *  Tests that addChild() successfully adds a parent to a Node
	 */
	@Test
	public void addChildTest() {
		/* Makes a node and uses addChild() method to add it as a child to _baseNode */
		Node Child = new Node(1, "0");
		try {
			_baseNode.addChild(Child, 1);
			assertTrue(_baseNode.isChild(Child)); 
		}
		catch(Exception ex) {
			fail("Should add 'Child' as child node to _baseNode");
		}
	}

	@Test
	public void getPathWeightToChildTest() {
		Node Child = new Node(5, "x");
		Node Child2 = new Node(6, "y");
		_baseNode.addChild(Child, 4);
		assertTrue(_baseNode.getPathWeightToChild(Child) == 4);
		assertTrue(_baseNode.getPathWeightToChild(Child2) == -1);
	}

	@Test
	public void returnIDTest() {
		Node node = new Node(4, "xyz");
		assertTrue(node.getId().equals("xyz"));
	}

	@Test
	public void returnStartTimeTest() {
		Node node = new Node(4, "xyz");
		node.setStartTime(4);
		assertTrue(node.getStartTime() == 4);
	}

	@Test
	public void returnParentsTest() {
		Node node = new Node(4, "x");
		Node node2 = new Node(4, "y");
		Node node3 = new Node(2, "z");
		Node node4 = new Node(11, "abc");
		node4.addChild(node, 2);
		node4.addChild(node2, 6);
		node4.addChild(node3, 5);
		assertTrue(node4.getChildren().size() == 3);
		assertTrue(node4.isChild(node));
		assertTrue(node4.isChild(node2));
		assertTrue(node4.isChild(node3));
	} 
	
	@Test
	public void returnChildrenTest() {
		Node node = new Node(4, "x");
		Node node2 = new Node(4, "y");
		Node node3 = new Node(2, "z");
		Node node4 = new Node(11, "abc");
		Node node5 = new Node(11, "123");
		node3.addParent(node5, 17);
		node2.addParent(node5, 13);
		node2.addParent(node4, 8);
		node5.addParent(node, 4);
		node4.addParent(node, 1);
		assertTrue(node2.getParents().size() == 2);
		assertTrue(node2.isParent(node5));
		assertTrue(node2.isParent(node4));
		assertTrue(node5.isParent(node));
	} 
	
	@Test
	public void sortedParentsTests() {
		Node node = new Node(2, "xyz");
		Node node2 = new Node(2, "abc");
		Node node3 = new Node(2, "123");
		node.addParent(node2, 1);
		node.addParent(node3, 4);
		node.sortOneParent();
		assertFalse(node.parentsSorted());
		node.sortOneParent();
		assertTrue(node.parentsSorted());
	} 

}
