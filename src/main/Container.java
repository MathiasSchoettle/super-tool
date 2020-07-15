package main;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class Container {

    @FXML
    public GridPane container;

    @FXML
    public Pane node1;

    @FXML
    public Pane node2;

    @FXML
    public void initialize() {
    }

    public void setNodes(Pane nodeLeft, Pane nodeRight) {
        this.node1.getChildren().add(nodeLeft);
        this.node2.getChildren().add(nodeRight);
    }
}
