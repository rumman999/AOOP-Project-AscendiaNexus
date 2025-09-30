package com.example.aoop_project.messaging;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class UserSelectionDialog {

    public static List<Integer> show(List<String> userNames, List<Integer> userIds) {
        Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Select Group Members");

        ListView<CheckBox> listView = new ListView<>();
        for (int i = 0; i < userNames.size(); i++) {
            CheckBox cb = new CheckBox(userNames.get(i));
            cb.setUserData(userIds.get(i));
            listView.getItems().add(cb);
        }

        Button confirmBtn = new Button("Create Group");
        List<Integer> selected = new ArrayList<>();
        confirmBtn.setOnAction(e -> {
            selected.clear();
            for (CheckBox cb : listView.getItems()) {
                if (cb.isSelected()) {
                    selected.add((Integer) cb.getUserData());
                }
            }
            stage.setUserData(selected); // store selection
            stage.close();
        });

        stage.setOnCloseRequest(e -> stage.setUserData(new ArrayList<Integer>()));

        // ----- Root layout with orange background -----
        VBox layout = new VBox(10, listView, confirmBtn);
        layout.setPrefSize(300, 400);
        layout.setBackground(new Background(new BackgroundFill(Color.web("#FFD580"), CornerRadii.EMPTY, null))); // light orange tint

        stage.setScene(new Scene(layout));
        stage.showAndWait();

        return (List<Integer>) stage.getUserData();
    }
}
