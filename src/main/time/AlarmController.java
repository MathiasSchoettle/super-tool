package main.time;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.time.LocalTime;

public class AlarmController {

    @FXML
    private Text alarm1textHours;

    @FXML
    private Text alarm1textMinutes;

    @FXML
    private Text alarm2textHours;

    @FXML
    private Text alarm2textMinutes;

    @FXML
    private Text alarm3textHours;

    @FXML
    private Text alarm3textMinutes;

    @FXML
    private ToggleButton alarm3ToggleButton;

    @FXML
    private ToggleButton alarm2ToggleButton;

    @FXML
    private ToggleButton alarm1ToggleButton;

    private Timeline alarmTimeline1;

    private Timeline alarmTimeline2;

    private Timeline alarmTimeline3;

    // TODO this will become a attribute of the container
    private final MediaPlayer mediaPlayer = new MediaPlayer(new Media(new File(new File("").getAbsolutePath() + "/resources/alarm.wav").toURI().toString()));

    @FXML
    public void initialize() {
        this.addClickListenerToText(this.alarm1textHours, this.alarm1ToggleButton, 23);
        this.addClickListenerToText(this.alarm1textMinutes, this.alarm1ToggleButton, 59);
        this.addClickListenerToButton();

        this.addClickListenerToText(this.alarm2textHours, this.alarm2ToggleButton, 23);
        this.addClickListenerToText(this.alarm2textMinutes, this.alarm2ToggleButton, 59);
        this.addClickListenerToButton();

        this.addClickListenerToText(this.alarm3textHours, this.alarm3ToggleButton, 23);
        this.addClickListenerToText(this.alarm3textMinutes, this.alarm3ToggleButton, 59);
        this.addClickListenerToButton();
    }

    private java.time.Duration getDurationOfText(Text textHours, Text textMinutes) {

        int hours = Integer.parseInt(textHours.getText());
        int minute = Integer.parseInt(textMinutes.getText());

        LocalTime alarmTime = LocalTime.of(hours, minute, 0);
        LocalTime now = LocalTime.now();

        if (alarmTime.isAfter(now)) {
            return java.time.Duration.between(now, alarmTime);
        } else {
            return java.time.Duration.ofDays(1).minus(java.time.Duration.between(alarmTime, now));
        }
    }

    private Timeline setUpTimeline(java.time.Duration duration, ToggleButton button) {

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(duration.toSeconds()))
        );
        timeline.setOnFinished(actionEvent -> {
            button.setSelected(false);
            this.mediaPlayer.play();
            this.mediaPlayer.setOnEndOfMedia(this.mediaPlayer::stop);
        });

        return timeline;
    }


    private void addClickListenerToButton() {
        this.alarm1ToggleButton.setOnAction(actionEvent -> {
            this.removeStylesFromTexts("selectedTextField");
            if (!this.alarm1ToggleButton.isSelected()) {
                this.alarmTimeline1.stop();
            } else {
                this.alarmTimeline1 = this.setUpTimeline(this.getDurationOfText(this.alarm1textHours, this.alarm1textMinutes), this.alarm1ToggleButton);
                this.alarmTimeline1.play();
            }
        });

        this.alarm2ToggleButton.setOnAction(actionEvent -> {
            this.removeStylesFromTexts("selectedTextField");
            if (!this.alarm2ToggleButton.isSelected()) {
                this.alarmTimeline2.stop();
            } else {
                this.alarmTimeline2 = this.setUpTimeline(this.getDurationOfText(this.alarm2textHours, this.alarm2textMinutes), this.alarm2ToggleButton);
                this.alarmTimeline2.play();
            }
        });

        this.alarm3ToggleButton.setOnAction(actionEvent -> {
            this.removeStylesFromTexts("selectedTextField");
            if (!this.alarm3ToggleButton.isSelected()) {
                this.alarmTimeline3.stop();
            } else {
                this.alarmTimeline3 = this.setUpTimeline(this.getDurationOfText(this.alarm3textHours, this.alarm3textMinutes), this.alarm3ToggleButton);
                this.alarmTimeline3.play();
            }
        });
    }

    private void addClickListenerToText(Text text, ToggleButton button, int max) {
        text.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                this.removeStylesFromTexts("selectedTextField");

                if (button.isSelected())
                    button.fire();

                text.getStyleClass().add("selectedTextField");
                text.requestFocus();
                text.setOnKeyPressed(keyEvent -> {
                    String keyText = keyEvent.getText();
                    String clockTextTemp = text.getText();
                    KeyCode code = keyEvent.getCode();

                    if (keyText.matches("[0-9]") && clockTextTemp.startsWith("0")) {
                        clockTextTemp = clockTextTemp.substring(1) + keyText;
                        text.setText(Integer.parseInt(clockTextTemp) > max ? Integer.toString(max) : clockTextTemp);
                    } else if (code.equals(KeyCode.BACK_SPACE) && !clockTextTemp.equals("00")) {
                        text.setText(0 + clockTextTemp.substring(0, clockTextTemp.length() - 1));
                    }
                });
            }
        });
        text.setCursor(Cursor.HAND);
    }

    private void removeStylesFromTexts(String className) {
        this.alarm1textHours.getStyleClass().remove(className);
        this.alarm1textMinutes.getStyleClass().remove(className);
        this.alarm2textHours.getStyleClass().remove(className);
        this.alarm2textMinutes.getStyleClass().remove(className);
        this.alarm3textHours.getStyleClass().remove(className);
        this.alarm3textMinutes.getStyleClass().remove(className);
    }
}
