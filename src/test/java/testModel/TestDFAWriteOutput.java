/*
package testModel;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.*;

import org.junit.Test;
import application.DotFileAdapter;
import model.Node;
import model.Processor;
public class TestDFAWriteOutput{

	private List<Node> _graph = new ArrayList<>();
	private Map<String, Node> _schedule = new HashMap<>();
	private DotFileAdapter _dfa;
	private Node n0,n1,n2,n3,n4,n5,n6;
	
	@Before
	public void setup() {
		// add input path to Nodes_7_OutTree
		try {
			_dfa = new DotFileAdapter("src/Nodes_7_OutTree.dot");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Processor p1 = new Processor(0);
		Processor p2 = new Processor(1);
		Processor p3 = new Processor(2);
		Processor p4 = new Processor(3);
				


		n0 = new Node(5, "0");
        n1 = new Node(6, "1");
        n0.addChild(n1, 15);
        n1.addParent(n0, 15);
        n0.setProcessor(p1);
        n1.setProcessor(p2);


        n2 = new Node(5, "2");
        n0.addChild(n2, 11);
        n2.addParent(n0, 11);
        n2.setProcessor(p3);

        n3 = new Node(6, "3");
        n0.addChild(n3, 11);
        n3.addParent(n0, 11);
        n3.setProcessor(p4);

        n4 = new Node(4, "4");
        n1.addChild(n4, 19);
        n4.addParent(n1, 19);
        n4.setProcessor(p1);

        n5 = new Node(7, "5");
        n1.addChild(n5, 4);
        n5.addParent(n1, 4);
        n5.setProcessor(p2);

        n6 = new Node(7, "6");
        n1.addChild(n6, 21);
        n6.addParent(n1, 21);
        n6.setProcessor(p3);

        _schedule.put("0", n0);
        _schedule.put("1", n1);
        _schedule.put("2", n2);
        _schedule.put("3", n3);
        _schedule.put("4", n4);
        _schedule.put("5", n5);
        _schedule.put("6", n6);
        
	}
	/**
	 * Manual Test for Tester to visually see that output prints correctly and has the expected values
	 *
	@Test
	public void simpleManualPrintTest() {
		try {
			_dfa.writeGreedySchedule(_schedule, "src/test/dotfiles/Nodes_7_OutTree_Output.dot");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file = new File("src/test/dotfiles/Nodes_7_OutTree_Output.dot");
		Scanner sc;
		try {
			sc = new Scanner(file);
			while(sc.hasNextLine()) {
				String Line = sc.nextLine();
				System.out.println(Line);
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// Delete Output File		
		if (file.delete()) {
			System.out.println("File Deleted");
		}
		else {
			System.out.println("File Not deleted");
		}
	}
	

	 * Tests that output file has the correct values and in the appropriate locations on each line
	 *
	@Test
	public void simpleTest() {
		try {
			_dfa.writeGreedySchedule(_schedule, "src/test/dotfiles/Nodes_7_OutTree_Output.dot");
			File file = new File("src/test/dotfiles/Nodes_7_OutTree_Output.dot");
			File originalFile = new File("src/test/dotfiles/Nodes_7_OutTree.dot");
			Scanner osc;
			Scanner sc;
			try {
				sc = new Scanner(file);
				osc = new Scanner(originalFile);
				while(sc.hasNextLine()) {
					String Line = sc.nextLine();
					String oLine = osc.nextLine();
					String numbersOnly = Line.replaceAll("\\D+","");
					char[] numbers = numbersOnly.toCharArray();
					assertEquals(n0, n0);
					
					if (Line.contains("Weight=") && !Line.contains("->")){
						String node = Line.split(" ")[0].trim();
						assertEquals(Character.toString(numbers[0]),_schedule.get(node).getId());
						assertEquals(Character.getNumericValue(numbers[1]),_schedule.get(node).getWeight());
						assertEquals(Character.getNumericValue(numbers[2]),_schedule.get(node).getStartTime());
						assertEquals(Character.getNumericValue(numbers[3]),_schedule.get(node).getProcessor().getID());

					}
					else {
						assertEquals(Line,oLine);
					}
				}
				sc.close();
				osc.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			// Delete Output File
			if (file.delete()) {
				System.out.println("File Deleted");
			}
			else {
				System.out.println("File Not deleted");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
*/