package main;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class clockPaneController {

    @FXML
    public FontAwesomeIcon playPauseIcon;

    @FXML
    public FontAwesomeIcon refreshIcon;

    @FXML
    private Arc arc;

    @FXML
    private Text clockText;

    @FXML
    private Button startPauseButton;

    @FXML
    private Button resetButton;

    private LocalTime constantTimer;

    private LocalTime timer;

    private Timeline timelineClockText;

    private Timeline timelineArc;

    private TimerState state = TimerState.Waiting;

    private final Media sound = new Media(new File("unbenannt.mp3").toURI().toString());

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @FXML
    private void initialize() {
        this.constantTimer = LocalTime.of(0, 0, 15);
        this.timer = LocalTime.ofSecondOfDay(this.constantTimer.toSecondOfDay());

        this.timelineClockText = new Timeline(
                new KeyFrame(Duration.ZERO,
                        actionEvent -> {
                            this.timer = this.timer.minusNanos(1000000);
                            this.clockText.setText(this.formatter.format(this.timer));

                            if (this.timer.toNanoOfDay() == 0) {
                                this.state = TimerState.Finished;
                                this.refreshClockCountDown();
                                this.playPauseIcon.setIconName("PLAY");
                                new MediaPlayer(this.sound).play();
                            }
                        }
                ),
                new KeyFrame(Duration.millis(1))
        );


        this.timelineArc = new Timeline();
        this.timelineArc.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(this.arc.lengthProperty(), 0)
                ),
                new KeyFrame(Duration.seconds(this.constantTimer.toSecondOfDay()),
                        new KeyValue(this.arc.lengthProperty(), 360)
                )
        );

        this.clockText.setText(this.formatter.format(this.timer));
        this.startPauseButton.setOnAction(actionEvent -> {
            switch (this.state) {
                case Finished:
                case Waiting:
                    this.startClockCountDown();
                    this.state = TimerState.Running;
                    this.playPauseIcon.setIconName("PAUSE");

                    break;
                case Running:
                    this.pauseClockCountDown();
                    this.state = TimerState.Paused;
                    this.playPauseIcon.setIconName("PLAY");
                    break;
                case Paused:
                    this.resumeClockCountDown();
                    this.state = TimerState.Running;
                    this.playPauseIcon.setIconName("PAUSE");
                    break;
            }
        });

        this.resetButton.setOnAction(actionEvent -> {
            switch (this.state) {
                case Running:
                case Paused:
                    this.timelineClockText.stop();
                    this.timelineArc.stop();
                    this.refreshClockCountDown();
                    this.state = TimerState.Waiting;
                    this.playPauseIcon.setIconName("PLAY");
                    break;
            }
        });
    }

    private void refreshClockCountDown() {
        this.clockText.setText(this.formatter.format(this.constantTimer));
        this.timer = LocalTime.ofSecondOfDay(this.constantTimer.toSecondOfDay());
        this.arc.setLength(0);
    }

    private void pauseClockCountDown() {
        this.timelineClockText.pause();
        this.timelineArc.pause();
    }

    private void resumeClockCountDown() {
        this.timelineClockText.play();
        this.timelineArc.play();
    }

    private void startClockCountDown() {
        this.timelineClockText.setCycleCount(this.timer.toSecondOfDay() * 1000);
        this.timelineClockText.play();
        this.timelineArc.play();
    }
}
