package com.example.aoop_project.chat;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private int userId;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // First line = username#userId
            String firstLine = in.readLine();
            String[] parts = firstLine.split("#");
            username = parts[0];
            userId = Integer.parseInt(parts[1]);

            ChatServer.clients.put(userId, this);
            broadcast(username + " joined the chat");

            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("MSG")) {
                    // format: MSG receiverId message
                    String[] tokens = line.split(" ", 3);
                    int receiverId = Integer.parseInt(tokens[1]);
                    String msg = tokens[2];
                    sendPrivateMessage(receiverId, msg); // ✅ send only to private target
                } else if (line.startsWith("GRP")) {
                    // format: GRP groupId message
                    String[] tokens = line.split(" ", 3);
                    int groupId = Integer.parseInt(tokens[1]);
                    String msg = tokens[2];
                    sendGroupMessage(groupId, msg); // ✅ group message only to members
                } else if (line.startsWith("ALL")) {
                    // format: ALL message
                    String[] tokens = line.split(" ", 2);
                    String msg = tokens[1];
                    broadcast(username + ": " + msg); // ✅ broadcast
                    DBUtils.saveMessage(userId, null, null, msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ChatServer.clients.remove(userId);
            broadcast(username + " left the chat");
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private void sendPrivateMessage(int receiverId, String msg) {
        ClientHandler target = ChatServer.clients.get(receiverId);
        if (target != null) {
            target.send(username + " (private): " + msg); // ✅ only receiver sees it
            DBUtils.saveMessage(userId, receiverId, null, msg);
        } else {
            send("User " + receiverId + " not online.");
        }
    }

    private void sendGroupMessage(int groupId, String msg) {
        try {
            var rs = DBUtils.getGroupMembers(groupId);
            while (rs.next()) {
                int memberId = rs.getInt("user_id");
                ClientHandler target = ChatServer.clients.get(memberId);

                if (target != null && memberId != userId) { // ✅ skip sender
                    target.send("[Group " + groupId + "] " + username + ": " + msg);
                }
            }
            DBUtils.saveMessage(userId, null, groupId, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String msg) {
        for (ClientHandler client : ChatServer.clients.values()) {
            client.send(msg);
        }
    }

    private void send(String msg) {
        out.println(msg);
    }
}
