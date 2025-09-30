package com.example.aoop_project.messaging;

import com.example.aoop_project.ProfileController;
import com.example.aoop_project.Session;
import com.example.aoop_project.chat.ChatClient;
import com.example.aoop_project.chat.DBUtils;
import com.example.aoop_project.getStartedApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ChatController {

    @FXML private ListView<String> chatListView;
    @FXML private ListView<String> groupListView;
    @FXML private Label chatTitle;
    @FXML private VBox messageContainer;
    @FXML private TextField messageField;
    @FXML private ScrollPane scrollPane;
    @FXML private Label loggedInUser;

    private ChatClient client;
    private String currentTargetType = "ALL"; // ✅ stays ALL initially
    private Integer currentTargetId;
    private Timestamp lastLoadedTimestamp;

    public void initialize() {
        try {
            loggedInUser.setText("Logged in as "+Session.getLoggedInUserName());
            setupClient();
            chatTitle.setText("Broadcast Chat"); // ✅ default ALL chat

            loadUsers();
            loadGroups();
            setupListHandlers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupClient() throws Exception {
        client = new ChatClient("localhost", 12345,
                Session.getLoggedInUserId(),
                Session.getLoggedInUserName());

        client.setOnMessage(msg -> Platform.runLater(() -> {
            HBox bubble = MessageBubble.create("Server", msg, System.currentTimeMillis(), false);
            messageContainer.getChildren().add(bubble);
            scrollToBottom();
        }));
    }

    private void setupListHandlers() {
        chatListView.setOnMouseClicked(e -> selectUserChat());   // ✅ switch to USER
        groupListView.setOnMouseClicked(e -> selectGroupChat()); // ✅ switch to GROUP
    }

    private void loadUsers() {
        try (ResultSet rs = DBUtils.getAllOtherUsers(Session.getLoggedInUserId())) {
            chatListView.getItems().clear();
            while (rs.next()) {
                chatListView.getItems().add(rs.getInt("id") + ": " + rs.getString("full_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadGroups() {
        try (ResultSet rs = DBUtils.getUserGroups(Session.getLoggedInUserId())) {
            groupListView.getItems().clear();
            while (rs.next()) {
                groupListView.getItems().add(rs.getInt("id") + ": " + rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSend() {
        String text = messageField.getText();
        if (text == null || text.isBlank()) return;

        switch (currentTargetType) {
            case "ALL" -> client.sendMessage(text); // ✅ broadcast
            case "USER" -> { if (currentTargetId != null) client.sendPrivateMessage(currentTargetId, text); }
            case "GROUP" -> { if (currentTargetId != null) client.sendGroupMessage(currentTargetId, text); }
        }

        addMessage(Session.getLoggedInUserName(), text, System.currentTimeMillis(), true);
        messageField.clear();
    }

    private void selectUserChat() {
        String selected = chatListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String[] parts = selected.split(":");
        currentTargetId = Integer.parseInt(parts[0].trim());

        // ✅ SWITCH TO USER
        currentTargetType = "USER";
        chatTitle.setText("Chat with " + parts[1].trim());
        messageContainer.getChildren().clear(); // ✅ clear previous messages

        try (ResultSet rs = DBUtils.getPrivateMessages(
                Session.getLoggedInUserId(),
                currentTargetId,
                50,
                lastLoadedTimestamp
        )) {
            while (rs.next()) {
                addMessage(
                        rs.getString("sender_name"),
                        rs.getString("message_text"),
                        rs.getTimestamp("timestamp").getTime(),
                        rs.getString("sender_name").equals(Session.getLoggedInUserName())
                );
                lastLoadedTimestamp = rs.getTimestamp("timestamp");
            }
            scrollToBottom();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectGroupChat() {
        String selected = groupListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String[] parts = selected.split(":");
        currentTargetId = Integer.parseInt(parts[0].trim());

        // ✅ SWITCH TO GROUP
        currentTargetType = "GROUP";
        chatTitle.setText("Group: " + parts[1].trim());
        messageContainer.getChildren().clear(); // ✅ clear previous messages

        try (ResultSet rs = DBUtils.getGroupMessages(currentTargetId, 50)) {
            while (rs.next()) {
                addMessage(
                        rs.getString("sender_name"),
                        rs.getString("message_text"),
                        rs.getTimestamp("timestamp").getTime(),
                        rs.getString("sender_name").equals(Session.getLoggedInUserName())
                );
            }
            scrollToBottom();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMessage(String sender, String msg, long ts, boolean isOwn) {
        HBox bubble = MessageBubble.create(sender, msg, ts, isOwn);
        messageContainer.getChildren().add(messageContainer.getChildren().size(), bubble);
    }

    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    @FXML
    private void handleCreateGroup() {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Create Group");
        nameDialog.setHeaderText("Enter Group Name:");
        nameDialog.setContentText("Name:");
        String groupName = nameDialog.showAndWait().orElse(null);
        if (groupName == null || groupName.isBlank()) return;

        List<Integer> members = new ArrayList<>();
        members.add(Session.getLoggedInUserId());
        // TODO: show dialog to pick members
    }

    @FXML
    private void handleDashboard(ActionEvent e){
        getStartedApplication.launchScene("JobSeekerDashboard.fxml");
    }
}
