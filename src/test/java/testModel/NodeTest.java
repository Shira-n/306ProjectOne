package testModel;

import model.Node;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
	
	//@TODO @Josh
	public void isChildTest() {};
	public void isParentTest() {};
	public void getPathWeightToChildTest() {};
	

}
