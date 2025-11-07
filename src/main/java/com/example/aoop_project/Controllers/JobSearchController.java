package com.example.aoop_project.Controllers;

import com.example.aoop_project.*;
import com.example.aoop_project.dao.JobApplicationDAO;
import com.example.aoop_project.dao.JobDAO;
import com.example.aoop_project.models.Job;
import com.example.aoop_project.models.JobApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
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
    @FXML private Label lblCvFileName;

    private final JobDAO jobDAO = new JobDAO();
    private final JobApplicationDAO appDAO = new JobApplicationDAO();
    private final ObservableList<Job> results = FXCollections.observableArrayList();
    private File selectedCvFile;

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
                btn.setStyle(
                        "-fx-background-color: #00F270;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 4 10 4 10;"
                );

                btn.setOnMouseEntered(e ->
                        btn.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 4 10 4 10;")
                );
                btn.setOnMouseExited(e ->
                        btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 4 10 4 10;")
                );

                btn.setOnAction(e -> apply(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Initial load
        refreshJobList();
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

    @FXML
    private void handleUploadCv(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CV File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Word Documents", "*.docx", "*.doc"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        selectedCvFile = fileChooser.showOpenDialog(new Stage());
        if (selectedCvFile != null) {
            lblCvFileName.setText(selectedCvFile.getName());
        } else {
            lblCvFileName.setText("No file selected");
        }
    }

    private void apply(Job job) {
        try {
            int userId = Session.getLoggedInUserId();

            if (appDAO.hasUserApplied(job.getId(), userId)) {
                Alert info = new Alert(Alert.AlertType.INFORMATION, "Already applied");
                info.initOwner(tblResults.getScene().getWindow());
                info.initModality(Modality.WINDOW_MODAL);
                info.showAndWait();
                return;
            }

            if (selectedCvFile == null) {
                Alert warning = new Alert(Alert.AlertType.WARNING, "Please upload your CV before applying.");
                warning.initOwner(tblResults.getScene().getWindow());
                warning.initModality(Modality.WINDOW_MODAL);
                warning.showAndWait();
                return;
            }

            String cvPath = appDAO.saveCvFile(selectedCvFile);

            JobApplication app = new JobApplication();
            app.setJobId(job.getId());
            app.setApplicantId(userId);
            app.setCoverLetter(""); // Default empty cover letter
            app.setCvPath(cvPath);

            appDAO.create(app);

            Alert success = new Alert(Alert.AlertType.INFORMATION, "Application submitted!");
            success.initOwner(tblResults.getScene().getWindow());
            success.initModality(Modality.WINDOW_MODAL);
            success.showAndWait();

            selectedCvFile = null;
            lblCvFileName.setText("No file selected");

        } catch (IOException e) {
            Alert error = new Alert(Alert.AlertType.ERROR, "Failed to upload CV: " + e.getMessage());
            error.initOwner(tblResults.getScene().getWindow());
            error.initModality(Modality.WINDOW_MODAL);
            error.showAndWait();
        } catch (Exception ex) {
            Alert error = new Alert(Alert.AlertType.WARNING, "Apply failed: " + ex.getMessage());
            error.initOwner(tblResults.getScene().getWindow());
            error.initModality(Modality.WINDOW_MODAL);
            error.showAndWait();
        }
    }

    @FXML
    private void handleClear(ActionEvent e){
        fldKeyword.clear();
        fldJobType.clear();
        fldLocation.clear();
        fldTechStack.clear();
        selectedCvFile = null;
        lblCvFileName.setText("No file selected");
        refreshJobList();
    }

    private void refreshJobList() {
        try {
            List<Job> all = jobDAO.findAll();
            results.setAll(all);
        } catch (Exception e) {
            new Alert(Alert.AlertType.WARNING, "Failed to load jobs: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleClose(ActionEvent e) {
        AscendiaNexusApp.launchScene("JobSeekerDashboard.fxml");
    }

    @FXML
    public void handleCV(ActionEvent e){
        // This will navigate to your CV builder screen
        AscendiaNexusApp.launchScene("CVBuilder.fxml");
    }
}