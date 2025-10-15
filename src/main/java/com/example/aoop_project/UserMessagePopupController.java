package com.example.aoop_project;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.*;

public class UserMessagePopupController {

    @FXML private Label lblUser;
    @FXML private VBox chatBox;
    @FXML private TextField txtMessage;
    @FXML private ScrollPane scrollPane;

    private String currentUserEmail;   // Your email
    private String targetUserEmail;    // Chat partner email

    // Database connection (adjust credentials)
    private final String URL = "jdbc:mysql://localhost:3306/your_database";
    private final String USER = "root";
    private final String PASS = "your_password";

    public void setTargetUser(String fullName, String targetEmail, String currentUserEmail) {
        this.targetUserEmail = targetEmail;
        this.currentUserEmail = currentUserEmail;
        lblUser.setText("Chat with: " + fullName);
        loadMessages();
    }


    // Load chat messages
    private void loadMessages() {
        chatBox.getChildren().clear();

        String query = "SELECT sender_email, message_text, timestamp FROM DMmessages " +
                "WHERE (sender_email=? AND receiver_email=?) " +
                "OR (sender_email=? AND receiver_email=?) " +
                "ORDER BY timestamp ASC";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, currentUserEmail);
            pst.setString(2, targetUserEmail);
            pst.setString(3, targetUserEmail);
            pst.setString(4, currentUserEmail);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String sender = rs.getString("sender_email");
                String message = rs.getString("message_text");
                Label lbl = new Label((sender.equals(currentUserEmail) ? "You: " : sender + ": ") + message);
                lbl.setWrapText(true);
                chatBox.getChildren().add(lbl);
            }

            scrollPane.layout(); // refresh scroll pane
            scrollPane.setVvalue(1.0); // scroll to bottom

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Send a message
    @FXML
    private void handleSendMessage() {
        String message = txtMessage.getText().trim();
        if (message.isEmpty()) return;

        String insert = "INSERT INTO DMmessages (sender_email, receiver_email, message_text) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pst = conn.prepareStatement(insert)) {

            pst.setString(1, currentUserEmail);
            pst.setString(2, targetUserEmail);
            pst.setString(3, message);
            pst.executeUpdate();

            txtMessage.clear();
            loadMessages(); // refresh chat

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
