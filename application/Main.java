package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.DotFileAdapter;

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

        DotFileAdapter reader = new DotFileAdapter(args[0]);
        Object graph = reader.getData();
        Scheduler schedule = new Scheduler();
        if (Visualisation) {
            launch(args);
        }
    }
}
