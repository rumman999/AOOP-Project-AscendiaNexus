package com.example.aoop_project.messaging;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class UserSelectionDialog {

    // ❌ Old method removed — must pass owner stage now
    public static List<Integer> show(List<String> userNames, List<Integer> userIds) {
        throw new IllegalArgumentException("You must pass the owner Stage to ensure the dialog appears over the chat window!");
    }

    public static List<Integer> show(List<String> userNames, List<Integer> userIds, Stage ownerStage) {
        if (ownerStage == null) {
            throw new IllegalArgumentException("ownerStage cannot be null — must pass the chat window stage!");
        }

        Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.WINDOW_MODAL); // tied only to owner window
        stage.initOwner(ownerStage);
        stage.setTitle("Create Group");

        // ----- Title Label -----
        Label header = new Label("Select Members for Group");
        header.setStyle("""
                -fx-font-size: 16px;
                -fx-font-weight: bold;
                -fx-text-fill: #ff9800;
        """);

        // ----- Styled ListView -----
        ListView<CheckBox> listView = new ListView<>();
        listView.setPrefHeight(300);
        listView.setStyle("""
                -fx-control-inner-background: #10231c;
                -fx-cell-hover-color: #1a3b2e;
                -fx-text-fill: white;
                -fx-padding: 5;
        """);

        for (int i = 0; i < userNames.size(); i++) {
            CheckBox cb = new CheckBox(userNames.get(i));
            cb.setUserData(userIds.get(i));
            cb.setTextFill(Color.WHITE);
            listView.getItems().add(cb);
        }

        // ----- Styled Confirm Button -----
        Button confirmBtn = new Button("Create Group");
        confirmBtn.setStyle("""
                -fx-background-color: #ff9800;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                -fx-padding: 8 16 8 16;
        """);

        // Hover animation
        confirmBtn.setOnMouseEntered(e -> confirmBtn.setStyle("""
                -fx-background-color: #ffa733;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                -fx-padding: 8 16 8 16;
        """));
        confirmBtn.setOnMouseExited(e -> confirmBtn.setStyle("""
                -fx-background-color: #ff9800;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                -fx-padding: 8 16 8 16;
        """));

        List<Integer> selected = new ArrayList<>();
        confirmBtn.setOnAction(e -> {
            selected.clear();
            for (CheckBox cb : listView.getItems()) {
                if (cb.isSelected()) {
                    selected.add((Integer) cb.getUserData());
                }
            }
            stage.setUserData(selected);
            stage.close();
        });

        stage.setOnCloseRequest(e -> stage.setUserData(new ArrayList<>()));

        // ----- Layout -----
        VBox layout = new VBox(15, header, listView, confirmBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(
                Color.web("#10231c"), new CornerRadii(12), Insets.EMPTY
        )));
        layout.setBorder(new Border(new BorderStroke(
                Color.web("#ff9800"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(12),
                new BorderWidths(2)
        )));

        Scene scene = new Scene(layout, 340, 450);
        stage.setScene(scene);

        // ----- Always position relative to owner window -----
        stage.setX(ownerStage.getX() + (ownerStage.getWidth() - scene.getWidth()) / 2);
        stage.setY(ownerStage.getY() + 50);

        stage.showAndWait();
        return (List<Integer>) stage.getUserData();
    }
}
