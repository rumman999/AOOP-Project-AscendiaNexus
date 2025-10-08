package com.example.aoop_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class JobSearchController implements Initializable {

    @FXML private TextField fldKeyword, fldJobType, fldLocation, fldTechStack;
    @FXML private TableView<Job> tblResults;
    @FXML private TableColumn<Job, Integer> colId;
    @FXML private TableColumn<Job, String> colTitle, colType, colLocation, colTech;
    @FXML private TableColumn<Job, LocalDateTime> colPosted;
    @FXML private TableColumn<Job, Void> colAction;

    private final JobDAO jobDAO = new JobDAO();
    private final JobApplicationDAO appDAO = new JobApplicationDAO();
    private final ObservableList<Job> results = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configure columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colType.setCellValueFactory(new PropertyValueFactory<>("jobType"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colTech.setCellValueFactory(new PropertyValueFactory<>("techStack"));
        colPosted.setCellValueFactory(new PropertyValueFactory<>("postedAt"));
        tblResults.setItems(results);

        // Add Apply button per row
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Apply");
            {
                btn.setOnAction(e -> apply(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Initial load: fetch all jobs
        try {
            List<Job> all = jobDAO.findAll();
            results.setAll(all);
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Failed to load jobs: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleSearch(ActionEvent e) {
        try {
            List<Job> list = jobDAO.searchJobs(
                    fldKeyword.getText(),
                    fldJobType.getText(),
                    fldLocation.getText(),
                    fldTechStack.getText()
            );
            results.setAll(list);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.WARNING, "Search failed: " + ex.getMessage()).showAndWait();
        }
    }

    private void apply(Job job) {
        try {
            int userId = Session.getLoggedInUserId();
            if (appDAO.hasUserApplied(job.getId(), userId)) {
                new Alert(Alert.AlertType.INFORMATION, "Already applied").showAndWait();
                return;
            }
            JobApplication app = new JobApplication();
            app.setJobId(job.getId());
            app.setApplicantId(userId);
            app.setCoverLetter("");
            appDAO.create(app);
            new Alert(Alert.AlertType.INFORMATION, "Application submitted!").showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.WARNING, "Apply failed: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleClose(ActionEvent e) {
        getStartedApplication.launchScene("JobSeekerDashboard.fxml");
    }
}
