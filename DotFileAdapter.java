package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class  DotFileAdapter {
	  private List<Node> _data = new ArrayList<Node>();
	  private String[] _words;

    public DotFileAdapter(String inputPath) throws FileNotFoundException {
        readGraph(inputPath);
    }

	/**
	 * Method that scans through a dot file, retrieves the relevant info and places it in a list of nodes for retrieval from main
	 */
	private void readGraph(String inputPath) throws FileNotFoundException{
		File file = new File(inputPath);
		Scanner sc = new Scanner(file);

//		Scans Dot File, converts each line to a String Array to add to _data
		while (sc.hasNextLine()) {
			String string = sc.nextLine(); 
			_words = string.split("\\s+");

			//			Removes all lines that aren't a node or vertex
			if (string.toLowerCase().contains("weight=")) {		
//				Checks if the line is a vertex
				if (string.toLowerCase().contains("->")) {

//					All of these similar blocks break the string into an array and remove all non-number characters and retrieve number of node and the weight of itself or the two nodes edge
					String str = _words[4].toString();
					String numberOnly = str.replaceAll("[^0-9]", "");
					int edgeWeight = Integer.parseInt(numberOnly);

					String parentID = _words[1].toString();
					String childID = _words[3].toString();
					
					Node child = null;
					Node parent = null;
					
					for (Node e: _data) {
						if (e.getId().equals(parentID)) {
							parent = e;
						}
						else if (e.getId().equals(childID)) {
							child = e;
						}
					}
					
					//@ ASSUMES NODES GIVEN BEFORE EDGES
					try {
						int parentIndex = _data.indexOf(parent);
						int childIndex = _data.indexOf(child);
						
						_data.get(parentIndex).addChild(_data.get(childIndex), edgeWeight);
	
						_data.get(childIndex).addParent(_data.get(parentIndex), edgeWeight);
					}
					catch (Exception e) {
						;
					}
				}

//				if not a vertex, must be a node
				else {


					String str = _words[2].toString();
					String numberOnly = str.replaceAll("[^0-9]", "");
					int weight = Integer.parseInt(numberOnly);

					

					String nodeID = _words[1].toString();

					Node e = new Node(weight, nodeID);
					
					_data.add(e);
				}
			}
		}
		sc.close();
	}

    public void writeSchedule(List<Processor> schedule, String outputPath){
        //TODO Haven't decided on the output data structure.
        //I was thinking about passing List<Processor> from scheduler. Can leave this for now
    	
    }

    public List<Node> getData(){
        return _data;
    }

}
