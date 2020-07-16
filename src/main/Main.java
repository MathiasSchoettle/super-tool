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

        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("../fxml/clock.fxml"));
        Parent root = loader.load();


        Pane stopwatch = FXMLLoader.load(this.getClass().getResource("../fxml/stopwatch.fxml"));
        Pane timer = FXMLLoader.load(this.getClass().getResource("../fxml/timer.fxml"));
        Pane alarm = FXMLLoader.load(this.getClass().getResource("../fxml/alarm.fxml"));

        primaryStage.setTitle("Super Tool");
        Scene scene = new Scene(root);

        scene.getStylesheets().add(this.getClass().getResource("../css/time.css").toExternalForm());
        scene.getStylesheets().add(this.getClass().getResource("../css/alarm.css").toExternalForm());
        scene.getStylesheets().add(this.getClass().getResource("../css/main.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
