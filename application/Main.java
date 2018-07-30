package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import model.DotFileAdapter;
import model.Node;
import model.Notification;
import model.Scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class Main extends Application {
    //By default the visualisation option is not enabled.
    private static boolean _visualisation = false;
    //By default the output file is INPUT_output.dot.
    private static String _outputFileSuffix = "_output.dot";
    //By default the execution run sequentially on one core.
    private static int _noOfCores = 1;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../view/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {
        System.out.println(args.length);
        if (args.length < 2){
            Notification.message("Error: Not enough arguments");
            System.exit(1);
        }else if (args.length>7){
            Notification.message("Error: Too many arguments");
            System.exit(1);
        }

        try {
            //Read input file
            String filepath = args[0];
            checkFile(filepath);
            DotFileAdapter reader = new DotFileAdapter(filepath);
            List<Node> graph = reader.getData();
            act(args);

            if (_visualisation){
                launch(args);
            }

            //If the .dot filename is invalid, new DotFileAdapter(filepath) would throw exception
            //So you can assume the filename is valid here
            //TODO ask for overwriting if file already exists, or save as INPUT_ouput_1.dot. Can leave this for now

            //Read number of processors.
            int numberOfProcessor = Integer.parseInt(args[1]);

            //Run Scheduler to calculate the schedule.
            Scheduler schedule = new Scheduler(graph, numberOfProcessor);
            schedule.schedule();
            //TODO somehow get the result (can't implement now)

            //Write result
            //TODO pass result and output filename to adapter to write file. Can leave this for now.

        }catch(NumberFormatException e){
            Notification.message("Error: second argument must be an integer");
            System.exit(1);
        }catch(FileNotFoundException e){
            Notification.message("Error: File does not exist");
            System.exit(1);
        }

    }

    /**
     * Checks if the user input optional arguments are valid
     * @param args arguments that the user inputted in command line
     */
    private static void act(String[] args){
        for (int i=2;i<args.length;i++){
            if (args[i].equals("-p")){
                try {
                    _noOfCores = Integer.parseInt(args[i + 1]);
                }catch(NumberFormatException e){
                    Notification.message("Error: argument after -p must be an integer/no argument specified after -p");
                    System.exit(1);
                }catch(ArrayIndexOutOfBoundsException e1){
                    Notification.message("Error: no argument specified after -p");
                    System.exit(1);
                }
            }else if(args[i].equals("-v")){
                _visualisation = true;
            }else if(args[i].equals("-o")){
                try{
                    _outputFileSuffix=args[i+1]+".dot";
                }catch(ArrayIndexOutOfBoundsException e1){
                    Notification.message("Error: no argument specified after -o");
                    System.exit(1);
                }
            }else{
                if (args[i-1].equals("-p")||args[i-1].equals("-o")){
                    Notification.invalidInput();
                    System.exit(1);
                }
            }
        }

    }

    /**
     * checks if the file has valid type(.dot file) and exists in the directory
     */
    private static void checkFile(String filepath){

        if (!filepath.endsWith(".dot")){
            Notification.message("Error: input file has wrong suffix");
            System.exit(1);
        }

        File file = new File(filepath);
        boolean fileExists = file.exists();
        if (!fileExists){
            Notification.message("Error: File does not exist");
            System.exit(1);
        }
    }
}
