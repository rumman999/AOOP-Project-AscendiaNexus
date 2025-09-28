package com.example.aoop_project.messaging;

import com.example.aoop_project.Session;
import com.example.aoop_project.chat.ChatClient;
import com.example.aoop_project.chat.DBUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ChatController {

    @FXML
    private ListView<String> chatListView;
    @FXML
    private ListView<String> groupListView;
    @FXML
    private Label chatTitle;
    @FXML
    private VBox messageContainer;
    @FXML
    private TextField messageField;
    @FXML
    private ScrollPane scrollPane;

    private ChatClient client;
    private String currentTargetType; // "ALL", "USER", "GROUP"
    private Integer currentTargetId;

    public void initialize() {
        try {
            client = new ChatClient("localhost", 12345,
                    Session.getLoggedInUserId(),
                    Session.getLoggedInUserName());

            client.setOnMessage(msg -> {
                Platform.runLater(() -> {
                    HBox bubble = MessageBubble.create("Server", msg, System.currentTimeMillis(), false);
                    messageContainer.getChildren().add(bubble);
                    scrollToBottom();
                });
            });

            currentTargetType = "ALL";
            chatTitle.setText("Broadcast Chat");

            loadUsers();
            loadGroups();

            chatListView.setOnMouseClicked(e -> selectUserChat());
            groupListView.setOnMouseClicked(e -> selectGroupChat());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        try (ResultSet rs = DBUtils.getAllOtherUsers(Session.getLoggedInUserId())) {
            chatListView.getItems().clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("full_name");
                chatListView.getItems().add(id + ": " + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadGroups() {
        try (ResultSet rs = DBUtils.getUserGroups(Session.getLoggedInUserId())) {
            groupListView.getItems().clear();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                groupListView.getItems().add(id + ": " + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSend() {
        String text = messageField.getText();
        if (text == null || text.isBlank()) return;

        if ("ALL".equals(currentTargetType)) {
            client.sendMessage(text);
        } else if ("USER".equals(currentTargetType) && currentTargetId != null) {
            client.sendPrivateMessage(currentTargetId, text);
        } else if ("GROUP".equals(currentTargetType) && currentTargetId != null) {
            client.sendGroupMessage(currentTargetId, text);
        }

        HBox bubble = MessageBubble.create(Session.getLoggedInUserName(), text, System.currentTimeMillis(), true);
        messageContainer.getChildren().add(bubble);
        scrollToBottom();

        messageField.clear();
    }

    private Timestamp lastLoadedTimestamp = null;

    private void selectUserChat() {
        String selected = chatListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String[] parts = selected.split(":");
            currentTargetId = Integer.parseInt(parts[0].trim());
            currentTargetType = "USER";
            chatTitle.setText("Chat with " + parts[1].trim());

            try (ResultSet rs = DBUtils.getPrivateMessages(
                    Session.getLoggedInUserId(),
                    currentTargetId,
                    50,
                    lastLoadedTimestamp // pass last timestamp to query only new
            )) {
                while (rs.next()) {
                    String sender = rs.getString("sender_name");
                    String msg = rs.getString("message_text");
                    long ts = rs.getTimestamp("timestamp").getTime();

                    boolean isOwn = sender.equals(Session.getLoggedInUserName());
                    HBox bubble = MessageBubble.create(sender, msg, ts, isOwn);

                    messageContainer.getChildren().add(bubble);

                    // update last timestamp
                    lastLoadedTimestamp = new Timestamp(ts);
                }
                scrollToBottom();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void selectGroupChat() {
        String selected = groupListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String[] parts = selected.split(":");
            currentTargetId = Integer.parseInt(parts[0].trim());
            currentTargetType = "GROUP";
            chatTitle.setText("Group: " + parts[1].trim());
            messageContainer.getChildren().clear();

            try (ResultSet rs = DBUtils.getGroupMessages(currentTargetId, 50)) {
                while (rs.next()) {
                    String sender = rs.getString("sender_name");
                    String msg = rs.getString("message_text");
                    long ts = rs.getTimestamp("timestamp").getTime();
                    boolean isOwn = sender.equals(Session.getLoggedInUserName());
                    HBox bubble = MessageBubble.create(sender, msg, ts, isOwn);
                    messageContainer.getChildren().add(bubble);
                }
                scrollToBottom();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    /**
     * ✅ Group creation with member selection
     */
    @FXML
    private void handleCreateGroup() {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Create Group");
        nameDialog.setHeaderText("Enter Group Name:");
        nameDialog.setContentText("Name:");
        String groupName = nameDialog.showAndWait().orElse(null);
        if (groupName == null || groupName.isBlank()) return;

        List<Integer> members = new ArrayList<>();
        members.add(Session.getLoggedInUserId()); // always include creator

        // ✅ Pick
    }
}