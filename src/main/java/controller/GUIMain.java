package controller;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point of JavaFX. This is that standard class that starts the application window.
 */
public class GUIMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
        primaryStage.setTitle("Visualisation of Computation");
        primaryStage.setScene(new Scene(root, 650, 850));
        primaryStage.show();
    }

}
