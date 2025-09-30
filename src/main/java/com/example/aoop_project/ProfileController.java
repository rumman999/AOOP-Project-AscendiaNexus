package com.example.aoop_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;

public class ProfileController {

    @FXML private ImageView profilePicView;
    @FXML private Circle profileClip;
    @FXML private Label uuidLabel, coinLabel, accountTypeLabel, statusLabel;
    @FXML private TextField fullNameField, emailField, phoneField, addressField, ageField;

    // ✅ newly added fields for password update
    @FXML private TextField previousPass;
    @FXML private TextField updatedPass;

    @FXML private DatePicker dobPicker;

    private int userId;

    @FXML
    public void initialize() {
        double size = 140;
        profilePicView.setFitWidth(size);
        profilePicView.setFitHeight(size);
        profilePicView.setPreserveRatio(false);
        profilePicView.setSmooth(true);

        profileClip.setRadius(size / 2);
        profileClip.setCenterX(size / 2);
        profileClip.setCenterY(size / 2);
        profilePicView.setClip(profileClip);

        userId = Session.getLoggedInUserId();
        System.out.println("🔑 Logged in userId = " + userId);

        loadUserData();
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:4306/java_user_database";
        String user = "root";
        String pass = "";
        Connection conn = DriverManager.getConnection(url, user, pass);
        conn.setAutoCommit(true);
        return conn;
    }

    private void loadUserData() {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE id=?")) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                fullNameField.setText(rs.getString("full_name"));
                emailField.setText(rs.getString("email"));
                phoneField.setText(rs.getString("phone_number"));
                addressField.setText(rs.getString("Address"));
                ageField.setText(String.valueOf(rs.getInt("Age")));

                Date dob = rs.getDate("DOB");
                dobPicker.setValue(dob != null ? dob.toLocalDate() : null);

                uuidLabel.setText("UUID: " + rs.getString("UUID"));
                coinLabel.setText("Coins: " + rs.getInt("Coin"));
                accountTypeLabel.setText("Account Type: " + rs.getString("account_type"));

                String picPath = rs.getString("profile_pic");
                Session.setPic(picPath);
                if (picPath != null && !picPath.isEmpty()) {
                    profilePicView.setImage(new Image(picPath));
                    profilePicView.setUserData(picPath);
                }
                statusLabel.setText("✅ Profile loaded");
                statusLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 16px;");
            } else {
                statusLabel.setText("⚠ No user found for id " + userId);
                statusLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 16px;");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("⚠ Error loading: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
        }
    }

    @FXML
    private void handleUploadPic() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = chooser.showOpenDialog(new Stage());
        if (file != null) {
            String uri = file.toURI().toString();
            profilePicView.setImage(new Image(uri));
            profilePicView.setUserData(uri);
            System.out.println("🖼 Selected new picPath = " + uri);
        }
    }

    @FXML
    private void handleSave() {
        String oldPass = previousPass.getText();
        String newPass = updatedPass.getText();

        // ✅ handle password update first
        if ((oldPass != null && !oldPass.isEmpty()) || (newPass != null && !newPass.isEmpty())) {
            if (!updatePassword(oldPass, newPass)) {
                // Stop saving profile if password update fails
                return;
            }
        }

        // Continue with profile update
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE user SET full_name=?, email=?, phone_number=?, Address=?, DOB=?, Age=?, profile_pic=? WHERE id=?")) {

            stmt.setString(1, fullNameField.getText());
            stmt.setString(2, emailField.getText());
            stmt.setString(3, phoneField.getText());
            stmt.setString(4, addressField.getText());

            LocalDate dob = dobPicker.getValue();
            if (dob != null) stmt.setDate(5, Date.valueOf(dob));
            else stmt.setNull(5, Types.DATE);

            int age = 0;
            try {
                age = Integer.parseInt(ageField.getText());
            } catch (NumberFormatException e) {
                System.out.println("⚠ Invalid age, defaulting to 0");
            }
            stmt.setInt(6, age);

            String picPath = profilePicView.getUserData() != null
                    ? profilePicView.getUserData().toString()
                    : null;
            stmt.setString(7, picPath);

            stmt.setInt(8, userId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                statusLabel.setText("✅ Profile updated successfully!");
                statusLabel.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 16px;");
            } else {
                statusLabel.setText("⚠ Nothing updated! (check userId)");
                statusLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 16px;");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("⚠ Error saving: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
        }
    }

    /** ✅ Updated password logic returning success/failure */
    private boolean updatePassword(String oldPass, String newPass) {
        if (oldPass == null || oldPass.isEmpty() || newPass == null || newPass.isEmpty()) {
            statusLabel.setText("❌ Please enter both current and new password!");
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            return false;
        }

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement("SELECT password FROM user WHERE id=?")) {

            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String currentPass = rs.getString("password");
                if (!currentPass.equals(oldPass)) {
                    statusLabel.setText("❌ Current password is incorrect!");
                    statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
                    return false;
                }
            } else {
                statusLabel.setText("⚠ User not found!");
                statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
                return false;
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE user SET password=? WHERE id=?")) {
                updateStmt.setString(1, newPass);
                updateStmt.setInt(2, userId);

                int updated = updateStmt.executeUpdate();
                if (updated > 0) {
                    statusLabel.setText("🔑 Password updated successfully!");
                    statusLabel.setStyle("-fx-text-fill: #00ccff; -fx-font-size: 16px;");
                    previousPass.clear();
                    updatedPass.clear();
                    return true;
                } else {
                    statusLabel.setText("⚠ Password update failed!");
                    statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("⚠ Error updating password: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            return false;
        }
    }

    @FXML
    private void handleDashboard(ActionEvent e){
        getStartedApplication.launchScene("JobSeekerDashboard.fxml");
    }
}
