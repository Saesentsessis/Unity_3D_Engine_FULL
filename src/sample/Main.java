package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene current = new Scene(root, 1280, 720);
        Controller.scene = current;
        primaryStage.setTitle("Unity v0.1_alpha");
        primaryStage.setScene(current);
        primaryStage.show();
        Controller.Instance.Start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
