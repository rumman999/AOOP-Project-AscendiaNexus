package com.example.aoop_project.chat;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Consumer<String> messageHandler;

    public ChatClient(String host, int port, int userId, String username) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Identify self to server
        out.println(username + "#" + userId);

        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    if (messageHandler != null) messageHandler.accept(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /** Broadcast */
    public void sendMessage(String text) {
        out.println("ALL " + text);
    }

    /** One-to-one */
    public void sendPrivateMessage(int receiverId, String text) {
        out.println("MSG " + receiverId + " " + text);
    }

    /** Group chat */
    public void sendGroupMessage(int groupId, String text) {
        out.println("GRP " + groupId + " " + text);
    }

    public void setOnMessage(Consumer<String> handler) {
        this.messageHandler = handler;
    }
}
