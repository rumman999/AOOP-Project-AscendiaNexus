package com.example.aoop_project;

import com.example.aoop_project.Session;
import com.example.aoop_project.getStartedApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class JobSeekerDashboardController implements Initializable {

    @FXML private Label dashBoardName;
    @FXML private Label UserName;
    @FXML private Label accountDes;
    @FXML private Button logout_button;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUserDataFromDB(); // fetch logged-in user data automatically
    }

    private void loadUserDataFromDB() {
        String userEmail = Session.getLoggedInUserEmail();
        if(userEmail == null || userEmail.isEmpty()) return;

        String SUrl = "jdbc:mysql://localhost:4306/java_user_database";
        String SUser = "root";
        String SPass = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(SUrl, SUser, SPass);

            String query = "SELECT full_name, account_type FROM user WHERE email=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, userEmail);

            ResultSet rs = pst.executeQuery();
            if(rs.next()) {
                String fullName = rs.getString("full_name");
                String accountType = rs.getString("account_type");

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

    @FXML
    private void handleLogout(ActionEvent e) {
        Session.clear(); // clear logged-in user

        getStartedApplication.launchScene("login.fxml"); // use existing launcher
    }


    private Stage chatStage;

    @FXML
    private void handleChatbot(ActionEvent e) {
        try {
            // Reuse existing window if it's already created
            if (chatStage != null) {
                if (!chatStage.isShowing()) chatStage.show();
                chatStage.toFront();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatBot.fxml"));
            Parent root = loader.load();

            Stage owner = (Stage) ((Node) e.getSource()).getScene().getWindow();

            // Undecorated, stable (won't auto-hide on focus loss)
            chatStage = new Stage(StageStyle.UNDECORATED);
            chatStage.setTitle("Skill Buddy");
            chatStage.setScene(new Scene(root, 450, 550));
            chatStage.setResizable(false);
            chatStage.initOwner(owner);
            chatStage.initModality(Modality.NONE);     // dashboard stays interactive
            chatStage.setAlwaysOnTop(true);           // set true if you want it to float above

            // Pin to bottom-right of the owner window
            Runnable positionChat = () -> {
                double x = owner.getX() + owner.getWidth()  - chatStage.getWidth()  - 20;
                double y = owner.getY() + owner.getHeight() - chatStage.getHeight() - 35;
                chatStage.setX(x);
                chatStage.setY(y);
            };

            chatStage.setOnShown(ev -> positionChat.run());
            owner.xProperty().addListener((obs, o, n) -> positionChat.run());
            owner.yProperty().addListener((obs, o, n) -> positionChat.run());
            owner.widthProperty().addListener((obs, o, n) -> positionChat.run());
            owner.heightProperty().addListener((obs, o, n) -> positionChat.run());

            // When user closes it, clear the reference so next click recreates it
            chatStage.setOnCloseRequest(ev -> chatStage = null);

            chatStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    // Class-level field
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

                // If user clicks window X -> hide only (do not close)
                musicStage.setOnCloseRequest(event -> {
                    Boolean forceClose = Boolean.TRUE.equals(musicStage.getProperties().get("forceClose"));
                    if (!forceClose) {
                        event.consume();       // prevent default close
                        musicStage.hide();     // just hide (keeps background running)
                    }
                    // if forceClose==true, allow the close to proceed
                });

                // After it's hidden/closed, check if it was a forced close and free the reference
                musicStage.setOnHidden(evt -> {
                    Boolean forceClose = Boolean.TRUE.equals(musicStage.getProperties().get("forceClose"));
                    if (Boolean.TRUE.equals(forceClose)) {
                        // it was a real close -> cleanup
                        musicStage.getProperties().remove("forceClose");
                        musicStage = null; // next click will recreate
                    }
                    // if it was just hide(), we keep musicStage alive (so music continues)
                });

                // Optional: give controller a reference
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

    @FXML
    public void handleTodolist(ActionEvent e){
        getStartedApplication.launchScene("TodoList.fxml");
    }

    @FXML
    public void handleCV(ActionEvent e){
        getStartedApplication.launchScene("CVBuilder.fxml");
    }
}
