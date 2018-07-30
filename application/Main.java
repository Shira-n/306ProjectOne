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
    private static boolean visualisation = false;
    //By default the output file is INPUT_output.dot.
    private static String outputFileSuffix = "_output.dot";
    //By default the execution run sequentially on one core.
    private static int coreNo = 1;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../view/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {
        if (args.length < 2){
            Notification.message("Error: Not enough arguments");
            System.exit(1);
        }

        try {
            //Read input file
            String filepath = args[0];
            if (!checkFile(filepath)) {
                //TODO handle this situation
                //Basically the same with handling FileNotFoundException (that's why it's redundant)
            }

            //Read the input file and extract data.
            DotFileAdapter adapter = new DotFileAdapter(filepath);
            List<Node> graph = adapter.getData();

            //Reconfigure output filename according to input filename.
            //TODO write a private method or just lines of code here. The output file name would be INPUT_output.dot
            //TODO where INPUT is args[0] without .dot
            //If the .dot filename is invalid, new DotFileAdapter(filepath) would throw exception
            //So you can assume the filename is valid here
            //TODO ask for overwriting if file already exists, or save as INPUT_ouput_1.dot. Can leave this for now

            //Read number of processors.
            int numberOfProcessor = Integer.parseInt(args[1]);

            //Read optional arguments.
            //TODO implement reading optional arguments here or use private methods. Set above private static fields

            //Run Scheduler to calculate the schedule.
            Scheduler schedule = new Scheduler(graph, numberOfProcessor);
            schedule.schedule();
            //TODO somehow get the result (can't implement now)

            //Write result
            //TODO pass result and output filename to adapter to write file. Can leave this for now.

        }catch(NumberFormatException e){
            Notification.message("Error: second argument must be an integer");
            System.exit(1);
        }catch(FileNotFoundException e){ }

        act(args);
    }

    private static boolean checkFile(String filepath){
        //TODO Check whether file exist and is a valid .dot file
        //It's a bit redundant but we can have it
        return false;
    }

    private static void act(String[] args){
        if (visualisation){
            launch(args);
        }
    }
}
