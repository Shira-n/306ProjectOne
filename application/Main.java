package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import model.DotFileAdapter;
import model.Node;
import model.Scheduler;

import java.util.List;

public class Main extends Application {
    private static boolean Visualisation=false;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../view/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        if (args.length<2){
            System.out.println("Error: Not enough arguments");
            System.exit(1);
        }
        //passing arguments for
        DotFileAdapter reader = new DotFileAdapter(args[0]);
        List<Node> graph = reader.getData();
        args[1].parseInt();
        Scheduler schedule = new Scheduler(graph, args[1]);
        if (Visualisation) {
            launch(args);
        }
    }


    //@TODO User Input @Jenny

}
