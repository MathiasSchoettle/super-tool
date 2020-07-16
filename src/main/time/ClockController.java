package main.time;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class ClockController {

    @FXML
    private Circle mainCircle;

    @FXML
    private Line minuteHand;

    @FXML
    private Line hourHand;

    @FXML
    private Line secondsHand;

    private Point2D centerOfClock;

    @FXML
    public void initialize() {
        this.centerOfClock = new Point2D(this.mainCircle.getLayoutX(), this.mainCircle.getLayoutY());
        double speed = 0.005;

        Timeline secondTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1 * speed), actionEvent -> {
                    this.rotateLineAroundPoint(this.secondsHand, this.centerOfClock, Math.toRadians(6));
                })
        );
        secondTimeline.setCycleCount(Animation.INDEFINITE);
        secondTimeline.play();

        Timeline minuteTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1 * speed), actionEvent -> {
                    this.rotateLineAroundPoint(this.minuteHand, this.centerOfClock, Math.toRadians(0.1));
                })
        );
        minuteTimeline.setCycleCount(Animation.INDEFINITE);
        minuteTimeline.play();

        Timeline hourTimeline = new Timeline(
                new KeyFrame(Duration.seconds(30 * speed), actionEvent -> {
                    this.rotateLineAroundPoint(this.hourHand, this.centerOfClock, Math.toRadians(0.25));
                })
        );
        hourTimeline.setCycleCount(Animation.INDEFINITE);
        hourTimeline.play();
    }

    private void rotateLineAroundPoint(Line line, Point2D point, double angle) {
        double xe = line.getEndX() - point.x;
        double ye = line.getEndY() - point.y;
        double xs = line.getStartX() - point.x;
        double ys = line.getStartY() - point.y;

        double s = Math.sin(angle);
        double c = Math.cos(angle);

        line.setEndX(xe * c - ye * s + point.x);
        line.setEndY(xe * s + ye * c + point.y);
        line.setStartX(xs * c - ys * s + point.x);
        line.setStartY(xs * s + ys * c + point.y);
    }

    private static class Point2D {
        public double x;
        public double y;

        public Point2D(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "x: " + this.x + ", y: " + this.y;
        }
    }
}
