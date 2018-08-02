package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
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

					String str2 = _words[1].toString();
					String nodeNumParent = str2.replaceAll("[^0-9]", "");
					int parentNum = Integer.parseInt(nodeNumParent);

					String str3 = _words[3].toString();
					String nodeNumChild = str3.replaceAll("[^0-9]", "");
					int childNum = Integer.parseInt(nodeNumChild);

					_data.get(parentNum).addChild(_data.get(childNum), edgeWeight);

					_data.get(childNum).addParent(_data.get(parentNum), edgeWeight);
				}

//				if not a vertex, must be a node
				else {


					String str = _words[2].toString();
					String numberOnly = str.replaceAll("[^0-9]", "");
					int weight = Integer.parseInt(numberOnly);

					Node e = new Node(numberOnly, weight);

					String str2 = _words[1].toString();
					String nodeNum = str2.replaceAll("[^0-9]", "");
					int nodePlace = Integer.parseInt(nodeNum);

					_data.add(nodePlace , e);
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
