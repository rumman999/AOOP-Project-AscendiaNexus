package com.example.aoop_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

public class ViewProfileController implements Initializable {

    // FXML Fields from the "Profile" VBox (Left)
    @FXML private ImageView profilePicView; // The main, large profile picture
    @FXML private Label fullNameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label followersLabel;
    @FXML private Label followingLabel;
    @FXML private Label coinsLabel;
    @FXML private Label followLabel;
    @FXML private Label backLabel;

    // FXML Fields from the "Feed" BorderPane (Top Bar)
    @FXML private ImageView topProfilePicView; // The small, top-bar profile picture
    @FXML private Label UserName;
    @FXML private Label accountDes;

    // FXML Fields from the "Feed" BorderPane (Center Content)
    @FXML private TextArea captionArea;
    @FXML private Label postAuthorName;
    @FXML private Button addImageBtn;
    @FXML private Label selectedImageLabel;
    @FXML private Button postBtn;
    @FXML private VBox postsContainer;

    // DAO Instances
    private final PostDAO postDAO = new PostDAO();
    private final FollowDAO followDAO = new FollowDAO(); // <-- ADDED

    private String selectedImagePath = null;
    private int profileOwnerUserId; // The ID of the user whose profile is being viewed

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // --- Assume we are viewing the logged-in user's own profile ---
        // To view *other* profiles, you would pass the user ID to this controller
        this.profileOwnerUserId = Session.getLoggedInUserId();

        loadUserInfo();
        loadUserStats(); // This will now also update the follow button
        loadUserPosts();

        // Set up the "create post" section to be by the logged-in user
        postAuthorName.setText(Session.getLoggedInUserName());
        captionArea.setStyle("-fx-text-fill: white; -fx-prompt-text-fill: gray;");

        // Add click handlers to the labels used as buttons
        backLabel.setOnMouseClicked(e -> handleDashboard(null));
        followLabel.setOnMouseClicked(e -> handleFollow());
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

    /**
     * Loads the main user info (name, email, pic) into all relevant labels.
     */
    private void loadUserInfo() {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE id=?")) {

            stmt.setInt(1, this.profileOwnerUserId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("full_name");
                String accType = rs.getString("account_type");
                String email = rs.getString("email");
                String picPath = rs.getString("profile_pic");

                // Set profile-specific labels
                fullNameLabel.setText(fullName);
                usernameLabel.setText("@" + email.split("@")[0]); // Example username

                // Set top-bar labels
                UserName.setText(fullName);
                accountDes.setText(accType);

                // Set profile picture
                Image profileImage = new Image(getClass().getResourceAsStream("/com/example/aoop_project/Images/chatbot2-unscreen.gif")); // Default
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

    /**
     * Loads stats like followers, following, and coins.
     */
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

            // *** NEW: Update the follow button status ***
            updateFollowButton();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading user stats: " + e.getMessage());
        }
    }

    /**
     * Checks if the logged-in user is following this profile and updates the button.
     */
    private void updateFollowButton() {
        // Disable button if viewing your own profile
        if (this.profileOwnerUserId == Session.getLoggedInUserId()) {
            followLabel.setText("Your Profile");
            followLabel.setDisable(true);
            followLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #aaaaaa; -fx-border-color: #aaaaaa; -fx-border-width: 2; -fx-padding: 5 20 5 20; -fx-border-radius: 12px;");
        } else {
            // Check follow status
            try {
                boolean isFollowing = followDAO.isFollowing(Session.getLoggedInUserId(), this.profileOwnerUserId);
                followLabel.setDisable(false); // Re-enable if it was disabled
                if (isFollowing) {
                    followLabel.setText("Following");
                    // Style for "Following"
                    followLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #e67e22; -fx-border-color: #e67e22; -fx-border-width: 2; -fx-padding: 5 20 5 20; -fx-border-radius: 12px; -fx-cursor: hand;");
                } else {
                    followLabel.setText("Follow");
                    // Style for "Follow" (your original style)
                    followLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #00f270; -fx-border-color: #00f270; -fx-border-width: 2; -fx-padding: 5 20 5 20; -fx-border-radius: 12px; -fx-cursor: hand;");
                }
            } catch (Exception e) {
                e.printStackTrace();
                followLabel.setText("Error");
                followLabel.setDisable(true);
            }
        }
    }

    /**
     * Loads only the posts belonging to this profile's owner.
     */
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

    /**
     * This method retrieves posts for a specific user.
     */
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


    // --- This block of code is from FeedController.java ---
    // --- It is needed to manage the post feed. ---

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
            p.setUserId(Session.getLoggedInUserId()); // Post as the logged-in user
            p.setCaption(caption);
            p.setImagePath(selectedImagePath);
            postDAO.createPost(p);
            captionArea.clear();
            selectedImagePath = null;
            selectedImageLabel.setText("");
            loadUserPosts(); // Reload this user's posts
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
            loadUserPosts(); // Reload posts to show new like count
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
                    loadUserPosts(); // Reload posts to show new comment
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error adding comment: " + e.getMessage());
                }
            }
        });
    }

    // --- End of FeedController code block ---


    // --- Navigation Handlers ---

    @FXML
    private void handleDashboard(ActionEvent e) {
        getStartedApplication.launchScene("JobSeekerDashboard.fxml");
    }

    @FXML
    private void handleOpenExplore(ActionEvent e) {
        getStartedApplication.launchScene("Explore.fxml");
    }

    /**
     * This method is now fully implemented.
     */
    private void handleFollow() {
        if (this.profileOwnerUserId == Session.getLoggedInUserId()) {
            showAlert("You cannot follow yourself!");
            return;
        }

        try {
            int loggedInUserId = Session.getLoggedInUserId();

            if (followDAO.isFollowing(loggedInUserId, this.profileOwnerUserId)) {
                // --- UNFOLLOW logic ---
                followDAO.unfollow(loggedInUserId, this.profileOwnerUserId);
            } else {
                // --- FOLLOW logic ---
                followDAO.follow(loggedInUserId, this.profileOwnerUserId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error updating follow status: " + e.getMessage());
        }

        // Refresh stats to show new follower count and update button text
        loadUserStats();
    }


    // --- Utility Methods ---

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private void showSuccessAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}