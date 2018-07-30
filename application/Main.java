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

import java.io.FileNotFoundException;
import java.util.List;

public class Main extends Application {
    private static boolean visualisation = false;

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
            String filepath = args[0];
            DotFileAdapter reader = new DotFileAdapter(filepath);
            List<Node> graph = reader.getData();

            int numberOfProcessor = Integer.parseInt(args[1]);
            Scheduler schedule = new Scheduler(graph, numberOfProcessor);
            schedule.schedule();
        }catch(NumberFormatException e){
            Notification.message("Error: second argument must be an integer");
            System.exit(1);
        }//catch(FileNotFoundException e){ }

        act(args);
    }

    private static void act(String[] args){
        if (visualisation){
            launch(args);
        }
    }
}
