package com.example.aoop_project.messaging;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageBubble {

    // keep same signature
    public static HBox create(String sender, String text, long timestamp, boolean isOwn) {
        String timeString = new SimpleDateFormat("HH:mm").format(new Date(timestamp));

        // ----- Avatar -----
        Circle avatar = new Circle(18);
        avatar.setFill(Paint.valueOf(isOwn ? "#0066cc" : "#9e9e9e"));

        Label avatarLabel = new Label(sender != null && !sender.isBlank() ? sender.substring(0, 1).toUpperCase() : "?");
        avatarLabel.setFont(new Font(14));
        avatarLabel.setStyle("-fx-text-fill: white;");

        // Use StackPane to overlay label on circle
        StackPane avatarBox = new StackPane(avatar, avatarLabel);
        avatarBox.setMinSize(36, 36);
        avatarBox.setMaxSize(36, 36);
        avatarBox.setPrefSize(36, 36);
        avatarBox.setAlignment(Pos.CENTER);

        // ----- Message text -----
        Label messageLabel = new Label(text);
        messageLabel.setFont(new Font(14));
        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(8, 10, 6, 10));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(420);
        messageLabel.setMinHeight(Region.USE_PREF_SIZE);

        // Timestamp
        Label timeLabel = new Label(timeString);
        timeLabel.setFont(new Font(10));
        timeLabel.setStyle("-fx-opacity: 0.8; -fx-padding: 0 8 6 8;");

        // Sender line
        Label senderLabel = new Label(isOwn ? sender : sender);
        senderLabel.setFont(new Font(11));
        senderLabel.setStyle("-fx-opacity: 0.85; -fx-padding: 6 8 0 8;");

        // Bubble container
        VBox bubbleBox = new VBox(senderLabel, messageLabel, timeLabel);
        bubbleBox.setSpacing(2);
        bubbleBox.setMaxWidth(450);

        // Bubble styles
        String bubbleCss = isOwn
                ? "-fx-background-color: #e0e8ff; -fx-background-radius: 12; -fx-text-fill: black;"
                : "-fx-background-color: #e0fff7; -fx-background-radius: 12; -fx-text-fill: black;";
        messageLabel.setStyle(bubbleCss + (isOwn ? " -fx-text-fill: black;" : " -fx-text-fill: black;"));

        if (isOwn) {
            senderLabel.setStyle("-fx-text-fill: #333;");
            timeLabel.setStyle("-fx-text-fill: #666;");
        } else {
            senderLabel.setStyle("-fx-text-fill: #333;");
            timeLabel.setStyle("-fx-text-fill: #666;");
        }

        // Compose HBox with avatar
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

        HBox.setHgrow(bubbleBox, Priority.ALWAYS);
        return container;
    }
}
