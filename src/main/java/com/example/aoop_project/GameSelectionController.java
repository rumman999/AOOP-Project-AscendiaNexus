package com.example.aoop_project;

import com.example.aoop_project.BubbleShooter.BubbleShooter;
import com.example.aoop_project.FlappyBird.FlappyBird;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class GameSelectionController {

    private Stage flappyBirdStage;
    private Stage bubbleShooterStage;

    @FXML
    private void handleFlappyBird(ActionEvent event) {
        launchGame(new FlappyBird(), flappyBirdStage, "Flappy Bird");
    }

    @FXML
    private void handleBubbleShooter(ActionEvent event) {
        launchGame(new BubbleShooter(), bubbleShooterStage, "Bubble Shooter");
    }

    private void launchGame(Application gameApp, Stage gameStage, String title) {
        try {
            if (gameStage == null || !gameStage.isShowing()) {
                gameStage = new Stage();
                gameApp.start(gameStage);
                gameStage.setTitle(title);
                gameStage.setResizable(false);
                gameStage.show();
            } else {
                gameStage.toFront();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        // Close this selection window
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}