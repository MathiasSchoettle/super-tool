package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("../fxml/container.fxml"));
        Parent root = loader.load();
        Container c = loader.getController();

        Pane stopwatch = FXMLLoader.load(this.getClass().getResource("../fxml/stopwatch.fxml"));
        Pane timer = FXMLLoader.load(this.getClass().getResource("../fxml/timer.fxml"));

        primaryStage.setTitle("Stopwatch");
        Scene scene = new Scene(root);

        c.setNodes(stopwatch, timer);

        scene.getStylesheets().add(this.getClass().getResource("../css/time.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
