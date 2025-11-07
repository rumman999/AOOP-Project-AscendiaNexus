package com.example.aoop_project;


import com.example.aoop_project.services.ChatServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AscendiaNexusApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {

        startChatServer();


        primaryStage = stage;
        launchScene("login.fxml");
        primaryStage.setTitle("Hello!");
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    private void startChatServer() {

        Thread serverThread = new Thread(() -> {
            try {

                ChatServer.main(null);
            } catch (Exception e) {
                System.err.println("ERROR - Failed to start Chat Server: " + e.getMessage());
                e.printStackTrace();
            }
        });

        serverThread.setDaemon(true);
        serverThread.start();
        System.out.println("Chat Server started in background...");
    }

    public static void main(String[] args) {
        launch();
    }

    public static void launchScene(String fxmlFile) {
        try {

            String fxmlPath = "/com/example/aoop_project/" + fxmlFile;


            FXMLLoader loader = new FXMLLoader(AscendiaNexusApp.class.getResource(fxmlPath));


            if (loader.getLocation() == null) {
                throw new IOException("Cannot find FXML file at path: " + fxmlPath);
            }

            Scene scene = new Scene(loader.load());

            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setFullScreen(true);
            newStage.setFullScreenExitHint("");
            newStage.show();


            if (primaryStage != null) {
                primaryStage.close();
            }


            primaryStage = newStage;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}