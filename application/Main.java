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
    private static boolean _visualisation = false;
    private static int _numberOfCores = 1;
    private static boolean _outputSpecified = false;
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
        }

        try {
            String filepath = args[0];
            checkFile(filepath);
            DotFileAdapter reader = new DotFileAdapter(filepath);
            List<Node> graph = reader.getData();
            act(args);

            int numberOfProcessor = Integer.parseInt(args[1]);
            Scheduler schedule = new Scheduler(graph, numberOfProcessor);
            schedule.schedule();
        }catch(NumberFormatException e){
            Notification.message("Error: second argument must be an integer");
            System.exit(1);
        }//catch(FileNotFoundException e){ }
    }

    private static void act(String[] args){
        for (int i=2;i<args.length;i++){
            if (args[i].equals("-p")){
                try {
                    _numberOfCores = Integer.parseInt(args[i + 1]);
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
                _outputSpecified = true;
            }
        }

        if (_visualisation){
            launch(args);
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
