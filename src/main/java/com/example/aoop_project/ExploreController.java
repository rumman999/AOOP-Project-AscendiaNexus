package com.example.aoop_project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class ExploreController implements Initializable {

    @FXML private HBox adminControls;
    @FXML private TextField fldPlaylistName, fldPlaylistUrl;
    @FXML private ListView<Playlist> listPlaylists;
    @FXML private WebView webView;
    @FXML private TitledPane chatbotPane;

    @FXML private AnchorPane chatbotContainer;
    @FXML private AnchorPane todoContainer;

    @FXML private TextField focusTimerField; // user sets minutes
    @FXML private Button startFocusButton;
    @FXML private Label focusCounterLabel;

    private final PlaylistDAO dao = new PlaylistDAO();

    // Focus mode state
    private boolean focusModeActive = false;
    private Timeline focusTimeline;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Load chatbot and todo
        loadChatbot();
        loadTodoList();

        // Admin controls visibility
        adminControls.setVisible("Admin".equals(Session.getLoggedInUserType()));

        // Playlist ListView styling and wrapping
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
                    label.setText(item.getName());
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

        // Admin buttons styling
        adminControls.setSpacing(10);
        adminControls.setStyle("-fx-padding: 6;");
        adminControls.setAlignment(Pos.CENTER_LEFT);
        adminControls.getChildren().forEach(node -> {
            if (node instanceof Button btn) {
                styleAdminButton(btn);
            }
        });

        // Playlist ListView background
        listPlaylists.setStyle(
                "-fx-control-inner-background: #FFF7E0;" +
                        "-fx-selection-bar: #e67e22;" +
                        "-fx-selection-bar-text: white;"
        );

        // Ensure chatbot/todo scroll behavior
        setupScrollBehavior(chatbotContainer);
        setupScrollBehavior(todoContainer);

        // Setup ESC key blocking
        listPlaylists.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (focusModeActive && event.getCode().toString().equals("ESCAPE")) {
                        event.consume();
                        showTopAlert(Alert.AlertType.INFORMATION,
                                "Focus Mode is active. You cannot exit until the timer finishes.");
                    }
                });
            }
        });

        // Start Focus Mode button
        if (startFocusButton != null) {
            startFocusButton.setOnAction(e -> handleStartFocusMode());
        }
    }

    // -------------------- Focus Mode --------------------
    @FXML
    private void handleStartFocusMode() {
        String text = focusTimerField.getText().trim();
        if (text.isEmpty()) return;

        AtomicInteger seconds = new AtomicInteger();
        try {
            seconds.set(Integer.parseInt(text));
            if (seconds.get() <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showTopAlert(Alert.AlertType.WARNING, "Please enter a valid positive number");
            return;
        }

        focusModeActive = true;
        focusTimerField.setDisable(true);
        startFocusButton.setDisable(true);

        showTopAlert(Alert.AlertType.INFORMATION,
                "Focus Mode started for " + seconds + " seconds.\nYou cannot exit until the timer finishes.");

        // Show counter label
        focusCounterLabel.setVisible(true);
        focusCounterLabel.setText(formatTime(seconds.get()));

        // Countdown timer
        focusTimeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            seconds.getAndDecrement(); // decrement every second
            focusCounterLabel.setText(formatTime(seconds.get()));

            if (seconds.get() <= 0) {
                focusModeActive = false;
                focusTimerField.setDisable(false);
                startFocusButton.setDisable(false);
                focusCounterLabel.setVisible(false); // hide counter
                showTopAlert(Alert.AlertType.INFORMATION, "Focus Mode finished! You can now exit.");
            }
        }));
        focusTimeline.setCycleCount(seconds.get());
        focusTimeline.play();
    }

    // Helper method to show alert near the top of the screen
    private void showTopAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.initOwner(focusTimerField.getScene().getWindow()); // sets owner window

        // Position near top center
        alert.setOnShown(ev -> {
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setX(stage.getOwner().getX() + stage.getOwner().getWidth()/2 - stage.getWidth()/2);
            stage.setY(stage.getOwner().getY() + 20); // 20px from top
        });

        alert.showAndWait();
    }



    // -------------------- Helpers --------------------
    private void styleAdminButton(Button btn) {
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
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #ff914d;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-padding: 6 12;"
        ));
        btn.setOnMouseExited(e -> styleAdminButton(btn));
    }

    private void setupScrollBehavior(AnchorPane container) {
        container.getChildren().forEach(node -> {
            if (node instanceof ScrollPane sp) {
                sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            }
        });
    }

    private void loadChatbot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatBotMini.fxml"));
            Region content = loader.load();
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
            Region content = loader.load();
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

    private void loadAll() {
        try {
            List<Playlist> all = dao.findAll();
            listPlaylists.getItems().setAll(all);
        } catch (Exception e) {
            showTopAlert(Alert.AlertType.WARNING, "Failed to load playlists");
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
            showTopAlert(Alert.AlertType.WARNING, "Failed to add playlist");
        }
    }

    private void loadPlaylist(Playlist p) {
        WebEngine engine = webView.getEngine();
        String id = YouTubeUtil.extractPlaylistId(p.getUrl());
        engine.load(YouTubeUtil.toEmbedUrl(id));
    }

    @FXML
    private void handleBack() {
        if (focusModeActive) {
            showTopAlert(Alert.AlertType.INFORMATION,
                    "Focus Mode is active. You cannot exit until the timer finishes.");
            return;
        }

        webView.getEngine().load(null); // stop video
        getStartedApplication.launchScene("JobSeekerDashboard.fxml");
        Stage s = (Stage) webView.getScene().getWindow();
        s.close();
    }
    private String formatTime(int totalSeconds) {
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}
