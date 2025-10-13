package com.example.aoop_project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TodoController {

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, Boolean> doneColumn;
    @FXML private TableColumn<Task, String> taskColumn;
    @FXML private TextField taskField;
    @FXML private Label emailLabel;

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private final Gson gson = new Gson();
    private String loggedInEmail;

    @FXML
    public void initialize() {
        // Get logged-in user
        loggedInEmail = Session.getLoggedInUserEmail();
        String loggedInName = Session.getLoggedInUserName();

        if (loggedInName != null && !loggedInName.isEmpty()) {
            emailLabel.setText(" " + loggedInName + "!");
        } else if (loggedInEmail != null && !loggedInEmail.isEmpty()) {
            emailLabel.setText(" " + loggedInEmail + "!");
        } else {
            emailLabel.setText("Welcome back!");
        }

        // Configure table
        taskTable.setEditable(true);

        doneColumn.setCellValueFactory(cd -> cd.getValue().doneProperty());
        doneColumn.setCellFactory(CheckBoxTableCell.forTableColumn(doneColumn));

        taskColumn.setCellValueFactory(cd -> cd.getValue().taskProperty());

        taskTable.setItems(tasks);

        // Load from DB immediately
        loadTasksFromDB();

    /* ----------------------------------------------
       ✅ Add text wrapping for the taskColumn
       ---------------------------------------------- */
        taskColumn.setCellFactory(col -> {
            TableCell<Task, String> cell = new TableCell<>() {
                private final Label label = new Label();

                {
                    label.setWrapText(true);
                    label.setStyle("-fx-font-size: 11px; -fx-text-fill: #333333;"); // safe explicit color & size
                    setGraphic(label);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        label.setText(null);
                        setGraphic(null);
                        setPrefHeight(Control.USE_COMPUTED_SIZE);
                    } else {
                        label.setText(item);
                        setGraphic(label);

                        // 1) Ensure label width follows column width (subtract padding)
                        if (getTableColumn() != null) {
                            double padding = 16; // tweak if your cell has different horizontal padding
                            label.setMaxWidth(getTableColumn().getWidth() - padding);

                            // Keep label max width updated on column resize
                            getTableColumn().widthProperty().addListener((obs, oldW, newW) -> {
                                label.setMaxWidth(newW.doubleValue() - padding);
                                // force recompute when column resizes
                                Platform.runLater(() -> {
                                    double prefH = label.prefHeight(label.getMaxWidth());
                                    if (prefH > 0) setPrefHeight(prefH + 12);
                                });
                            });
                        }

                        // 2) After layout pass, compute pref height and set cell height
                        Platform.runLater(() -> {
                            double prefH = label.prefHeight(label.getMaxWidth());
                            if (prefH > 0) setPrefHeight(prefH + 12); // + padding
                        });
                    }
                }
            };

            // Defensive: disable cell caching (sometimes helps layout issues)
            cell.setCache(false);

            return cell;
        });
    }


    @FXML
    private void handleAddTask() {
        String text = taskField.getText().trim();
        if (text.isEmpty()) return;

        Task newTask = new Task(text, false, this::saveTasksToDB);
        tasks.add(newTask);
        saveTasksToDB();
        taskField.clear();
    }

    @FXML
    private void handleDeleteTask() {
        Task sel = taskTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            tasks.remove(sel);
            saveTasksToDB();
        }
    }

    @FXML
    private void handleDashboard(ActionEvent e) {
        getStartedApplication.launchScene("JobSeekerDashboard.fxml");
    }

    /** Save current tasks as JSON into DB */
    private void saveTasksToDB() {
        if (loggedInEmail == null) {
            System.out.println("No logged-in email → skipping save.");
            return;
        }

        String json = gson.toJson(toDTOs(new ArrayList<>(tasks)));

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:4306/java_user_database", "root", "");
             PreparedStatement pst = con.prepareStatement(
                     "UPDATE user SET todo_list=? WHERE email=?")) {

            pst.setString(1, json);
            pst.setString(2, loggedInEmail);
            int rows = pst.executeUpdate();

            if (rows == 0) {
                System.out.println("⚠️ No row updated! Check if email exists in DB: " + loggedInEmail);
            } else {
                System.out.println("✅ Tasks saved for " + loggedInEmail + ": " + json);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Load tasks JSON from DB into TableView */
    private void loadTasksFromDB() {
        if (loggedInEmail == null) {
            System.out.println("No logged-in email → skipping load.");
            return;
        }

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:4306/java_user_database", "root", "");
             PreparedStatement pst = con.prepareStatement(
                     "SELECT todo_list FROM user WHERE email=?")) {

            pst.setString(1, loggedInEmail);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String json = rs.getString("todo_list");
                if (json != null && !json.trim().isEmpty()) {
                    try {
                        Type type = new TypeToken<List<TaskDTO>>(){}.getType();
                        List<TaskDTO> list = gson.fromJson(json, type);
                        if (list != null) {
                            tasks.setAll(toTasks(list));
                        }
                    } catch (Exception ex) {
                        System.err.println("Invalid JSON in DB: " + json);
                        ex.printStackTrace();
                    }
                }
            }

            System.out.println("✅ Tasks loaded for " + loggedInEmail);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Task model ---
    public static class Task {
        private final StringProperty task = new SimpleStringProperty();
        private final BooleanProperty done = new SimpleBooleanProperty();

        public Task(String task, boolean done, Runnable onChange) {
            this.task.set(task);
            this.done.set(done);

            // Save whenever "done" changes
            this.done.addListener((obs, o, n) -> {
                if (onChange != null) onChange.run();
            });
        }

        public StringProperty taskProperty() { return task; }
        public BooleanProperty doneProperty() { return done; }

        public String getTask() { return task.get(); }
        public boolean isDone() { return done.get(); }
    }

    // --- DTO for JSON ---
    public static class TaskDTO {
        public String task;
        public boolean done;

        public TaskDTO(String t, boolean d) {
            this.task = t;
            this.done = d;
        }
    }

    private static List<TaskDTO> toDTOs(List<Task> list) {
        List<TaskDTO> out = new ArrayList<>();
        for (Task t : list) out.add(new TaskDTO(t.getTask(), t.isDone()));
        return out;
    }

    private static List<Task> toTasks(List<TaskDTO> list) {
        List<Task> out = new ArrayList<>();
        for (TaskDTO d : list) out.add(new Task(d.task, d.done, null));
        return out;
    }
    @FXML private void handleExplore(ActionEvent e){
        getStartedApplication.launchScene("Explore.fxml");
    }
}
