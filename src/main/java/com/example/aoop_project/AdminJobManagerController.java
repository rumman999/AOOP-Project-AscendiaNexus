package com.example.aoop_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class AdminJobManagerController {

    @FXML private TableView<Job> tblJobs;
    @FXML private TableColumn<Job, Integer> colId;
    @FXML private TableColumn<Job, String> colTitle, colLocation, colSalary, colJobType;
    @FXML private TableColumn<Job, LocalDateTime> colPostedAt;

    @FXML private TextField fldTitle, fldLocation, fldSalary, fldJobType, fldTechStack;
    @FXML private TextArea fldDescription, fldRequirements;

    @FXML private Button btnSave, btnClear, btnDelete, btnViewApplications;
    @FXML private Label lblPageTitle;

    // DAO instances
    private final JobDAO jobDAO = new JobDAO();
    private final JobApplicationDAO applicationDAO = new JobApplicationDAO();

    // In-memory list backing the TableView
    private final ObservableList<Job> jobList = FXCollections.observableArrayList();

    // Currently editing job (null → new)
    private Job selectedJob;

    @FXML
    public void initialize() {
        System.out.println("Initializing AdminJobManagerController...");

        // Debug: Check if all FXML elements are loaded
        if (colId == null) System.out.println("ERROR: colId is null");
        if (colTitle == null) System.out.println("ERROR: colTitle is null");
        if (colJobType == null) System.out.println("ERROR: colJobType is null");
        if (colLocation == null) System.out.println("ERROR: colLocation is null");
        if (colSalary == null) System.out.println("ERROR: colSalary is null");
        if (colPostedAt == null) System.out.println("ERROR: colPostedAt is null");

        // Only configure columns if they exist
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colTitle != null) colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        if (colLocation != null) colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        if (colSalary != null) colSalary.setCellValueFactory(new PropertyValueFactory<>("salaryRange"));
        if (colJobType != null) colJobType.setCellValueFactory(new PropertyValueFactory<>("jobType"));
        if (colPostedAt != null) colPostedAt.setCellValueFactory(new PropertyValueFactory<>("postedAt"));

        // Bind list to table
        if (tblJobs != null) {
            tblJobs.setItems(jobList);

            // Listen for selection
            tblJobs.getSelectionModel().selectedItemProperty()
                    .addListener((obs, old, sel) -> onSelect(sel));
        }

        // Set page title and load jobs based on user role
        String userType = Session.getLoggedInUserType();
        if (lblPageTitle != null) {
            if ("Admin".equals(userType)) {
                lblPageTitle.setText("Admin Job Management - All Jobs");
            } else if ("Recruiter".equals(userType)) {
                lblPageTitle.setText("Recruiter Job Management - My Jobs");
            }
        }

        if ("Admin".equals(userType)) {
            loadAllJobs();
        } else if ("Recruiter".equals(userType)) {
            loadRecruiterJobs();
        }

        System.out.println("AdminJobManagerController initialized successfully");
    }

    private void onSelect(Job job) {
        if (job == null) {
            clearForm();
            selectedJob = null;
            if (btnSave != null) btnSave.setText("Post Job");
            if (btnViewApplications != null) btnViewApplications.setDisable(true);
        } else {
            selectedJob = job;
            if (fldTitle != null) fldTitle.setText(job.getTitle());
            if (fldLocation != null) fldLocation.setText(job.getLocation());
            if (fldSalary != null) fldSalary.setText(job.getSalaryRange());
            if (fldJobType != null) fldJobType.setText(job.getJobType());
            if (fldTechStack != null) fldTechStack.setText(job.getTechStack());
            if (fldDescription != null) fldDescription.setText(job.getDescription());
            if (fldRequirements != null) fldRequirements.setText(job.getRequirements());
            if (btnSave != null) btnSave.setText("Update Job");
            if (btnViewApplications != null) btnViewApplications.setDisable(false);
        }
    }

    private void loadAllJobs() {
        try {
            List<Job> all = jobDAO.findAll();
            jobList.setAll(all);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading jobs: " + e.getMessage());
        }
        clearForm();
    }

    private void loadRecruiterJobs() {
        try {
            int recruiterId = Session.getLoggedInUserId();
            List<Job> recruiterJobs = jobDAO.findByPosterId(recruiterId);
            jobList.setAll(recruiterJobs);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading jobs: " + e.getMessage());
        }
        clearForm();
    }

    @FXML
    private void handleSave() {
        String title = fldTitle != null ? fldTitle.getText().trim() : "";
        String location = fldLocation != null ? fldLocation.getText().trim() : "";
        String salary = fldSalary != null ? fldSalary.getText().trim() : "";
        String jobType = fldJobType != null ? fldJobType.getText().trim() : "";
        String techStack = fldTechStack != null ? fldTechStack.getText().trim() : "";
        String description = fldDescription != null ? fldDescription.getText().trim() : "";
        String requirements = fldRequirements != null ? fldRequirements.getText().trim() : "";

        if (title.isEmpty() || description.isEmpty()) {
            showAlert("Title and Description are required.");
            return;
        }

        try {
            if (selectedJob == null) {
                // Insert new job
                Job job = new Job();
                job.setPosterId(Session.getLoggedInUserId());
                job.setTitle(title);
                job.setLocation(location);
                job.setSalaryRange(salary);
                job.setJobType(jobType);
                job.setTechStack(techStack);
                job.setDescription(description);
                job.setRequirements(requirements);
                jobDAO.create(job);
            } else {
                // Update existing job
                selectedJob.setTitle(title);
                selectedJob.setLocation(location);
                selectedJob.setSalaryRange(salary);
                selectedJob.setJobType(jobType);
                selectedJob.setTechStack(techStack);
                selectedJob.setDescription(description);
                selectedJob.setRequirements(requirements);
                jobDAO.update(selectedJob);
            }

            // Refresh the appropriate job list
            String userType = Session.getLoggedInUserType();
            if ("Admin".equals(userType)) {
                loadAllJobs();
            } else {
                loadRecruiterJobs();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Save failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Job job = tblJobs != null ? tblJobs.getSelectionModel().getSelectedItem() : null;
        if (job == null) {
            showAlert("Select a job to delete.");
            return;
        }

        // Check if user has permission to delete this job
        String userType = Session.getLoggedInUserType();
        if ("Recruiter".equals(userType) && job.getPosterId() != Session.getLoggedInUserId()) {
            showAlert("You can only delete jobs that you posted.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Job");
        confirmation.setContentText("Are you sure you want to delete this job? This will also delete all applications for this job.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    jobDAO.delete(job.getId());

                    // Refresh the appropriate job list
                    if ("Admin".equals(userType)) {
                        loadAllJobs();
                    } else {
                        loadRecruiterJobs();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Delete failed: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleViewApplications() {
        Job job = tblJobs != null ? tblJobs.getSelectionModel().getSelectedItem() : null;
        if (job == null) {
            showAlert("Select a job to view applications.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/aoop_project/JobApplications.fxml"));
            Parent root = loader.load();

            JobApplicationsController controller = loader.getController();
            controller.setJob(job);

            Stage stage = new Stage();
            stage.setTitle("Applications for: " + job.getTitle());
            stage.setScene(new Scene(root, 800, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(((Node) btnViewApplications).getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error opening applications window: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent e) throws IOException {
        getStartedApplication.launchScene("JobSeekerDashboard.fxml");
    }

    @FXML
    private void handleClear() {
        if (tblJobs != null) tblJobs.getSelectionModel().clearSelection();
        clearForm();
    }

    private void clearForm() {
        if (fldTitle != null) fldTitle.clear();
        if (fldLocation != null) fldLocation.clear();
        if (fldSalary != null) fldSalary.clear();
        if (fldJobType != null) fldJobType.clear();
        if (fldTechStack != null) fldTechStack.clear();
        if (fldDescription != null) fldDescription.clear();
        if (fldRequirements != null) fldRequirements.clear();
        if (btnSave != null) btnSave.setText("Post Job");
        if (btnViewApplications != null) btnViewApplications.setDisable(true);
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }
}
