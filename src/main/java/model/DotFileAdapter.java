package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class  DotFileAdapter {
	private String[] _words;
	private File _inputFile;

	public DotFileAdapter(String inputPath){
		_inputFile = new File(inputPath);
	}

	/*
		File Input
	 */
	/**
	 * Method that scans through a dot file, retrieves the relevant info and places it in a list of nodes for retrieval
	 * from main
	 */
	private List<Node> readGraph() throws FileNotFoundException{
		List<Node> data = new ArrayList<>();
		Scanner sc = new Scanner(_inputFile);

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

					for (Node e: data) {
						if (e.getId().equals(parentID)) {
							parent = e;
						}
						else if (e.getId().equals(childID)) {
							child = e;
						}
					}

					//@ ASSUMES NODES GIVEN BEFORE EDGES
					try {
						int parentIndex = data.indexOf(parent);
						int childIndex = data.indexOf(child);

						data.get(parentIndex).addChild(data.get(childIndex), edgeWeight);

						data.get(childIndex).addParent(data.get(parentIndex), edgeWeight);
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

					data.add(e);
				}
			}
		}
		sc.close();
		return  data;
	}

	private Map<String, Node> readMap() throws FileNotFoundException {
		Map<String, Node> map = new HashMap<>();
		Scanner sc = new Scanner(_inputFile);
		String read, parent, child, weight;
		while (sc.hasNextLine()) {
			read = sc.nextLine();
			if (read.toLowerCase().contains("weight=")) {
				if (read.toLowerCase().contains("->")) {
					//Edges
					parent = read.split("->")[0].trim();
					child = read.split("->")[1].trim().split("\\[")[0].trim();
					weight = read.split("=")[1].split("]")[0].trim();
					map.get(parent).addChild(map.get(child), Integer.parseInt(weight));
					map.get(child).addParent(map.get(parent), Integer.parseInt(weight));
				}else{
					//Nodes
					parent = read.split("\\[")[0].trim();
					weight = read.split("=")[1].split("]")[0].trim();
					map.put(parent, new Node(Integer.parseInt(weight), parent));
				}
			}
		}
		sc.close();
		return  map;
	}

	public List<Node> getData() throws FileNotFoundException {
		return readGraph();
	}

	public Map<String, Node> getMap() throws FileNotFoundException {
		return readMap();
	}


	/*
		File Output
	 */
	public void writeOptimalSchedule(AbstractState optimalState, String outputPath) throws IOException {
		File file = new File(outputPath);
		FileWriter fw = new FileWriter(file);
		Scanner sc = new Scanner(_inputFile);
		String processLine, node, firstHalf, start, processor;
		Map<String, String[]> translation = optimalState.translate();

		while (sc.hasNext()){
			processLine = sc.nextLine();
			if (processLine.contains("Weight=") && !processLine.contains("->")){
				node = processLine.split("\\[")[0].trim();
				firstHalf = processLine.split("]")[0] + ",";
				processor =  "Processor=" + translation.get(node)[0] + "];";
				start = "Start=" + translation.get(node)[1] + ",";
				processLine = firstHalf + start + processor;
			}
			fw.write(processLine + "\n");
			fw.flush();
		}
		fw.close();
		sc.close();
	}

	/**
	 * Used in Basic milestone
	 */
	public void writeGreedySchedule(Map<String, Node> scheduledNodes, String outputPath) throws IOException{
		File file = new File(outputPath);
		FileWriter fw = new FileWriter(file);
		Scanner sc = new Scanner(_inputFile);
		String processLine, node, weight, start, processor;
		while (sc.hasNext()){
			processLine = sc.nextLine();
			if (processLine.contains("Weight=") && !processLine.contains("->")){
				node = processLine.split(" ")[0].trim();
				weight = " [Weight=" + scheduledNodes.get(node).getWeight() + ",";
				start = "Start=" + scheduledNodes.get(node).getStartTime() + ",";
				processor =  "Processor=" + scheduledNodes.get(node).getProcessor().getID() + "];";
				processLine = processLine.split(" ")[0] + weight + start + processor;
			}
			fw.write(processLine + "\n");
			fw.flush();
		}
		fw.close();
		sc.close();
	}
}
