package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import model.Node;
import model.scheduler.AbstractScheduler;
import controller.Controller;
import controller.GUIEntry;
import model.*;
import model.scheduler.OptimalScheduler;
import model.scheduler.ParallelScheduler;
import model.state.AbstractState;

public class Main {
    //By default the visualisation option is not enabled.
    private static boolean _visualisation = false;
    //By default the output file is INPUT-output.dot.
    private static String _outputFile = "-output.dot";
    //By default the execution run sequentially on one core.
    private static int _noOfThreads = 1;

    private static String _inputFile;
    private static int _noOfProcessors;
    private static DotFileAdapter _reader;
    private static AbstractScheduler _scheduler;

    private static List<Node> _graph;

    public static void main(String[] args) {
        //Start Timer
        long startTime = System.currentTimeMillis();

        //Handle necessary user input
        checkNecessaryInput(args);

        try {
            //Read input filename
            _inputFile = args[0];
            checkInputFile();
            _reader = new DotFileAdapter(_inputFile);
            //Modify output filename
            _outputFile = _inputFile.substring(0, _inputFile.length() - 4) + _outputFile;
            //Read number of processors.
            _noOfProcessors = Integer.parseInt(args[1]);

            //Read optional arguments to decide visualisation, parallelisation and customized output filename
            readOptionalArgs(args);
            checkOutputFile(_outputFile);
            _graph = _reader.getData();

            if (_noOfThreads > 1){ //Multithreading
                _scheduler = getParallelScheduler();
            }else{//Sequential
                _scheduler = getSequentialScheduler();
            }

            //TODO GUI option here
            if (_visualisation) {
                GUIEntry entry = new GUIEntry(_graph, _scheduler,"S", _noOfProcessors, _noOfThreads>1);
            }
            else {
                _reader.writeOptimalSchedule(_scheduler.getSchedule(),_outputFile);
            }

        }catch(NumberFormatException e){
            Notification.message("Error: second argument must be an integer");
            System.exit(1);
        }catch(FileNotFoundException e){
            Notification.message("Error: File does not exist");
            System.exit(1);
        } catch (IOException e) {
            Notification.message("Error: IO Exception");
            System.exit(1);
        }

    }

    /**
     * Checks if the user input optional arguments are valid
     * @param args arguments that the user inputted in command line
     */
    private static void readOptionalArgs(String[] args){
        for (int i = 2; i < args.length; i++){
            //If -p is specified, read how many cores are supplied.
            if (args[i].equals("-p")){
                try {
                    _noOfThreads = Integer.parseInt(args[i+1]);
                    i++;
                }catch(NumberFormatException e){
                    Notification.message("Error: Argument after -p must be an integer");
                    System.exit(1);
                }catch(ArrayIndexOutOfBoundsException e1){
                    Notification.message("Error: Please enter an integer after -p");
                    System.exit(1);
                }

            //If -o is specified, read the specified output filename
            }else if(args[i].equals("-o")) {
                try {
                    if (args[i+1].equals("-v")||args[i+1].equals("-p")){
                        Notification.message("Error: filename for output is not valid please choose another name");
                        System.exit(1);
                    }
                    _outputFile = args[i + 1] + ".dot";
                    i++;
                } catch (ArrayIndexOutOfBoundsException e1) {
                    Notification.message("Error: Please enter a filename after -o");
                    System.exit(1);
                }

            //If -v is specified, mark _visualisation as true.
            }else if (args[i].equals("-v")){
                _visualisation = true;

            //Display error message and help for invalid inputs
            }else{
                Notification.invalidInput();
                System.exit(1);
            }
        }
    }

    private static void checkNecessaryInput(String[] args){
        if (args.length < 2){
            Notification.message("Error: Please specify the filename and numbers of processors to use");
            System.exit(1);
        }else if (args.length > 7){
            Notification.message("Error: Too many arguments");
            System.exit(1);
        }
    }

    /**
     * checks if the file has valid type(.dot file) and exists in the directory
     */
    private static void checkInputFile(){
        if (!_inputFile.endsWith(".dot")){
            Notification.message("Error: input file has wrong suffix");
            System.exit(1);
        }

        File file = new File(_inputFile);
        boolean fileExists = file.exists();
        if (!fileExists){
            Notification.message("Error: File does not exist");
            System.exit(1);
        }
    }

    //TODO @Jenny
    //Ask the user to overwrite the output file if exists
    private static void checkOutputFile(String filepath){
        File tmpDir = new File(filepath);
        boolean exists = tmpDir.exists();
        if(exists) {
            StringBuffer s = new StringBuffer("File name exists, do you want to override y/n");
            System.out.println(s);
            Scanner scanner = new Scanner(System.in);
            String response = scanner.nextLine();
            if (response.contains("y")) {
                tmpDir.delete();
            } else {
                Notification.message("File not overriden, system exiting");
                System.exit(1);
            }
        }
    }

    private static AbstractScheduler getSequentialScheduler() {
        return new OptimalScheduler(_graph, _noOfProcessors);
    }

    private static AbstractScheduler getParallelScheduler() throws FileNotFoundException {
        List<Map<String, Node>> graphs = new ArrayList<>();
        for (int i = 0; i <= _noOfThreads; i++) {
            graphs.add(_reader.getMap());
        }
        return new ParallelScheduler(_noOfThreads, graphs, _noOfProcessors);
    }

    public static AbstractScheduler getScheduler() {
        return _scheduler;
    }

    public static void writeResult(AbstractState state) {
        try {
            _reader.writeOptimalSchedule(state, _outputFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}