module com.example.aoop_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.desktop;
    requires java.net.http;

    requires google.genai;
    requires javafx.media;
    requires com.google.gson;
    requires org.apache.pdfbox;
    requires org.json;
    requires org.slf4j;

    opens com.example.aoop_project.games.chess to javafx.fxml;
    opens com.example.aoop_project to javafx.fxml;
    opens com.example.aoop_project.messaging to javafx.fxml;   // 🔑 added

    exports com.example.aoop_project.games.chess;
    exports com.example.aoop_project;
    exports com.example.aoop_project.messaging;
    exports com.example.aoop_project.chat;
}
