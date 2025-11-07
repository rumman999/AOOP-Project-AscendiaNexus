package com.example.aoop_project.Controllers;

// --- Keep ALL existing imports ---
import com.example.aoop_project.*;
import com.example.aoop_project.dao.PostDAO;
import com.example.aoop_project.models.Comment;
import com.example.aoop_project.models.Post;
import com.example.aoop_project.models.UserData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class JobSeekerDashboardController implements Initializable {

    @FXML private Label dashBoardName;
    @FXML private Label UserName;
    @FXML private Label accountDes;
    @FXML private Button logout_button;
    @FXML private ImageView dassprofilePicView;
    /*@FXML private Button ModerateContent;*/
    @FXML private Button postJobs;
    @FXML private Button btnUserSearch;
    @FXML private TextField searchUserField;
    @FXML private Button btnJobSearch;
    @FXML private VBox postsContainerDashboard; // Container for feed posts on dashboard

    private static String profile;
    private static Image defaultImage;
    private Stage userSearchStage;
    private Stage gameSelectionStage;

    private ContextMenu searchResultsPopup;
    private List<UserData> allUsersCache = new ArrayList<>();

    private final PostDAO postDAO = new PostDAO();
    private final int POST_PREVIEW_LIMIT = 3; // Number of posts to show on dashboard

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String acc = Session.getLoggedInUserType();
        if(acc == null) acc = ""; // Avoid NullPointerException

        // Correctly hide/show buttons based on role
        if(acc.equals("Jobseeker")){
            postJobs.setVisible(false); postJobs.setManaged(false);
            /*ModerateContent.setVisible(false); ModerateContent.setManaged(false);*/
        } else if(acc.equals("Recruiter")){
            /*ModerateContent.setVisible(false); ModerateContent.setManaged(false);*/
            postJobs.setVisible(true); postJobs.setManaged(true);
        } else { // Admin or others (assuming Admin should see both)
            postJobs.setVisible(true); postJobs.setManaged(true);
            /*ModerateContent.setVisible(true); ModerateContent.setManaged(true);*/
        }


        if (defaultImage == null) {
            try {
                defaultImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/aoop_project/images/chatbot2-unscreen.gif")));
            } catch (Exception ex) {
                System.out.println("⚠ Default profile image not found!");
                // Consider loading a simpler fallback
            }
        }

        loadUserDataFromDB();
        setupLiveUserSearch();

        // Load Limited Posts
        if (postsContainerDashboard != null) {
            loadPosts(POST_PREVIEW_LIMIT);
        } else {
            System.err.println("postsContainerDashboard is null. Check FXML fx:id.");
        }

        // Profile Picture Loading
        loadAndSetProfilePicture();

        double size = 72;
        dassprofilePicView.setFitWidth(size);
        dassprofilePicView.setFitHeight(size);
        dassprofilePicView.setPreserveRatio(false);
        dassprofilePicView.setSmooth(true);

        Circle clip = new Circle(size / 2, size / 2, size / 2);
        dassprofilePicView.setClip(clip);

        dassprofilePicView.setOnMouseClicked(e -> handleUploadPic());
    }

    // --- Refactored Profile Picture Loading ---
    private void loadAndSetProfilePicture() {
        Image imgToShow = defaultImage; // Start with default
        if (profile != null && !profile.isEmpty()) {
            try {
                if (profile.startsWith("file:")) {
                    imgToShow = new Image(profile, true); // Load in background
                } else {
                    File imgFile = new File(profile);
                    if (imgFile.exists()) {
                        imgToShow = new Image(imgFile.toURI().toString(), true); // Load in background
                    } else {
                        System.out.println("⚠ Profile image file not found at: " + profile + ". Using default.");
                    }
                }
                // Check for loading errors after attempting to load
                if (imgToShow.isError()) {
                    System.out.println("⚠ Error loading profile image URI: " + profile + ". Using default.");
                    if(imgToShow.getException() != null) imgToShow.getException().printStackTrace(); // Print error details
                    imgToShow = defaultImage;
                }
            } catch (Exception ex) {
                System.out.println("⚠ Exception loading profile image: " + ex.getMessage() + ". Using default.");
                ex.printStackTrace(); // Print error details
                imgToShow = defaultImage;
            }
        }

        // Ensure dassprofilePicView is not null before setting the image
        if (dassprofilePicView != null) {
            // If imgToShow is still null (e.g., default failed too), handle it
            if (imgToShow == null) {
                System.err.println("Fallback default image is also null. Cannot set profile picture.");
                // Optionally create a placeholder color or leave it empty
                return; // Exit if no image can be set
            }

            dassprofilePicView.setImage(imgToShow);
            // Add listener for background loading status
            Image finalImgToShow = imgToShow;
            imgToShow.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                if (newProgress.doubleValue() == 1.0) {
                    if (finalImgToShow.isError()) {
                        System.out.println("⚠ Background load failed for: " + profile + ". Reverting to default.");
                        dassprofilePicView.setImage(defaultImage);
                    } else {
                        // Image loaded successfully in background
                    }
                }
            });
            imgToShow.errorProperty().addListener((obs, wasError, isError) -> {
                if (isError) {
                    System.out.println("⚠ Error property triggered for: " + profile + ". Reverting to default.");
                    // Check if defaultImage itself is the problem
                    if (dassprofilePicView.getImage() != defaultImage || defaultImage == null) {
                        dassprofilePicView.setImage(defaultImage);
                    }
                }
            });
        } else {
            System.err.println("dassprofilePicView is null during image setting.");
        }
    }


    private Connection getConnection() throws SQLException {
        String SUrl = "jdbc:mysql://localhost:4306/java_user_database";
        String SUser = "root";
        String SPass = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL Driver not found", e);
        }
        return DriverManager.getConnection(SUrl, SUser, SPass);
    }


    // --- LIVE SEARCH METHODS ---
    private void setupLiveUserSearch() {
        searchResultsPopup = new ContextMenu();
        searchResultsPopup.setStyle("-fx-max-width: 393px; -fx-pref-width: 393px;");
        loadAllUsersIntoCache();
        searchUserField.textProperty().addListener((obs, oldVal, newVal) -> onSearchTextChanged(newVal));
        searchUserField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { Platform.runLater(() -> { if (!searchResultsPopup.isShowing()) searchResultsPopup.hide(); });
            } else { onSearchTextChanged(searchUserField.getText()); }
        });
        searchResultsPopup.setOnAction(e -> searchResultsPopup.hide());
    }

    private void loadAllUsersIntoCache() {
        allUsersCache.clear();
        String query = "SELECT id, full_name, email, account_type FROM user WHERE id != ?";
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, Session.getLoggedInUserId());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                allUsersCache.add(new UserData(rs.getInt("id"), rs.getString("full_name"), rs.getString("email"), rs.getString("account_type"), null));
            }
        } catch (Exception e) { e.printStackTrace(); System.err.println("Error loading users into cache: " + e.getMessage()); }
    }

    private void onSearchTextChanged(String query) {
        searchResultsPopup.getItems().clear();
        if (query == null || query.trim().isEmpty()) { searchResultsPopup.hide(); return; }
        String lowerCaseQuery = query.toLowerCase().trim();
        List<UserData> filteredUsers = allUsersCache.stream()
                .filter(user -> user.getFullName().toLowerCase().contains(lowerCaseQuery) || user.getEmail().toLowerCase().contains(lowerCaseQuery))
                .limit(7).collect(Collectors.toList());
        if (filteredUsers.isEmpty()) { MenuItem noResult = new MenuItem("No users found."); noResult.setDisable(true); searchResultsPopup.getItems().add(noResult);
        } else {
            for (UserData user : filteredUsers) {
                String itemText = user.getFullName() + " (" + user.getAccountType() + ")";
                MenuItem item = new MenuItem(itemText);
                item.setOnAction(e -> onUserSelected(user));
                searchResultsPopup.getItems().add(item);
            }
        }
        if (searchUserField.isFocused() && !searchResultsPopup.getItems().isEmpty()) { searchResultsPopup.show(searchUserField, Side.BOTTOM, 0, 0);
        } else { searchResultsPopup.hide(); }
    }

    private void onUserSelected(UserData user) {
        System.out.println("Selected user: " + user.getFullName() + " (ID: " + user.getId() + ")");
        Session.setProfileToViewId(user.getId());
        AscendiaNexusApp.launchScene("ViewProfile.fxml");
        Platform.runLater(() -> { searchUserField.clear(); searchResultsPopup.hide(); });
    }
    // --- END OF LIVE SEARCH METHODS ---

    private void loadUserDataFromDB() {
        int userId = Session.getLoggedInUserId();
        if (userId <= 0) { System.err.println("No logged-in user ID found in session."); return; }
        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement("SELECT full_name, account_type, profile_pic FROM user WHERE id=?")) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String fullName = rs.getString("full_name");
                String accountType = rs.getString("account_type");
                String profilePic = rs.getString("profile_pic");
                Session.setLoggedInUserName(fullName); Session.setLoggedInUserType(accountType);
                profile = (profilePic != null && !profilePic.isEmpty()) ? profilePic : null;
                UserName.setText(fullName); dashBoardName.setText(fullName); accountDes.setText(accountType);
            } else { System.err.println("Could not find user data for ID: " + userId); UserName.setText("User Not Found"); dashBoardName.setText(""); accountDes.setText(""); }
        } catch (Exception e) { e.printStackTrace(); showAlert("Error loading user data: " + e.getMessage()); }
    }

    @FXML
    private void handleLogout(ActionEvent e) {
        Session.clear();
        AscendiaNexusApp.launchScene("login.fxml");
    }

    @FXML
    private void handleGame(ActionEvent e) {
        try {
            if (gameSelectionStage == null || !gameSelectionStage.isShowing()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("GameSelection.fxml"));
                Parent root = loader.load();
                Stage owner = (Stage) ((Node) e.getSource()).getScene().getWindow();
                gameSelectionStage = new Stage();
                gameSelectionStage.setScene(new Scene(root));
                gameSelectionStage.setTitle("Game Selection");
                gameSelectionStage.initOwner(owner);
                gameSelectionStage.initModality(Modality.WINDOW_MODAL);
                gameSelectionStage.setResizable(false);
                gameSelectionStage.setOnShown(event -> {
                    gameSelectionStage.setX(owner.getX() + (owner.getWidth() - gameSelectionStage.getWidth()) / 2);
                    gameSelectionStage.setY(owner.getY() + (owner.getHeight() - gameSelectionStage.getHeight()) / 2);
                });
                gameSelectionStage.show();
            } else { gameSelectionStage.toFront(); }
        } catch (IOException ex) { ex.printStackTrace(); new Alert(Alert.AlertType.ERROR, "Could not load game selection window.").showAndWait(); }
    }

    private Stage chatStage;
    @FXML
    private void handleChatbot(ActionEvent e) {
        try {
            if (chatStage != null) { if (!chatStage.isShowing()) chatStage.show(); chatStage.toFront(); return; }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/aoop_project/ChatBot.fxml"));
            Parent root = loader.load();
            Stage owner = (Stage) ((Node) e.getSource()).getScene().getWindow();
            chatStage = new Stage(StageStyle.UNDECORATED);
            chatStage.setTitle("Skill Buddy");
            chatStage.setScene(new Scene(root, 450, 550));
            chatStage.setResizable(false);
            chatStage.initOwner(owner);
            chatStage.initModality(Modality.NONE);
            chatStage.setAlwaysOnTop(true);
            Runnable positionChat = () -> {
                if (chatStage == null || owner == null) return; // Add null checks
                double x = owner.getX() + owner.getWidth() - chatStage.getWidth() - 20;
                double y = owner.getY() + owner.getHeight() - chatStage.getHeight() - 35;
                // Ensure stage is not positioned off-screen
                x = Math.max(0, x);
                y = Math.max(0, y);
                chatStage.setX(x); chatStage.setY(y);
            };
            chatStage.setOnShown(ev -> positionChat.run());
            // Add listeners safely
            owner.xProperty().addListener((obs, o, n) -> Platform.runLater(positionChat));
            owner.yProperty().addListener((obs, o, n) -> Platform.runLater(positionChat));
            owner.widthProperty().addListener((obs, o, n) -> Platform.runLater(positionChat));
            owner.heightProperty().addListener((obs, o, n) -> Platform.runLater(positionChat));

            chatStage.setOnCloseRequest(ev -> chatStage = null);
            chatStage.show();
        } catch (IOException ex) { ex.printStackTrace(); showAlert("Could not load Chatbot: " + ex.getMessage());}
        catch (Exception ex) { // Catch other potential runtime errors
            ex.printStackTrace();
            showAlert("An error occurred opening the Chatbot: " + ex.getMessage());
        }
    }


    @FXML
    private void handleOpenExplore(ActionEvent e) {
        AscendiaNexusApp.launchScene("com/example/aoop_project/Explore.fxml");
    }

    private Stage musicStage;
    @FXML
    public void handleMusic(ActionEvent e) {
        try {
            if (musicStage == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/aoop_project/LofiMusic.fxml"));
                Parent root = loader.load();
                musicStage = new Stage();
                musicStage.setScene(new Scene(root));
                // Ensure the owner stage is valid before setting owner
                Node sourceNode = (Node) e.getSource();
                if (sourceNode != null && sourceNode.getScene() != null && sourceNode.getScene().getWindow() != null) {
                    musicStage.initOwner(sourceNode.getScene().getWindow());
                } else {
                    System.err.println("Could not determine owner window for music stage.");
                }

                musicStage.initModality(Modality.NONE);
                musicStage.setWidth(420); musicStage.setHeight(200);
                musicStage.setResizable(false); musicStage.initStyle(StageStyle.UNDECORATED);
                musicStage.setAlwaysOnTop(true);
                musicStage.setOnCloseRequest(event -> {
                    Boolean forceClose = Boolean.TRUE.equals(musicStage.getProperties().get("forceClose"));
                    if (!forceClose) { event.consume(); musicStage.hide(); }
                });
                musicStage.setOnHidden(evt -> {
                    Boolean forceClose = Boolean.TRUE.equals(musicStage.getProperties().get("forceClose"));
                    if (Boolean.TRUE.equals(forceClose)) { musicStage.getProperties().remove("forceClose"); musicStage = null; }
                });
                LofiMusicController controller = loader.getController();
                controller.initStage(musicStage);
                musicStage.show();
            } else { if (!musicStage.isShowing()) musicStage.show(); else musicStage.toFront(); }
        } catch (IOException ex) { ex.printStackTrace(); showAlert("Could not load Music Player: " + ex.getMessage()); }
        catch (Exception ex) { // Catch other potential runtime errors
            ex.printStackTrace();
            showAlert("An error occurred opening the Music Player: " + ex.getMessage());
        }
    }


    @FXML
    private void handleJobSearch(ActionEvent e) {
        AscendiaNexusApp.launchScene("JobSearch.fxml");
    }

    @FXML
    private void handlePostJobs(ActionEvent e) {
        AscendiaNexusApp.launchScene("AdminJobManager.fxml");
    }


    @FXML
    private void handleFeed(ActionEvent e) {
        AscendiaNexusApp.launchScene("Feed.fxml"); // Navigate to the full feed
    }

    @FXML
    public void handleTodolist(ActionEvent e){
        AscendiaNexusApp.launchScene("TodoList.fxml");
    }

    @FXML
    public void handleCV(ActionEvent e){
        AscendiaNexusApp.launchScene("CVBuilder.fxml");
    }

    @FXML
    public void handleProfile(ActionEvent e){
        Session.setProfileToViewId(-1);
        AscendiaNexusApp.launchScene("ViewProfile.fxml");
    }

    @FXML
    public void handleMessages(ActionEvent e){
        AscendiaNexusApp.launchScene("ChatUI.fxml");
    }

    @FXML
    private void handleUserSearch() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserSearch.fxml"));
            Parent root = loader.load();
            Node sourceNode = btnUserSearch; // Use the button as the source
            if (sourceNode == null || sourceNode.getScene() == null || sourceNode.getScene().getWindow() == null) {
                showAlert("Cannot open user search: main window context lost.");
                return;
            }
            Stage owner = (Stage) sourceNode.getScene().getWindow();

            Stage userSearchStage = new Stage();
            userSearchStage.setScene(new Scene(root));
            userSearchStage.setTitle("Search Users");
            userSearchStage.setResizable(false);
            userSearchStage.initOwner(owner);
            userSearchStage.initModality(Modality.WINDOW_MODAL);
            userSearchStage.setOnShown(ev -> {
                double centerX = owner.getX() + (owner.getWidth() - userSearchStage.getWidth()) / 2;
                double centerY = owner.getY() + (owner.getHeight() - userSearchStage.getHeight()) / 2;
                userSearchStage.setX(centerX); userSearchStage.setY(centerY);
            });
            userSearchStage.showAndWait();
        } catch (Exception ex) { ex.printStackTrace(); showAlert("Failed to open user search: " + ex.getMessage()); }
    }


    @FXML
    private void handleUploadPic() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        if (dassprofilePicView == null || dassprofilePicView.getScene() == null || dassprofilePicView.getScene().getWindow() == null) {
            showAlert("Cannot open file chooser: window context lost.");
            return;
        }
        File file = chooser.showOpenDialog(dassprofilePicView.getScene().getWindow());
        if (file != null) {
            String newProfileUri = file.toURI().toString(); // Store the file URI
            try {
                // Load in background and set listeners
                Image newImage = new Image(newProfileUri, true);
                newImage.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                    if (newProgress.doubleValue() == 1.0) {
                        if (newImage.isError()) {
                            showAlert("Failed to load selected image.");
                            dassprofilePicView.setImage(defaultImage);
                            profile = null; // Clear invalid path
                        } else {
                            dassprofilePicView.setImage(newImage);
                            profile = newProfileUri; // Update static variable only on success
                            saveProfilePicToDB(profile); // Save the valid URI
                            System.out.println("🖼 Selected profile image: " + profile);
                        }
                    }
                });
                newImage.errorProperty().addListener((obs, wasError, isError) -> {
                    if (isError) {
                        showAlert("Failed to load selected image.");
                        dassprofilePicView.setImage(defaultImage);
                        profile = null; // Clear invalid path
                    }
                });

            } catch (Exception e) { // Catch initial instantiation errors
                showAlert("Failed to initiate image loading: " + e.getMessage());
                dassprofilePicView.setImage(defaultImage);
                profile = null;
            }
        }
    }

    private void saveProfilePicToDB(String picPath) {
        // Ensure picPath is not null or empty before saving
        if (picPath == null || picPath.trim().isEmpty()) {
            System.err.println("Attempted to save null or empty profile pic path. Aborting save.");
            return;
        }
        String sql = "UPDATE user SET profile_pic = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, picPath); // Save the URI string
            stmt.setInt(2, Session.getLoggedInUserId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Profile picture saved to DB for user " + Session.getLoggedInUserId());
            } else {
                System.err.println("Failed to save profile picture to DB - user ID not found or path unchanged.");
            }
        } catch (Exception e) { e.printStackTrace(); showAlert("Failed to save profile picture: " + e.getMessage()); }
    }

    // --- Feed Methods ---

    /** Loads only a specific number of recent posts */
    private void loadPosts(int limit) {
        if (postsContainerDashboard == null) { System.err.println("postsContainerDashboard is null in loadPosts."); return; }
        postsContainerDashboard.getChildren().clear();
        try {
            List<Post> allPosts = postDAO.getAllPosts();
            List<Post> limitedPosts = allPosts.stream().limit(limit).collect(Collectors.toList());
            for (Post post : limitedPosts) {
                VBox postCard = createPostCard(post);
                postsContainerDashboard.getChildren().add(postCard);
            }
        } catch (Exception e) { e.printStackTrace(); showAlert("Error loading posts: " + e.getMessage()); }
    }

    private VBox createPostCard(Post post) {
        VBox postCard = new VBox(15);
        postCard.setStyle("-fx-background-color: #1a2f26; -fx-background-radius: 15; -fx-padding: 25;");
        // Header
        HBox header = new HBox(15); header.setAlignment(Pos.CENTER_LEFT);
        Circle avatar = new Circle(25, Color.web("#379172")); // Placeholder
        VBox info = new VBox(2);
        Label nameLbl = new Label(post.getUserName()); nameLbl.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
        Label timeLbl = new Label(post.getCreatedAt().format(fmt)); timeLbl.setStyle("-fx-text-fill: #888; -fx-font-size: 12;");
        info.getChildren().addAll(nameLbl, timeLbl); header.getChildren().addAll(avatar, info);
        // Content
        VBox content = new VBox(10);
        if (post.getCaption() != null && !post.getCaption().isBlank()) { Label cap = new Label(post.getCaption()); cap.setWrapText(true); cap.setStyle("-fx-text-fill: white; -fx-font-size: 14;"); content.getChildren().add(cap); }
        if (post.getImagePath() != null && !post.getImagePath().isBlank()) {
            try {
                Image postImage;
                if (post.getImagePath().startsWith("file:")) { postImage = new Image(post.getImagePath(), true); }
                else { File imgFile = new File(post.getImagePath()); postImage = imgFile.exists() ? new Image(imgFile.toURI().toString(), true) : null; }
                ImageView img = new ImageView(); img.setFitWidth(600); img.setPreserveRatio(true); img.setSmooth(true); content.getChildren().add(img);
                if (postImage != null) {
                    postImage.progressProperty().addListener((obs, oldP, newP) -> { if (newP.doubleValue() == 1.0) { if(postImage.isError()){ Label el = new Label("[Image load error]"); el.setStyle("-fx-text-fill: orange;"); int idx = content.getChildren().indexOf(img); if (idx != -1) content.getChildren().set(idx, el);} else { img.setImage(postImage);}}});
                    postImage.errorProperty().addListener((obs, wasError, isError) -> { if (isError) { Label el = new Label("[Image load error]"); el.setStyle("-fx-text-fill: orange;"); int idx = content.getChildren().indexOf(img); if (idx != -1) content.getChildren().set(idx, el);}});
                    if(postImage.getProgress() == 1.0 && !postImage.isError()) { img.setImage(postImage); }
                } else { Label el = new Label("[Image not found]"); el.setStyle("-fx-text-fill: red;"); content.getChildren().set(content.getChildren().indexOf(img), el); }
            } catch (Exception e) { Label el = new Label("[Image display error]"); el.setStyle("-fx-text-fill: red;"); if (!content.getChildren().isEmpty() && content.getChildren().get(content.getChildren().size()-1) instanceof ImageView) { content.getChildren().set(content.getChildren().size()-1, el); } else { content.getChildren().add(el); } System.err.println("Img Err: "+e.getMessage()); }
        }
        // Actions
        HBox actions = new HBox(20); actions.setAlignment(Pos.CENTER_LEFT);
        Button likeBtn = new Button("❤️ " + post.getLikesCount()); likeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #379172; -fx-cursor: hand;"); likeBtn.setOnAction(e -> handleLikePost(post.getId()));
        Button commentBtn = new Button("💬 " + post.getCommentsCount()); commentBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #379172; -fx-cursor: hand;"); commentBtn.setOnAction(e -> handleCommentPost(post.getId()));
        actions.getChildren().addAll(likeBtn, commentBtn);
        postCard.getChildren().addAll(header, content, actions);
        // Comments
        try {
            List<Comment> cmts = postDAO.getCommentsForPost(post.getId());
            if (!cmts.isEmpty()) {
                VBox commentBox = new VBox(10); commentBox.setStyle("-fx-background-color: #0f1f1a; -fx-background-radius: 10; -fx-padding: 10;"); commentBox.setMaxHeight(150);
                ScrollPane commentScrollPane = new ScrollPane(commentBox); commentScrollPane.setFitToWidth(true); commentScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); commentScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); commentScrollPane.setStyle("-fx-background: #0f1f1a; -fx-background-color: #0f1f1a;");
                for (Comment cm : cmts) {
                    HBox row = new HBox(8); row.setAlignment(Pos.TOP_LEFT);
                    Label user = new Label(cm.getUserName() + ":"); user.setStyle("-fx-text-fill: #379172; -fx-font-weight: bold;"); user.setMinWidth(Region.USE_PREF_SIZE);
                    Label txt = new Label(cm.getText()); txt.setWrapText(true); txt.setStyle("-fx-text-fill: white;"); HBox.setHgrow(txt, Priority.ALWAYS);
                    Label ts = new Label(cm.getCreatedAt().format(fmt)); ts.setStyle("-fx-text-fill: #888; -fx-font-size: 10;");
                    VBox v = new VBox(2, txt, ts); HBox.setHgrow(v, Priority.ALWAYS);
                    row.getChildren().addAll(user, v); commentBox.getChildren().add(row);
                } postCard.getChildren().add(commentScrollPane);
            }
        } catch (Exception ex) { ex.printStackTrace(); Label commentErrorLabel = new Label("[Could not load comments]"); commentErrorLabel.setStyle("-fx-text-fill: orange;"); postCard.getChildren().add(commentErrorLabel); }
        return postCard;
    }


    private void handleLikePost(int postId) {
        try {
            int userId = Session.getLoggedInUserId();
            if (postDAO.hasUserLikedPost(postId, userId)) { postDAO.unlikePost(postId, userId); }
            else { postDAO.likePost(postId, userId); }
            loadPosts(POST_PREVIEW_LIMIT);
        } catch (Exception e) { e.printStackTrace(); showAlert("Error processing like: " + e.getMessage()); }
    }

    private void handleCommentPost(int postId) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Comment"); dialog.setHeaderText("Write a comment for post ID: " + postId); dialog.setContentText("Comment:");
        DialogPane dialogPane = dialog.getDialogPane(); dialogPane.setStyle("-fx-background-color: #1a2f26;");
        Platform.runLater(() -> {
            try {
                Node header = dialogPane.lookup(".header-panel"); if(header != null) header.setStyle("-fx-background-color: #1a2f26;");
                Label headerLabel = (Label) dialogPane.lookup(".header-panel .label"); if(headerLabel != null) headerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                Label contentLabel = (Label) dialogPane.lookup(".content .label"); if(contentLabel != null) contentLabel.setStyle("-fx-text-fill: white;");
                dialogPane.lookup(".text-field").setStyle("-fx-background-color: #0f1f1a; -fx-text-fill: white; -fx-border-color: #379172;");
                dialogPane.lookupButton(ButtonType.OK).setStyle("-fx-background-color: #e67e22; -fx-text-fill: white;");
                dialogPane.lookupButton(ButtonType.CANCEL).setStyle("-fx-background-color: #555; -fx-text-fill: white;");
            } catch (Exception e) { System.err.println("Error styling comment dialog: " + e.getMessage()); }
        });
        setupAlertOwner(dialog); // Use helper to set owner
        dialog.showAndWait().ifPresent(commentText -> {
            if (commentText != null && !commentText.trim().isEmpty()) {
                try { postDAO.addComment(postId, Session.getLoggedInUserId(), commentText.trim()); loadPosts(POST_PREVIEW_LIMIT); }
                catch (Exception e) { e.printStackTrace(); showAlert("Error adding comment: " + e.getMessage()); }
            }
        });
    }

    // --- Helper Methods ---
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        setupAlertOwner(alert);
        alert.showAndWait();
    }

    private void showSuccessAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        setupAlertOwner(alert);
        alert.showAndWait();
    }

    private void setupAlertOwner(Dialog<?> dialog) {
        Node ownerNode = postsContainerDashboard; // Default to posts container
        if (ownerNode == null) ownerNode = searchUserField; // Fallback to search field
        if (ownerNode != null && ownerNode.getScene() != null && ownerNode.getScene().getWindow() != null) {
            dialog.initOwner(ownerNode.getScene().getWindow());
            dialog.initModality(Modality.WINDOW_MODAL);
        } else { System.err.println("Could not set owner for dialog - relevant node or scene not available."); }
    }
    // ------------------------------------------------------------

} // End of JobSeekerDashboardController class