package com.example.aoop_project.Controllers;

import com.example.aoop_project.games.bubbleshooter.BubbleShooter;
import com.example.aoop_project.games.flappybird.FlappyBird;
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
        launchGameOnce(new FlappyBird(), "Flappy Bird", () -> flappyBirdStage, stage -> flappyBirdStage = stage);
    }

    @FXML
    private void handleBubbleShooter(ActionEvent event) {
        launchGameOnce(new BubbleShooter(), "Bubble Shooter", () -> bubbleShooterStage, stage -> bubbleShooterStage = stage);
    }

    /**
     * Launches the game only once. If the stage already exists, it brings it to the front.
     *
     * @param gameApp The JavaFX Application
     * @param title   Window title
     * @param stageGetter Lambda to get the current Stage reference
     * @param stageSetter Lambda to set the Stage reference
     */
    private void launchGameOnce(Application gameApp, String title,
                                java.util.function.Supplier<Stage> stageGetter,
                                java.util.function.Consumer<Stage> stageSetter) {
        try {
            Stage gameStage = stageGetter.get();
            if (gameStage != null && gameStage.isShowing()) {
                gameStage.toFront();  // bring existing window to front
                return;
            }

            // Create new stage if not already open
            gameStage = new Stage();
            gameApp.start(gameStage);
            gameStage.setTitle(title);
            gameStage.setResizable(false);
            gameStage.setAlwaysOnTop(true);
            gameStage.setOnCloseRequest(e -> stageSetter.accept(null)); // clear reference when closed
            gameStage.show();

            stageSetter.accept(gameStage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
