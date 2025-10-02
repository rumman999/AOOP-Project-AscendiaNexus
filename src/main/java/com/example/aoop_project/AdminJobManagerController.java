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
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class AdminJobManagerController {

    @FXML private TableView<Job> tblJobs;
    @FXML private TableColumn<Job, Integer>    colId;
    @FXML private TableColumn<Job, String>     colTitle, colLocation, colSalary;
    @FXML private TableColumn<Job, LocalDateTime> colPostedAt;

    @FXML private TextField fldTitle, fldLocation, fldSalary;
    @FXML private TextArea  fldDescription;

    @FXML private Button btnSave, btnClear, btnDelete;

    // DAO instance
    private final JobDAO jobDAO = new JobDAO();

    // In-memory list backing the TableView
    private final ObservableList<Job> jobList = FXCollections.observableArrayList();

    // Currently editing job (null → new)
    private Job selectedJob;


    @FXML
    public void initialize() {
        // 1) configure columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salaryRange"));
        colPostedAt.setCellValueFactory(new PropertyValueFactory<>("postedAt"));

        // 2) bind list to table
        tblJobs.setItems(jobList);

        // 3) listen for selection
        tblJobs.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, sel) -> onSelect(sel));

        // 4) load existing jobs
        refreshTable();
    }

    private void onSelect(Job job) {
        if (job == null) {
            clearForm();
            selectedJob = null;
            btnSave.setText("Save");
        } else {
            selectedJob = job;
            fldTitle.setText(job.getTitle());
            fldLocation.setText(job.getLocation());
            fldSalary.setText(job.getSalaryRange());
            fldDescription.setText(job.getDescription());
            btnSave.setText("Update");
        }
    }

    private void refreshTable() {
        try {
            List<Job> all = jobDAO.findAll();
            jobList.setAll(all);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading jobs: " + e.getMessage());
        }
        clearForm();
    }

    @FXML
    private void handleSave() {
        String title = fldTitle.getText().trim();
        String loc   = fldLocation.getText().trim();
        String sal   = fldSalary.getText().trim();
        String desc  = fldDescription.getText().trim();

        if (title.isEmpty() || desc.isEmpty()) {
            showAlert("Title and Description are required.");
            return;
        }

        try {
            if (selectedJob == null) {
                // insert
                Job j = new Job();
                j.setPosterId(Session.getLoggedInUserId());     // admin’s user ID
                j.setTitle(title);
                j.setLocation(loc);
                j.setSalaryRange(sal);
                j.setDescription(desc);
                jobDAO.create(j);
            } else {
                // update
                selectedJob.setTitle(title);
                selectedJob.setLocation(loc);
                selectedJob.setSalaryRange(sal);
                selectedJob.setDescription(desc);
                jobDAO.update(selectedJob);
            }
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Save failed: " + e.getMessage());
        }
    }



    @FXML
    private void handleDelete() {
        Job job = tblJobs.getSelectionModel().getSelectedItem();
        if (job == null) {
            showAlert("Select a job to delete.");
            return;
        }
        try {
            jobDAO.delete(job.getId());
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Delete failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent e) throws IOException {
        // Close current window
        Stage currentStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        currentStage.close();

        // Load JobseekerDashboard.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/aoop_project/JobSeekerDashboard.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Jobseeker Dashboard");
        stage.show();
    }


    @FXML
    private void handleClear() {
        tblJobs.getSelectionModel().clearSelection();
        clearForm();
    }

    private void clearForm() {
        fldTitle.clear();
        fldLocation.clear();
        fldSalary.clear();
        fldDescription.clear();
        btnSave.setText("Save");
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }
}
