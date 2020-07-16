package main.time;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;

public class StopwatchController {

    @FXML
    private Button startPauseButton;

    @FXML
    private FontAwesomeIcon playPauseIcon;

    @FXML
    private Button resetButton;

    @FXML
    private FontAwesomeIcon refreshIcon;

    @FXML
    private Arc arc;

    @FXML
    private Text clockHours;

    @FXML
    private Text clockMinutes;

    @FXML
    private Text clockSeconds;

    @FXML
    private Text clockMillis;

    private Timeline arcTimeline;

    private Timeline clockTextTimeline;

    private TimerState state = TimerState.Waiting;

    private java.time.Duration time = java.time.Duration.ZERO;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @FXML
    private void initialize() {

        this.setUpStartPauseButton();
        this.setUpRestartButton();

        this.clockTextTimeline = this.getClockTextTimeline();
        this.arcTimeline = this.getArcTimeline();
    }

    private Timeline getArcTimeline() {
        Timeline returnTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(this.arc.lengthProperty(), 360),
                        new KeyValue(this.arc.startAngleProperty(), 90)
                ),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(this.arc.lengthProperty(), 0),
                        new KeyValue(this.arc.startAngleProperty(), 450)
                )
        );
        returnTimeline.setCycleCount(Animation.INDEFINITE);
        returnTimeline.setOnFinished(actionEvent -> {
            this.arc.setLength(0);
            this.arc.setStartAngle(90);
        });
        return returnTimeline;
    }

    private Timeline getClockTextTimeline() {
        Timeline returnTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, actionEvent -> {
                    this.time = this.time.plus(java.time.Duration.ofMillis(1));
                    this.setClockText(this.time);
                }),
                new KeyFrame(Duration.millis(1))
        );
        returnTimeline.setCycleCount(Animation.INDEFINITE);
        return returnTimeline;
    }

    private void setUpRestartButton() {
        this.resetButton.setOnAction(actionEvent -> {
            switch (this.state) {
                case Paused:
                case Running:
                    this.arcTimeline.stop();
                    this.clockTextTimeline.stop();
                    this.arc.setLength(0);
                    this.arc.setStartAngle(90);
                    this.playPauseIcon.setIconName("PLAY");
                    this.state = TimerState.Waiting;
                    this.time = java.time.Duration.ZERO;
                    this.setClockText(this.time);
                    break;
            }
        });
    }

    private void setUpStartPauseButton() {
        this.startPauseButton.setOnAction(actionEvent -> {
            switch (this.state) {
                case Waiting:
                case Paused:
                    this.arcTimeline.play();
                    this.clockTextTimeline.play();
                    this.playPauseIcon.setIconName("PAUSE");
                    this.state = TimerState.Running;
                    break;
                case Running:
                    this.arcTimeline.pause();
                    this.clockTextTimeline.pause();
                    this.playPauseIcon.setIconName("PLAY");
                    this.state = TimerState.Paused;
                    break;
            }
        });
    }

    private void setClockText(java.time.Duration time) {
        this.clockHours.setText(String.format("%02d", time.toHours()));
        this.clockMinutes.setText(String.format("%02d", time.toMinutes() % 60));
        this.clockSeconds.setText(String.format("%02d", time.toSeconds() % 60));
        this.clockMillis.setText(String.format("%03d", time.toMillis() % 1000));
    }
}
