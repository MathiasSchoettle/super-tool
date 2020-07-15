package main;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimerController {

    @FXML
    public FontAwesomeIcon playPauseIcon;

    @FXML
    public FontAwesomeIcon refreshIcon;

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

    @FXML
    private Button startPauseButton;

    @FXML
    private Button resetButton;

    private LocalTime constantTimer;

    private LocalTime timer;

    private Timeline timelineClockText;

    private Timeline timelineArc;

    private TimerState state = TimerState.Waiting;

    private boolean lastPressWasButton = false;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    // TODO this will become a attribute of the container
    private final MediaPlayer mediaPlayer = new MediaPlayer(new Media(new File(new File("").getAbsolutePath() + "/resources/alarm.wav").toURI().toString()));

    @FXML
    private void initialize() {
        this.setTimers(LocalTime.of(0, 0, 0));

        this.timelineClockText = this.getClockTextTimeline();
        this.timelineArc = this.getArcTimeline();

        this.addListenerToPlayPauseButton();
        this.addListenerToResetButton();
        this.addClickListenersToClockTextField();
    }

    private void addClickListenersToClockTextField() {
        this.clockHours.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                this.onClockNumberClicked(this.clockHours, 23);
            }
        });

        this.clockMinutes.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                this.onClockNumberClicked(this.clockMinutes, 59);
            }
        });


        this.clockSeconds.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                this.onClockNumberClicked(this.clockSeconds, 59);
            }
        });
    }

    private void onClockNumberClicked(Text clockText, int maxAmount) {
        this.removeStyleClassFromClockTexts("selectedTextField", true);
        this.lastPressWasButton = false;
        clockText.getStyleClass().add("selectedTextField");
        clockText.requestFocus();
        clockText.setOnKeyPressed(keyEvent -> {
            String keyText = keyEvent.getText();
            String clockTextTemp = clockText.getText();
            KeyCode code = keyEvent.getCode();

            if (keyText.matches("[0-9]") && clockTextTemp.startsWith("0")) {
                clockTextTemp = clockTextTemp.substring(1) + keyText;
                clockText.setText(Integer.parseInt(clockTextTemp) > maxAmount ? Integer.toString(maxAmount) : clockTextTemp);
            } else if (code.equals(KeyCode.BACK_SPACE) && !clockTextTemp.equals("00")) {
                clockText.setText(0 + clockTextTemp.substring(0, clockTextTemp.length() - 1));
            }

            this.resetConstantTimer();
        });
    }

    private void resetConstantTimer() {
        int hours = Integer.parseInt(this.clockHours.getText());
        int minutes = Integer.parseInt(this.clockMinutes.getText());
        int seconds = Integer.parseInt(this.clockSeconds.getText());
        this.constantTimer = LocalTime.of(hours, minutes, seconds);
        this.timer = LocalTime.of(hours, minutes, seconds);
        this.timelineArc = this.getArcTimeline();
    }

    private void removeStyleClassFromClockTexts(String className, boolean fireResetButton) {
        if (fireResetButton && this.lastPressWasButton)
            this.resetButton.fire();

        this.clockHours.getStyleClass().remove(className);
        this.clockMinutes.getStyleClass().remove(className);
        this.clockSeconds.getStyleClass().remove(className);
        this.clockMillis.getStyleClass().remove(className);
    }

    private void setTimers(LocalTime time) {
        this.constantTimer = time;
        this.timer = time;
        this.setClockText(this.timer);
    }

    private void addListenerToResetButton() {
        this.resetButton.setOnAction(actionEvent -> {
            this.removeStyleClassFromClockTexts("selectedTextField", false);
            this.lastPressWasButton = true;
            switch (this.state) {
                case Running:
                case Paused:
                    this.timelineClockText.stop();
                    this.timelineArc.stop();
                    this.refreshClockCountDown();
                    this.state = TimerState.Waiting;
                    this.playArcResetAnimation(false);
                    this.playPauseIcon.setIconName("PLAY");
                    break;
                case Waiting:
                case Finished:
                    this.setTimers(LocalTime.of(0, 0, 0));
                    break;
            }
        });
    }

    private void addListenerToPlayPauseButton() {
        this.startPauseButton.setOnAction(actionEvent -> {
            this.removeStyleClassFromClockTexts("selectedTextField", false);
            this.lastPressWasButton = true;
            switch (this.state) {
                case Finished:
                case Waiting:
                    if (this.constantTimer.toNanoOfDay() != 0) {
                        this.startClockCountDown();
                        this.state = TimerState.Running;
                        this.playPauseIcon.setIconName("PAUSE");
                    }
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
    }

    private void setClockText(LocalTime time) {
        String[] timeFormatted = this.formatter.format(time).split(":"); // HH, mm, ss.SSS
        this.clockHours.setText(timeFormatted[0]);
        this.clockMinutes.setText(timeFormatted[1]);
        timeFormatted = timeFormatted[2].split("\\.");
        this.clockSeconds.setText(timeFormatted[0]);
        this.clockMillis.setText(timeFormatted[1]);
    }

    private void refreshClockCountDown() {
        this.setClockText(this.constantTimer);
        this.timer = LocalTime.ofSecondOfDay(this.constantTimer.toSecondOfDay());
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

    private void playArcResetAnimation(boolean isFinished) {
        if (isFinished) {
            new Timeline(
                    new KeyFrame(
                            Duration.ZERO,
                            new KeyValue(this.arc.lengthProperty(), 360, Interpolator.EASE_OUT)
                    ),
                    new KeyFrame(
                            Duration.seconds(0.3),
                            new KeyValue(this.arc.lengthProperty(), 0)
                    )).play();
        } else {
            new Timeline(
                    new KeyFrame(
                            Duration.ZERO,
                            new KeyValue(this.arc.lengthProperty(), this.arc.getLength())
                    ),
                    new KeyFrame(
                            Duration.seconds(0.1),
                            new KeyValue(this.arc.lengthProperty(), 0)
                    )).play();
        }
    }

    private Timeline getClockTextTimeline() {
        return new Timeline(
                new KeyFrame(Duration.ZERO,
                        actionEvent -> {
                            this.timer = this.timer.minusNanos(1000000);
                            this.setClockText(this.timer);

                            if (this.timer.toNanoOfDay() == 0) {
                                this.state = TimerState.Finished;
                                this.refreshClockCountDown();
                                this.playPauseIcon.setIconName("PLAY");
                                this.mediaPlayer.play();
                                this.mediaPlayer.setOnEndOfMedia(this.mediaPlayer::stop);
                            }
                        }
                ),
                new KeyFrame(Duration.millis(1))
        );
    }

    private Timeline getArcTimeline() {
        Timeline returnTimeline = new Timeline();
        returnTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(this.arc.lengthProperty(), 0)
                ),
                new KeyFrame(Duration.seconds(this.constantTimer.toSecondOfDay()),
                        new KeyValue(this.arc.lengthProperty(), 360)
                )
        );
        returnTimeline.setOnFinished(actionEvent -> this.playArcResetAnimation(true));

        return returnTimeline;
    }
}
