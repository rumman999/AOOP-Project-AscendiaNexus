package com.example.aoop_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ExploreController implements Initializable {

    @FXML private HBox adminControls;
    @FXML private TextField fldPlaylistUrl;
    @FXML private ListView<String> listPlaylists;
    @FXML private WebView webView;

    private final PlaylistDAO playlistDAO = new PlaylistDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Show admin controls if user is admin
        boolean isAdmin = "Admin".equals(Session.getLoggedInUserType());
        adminControls.setVisible(isAdmin);

        loadPlaylists();
    }

    private void loadPlaylists() {
        try {
            List<String> urls = playlistDAO.findAllUrls();
            listPlaylists.getItems().setAll(urls);
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Failed to load playlists").showAndWait();
        }
    }

    @FXML
    private void handleAddPlaylist() {
        String url = fldPlaylistUrl.getText().trim();
        if (url.isEmpty()) return;

        try {
            playlistDAO.create(url, Session.getLoggedInUserId());
            fldPlaylistUrl.clear();
            loadPlaylists();
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Failed to add playlist").showAndWait();
        }
    }

    @FXML
    private void handleSelectPlaylist() {
        String selected = listPlaylists.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        String playlistId = YouTubeUtil.extractPlaylistId(selected);
        String embedUrl = YouTubeUtil.toEmbedUrl(playlistId);
        WebEngine engine = webView.getEngine();
        engine.load(embedUrl);
    }

    @FXML
    private void handleBack() {
        getStartedApplication.launchScene("JobSeekerDashboard.fxml");
    }
}
