package com.example.aoop_project;

import com.example.aoop_project.games.chess.Interface;
import com.example.aoop_project.games.chess.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.ResourceBundle;

public class JobSeekerDashboardController implements Initializable {

    @FXML private Label dashBoardName;
    @FXML private Label UserName;
    @FXML private Label accountDes;
    @FXML private Button logout_button;
    @FXML private ImageView dassprofilePicView;
    @FXML private Button ModerateContent;
    @FXML private Button postJobs;
    @FXML private Button btnUserSearch;

    private static String profile; // stores user image path
    private static Image defaultImage; // default GIF
    private Stage userSearchStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String acc = Session.getLoggedInUserType();
        if(acc.equals("Jobseeker")){
            postJobs.setDisable(true);
            ModerateContent.setDisable(true);
        }

        if(acc.equals("Recruiter")){
            ModerateContent.setDisable(true);
        }

        // Load default image safely
        if (defaultImage == null) {
            try {
                defaultImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/aoop_project/Images/chatbot2-unscreen.gif")));
            } catch (Exception ex) {
                System.out.println("⚠ Default profile image not found!");
            }
        }

        loadUserDataFromDB(); // fetch logged-in user data automatically

        // Set image: user's image if exists, else default
        Image imgToShow;
        if (profile != null && !profile.isEmpty()) {
            try {
                imgToShow = new Image(profile);
            } catch (Exception ex) {
                imgToShow = defaultImage;
            }
        } else {
            imgToShow = defaultImage;
        }
        dassprofilePicView.setImage(imgToShow);

        // Set image view properties
        double size = 72;
        dassprofilePicView.setFitWidth(size);
        dassprofilePicView.setFitHeight(size);
        dassprofilePicView.setPreserveRatio(false);
        dassprofilePicView.setSmooth(true);

        // Clip image into circle
        Circle clip = new Circle(size / 2, size / 2, size / 2);
        dassprofilePicView.setClip(clip);

        // Allow user to change profile picture
        dassprofilePicView.setOnMouseClicked(e -> handleUploadPic());
    }

    private void loadUserDataFromDB() {
        String userEmail = Session.getLoggedInUserEmail();
        if (userEmail == null || userEmail.isEmpty()) return;

        String SUrl = "jdbc:mysql://localhost:4306/java_user_database";
        String SUser = "root";
        String SPass = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(SUrl, SUser, SPass);

            String query = "SELECT full_name, account_type, profile_pic FROM user WHERE email=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, userEmail);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String fullName = rs.getString("full_name");
                String accountType = rs.getString("account_type");
                String profilePic = rs.getString("profile_pic");

                if (profilePic != null && !profilePic.isEmpty()) {
                    profile = profilePic; // update user image
                }

                UserName.setText(fullName);
                dashBoardName.setText(fullName);
                accountDes.setText(accountType);
            }

            rs.close();
            pst.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Allows user to upload a new profile picture
    private void handleUploadPic() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = chooser.showOpenDialog(dassprofilePicView.getScene().getWindow());
        if (file != null) {
            profile = file.toURI().toString();
            dassprofilePicView.setImage(new Image(profile));
            // Optional: save this path to DB immediately or on next profile save
            System.out.println("🖼 Selected profile image: " + profile);
        }
    }

    @FXML
    private void handleLogout(ActionEvent e) {
        Session.clear(); // clear logged-in user
        getStartedApplication.launchScene("login.fxml");
    }

    // Keep one reference
    private Stage chessStage;

    @FXML
    private void handleGame(ActionEvent e) {
        try {
            if (chessStage == null) {
                Stage owner = (Stage) ((Node) e.getSource()).getScene().getWindow();

                chessStage = new Stage();
                chessStage.initOwner(owner);       // tie to dashboard (z-order)
                chessStage.initModality(Modality.NONE);
                chessStage.setAlwaysOnTop(true);   // keep it above if desired
                chessStage.setResizable(false);

                // Launch your chess UI
                Interface chessApp = new Interface();
                chessApp.start(chessStage);

                // Instead of destroying the stage on close, just hide it
                chessStage.setOnCloseRequest(event -> {
                    event.consume();   // prevent default close
                    chessStage.hide(); // just hide
                });

                chessStage.show();
            } else {
                if (!chessStage.isShowing()) {
                    chessStage.show();
                }
                chessStage.toFront();
                chessStage.requestFocus();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }




    // Chatbot popup
    private Stage chatStage;
    @FXML
    private void handleChatbot(ActionEvent e) {
        try {
            if (chatStage != null) {
                if (!chatStage.isShowing()) chatStage.show();
                chatStage.toFront();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatBot.fxml"));
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
                double x = owner.getX() + owner.getWidth() - chatStage.getWidth() - 20;
                double y = owner.getY() + owner.getHeight() - chatStage.getHeight() - 35;
                chatStage.setX(x);
                chatStage.setY(y);
            };

            chatStage.setOnShown(ev -> positionChat.run());
            owner.xProperty().addListener((obs, o, n) -> positionChat.run());
            owner.yProperty().addListener((obs, o, n) -> positionChat.run());
            owner.widthProperty().addListener((obs, o, n) -> positionChat.run());
            owner.heightProperty().addListener((obs, o, n) -> positionChat.run());

            chatStage.setOnCloseRequest(ev -> chatStage = null);
            chatStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleOpenExplore(ActionEvent e) {
        getStartedApplication.launchScene("Explore.fxml");
    }



    // Music popup
    private Stage musicStage;
    @FXML
    public void handleMusic(ActionEvent e) {
        try {
            if (musicStage == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/aoop_project/LofiMusic.fxml"));
                Parent root = loader.load();

                musicStage = new Stage();
                musicStage.setScene(new Scene(root));
                musicStage.initOwner(((Node) e.getSource()).getScene().getWindow());
                musicStage.initModality(Modality.NONE);
                musicStage.setWidth(420);
                musicStage.setHeight(200);
                musicStage.setResizable(false);
                musicStage.initStyle(StageStyle.UNDECORATED);
                musicStage.setAlwaysOnTop(true);

                musicStage.setOnCloseRequest(event -> {
                    Boolean forceClose = Boolean.TRUE.equals(musicStage.getProperties().get("forceClose"));
                    if (!forceClose) {
                        event.consume();
                        musicStage.hide();
                    }
                });

                musicStage.setOnHidden(evt -> {
                    Boolean forceClose = Boolean.TRUE.equals(musicStage.getProperties().get("forceClose"));
                    if (Boolean.TRUE.equals(forceClose)) {
                        musicStage.getProperties().remove("forceClose");
                        musicStage = null;
                    }
                });

                LofiMusicController controller = loader.getController();
                controller.initStage(musicStage);

                musicStage.show();
            } else {
                if (!musicStage.isShowing()) musicStage.show();
                else musicStage.toFront();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML private Button btnJobSearch;    // inject a button in your dashboard FXML

    @FXML
    private void handleJobSearch(ActionEvent e) {
        getStartedApplication.launchScene("JobSearch.fxml");
    }


    @FXML
    private void handlePostJobs(ActionEvent e) throws IOException {
        getStartedApplication.launchScene("AdminJobManager.fxml");
    }


    @FXML
    private void handleFeed(ActionEvent e) {
        getStartedApplication.launchScene("Feed.fxml");
    }


    @FXML
    public void handleTodolist(ActionEvent e){
        getStartedApplication.launchScene("TodoList.fxml");
    }

    @FXML
    public void handleCV(ActionEvent e){
        getStartedApplication.launchScene("CVBuilder.fxml");
    }

    @FXML
    public void handleProfile(ActionEvent e){
        getStartedApplication.launchScene("Profile.fxml");
    }

    @FXML
    public void handleMessages(ActionEvent e){
        getStartedApplication.launchScene("ChatUI.fxml");
    }

    @FXML
    private void handleUserSearch() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserSearch.fxml"));
            Parent root = loader.load();

            Stage owner = (Stage) btnUserSearch.getScene().getWindow();

            Stage userSearchStage = new Stage();
            userSearchStage.setScene(new Scene(root));
            userSearchStage.setTitle("Search Users");
            userSearchStage.setResizable(false);
            userSearchStage.initOwner(owner);
            userSearchStage.initModality(Modality.NONE);
            userSearchStage.initStyle(StageStyle.UNDECORATED);
            userSearchStage.setAlwaysOnTop(true);

            // Center the popup relative to dashboard
            double centerX = owner.getX() + (owner.getWidth() - 740) / 2;
            double centerY = owner.getY() + (owner.getHeight() - 600) / 2;
            userSearchStage.setX(centerX);
            userSearchStage.setY(centerY);

            userSearchStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
