package com.example.aoop_project.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class LofiMusicController {

    @FXML private Label songLabel;
    @FXML private Button playPauseBtn;
    @FXML private Button exitButton;   // add fx:id="exitButton" in FXML

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private int currentIndex = 0;

    private final List<String> playlist = new ArrayList<>();
    private Stage stage; // reference to this popup stage

    @FXML
    public void initialize() {
        // Load songs into playlist (paths must exist in resources)
        playlist.add(getClass().getResource("/com/example/aoop_project/music/lofi1.mp3").toExternalForm());
        playlist.add(getClass().getResource("/com/example/aoop_project/music/lofi2.mp3").toExternalForm());
        playlist.add(getClass().getResource("/com/example/aoop_project/music/lofi3.mp3").toExternalForm());

        // Start with the first song
        playSong(currentIndex);
    }

    /** Called by MainController after FXML is loaded */
    public void initStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handlePlayPause() {
        if (mediaPlayer == null) return;

        if (isPlaying) {
            mediaPlayer.pause();
            playPauseBtn.setText("Play");
        } else {
            mediaPlayer.play();
            playPauseBtn.setText("Pause");
        }
        isPlaying = !isPlaying;
    }

    @FXML
    private void handleNext() {
        currentIndex++;
        if (currentIndex >= playlist.size()) currentIndex = 0;
        playSong(currentIndex);
    }

    @FXML
    private void handlePrev() {
        currentIndex--;
        if (currentIndex < 0) currentIndex = playlist.size() - 1;
        playSong(currentIndex);
    }

    private void playSong(int index) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose(); // clean up old instance
        }

        Media sound = new Media(playlist.get(index));
        mediaPlayer = new MediaPlayer(sound);

        mediaPlayer.setOnEndOfMedia(this::handleNext);

        songLabel.setText("Now Playing: Lofi Track " + (index + 1));
        mediaPlayer.play();
        playPauseBtn.setText("Pause");
        isPlaying = true;
    }

    /** Exit button inside popup — really closes the window */
    @FXML
    private void handleExit() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        if (stage == null) {
            stage = (Stage) exitButton.getScene().getWindow();
        }

        // Mark for force close so MainController lets it close
        stage.getProperties().put("forceClose", true);
        stage.close();
    }
}


