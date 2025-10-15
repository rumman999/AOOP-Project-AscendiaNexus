package com.example.aoop_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop; // Requires java.desktop module
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class JobApplicationsController {

    @FXML private Label lblJobTitle;
    @FXML private TableView<JobApplication> tblApplications;
    @FXML private TableColumn<JobApplication, Integer> colId;
    @FXML private TableColumn<JobApplication, String> colApplicantName, colApplicantEmail, colStatus;
    @FXML private TableColumn<JobApplication, LocalDateTime> colAppliedAt;
    @FXML private TableColumn<JobApplication, Void> colCvAction; // This column will hold our button
    @FXML private TextArea txtCoverLetter;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private Button btnUpdateStatus;

    private final JobApplicationDAO applicationDAO = new JobApplicationDAO();
    private final ObservableList<JobApplication> applicationList = FXCollections.observableArrayList();
    private Job currentJob;
    private JobApplication selectedApplication;

    @FXML
    public void initialize() {
        // Configure columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colApplicantName.setCellValueFactory(new PropertyValueFactory<>("applicantName"));
        colApplicantEmail.setCellValueFactory(new PropertyValueFactory<>("applicantEmail"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAppliedAt.setCellValueFactory(new PropertyValueFactory<>("appliedAt"));

        // Bind list to table
        tblApplications.setItems(applicationList);

        // Setup status combo box
        cmbStatus.setItems(FXCollections.observableArrayList("pending", "reviewed", "accepted", "rejected"));

        // Listen for selection
        tblApplications.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, sel) -> onSelectApplication(sel));

        btnUpdateStatus.setDisable(true);

        // Configure the CV Action column to show a "View CV" button
        colCvAction.setCellFactory(col -> new TableCell<>() {
            private final Button viewCvButton = new Button("View CV");

            {
                viewCvButton.setStyle(
                        "-fx-background-color: #008CBA;" + // Blue background
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 4 10 4 10;"
                );
                viewCvButton.setOnAction(event -> {
                    JobApplication application = getTableView().getItems().get(getIndex());
                    handleViewCv(application);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    JobApplication application = getTableView().getItems().get(getIndex());
                    // Only show button if a CV path exists
                    boolean hasCv = application.getCvPath() != null && !application.getCvPath().isEmpty();
                    setGraphic(hasCv ? viewCvButton : null);
                }
            }
        });
    }

    public void setJob(Job job) {
        this.currentJob = job;
        lblJobTitle.setText("Applications for: " + job.getTitle());
        loadApplications();
    }

    private void loadApplications() {
        if (currentJob == null) return;
        try {
            List<JobApplication> applications = applicationDAO.findByJobId(currentJob.getId());
            applicationList.setAll(applications);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading applications: " + e.getMessage());
        }
    }

    private void onSelectApplication(JobApplication application) {
        selectedApplication = application;
        if (application == null) {
            txtCoverLetter.clear();
            cmbStatus.setValue(null);
            btnUpdateStatus.setDisable(true);
        } else {
            txtCoverLetter.setText(application.getCoverLetter() != null ? application.getCoverLetter() : "No cover letter provided.");
            cmbStatus.setValue(application.getStatus());
            btnUpdateStatus.setDisable(false);
        }
    }

    @FXML
    private void handleUpdateStatus() {
        if (selectedApplication == null) {
            showAlert("Please select an application.");
            return;
        }
        String newStatus = cmbStatus.getValue();
        if (newStatus == null) {
            showAlert("Please select a status.");
            return;
        }
        try {
            applicationDAO.updateStatus(selectedApplication.getId(), newStatus);
            selectedApplication.setStatus(newStatus);
            tblApplications.refresh();
            showSuccessAlert("Status updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error updating status: " + e.getMessage());
        }
    }

    /**
     * Handles opening the CV. It minimizes the main window first.
     */
    private void handleViewCv(JobApplication application) {
        String cvPath = application.getCvPath();
        if (cvPath == null || cvPath.isEmpty()) {
            showAlert("No CV uploaded for this application.");
            return;
        }

        File cvFile = new File(cvPath);
        if (cvFile.exists()) {
            // ** START OF KEY CHANGE **
            // Get the current window (Stage)
            Stage stage = (Stage) tblApplications.getScene().getWindow();
            // Minimize the window
            stage.setIconified(true);
            // ** END OF KEY CHANGE **

            try {
                // Open the file with the default system application
                Desktop.getDesktop().open(cvFile);
            } catch (IOException e) {
                showAlert("Error opening CV file: " + e.getMessage());
                // If opening fails, restore the window
                stage.setIconified(false);
            } catch (UnsupportedOperationException e) {
                showAlert("Desktop operations not supported on this system.");
                stage.setIconified(false);
            }
        } else {
            showAlert("CV file not found at the specified path: " + cvPath);
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) lblJobTitle.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String msg) {
        Stage owner = (Stage) lblJobTitle.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.initOwner(owner);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.showAndWait();
    }

    private void showSuccessAlert(String msg) {
        Stage owner = (Stage) lblJobTitle.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.initOwner(owner);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.showAndWait();
    }
}