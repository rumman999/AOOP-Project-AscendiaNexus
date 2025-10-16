module com.example.aoop_project {
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;      // <-- add this line
    requires javafx.web;
    requires google.genai;
    requires com.google.gson;
    requires org.apache.pdfbox;
    requires org.json;
    requires org.slf4j;
    requires javafx.media;
    requires javafx.controls;
    requires javafx.graphics;


    opens com.example.aoop_project to javafx.fxml;
    opens com.example.aoop_project.games.chess to javafx.fxml;
    opens com.example.aoop_project.messaging to javafx.fxml;
    opens com.example.aoop_project.BubbleShooter to javafx.fxml;

    exports com.example.aoop_project;
    exports com.example.aoop_project.games.chess;
    exports com.example.aoop_project.messaging;
    exports com.example.aoop_project.chat;
    exports com.example.aoop_project.FlappyBird;
    exports com.example.aoop_project.BubbleShooter;
}
