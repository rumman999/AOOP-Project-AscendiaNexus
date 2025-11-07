package com.example.aoop_project.Controllers;

import com.example.aoop_project.*;
import com.example.aoop_project.dao.FollowDAO;
import com.example.aoop_project.dao.PostDAO;
import com.example.aoop_project.models.Comment;
import com.example.aoop_project.models.Post;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ViewProfileController implements Initializable {

    // FXML Fields from the "Profile" VBox (Left)
    @FXML private ImageView profilePicView;
    @FXML private Label fullNameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label followersLabel;
    @FXML private Label followingLabel;
    @FXML private Label coinsLabel;
    @FXML private Label followLabel;
    @FXML private Label messageLabel;

    // FXML Fields from the "Feed" BorderPane (Top Bar)
    @FXML private ImageView topProfilePicView;
    @FXML private Label UserName;
    @FXML private Label accountDes;

    // FXML Fields from the "Feed" BorderPane (Center Content)
    @FXML private TextArea captionArea;
    @FXML private Label postAuthorName;
    @FXML private Button addImageBtn;
    @FXML private Label selectedImageLabel;
    @FXML private Button postBtn;
    @FXML private VBox postsContainer;

    // *** ADDED: Live Search Fields ***
    @FXML private TextField searchUserField;
    @FXML private Button btnUserSearch;
    // *********************************

    // DAO Instances
    private final PostDAO postDAO = new PostDAO();
    private final FollowDAO followDAO = new FollowDAO();

    private String selectedImagePath = null;
    private int profileOwnerUserId;
    private String profileOwnerEmail;
    private String profileOwnerFullName;

    // *** ADDED: For Live Search ***
    private ContextMenu searchResultsPopup;
    private List<UserSearchData> allUsersCache = new ArrayList<>();

    // Inner class for user search data
    private static class UserSearchData {
        private final int id;
        private final String fullName;
        private final String email;
        private final String accountType;

        public UserSearchData(int id, String fullName, String email, String accountType) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.accountType = accountType;
        }

        public int getId() { return id; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getAccountType() { return accountType; }
    }
    // ******************************

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        int userIdToLoad = Session.getProfileToViewId();
        this.profileOwnerUserId = (userIdToLoad > 0) ? userIdToLoad : Session.getLoggedInUserId();
        Session.setProfileToViewId(-1);

        loadUserInfo();
        loadUserStats();
        loadUserPosts();

        postAuthorName.setText(Session.getLoggedInUserName());
        captionArea.setStyle("-fx-text-fill: black; -fx-prompt-text-fill: gray;");

        // *** ADDED: Setup live search ***
        if (searchUserField != null) {
            setupLiveUserSearch();
        }
        // ********************************
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:4306/java_user_database";
        String user = "root";
        String pass = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(url, user, pass);
    }

    // ===== LIVE SEARCH METHODS (ADDED) =====

    /**
     * Initializes the live search functionality on the search field.
     */
    private void setupLiveUserSearch() {
        searchResultsPopup = new ContextMenu();
        searchResultsPopup.setStyle("-fx-max-width: 393px; -fx-pref-width: 393px;");

        loadAllUsersIntoCache();

        searchUserField.textProperty().addListener((obs, oldVal, newVal) -> {
            onSearchTextChanged(newVal);
        });

        // Hide popup if text field loses focus
        searchUserField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                Platform.runLater(() -> searchResultsPopup.hide());
            }
        });
    }

    /**
     * Fetches all users from the DB and stores them in a local list for fast filtering.
     */
    private void loadAllUsersIntoCache() {
        allUsersCache.clear();
        String query = "SELECT id, full_name, email, account_type FROM user WHERE id != ?";

        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, Session.getLoggedInUserId());
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                allUsersCache.add(new UserSearchData(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("account_type")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading users into cache: " + e.getMessage());
        }
    }

    /**
     * Called on every keystroke in the search field.
     * Filters the local user cache and displays matching results in the popup.
     */
    private void onSearchTextChanged(String query) {
        searchResultsPopup.getItems().clear();
        if (query == null || query.isEmpty()) {
            searchResultsPopup.hide();
            return;
        }

        String lowerCaseQuery = query.toLowerCase();

        // Filter users based on name or email
        List<UserSearchData> filteredUsers = allUsersCache.stream()
                .filter(user -> user.getFullName().toLowerCase().contains(lowerCaseQuery) ||
                        user.getEmail().toLowerCase().contains(lowerCaseQuery))
                .limit(7) // Show only top 7 matches
                .collect(Collectors.toList());

        if (filteredUsers.isEmpty()) {
            MenuItem noResult = new MenuItem("No users found.");
            noResult.setDisable(true);
            searchResultsPopup.getItems().add(noResult);
        } else {
            for (UserSearchData user : filteredUsers) {
                String itemText = user.getFullName() + " (" + user.getAccountType() + ")";
                MenuItem item = new MenuItem(itemText);
                item.setOnAction(e -> onUserSelected(user));
                searchResultsPopup.getItems().add(item);
            }
        }

        // Show the dropdown popup anchored to the bottom of the search field
        if (!searchResultsPopup.isShowing()) {
            searchResultsPopup.show(searchUserField, Side.BOTTOM, 0, 0);
        }
    }

    /**
     * Called when a user is clicked from the search results dropdown.
     * Sets the session flag and reloads the profile data.
     */
    private void onUserSelected(UserSearchData user) {
        System.out.println("Selected user: " + user.getFullName() + " (ID: " + user.getId() + ")");

        // Set the new profile owner ID
        this.profileOwnerUserId = user.getId();

        // Reload the profile data
        loadUserInfo();
        loadUserStats();
        loadUserPosts();

        // Clear search field and hide popup
        searchUserField.clear();
        searchResultsPopup.hide();
    }

    // ===== END OF LIVE SEARCH METHODS =====

    private void loadUserInfo() {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE id=?")) {

            stmt.setInt(1, this.profileOwnerUserId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                this.profileOwnerFullName = rs.getString("full_name");
                this.profileOwnerEmail = rs.getString("email");
                String accType = rs.getString("account_type");
                String picPath = rs.getString("profile_pic");

                fullNameLabel.setText(this.profileOwnerFullName);
                usernameLabel.setText("@" + this.profileOwnerEmail.split("@")[0]);
                UserName.setText(this.profileOwnerFullName);
                accountDes.setText(accType);

                Image profileImage = new Image(getClass().getResourceAsStream("/com/example/aoop_project/images/chatbot2-unscreen.gif"));
                if (picPath != null && !picPath.isEmpty()) {
                    try {
                        if (picPath.startsWith("http") || picPath.startsWith("file:")) {
                            profileImage = new Image(picPath);
                        } else {
                            profileImage = new Image(new File(picPath).toURI().toString());
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to load profile image: " + e.getMessage());
                    }
                }

                profilePicView.setImage(profileImage);
                topProfilePicView.setImage(profileImage);

            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading user info: " + e.getMessage());
        }
    }

    private void loadUserStats() {
        try (Connection conn = getConnection()) {
            // Get Coins
            try (PreparedStatement stmt = conn.prepareStatement("SELECT Coin FROM user WHERE id=?")) {
                stmt.setInt(1, this.profileOwnerUserId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    coinsLabel.setText(String.valueOf(rs.getInt("Coin")));
                }
            }

            // Get Followers
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM followers WHERE following_id = ?")) {
                stmt.setInt(1, this.profileOwnerUserId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    followersLabel.setText(String.valueOf(rs.getInt(1)));
                }
            }

            // Get Following
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM followers WHERE follower_id = ?")) {
                stmt.setInt(1, this.profileOwnerUserId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    followingLabel.setText(String.valueOf(rs.getInt(1)));
                }
            }

            updateActionButtons();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading user stats: " + e.getMessage());
        }
    }

    private void updateActionButtons() {
        if (this.profileOwnerUserId == Session.getLoggedInUserId()) {

            followLabel.setText("Edit Profile");
            followLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #00f270; -fx-border-color: #00f270; -fx-border-width: 2; -fx-padding: 5 20 5 20; -fx-border-radius: 12px; -fx-cursor: hand;");
            followLabel.setOnMouseClicked(e -> handleEditProfile());
            followLabel.setDisable(false);

            messageLabel.setText("Back");
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #eeeeee; -fx-border-color: #eeeeee; -fx-border-width: 2; -fx-padding: 5 20 5 20; -fx-border-radius: 12px; -fx-cursor: hand;");
            messageLabel.setOnMouseClicked(e -> handleDashboard(null));
            messageLabel.setDisable(false);

        } else {
            try {
                boolean isFollowing = followDAO.isFollowing(Session.getLoggedInUserId(), this.profileOwnerUserId);
                if (isFollowing) {
                    followLabel.setText("Following");
                    followLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #e67e22; -fx-border-color: #e67e22; -fx-border-width: 2; -fx-padding: 5 20 5 20; -fx-border-radius: 12px; -fx-cursor: hand;");
                } else {
                    followLabel.setText("Follow");
                    followLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #00f270; -fx-border-color: #00f270; -fx-border-width: 2; -fx-padding: 5 20 5 20; -fx-border-radius: 12px; -fx-cursor: hand;");
                }
                followLabel.setOnMouseClicked(e -> handleFollow());
                followLabel.setDisable(false);

            } catch (Exception e) {
                e.printStackTrace();
                followLabel.setText("Error");
                followLabel.setDisable(true);
            }

            messageLabel.setText("Message");
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #00f270; -fx-border-color: #00f270; -fx-border-width: 2; -fx-padding: 5 20 5 20; -fx-border-radius: 12px; -fx-cursor: hand;");
            messageLabel.setOnMouseClicked(e -> handleMessage());
            messageLabel.setDisable(false);
        }
    }

    private void loadUserPosts() {
        postsContainer.getChildren().clear();
        try {
            List<Post> posts = getPostsByUserId(this.profileOwnerUserId);
            for (Post post : posts) {
                VBox postCard = createPostCard(post);
                postsContainer.getChildren().add(postCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading posts: " + e.getMessage());
        }
    }

    public List<Post> getPostsByUserId(int userId) throws Exception {
        String sql = """
            SELECT p.*, u.full_name as user_name
            FROM posts p
            JOIN user u ON p.user_id = u.id
            WHERE p.user_id = ?
            ORDER BY p.created_at DESC
        """;
        List<Post> posts = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post();
                    post.setId(rs.getInt("id"));
                    post.setUserId(rs.getInt("user_id"));
                    post.setUserName(rs.getString("user_name"));
                    post.setCaption(rs.getString("caption"));
                    post.setImagePath(rs.getString("image_path"));
                    post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    post.setLikesCount(rs.getInt("likes_count"));
                    post.setCommentsCount(rs.getInt("comments_count"));
                    posts.add(post);
                }
            }
        }
        return posts;
    }

    private VBox createPostCard(Post post) {
        VBox postCard = new VBox(15);
        postCard.setStyle("-fx-background-color: #1a2f26; -fx-background-radius: 15; -fx-padding: 25;");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        Circle avatar = new Circle(25, Color.web("#379172"));
        VBox info = new VBox(2);
        Label nameLbl = new Label(post.getUserName());
        nameLbl.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
        Label timeLbl = new Label(post.getCreatedAt().format(fmt));
        timeLbl.setStyle("-fx-text-fill: #888; -fx-font-size: 12;");
        info.getChildren().addAll(nameLbl, timeLbl);
        header.getChildren().addAll(avatar, info);

        VBox content = new VBox(10);
        if (post.getCaption() != null && !post.getCaption().isBlank()) {
            Label cap = new Label(post.getCaption());
            cap.setWrapText(true);
            cap.setStyle("-fx-text-fill: white; -fx-font-size: 14;");
            content.getChildren().add(cap);
        }
        if (post.getImagePath() != null && !post.getImagePath().isBlank()) {
            try {
                ImageView img = new ImageView(new Image("file:" + post.getImagePath()));
                img.setFitWidth(600); img.setPreserveRatio(true);
                content.getChildren().add(img);
            } catch (Exception e) {
                System.err.println("Image load error: "+e.getMessage());
            }
        }

        HBox actions = new HBox(20);
        actions.setAlignment(Pos.CENTER_LEFT);
        Button likeBtn = new Button("❤️ "+post.getLikesCount());
        likeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #379172;");
        likeBtn.setOnAction(e -> handleLikePost(post.getId()));
        Button commentBtn = new Button("💬 "+post.getCommentsCount());
        commentBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #379172;");
        commentBtn.setOnAction(e -> handleCommentPost(post.getId()));
        actions.getChildren().addAll(likeBtn, commentBtn);

        postCard.getChildren().addAll(header, content, actions);

        try {
            List<Comment> cmts = postDAO.getCommentsForPost(post.getId());
            if (!cmts.isEmpty()) {
                VBox commentBox = new VBox(10);
                commentBox.setStyle("-fx-background-color: #0f1f1a; -fx-background-radius: 10; -fx-padding: 10;");
                for (Comment cm : cmts) {
                    HBox row = new HBox(8);
                    row.setAlignment(Pos.TOP_LEFT);
                    Label user = new Label(cm.getUserName()+":");
                    user.setStyle("-fx-text-fill: #379172; -fx-font-weight: bold;");
                    Label txt = new Label(cm.getText());
                    txt.setWrapText(true);
                    txt.setStyle("-fx-text-fill: white;");
                    Label ts = new Label(cm.getCreatedAt().format(fmt));
                    ts.setStyle("-fx-text-fill: #888; -fx-font-size: 10;");
                    VBox v = new VBox(2, txt, ts);
                    row.getChildren().addAll(user, v);
                    commentBox.getChildren().add(row);
                }
                postCard.getChildren().add(commentBox);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return postCard;
    }

    @FXML
    private void handleAddImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Image");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files","*.png","*.jpg","*.jpeg","*.gif"));
        File file = chooser.showOpenDialog(addImageBtn.getScene().getWindow());
        if (file != null) {
            selectedImagePath = file.getAbsolutePath();
            selectedImageLabel.setText("Image selected: " + file.getName());
        }
    }

    @FXML
    private void handleCreatePost() {
        String caption = captionArea.getText().trim();
        if (caption.isEmpty() && selectedImagePath == null) {
            showAlert("Please add some content to your post!");
            return;
        }
        try {
            Post p = new Post();
            p.setUserId(Session.getLoggedInUserId());
            p.setCaption(caption);
            p.setImagePath(selectedImagePath);
            postDAO.createPost(p);
            captionArea.clear();
            selectedImagePath = null;
            selectedImageLabel.setText("");
            loadUserPosts();
            showSuccessAlert("Post created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error creating post: " + e.getMessage());
        }
    }

    private void handleLikePost(int postId) {
        try {
            int userId = Session.getLoggedInUserId();
            if (postDAO.hasUserLikedPost(postId, userId)) postDAO.unlikePost(postId, userId);
            else postDAO.likePost(postId, userId);
            loadUserPosts();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error processing like: " + e.getMessage());
        }
    }

    private void handleCommentPost(int postId) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Comment");
        dialog.setHeaderText("Write a comment:");
        dialog.setContentText("Comment:");
        dialog.showAndWait().ifPresent(c -> {
            if (!c.trim().isEmpty()) {
                try {
                    postDAO.addComment(postId, Session.getLoggedInUserId(), c);
                    loadUserPosts();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error adding comment: " + e.getMessage());
                }
            }
        });
    }

    // --- Navigation Handlers ---

    @FXML
    private void handleDashboard(ActionEvent e) {
        AscendiaNexusApp.launchScene("JobSeekerDashboard.fxml");
    }

    @FXML
    private void handleOpenExplore(ActionEvent e) {
        AscendiaNexusApp.launchScene("Explore.fxml");
    }

    private void handleFollow() {
        if (this.profileOwnerUserId == Session.getLoggedInUserId()) {
            showAlert("You cannot follow yourself!");
            return;
        }
        try {
            int loggedInUserId = Session.getLoggedInUserId();
            if (followDAO.isFollowing(loggedInUserId, this.profileOwnerUserId)) {
                followDAO.unfollow(loggedInUserId, this.profileOwnerUserId);
            } else {
                followDAO.follow(loggedInUserId, this.profileOwnerUserId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error updating follow status: " + e.getMessage());
        }
        loadUserStats();
    }

    private void handleEditProfile() {
        AscendiaNexusApp.launchScene("Profile.fxml");
    }

    /**
     * Sets the target user in the Session and navigates to the main ChatUI scene.
     */
    private void handleMessage() {
        try {
            Session.setChatTarget(this.profileOwnerUserId, this.profileOwnerFullName);
            AscendiaNexusApp.launchScene("ChatUI.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Could not open chat scene.");
        }
    }

    // --- Utility Methods ---

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private void showSuccessAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    @FXML
    private void handleFeed(ActionEvent e) {
        AscendiaNexusApp.launchScene("Feed.fxml");
    }

    /**
     * Optional: Manual search button handler (if you want to keep the button).
     * The live search works without this, but this provides an alternative way to search.
     */
    @FXML
    private void handleUserSearch(ActionEvent e) {
        String query = searchUserField.getText();
        if (query == null || query.trim().isEmpty()) {
            showAlert("Please enter a search term.");
            return;
        }
        // The live search already handles this automatically,
        // but we can manually trigger it here if needed
        onSearchTextChanged(query);
    }

}