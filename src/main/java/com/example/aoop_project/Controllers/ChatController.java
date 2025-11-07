package com.example.aoop_project.Controllers;

import com.example.aoop_project.Session;
import com.example.aoop_project.services.ChatClient;
import com.example.aoop_project.dao.DBUtils;
import com.example.aoop_project.AscendiaNexusApp;
import com.example.aoop_project.utils.MessageBubble;
import com.example.aoop_project.utils.UserSelectionDialog;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

            // Common style for ListViews
            String listViewStyle = """
    -fx-background-color: transparent;
    -fx-control-inner-background: transparent;
    -fx-selection-bar: orange;
    -fx-selection-bar-non-focused: orange;
""";

            // Apply to both ListViews
            chatListView.setStyle(listViewStyle);
            groupListView.setStyle(listViewStyle);

            hideScrollBars(chatListView);
            hideScrollBars(groupListView);

            // Cell factory for styling individual rows
            chatListView.setCellFactory(lv -> new ListCell<>() {
                // listener created once per cell
                private final javafx.beans.value.ChangeListener<Boolean> selectionListener =
                        (obs, wasSelected, isNowSelected) -> applyStyle(isNowSelected);

                {
                    // add listener once when the cell instance is created
                    selectedProperty().addListener(selectionListener);
                }

                // centralised styling function
                private void applyStyle(boolean selected) {
                    if (getItem() == null || isEmpty()) {
                        setStyle(""); // clear for empty cells
                        return;
                    }

                    String base =
                            "-fx-font-size: 14px; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-padding: 5 10 5 10; " +
                                    "-fx-border-color: rgba(255,255,255,0.3); " +
                                    "-fx-border-width: 1; " +
                                    "-fx-background-insets: 0;";

                    String bg = selected ? "-fx-background-color: #ff9800;" : "-fx-background-color: transparent;";

                    setStyle(base + bg);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        // apply current selection state when the cell is updated
                        applyStyle(isSelected());
                    }
                }
            });

            // Apply same cell factory to groupListView
            groupListView.setCellFactory(chatListView.getCellFactory());

            loadUsers();
            loadGroups();
            setupListHandlers();
            messageField.setOnAction(event -> handleSend());

            // *** NEW: Check for chat target from ViewProfile ***
            Platform.runLater(this::openChatFromSession);
            // *************************************************

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * NEW METHOD
     * Checks if a chat target was set in the Session and opens it.
     */
    private void openChatFromSession() {
        int targetId = Session.getChatTargetId();
        String targetName = Session.getChatTargetName();

        // Check if valid target is set
        if (targetId > 0 && targetName != null) {
            String targetUserString = targetId + ": " + targetName;

            // Check if user exists in the list
            if (chatListView.getItems().contains(targetUserString)) {
                // Select the user
                chatListView.getSelectionModel().select(targetUserString);
                // Programmatically trigger the chat load
                selectUserChat();
            } else {
                // This can happen if the user is not in the "other users" list (e.g., self-chat)
                // Or if the list hasn't loaded yet.
                System.err.println("Could not find user " + targetUserString + " in chat list.");
            }

            // Clear the session flag so it doesn't re-open
            Session.clearChatTarget();
        }
    }

    private void setupClient() throws Exception {
        client = new ChatClient("localhost", 12345,
                Session.getLoggedInUserId(),
                Session.getLoggedInUserName());

        client.setOnMessage(msg -> Platform.runLater(() -> {
            // ✅ Determine if the message belongs to current chat
            boolean showMessage = false;
            String sender = "Server";
            String text = msg;

            // Check if it's a private message
            if (msg.contains("(private):")) {
                String[] parts = msg.split(" \\(private\\): ", 2);
                sender = parts[0];
                text = parts[1];

                // Only show if current chat is with this sender
                if ("USER".equals(currentTargetType) && currentTargetId != null) {
                    String selectedItem = chatListView.getSelectionModel().getSelectedItem();
                    // Check if sender name matches the selected user's name
                    if (selectedItem != null && sender.equals(selectedItem.split(": ")[1].trim())) {
                        showMessage = true;
                    }
                }
            }
            // Check if it's a group message
            else if (msg.startsWith("[Group ")) {
                int groupIdStart = msg.indexOf(" ") + 1;
                int groupIdEnd = msg.indexOf("]");
                int groupId = Integer.parseInt(msg.substring(groupIdStart, groupIdEnd));

                // Only show if current chat is this group
                if ("GROUP".equals(currentTargetType) && currentTargetId != null && currentTargetId == groupId) {
                    showMessage = true;
                }
            }
            // Broadcast message
            else if ("ALL".equals(currentTargetType)) {
                showMessage = true;
            }

            if (showMessage) {
                HBox bubble = MessageBubble.create(sender, text, System.currentTimeMillis(), false);
                messageContainer.getChildren().add(bubble);
                scrollToBottom();
            }
        }));
    }


    private void setupListHandlers() {
        chatListView.setOnMouseClicked(e -> selectUserChat());
        groupListView.setOnMouseClicked(e -> selectGroupChat());
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
            case "ALL" -> client.sendMessage(text);
            case "USER" -> { if (currentTargetId != null) client.sendPrivateMessage(currentTargetId, text); }
            case "GROUP" -> { if (currentTargetId != null) client.sendGroupMessage(currentTargetId, text); }
        }

        addMessage(Session.getLoggedInUserName(), text, System.currentTimeMillis(), true);
        messageField.clear();
    }

    private void selectUserChat() {
        String selected = chatListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // ** ADDED: Clear other list's selection **
        groupListView.getSelectionModel().clearSelection();

        String[] parts = selected.split(":");
        currentTargetId = Integer.parseInt(parts[0].trim());

        currentTargetType = "USER";
        chatTitle.setText("Chat with " + parts[1].trim());
        messageContainer.getChildren().clear();

        // ** ADDED: Reset timestamp and query from beginning **
        lastLoadedTimestamp = null;

        try (ResultSet rs = DBUtils.getPrivateMessages(
                Session.getLoggedInUserId(),
                currentTargetId,
                50, // Load 50 most recent messages
                null // Pass null to get latest messages
        )) {
            while (rs.next()) {
                addMessage(
                        rs.getString("sender_name"),
                        rs.getString("message_text"),
                        rs.getTimestamp("timestamp").getTime(),
                        rs.getString("sender_name").equals(Session.getLoggedInUserName())
                );
                // Keep track of the *oldest* message loaded if loading in reverse,
                // or the *newest* if loading chronologically.
                // Assuming getPrivateMessages fetches ASC order, this is correct:
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

        // ** ADDED: Clear other list's selection **
        chatListView.getSelectionModel().clearSelection();

        String[] parts = selected.split(":");
        currentTargetId = Integer.parseInt(parts[0].trim());

        currentTargetType = "GROUP";
        chatTitle.setText("Group: " + parts[1].trim());
        messageContainer.getChildren().clear();

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
        // Set dialog modality to block current window
        Stage owner = (Stage) chatListView.getScene().getWindow();
        nameDialog.initOwner(owner);
        nameDialog.initModality(Modality.WINDOW_MODAL);

        String groupName = nameDialog.showAndWait().orElse(null);
        if (groupName == null || groupName.isBlank()) return;

        try {
            List<Integer> userIds = new ArrayList<>();
            List<String> userNames = new ArrayList<>();
            try (ResultSet rs = DBUtils.getAllOtherUsers(Session.getLoggedInUserId())) {
                while (rs.next()) {
                    userIds.add(rs.getInt("id"));
                    userNames.add(rs.getString("full_name"));
                }
            }

            List<Integer> selectedMembers = UserSelectionDialog.show(
                    userNames,
                    userIds,
                    owner // pass owner for modality
            );

            if (selectedMembers == null || selectedMembers.isEmpty()) return;

            selectedMembers.add(Session.getLoggedInUserId());
            int groupId = DBUtils.createGroup(groupName, Session.getLoggedInUserId(), selectedMembers);

            if (groupId != -1) {
                loadGroups();
                Alert info = new Alert(Alert.AlertType.INFORMATION, "Group created!");
                info.initOwner(owner);
                info.initModality(Modality.WINDOW_MODAL);
                info.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleDashboard(ActionEvent e){
        AscendiaNexusApp.launchScene("JobSeekerDashboard.fxml");
    }


    private void hideScrollBars(ListView<?> listView) {
        Runnable doHide = () -> {
            for (Node node : listView.lookupAll(".scroll-bar")) {
                if (node instanceof ScrollBar sb) {
                    sb.setVisible(false);
                    sb.setManaged(false);
                    sb.setOpacity(0);
                }
            }
        };

        if (listView.getSkin() == null) {
            listView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
                if (newSkin != null) Platform.runLater(doHide);
            });
        } else {
            Platform.runLater(doHide);
        }
    }

}