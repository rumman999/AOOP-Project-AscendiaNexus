package com.example.aoop_project.messaging;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageBubble {

    public static HBox create(String sender, String text, long timestamp, boolean isOwn) {
        String timeString = new SimpleDateFormat("HH:mm").format(new Date(timestamp));

        Label label = new Label(sender + ": " + text + "\n" + timeString);
        label.setFont(new Font(14));
        label.setWrapText(true); // ✅ Enable wrapping
        label.setPadding(new Insets(8, 12, 8, 12));

        // Instead of fixed 300, bind max width to ScrollPane width
        label.setMaxWidth(400); // fallback
        label.setPrefWidth(Region.USE_COMPUTED_SIZE);

        if (isOwn) {
            label.setStyle("-fx-background-color: #0084FF; -fx-background-radius: 12; -fx-text-fill: white;");
        } else {
            label.setStyle("-fx-background-color: #E4E6EB; -fx-background-radius: 12; -fx-text-fill: black;");
        }

        HBox container = new HBox(label);
        container.setPadding(new Insets(5));
        container.setAlignment(isOwn ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        // ✅ Allow bubble to grow with parent width
        HBox.setHgrow(label, Priority.ALWAYS);

        return container;
    }

}
