package com.example.aoop_project;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.*;

public class UserMessagePopupController {

    @FXML private Label lblUser;
    @FXML private VBox chatBox;
    @FXML private TextField txtMessage;

    private String currentUserEmail;   // your user
    private String targetUserEmail;    // the user you chat with

    public void setTargetUser(String fullName, String targetEmail, String currentEmail) {
        lblUser.setText("Chat with " + fullName);
        targetUserEmail = targetEmail;
        currentUserEmail = currentEmail;
        loadMessages();
    }

    /** Load messages between currentUser and targetUser */
    private void loadMessages() {
        chatBox.getChildren().clear();
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:4306/java_user_database", "root", "")) {

            String query = "SELECT sender_email, message_text FROM messages " +
                    "WHERE (sender_email=? AND receiver_email=?) " +
                    "OR (sender_email=? AND receiver_email=?) ORDER BY timestamp ASC";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, currentUserEmail);
            pst.setString(2, targetUserEmail);
            pst.setString(3, targetUserEmail);
            pst.setString(4, currentUserEmail);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String sender = rs.getString("sender_email");
                String text = rs.getString("message_text");

                Label lbl = new Label(text);
                lbl.setStyle(sender.equals(currentUserEmail) ?
                        "-fx-background-color: #DCF8C6; -fx-padding:5; -fx-background-radius: 5;" :
                        "-fx-background-color: #FFFFFF; -fx-padding:5; -fx-background-radius: 5;");
                chatBox.getChildren().add(lbl);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSendMessage() {
        String msg = txtMessage.getText().trim();
        if (msg.isEmpty()) return;

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:4306/java_user_database", "root", "")) {

            String insert = "INSERT INTO messages (sender_email, receiver_email, message_text, timestamp) VALUES (?, ?, ?, NOW())";
            PreparedStatement pst = con.prepareStatement(insert);
            pst.setString(1, currentUserEmail);
            pst.setString(2, targetUserEmail);
            pst.setString(3, msg);
            pst.executeUpdate();

            txtMessage.clear();
            loadMessages(); // refresh messages

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
