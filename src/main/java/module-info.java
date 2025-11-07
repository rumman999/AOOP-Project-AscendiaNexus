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
    opens com.example.aoop_project.games.bubbleshooter to javafx.fxml;

    exports com.example.aoop_project;
    exports com.example.aoop_project.games.chess;
    exports com.example.aoop_project.games.flappybird;
    exports com.example.aoop_project.games.bubbleshooter;
    exports com.example.aoop_project.Controllers;
    opens com.example.aoop_project.Controllers to javafx.fxml;
    exports com.example.aoop_project.dao;
    opens com.example.aoop_project.dao to javafx.fxml;
    exports com.example.aoop_project.models;
    opens com.example.aoop_project.models to javafx.fxml;
    exports com.example.aoop_project.services;
    exports com.example.aoop_project.utils;
    opens com.example.aoop_project.utils to javafx.fxml;
}
