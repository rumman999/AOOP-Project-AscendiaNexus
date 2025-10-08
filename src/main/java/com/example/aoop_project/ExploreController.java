package com.example.aoop_project;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ExploreController implements Initializable {

    @FXML private HBox adminControls;
    @FXML private TextField fldPlaylistName, fldPlaylistUrl;
    @FXML private ListView<Playlist> listPlaylists;
    @FXML private WebView webView;

    private final PlaylistDAO dao = new PlaylistDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        adminControls.setVisible("Admin".equals(Session.getLoggedInUserType()));
        listPlaylists.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) loadPlaylist(sel);
        });
        loadAll();
    }

    private void loadAll() {
        try {
            List<Playlist> all = dao.findAll();
            listPlaylists.getItems().setAll(all);
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Failed to load playlists").showAndWait();
        }
    }

    @FXML
    private void handleAddPlaylist() {
        String name = fldPlaylistName.getText().trim();
        String url  = fldPlaylistUrl.getText().trim();
        if (name.isEmpty() || url.isEmpty()) return;
        try {
            dao.create(name, url, Session.getLoggedInUserId());
            fldPlaylistName.clear();
            fldPlaylistUrl.clear();
            loadAll();
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Failed to add playlist").showAndWait();
        }
    }

    private void loadPlaylist(Playlist p) {
        WebEngine engine = webView.getEngine();
        String id = YouTubeUtil.extractPlaylistId(p.getUrl());
        engine.load(YouTubeUtil.toEmbedUrl(id));
    }

    @FXML
    private void handleBack() {
        Stage s = (Stage) webView.getScene().getWindow();
        s.close();
        getStartedApplication.launchScene("JobSeekerDashboard.fxml");
    }
}
