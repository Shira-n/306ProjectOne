package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.CellEditor;

import java.util.HashMap;
import java.util.Map;

public class  DotFileAdapter {
	private List<Node> _data = new ArrayList<Node>();
	private String[] _words;
	private File _inputFile;

	public DotFileAdapter(String inputPath) throws FileNotFoundException {
		readGraph(inputPath);
	}

	/**
	 * Method that scans through a dot file, retrieves the relevant info and places it in a list of nodes for retrieval from main
	 */
	private void readGraph(String inputPath) throws FileNotFoundException{
		_inputFile = new File(inputPath);
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

	/**
	 * 
	 * @param schedule
	 * @param outputPath
	 * @throws IOException 
	 */
	public void writeSchedule(List<Processor> schedule, String outputPath) throws IOException{

		File file = new File(outputPath);
		FileWriter fw = new FileWriter(file);
		Scanner sc = new Scanner(_inputFile);

		while (sc.hasNextLine()) {
			String Line = sc.nextLine();

			if (Line.toLowerCase().contains("Weight=")) {

				if (Line.toLowerCase().contains("->")) {
					fw.write(Line + System.getProperty("line.separator"));
					fw.flush();

				}
				else {
					_words = Line.split("\\s+");
					String ID = _words[1];
					boolean found = false;
					while (!found) {
						for (int i = 0; i < schedule.size(); i++) {
							Processor p = schedule.get(i);
							for (Map.Entry<Integer, Node> e : p.getCurrentSchedule().entrySet()) {
								Node currentNode = e.getValue();
								Integer startTime = e.getKey();
								if (currentNode.getId().equalsIgnoreCase(ID)) {
									int PID = p.getID();
									int start = startTime.intValue(); 
									Line.replace("]", ",Start=" + start + ",Processor=" + PID + "]");
									found = true;
								}
							}
						}
					}
					fw.write(Line + System.getProperty("line.separator"));
					fw.flush();
				}
			}
			else {
				fw.write(Line + System.getProperty("line.separator"));
				fw.flush();
			}
		}
		fw.close();
		sc.close();

	}


	public void writeScheduleNew(Map<String, Node> scheduledNodes,String outputPath) throws IOException{
		//TODO
	}

	public List<Node> getData(){
		return _data;
	}

}
