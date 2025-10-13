package com.example.aoop_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ExploreController implements Initializable {

    @FXML private HBox adminControls;
    @FXML private TextField fldPlaylistName, fldPlaylistUrl;
    @FXML private ListView<Playlist> listPlaylists;
    @FXML private WebView webView;
    @FXML private TitledPane chatbotPane;

    // 🟢 Added only these two lines
    @FXML private AnchorPane chatbotContainer;
    @FXML private AnchorPane todoContainer;

    private final PlaylistDAO dao = new PlaylistDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // 🟢 Load chatbot and todo UI first
        loadChatbot();
        loadTodoList();

        // (Your existing initialization logic)
        adminControls.setVisible("Admin".equals(Session.getLoggedInUserType()));

        // ✅ Wrap text for Playlist ListView items and small elegant font
        listPlaylists.setCellFactory(lv -> new ListCell<Playlist>() {
            private final Label label = new Label();

            {
                label.setWrapText(true);
                label.setStyle("-fx-font-size: 11px; -fx-text-fill: #333333;");
                label.setMaxWidth(Double.MAX_VALUE);
            }

            @Override
            protected void updateItem(Playlist item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    label.setText(item.getName()); // show playlist name
                    setGraphic(label);
                }
            }
        });

        listPlaylists.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) -> {
                    if (newValue != null) loadPlaylist(newValue);
                }
        );

        loadAll();

        // ------------------------------
        // Styling enhancements (non-intrusive)
        // ------------------------------

        // Admin controls HBox
        adminControls.setSpacing(10);
        adminControls.setStyle("-fx-padding: 6;");
        adminControls.setAlignment(Pos.CENTER_LEFT);

        // Style each button inside adminControls
        adminControls.getChildren().forEach(node -> {
            if (node instanceof Button btn) {
                btn.setStyle(
                        "-fx-background-color: #e67e22;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 6px;" +
                                "-fx-padding: 6 12;"
                );
                btn.setMinWidth(Region.USE_PREF_SIZE);
                btn.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btn, Priority.ALWAYS);

                // Subtle hover effect
                btn.setOnMouseEntered(e -> btn.setStyle(
                        "-fx-background-color: #ff914d;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 6px;" +
                                "-fx-padding: 6 12;"
                ));
                btn.setOnMouseExited(e -> btn.setStyle(
                        "-fx-background-color: #e67e22;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 6px;" +
                                "-fx-padding: 6 12;"
                ));
            }
        });

        // Playlist ListView background and selection theme
        listPlaylists.setStyle(
                "-fx-control-inner-background: #FFF7E0;" +
                        "-fx-selection-bar: #e67e22;" +
                        "-fx-selection-bar-text: white;"
        );

        // Ensure chatbot and todo ScrollPane behavior is consistent
        chatbotContainer.getChildren().forEach(node -> {
            if (node instanceof ScrollPane sp) {
                sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            }
        });
        todoContainer.getChildren().forEach(node -> {
            if (node instanceof ScrollPane sp) {
                sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            }
        });
    }



    // 🟢 These two helper methods are new additions — do not touch existing logic
    private void loadChatbot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatBotMini.fxml"));
            javafx.scene.layout.Region content = loader.load();

            ScrollPane scroll = new ScrollPane(content);
            scroll.setFitToWidth(true);
            scroll.setFitToHeight(true);
            scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            AnchorPane.setTopAnchor(scroll, 0.0);
            AnchorPane.setRightAnchor(scroll, 0.0);
            AnchorPane.setBottomAnchor(scroll, 0.0);
            AnchorPane.setLeftAnchor(scroll, 0.0);

            chatbotContainer.getChildren().setAll(scroll);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTodoList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TodoMini.fxml"));
            javafx.scene.layout.Region content = loader.load();

            ScrollPane scroll = new ScrollPane(content);
            scroll.setFitToWidth(true);
            scroll.setFitToHeight(true);
            scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            AnchorPane.setTopAnchor(scroll, 0.0);
            AnchorPane.setRightAnchor(scroll, 0.0);
            AnchorPane.setBottomAnchor(scroll, 0.0);
            AnchorPane.setLeftAnchor(scroll, 0.0);

            todoContainer.getChildren().setAll(scroll);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // 🟢 Everything below is unchanged (original logic)
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
        webView.getEngine().load(null); // this unloads the current page and stops the video

        // Switch scene
        getStartedApplication.launchScene("JobSeekerDashboard.fxml");

        // Close current stage
        Stage s = (Stage) webView.getScene().getWindow();
        s.close();
    }
}
