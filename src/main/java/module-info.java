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

    opens com.example.aoop_project.games.chess to javafx.fxml;
    opens com.example.aoop_project to javafx.fxml;

    exports com.example.aoop_project.games.chess;
    exports com.example.aoop_project;
}
