package com.example.aoop_project.messaging;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageBubble {

    public static HBox create(String sender, String text, long timestamp, boolean isOwn) {
        String timeString = new SimpleDateFormat("HH:mm").format(new Date(timestamp));

        // ----- Avatar -----
        Circle avatarCircle = new Circle(18); // radius 18
        avatarCircle.setFill(isOwn ? Color.web("#0066cc") : Color.web("#9e9e9e"));

        Label avatarLabel = new Label(sender != null && !sender.isBlank() ? sender.substring(0, 1).toUpperCase() : "?");
        avatarLabel.setFont(new Font(14));
        avatarLabel.setTextFill(Color.WHITE);

        // StackPane to center label on circle
        StackPane avatarBox = new StackPane();
        avatarBox.getChildren().addAll(avatarCircle, avatarLabel);
        avatarBox.setMinSize(36, 36);
        avatarBox.setPrefSize(36, 36);
        avatarBox.setMaxSize(36, 36);

        // ----- Message Label -----
        Label messageLabel = new Label(text);
        messageLabel.setFont(new Font(14));
        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(8, 10, 6, 10));
        messageLabel.setMaxWidth(600);
        messageLabel.setMinHeight(Region.USE_PREF_SIZE);

        // Timestamp
        Label timeLabel = new Label(timeString);
        timeLabel.setFont(new Font(10));
        timeLabel.setStyle("-fx-opacity: 0.8; -fx-padding: 0 8 6 8;");

        // Sender label (for others)
        Label senderLabel = new Label(isOwn ? "" : sender);
        senderLabel.setFont(new Font(11));
        senderLabel.setStyle("-fx-opacity: 0.85; -fx-padding: 6 8 0 8;");

        // ----- Bubble VBox -----
        VBox bubbleBox = new VBox(senderLabel, messageLabel, timeLabel);
        bubbleBox.setSpacing(2);
        bubbleBox.setFillWidth(true);

        // Bubble CSS
        String bubbleCss = isOwn
                ? "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #1c8ef9, #0077ff); -fx-background-radius: 12;"
                : "-fx-background-color: #F1F0F0; -fx-background-radius: 12;";
        messageLabel.setStyle(bubbleCss + (isOwn ? " -fx-text-fill: white;" : " -fx-text-fill: black;"));

        if (isOwn) {
            senderLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9);");
            timeLabel.setStyle("-fx-text-fill: rgba(230,230,230,0.9);");
        } else {
            senderLabel.setStyle("-fx-text-fill: #333;");
            timeLabel.setStyle("-fx-text-fill: #666;");
        }

        // ----- Compose HBox -----
        HBox container;
        if (isOwn) {
            container = new HBox(bubbleBox, avatarBox);
            container.setAlignment(Pos.CENTER_RIGHT);
            container.setSpacing(8);
            container.setPadding(new Insets(6, 10, 6, 30));
        } else {
            container = new HBox(avatarBox, bubbleBox);
            container.setAlignment(Pos.CENTER_LEFT);
            container.setSpacing(8);
            container.setPadding(new Insets(6, 10, 6, 10));
        }

        HBox.setHgrow(bubbleBox, Priority.NEVER);

        return container;
    }
}
