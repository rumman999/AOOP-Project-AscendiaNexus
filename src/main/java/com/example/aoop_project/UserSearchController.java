package com.example.aoop_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.sql.*;

public class UserSearchController {

    @FXML private TextField searchField;
    @FXML private TableView<UserData> userTable;
    @FXML private TableColumn<UserData, String> colName;
    @FXML private TableColumn<UserData, String> colEmail;
    @FXML private TableColumn<UserData, String> colType;
    @FXML private TableColumn<UserData, ImageView> colProfile;

    private ObservableList<UserData> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(data -> data.getValue().fullNameProperty());
        colEmail.setCellValueFactory(data -> data.getValue().emailProperty());
        colType.setCellValueFactory(data -> data.getValue().accountTypeProperty());
        colProfile.setCellValueFactory(data -> data.getValue().profilePicProperty());

        loadAllUsers();
    }

    /** Load all users from DB */
    public void loadAllUsers() {
        userList.clear();
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:4306/java_user_database","root","")) {

            String query = "SELECT full_name,email,account_type,profile_pic FROM user";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while(rs.next()) {
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String type = rs.getString("account_type");
                String profile = rs.getString("profile_pic");

                ImageView imgView = new ImageView();
                imgView.setFitHeight(40);
                imgView.setFitWidth(40);
                if(profile != null && !profile.isEmpty()){
                    imgView.setImage(new Image(profile, 40, 40, true, true));
                }

                userList.add(new UserData(fullName,email,type,imgView));
            }

            userTable.setItems(userList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Search users by name or email */
    @FXML
    private void handleSearch() {
        String text = searchField.getText().trim().toLowerCase();
        if(text.isEmpty()){
            userTable.setItems(userList);
            return;
        }

        ObservableList<UserData> filtered = FXCollections.observableArrayList();
        for(UserData u : userList){
            if(u.getFullName().toLowerCase().contains(text) || u.getEmail().toLowerCase().contains(text)){
                filtered.add(u);
            }
        }
        userTable.setItems(filtered);
    }

    /** Clear search field and reset table */
    @FXML
    private void handleClear() {
        searchField.clear();
        userTable.setItems(userList);
    }

    /** Reload all users from DB */
    @FXML
    private void handleRefresh() {
        loadAllUsers();
        searchField.clear();
    }

    /** Exit popup */
    @FXML
    private void handleExit() {
        Stage stage = (Stage) searchField.getScene().getWindow();
        stage.close();
    }
}
