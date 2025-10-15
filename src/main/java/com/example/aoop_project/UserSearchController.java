package com.example.aoop_project;

import com.example.aoop_project.chat.DBUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserSearchController {

    @FXML private TextField searchField;
    @FXML private TableView<UserData> userTable;
    @FXML private TableColumn<UserData, String> colName;
    @FXML private TableColumn<UserData, String> colEmail;
    @FXML private TableColumn<UserData, String> colType;
    @FXML private TableColumn<UserData, ImageView> colProfile;
    @FXML private TableColumn<UserData, Void> colMessage;

    private ObservableList<UserData> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(data -> data.getValue().fullNameProperty());
        colEmail.setCellValueFactory(data -> data.getValue().emailProperty());
        colType.setCellValueFactory(data -> data.getValue().accountTypeProperty());
        colProfile.setCellValueFactory(data -> data.getValue().profilePicProperty());

        addMessageButtonToTable();
        loadAllUsers();
    }

    /** Load all users from DB */
    private void loadAllUsers() {
        userList.clear();
        try (Connection con = DBUtils.getConnection()) {
            String query = "SELECT full_name,email,account_type,profile_pic FROM user";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String type = rs.getString("account_type");
                String profile = rs.getString("profile_pic");

                ImageView imgView = new ImageView();
                imgView.setFitHeight(40);
                imgView.setFitWidth(40);
                if (profile != null && !profile.isEmpty()) {
                    imgView.setImage(new Image(profile, 40, 40, true, true));
                }

                userList.add(new UserData(fullName, email, type, imgView));
            }

            userTable.setItems(userList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Search users by name or email */
    @FXML
    private void handleSearch() {
        String text = searchField.getText().trim().toLowerCase();
        if (text.isEmpty()) {
            userTable.setItems(userList);
            return;
        }

        ObservableList<UserData> filtered = FXCollections.observableArrayList();
        for (UserData u : userList) {
            if (u.getFullName().toLowerCase().contains(text) || u.getEmail().toLowerCase().contains(text)) {
                filtered.add(u);
            }
        }
        userTable.setItems(filtered);
    }

    /** Clear search field */
    @FXML
    private void handleClear() {
        searchField.clear();
        userTable.setItems(userList);
    }

    /** Refresh users from DB */
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

    /** Add Message button column */
    private void addMessageButtonToTable() {
        colMessage.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Message");

            {
                btn.setOnAction(event -> {
                    UserData user = getTableView().getItems().get(getIndex());
                    openMessagePopup(user.getFullName(), user.getEmail());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    /** Open messenger popup */
    private void openMessagePopup(String fullName, String targetEmail) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserMessagePopup.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            UserMessagePopupController controller = loader.getController();
            controller.setTargetUser(fullName, targetEmail, Session.getLoggedInUserEmail());

            stage.setTitle("Messenger");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
