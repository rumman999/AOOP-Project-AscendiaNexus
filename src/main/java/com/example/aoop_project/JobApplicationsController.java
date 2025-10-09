package com.example.aoop_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;

public class JobApplicationsController {

    @FXML private Label lblJobTitle;
    @FXML private TableView<JobApplication> tblApplications;
    @FXML private TableColumn<JobApplication, Integer> colId;
    @FXML private TableColumn<JobApplication, String> colApplicantName, colApplicantEmail, colStatus;
    @FXML private TableColumn<JobApplication, LocalDateTime> colAppliedAt;
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

    @FXML
    private void handleClose() {
        Stage stage = (Stage) lblJobTitle.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private void showSuccessAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
