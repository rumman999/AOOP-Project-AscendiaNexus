package com.example.aoop_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class FeedController implements Initializable {

    @FXML private Label UserName, accountDes, postAuthorName, selectedImageLabel;
    @FXML private Circle profileClip;
    @FXML private ImageView profilePicView;
    @FXML private TextArea captionArea;
    @FXML private Button addImageBtn, postBtn;
    @FXML private VBox postsContainer;

    private final PostDAO postDAO = new PostDAO();
    private String selectedImagePath = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load user info
        loadUserInfo();

        // Load all posts
        loadPosts();

        // Set user name in post form
        postAuthorName.setText(Session.getLoggedInUserName());
        captionArea.setStyle("-fx-text-fill: white; -fx-prompt-text-fill: gray;");
    }

    private void loadUserInfo() {
        String userName = Session.getLoggedInUserName();
        String userType = Session.getLoggedInUserType();

        UserName.setText(userName != null ? userName : "User");
        accountDes.setText(userType != null ? userType : "Member");
    }

    private void loadPosts() {
        postsContainer.getChildren().clear();

        try {
            List<Post> posts = postDAO.getAllPosts();
            for (Post post : posts) {
                VBox postCard = createPostCard(post);
                postsContainer.getChildren().add(postCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading posts: " + e.getMessage());
        }
    }

    private VBox createPostCard(Post post) {
        VBox postCard = new VBox(15);
        postCard.setStyle("-fx-background-color: #1a2f26; -fx-background-radius: 15; -fx-padding: 25;");

        // 1) Header
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

        // 2) Content
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

        // 3) Actions
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

        // 4) Comments
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
            loadPosts();
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
            loadPosts();
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
                    loadPosts();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error adding comment: " + e.getMessage());
                }
            }
        });
    }

    // Navigation stubs
    @FXML private void handleHome() {}
    @FXML private void handleMusic() {}
    @FXML private void handleMessages() {}
    @FXML private void handleTodolist() {}
    @FXML private void handleChatbot() {}
    @FXML private void handleGame() {}
    @FXML private void handleJobSearch() {}
    @FXML private void handleCV() {}
    @FXML private void handleProfile() {}
    @FXML private void handleLogout() {}
    @FXML private void handlePostJobs() {}
    @FXML private void handleOpenExplore() {}

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }
    private void showSuccessAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
