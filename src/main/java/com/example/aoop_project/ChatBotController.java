package com.example.aoop_project;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatBotController {

    @FXML
    private VBox chatBox;
    @FXML
    private Button chatDock;
    @FXML
    private TextField inputField;
    @FXML
    private Button sendButton;
    @FXML
    private ScrollPane chatBoxScrollPane;

    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String API_KEY = System.getenv("OPENROUTER_API_KEY");

    // Initialize method
    @FXML
    private void initialize() {
        // Setting the action for the Send button
        sendButton.setOnAction(e -> sendMessage());
        // Also trigger the message on pressing Enter
        inputField.setOnAction(e -> sendMessage());
    }

    @FXML
    private void sendMessage() {
        System.out.println("Hello");
        String userMessage = inputField.getText().trim();
        if (userMessage.isEmpty()) return;  // Don't send if the message is empty

        // Add the user message to the chat
        addMessage("You: " + userMessage, "user");
        inputField.clear();  // Clear the input field

        // Make API call to get bot's response
        new Thread(() -> {
            try {
                String botReply = callLlamaAPI(userMessage);
                Platform.runLater(() -> addMessage("\uD83E\uDD16 :  " + botReply, "bot"));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> addMessage("Bot: [Error] " + e.getMessage(), "bot"));
            }
        }).start();
    }

    // Add message to the chat box with styles based on the role (user or bot)
    private void addMessage(String message, String role) {
        Label label = new Label(message);
        label.setWrapText(true);

        // Apply oval shape and padding
        label.setStyle("-fx-background-color: #e1f5fe; -fx-padding: 10px 20px; -fx-background-radius: 25px; -fx-font-size: 14px;");

        // Style the user and bot messages differently
        if (role.equals("user")) {
            label.setStyle("-fx-background-color: #e67e22; -fx-padding: 10px 15px; -fx-background-radius: 25px; -fx-font-size: 14px;");
            label.setAlignment(Pos.TOP_RIGHT);  // User messages to the right
        } else {
            label.setStyle("-fx-background-color: #243240; -fx-padding: 10px 15px; -fx-background-radius: 25px; -fx-font-size: 14px;");
            label.setAlignment(Pos.TOP_LEFT);   // Bot messages to the left
        }

        // Add the label to the chatBox
        chatBox.getChildren().add(label);

        // Ensure the chat scrolls to the bottom after a new message is added
        chatBoxScrollPane.setVvalue(1.0);
    }

    // Call the API to get bot's response
    private String callLlamaAPI(String userMessage) throws Exception {
        if (API_KEY == null) {
            throw new IllegalArgumentException("API key not set. Use OPENROUTER_API_KEY env var.");
        }

        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject body = new JSONObject();
        body.put("model", "meta-llama/llama-4-maverick");
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "user").put("content", userMessage));
        body.put("messages", messages);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line.trim());
        }

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim();
    }

    @FXML
    public void handleExit(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Optional: check if we want to force close or just hide
        Boolean forceClose = Boolean.TRUE.equals(stage.getProperties().get("forceClose"));
        if (!forceClose) {
            // Prevent full close and just hide the window
            stage.hide();
        } else {
            // Allow proper close
            stage.close();
            stage.getProperties().remove("forceClose"); // clean up
        }
    }

}
