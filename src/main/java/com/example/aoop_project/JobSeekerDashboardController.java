package com.example.aoop_project;

import com.example.aoop_project.games.chess.Interface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField; // Import TextField
import javafx.scene.control.ContextMenu; // Import ContextMenu
import javafx.scene.control.MenuItem; // Import MenuItem
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList; // Import ArrayList
import java.util.List; // Import List
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors; // Import Collectors

public class JobSeekerDashboardController implements Initializable {

    @FXML private Label dashBoardName;
    @FXML private Label UserName;
    @FXML private Label accountDes;
    @FXML private Button logout_button;
    @FXML private ImageView dassprofilePicView;
    @FXML private Button ModerateContent;
    @FXML private Button postJobs;
    @FXML private Button btnUserSearch;
    @FXML private TextField searchUserField; // <-- Field for live search
    @FXML private Button btnJobSearch;

    private static String profile;
    private static Image defaultImage;
    private Stage userSearchStage;

    // --- For Live Search ---
    private ContextMenu searchResultsPopup;
    private List<UserData> allUsersCache = new ArrayList<>();
    // -------------------------


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String acc = Session.getLoggedInUserType();
        if(acc.equals("Jobseeker")){
            postJobs.setDisable(true);
            ModerateContent.setDisable(true);
        }

        if(acc.equals("Recruiter")){
            ModerateContent.setDisable(true);
        }

        if (defaultImage == null) {
            try {
                defaultImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/aoop_project/Images/chatbot2-unscreen.gif")));
            } catch (Exception ex) {
                System.out.println("⚠ Default profile image not found!");
            }
        }

        loadUserDataFromDB();

        // *** ADDED: Setup live search ***
        setupLiveUserSearch();
        // ******************************

        Image imgToShow;
        if (profile != null && !profile.isEmpty()) {
            try {
                imgToShow = new Image(profile);
            } catch (Exception ex) {
                imgToShow = defaultImage;
            }
        } else {
            imgToShow = defaultImage;
        }
        dassprofilePicView.setImage(imgToShow);

        double size = 72;
        dassprofilePicView.setFitWidth(size);
        dassprofilePicView.setFitHeight(size);
        dassprofilePicView.setPreserveRatio(false);
        dassprofilePicView.setSmooth(true);

        Circle clip = new Circle(size / 2, size / 2, size / 2);
        dassprofilePicView.setClip(clip);

        dassprofilePicView.setOnMouseClicked(e -> handleUploadPic());
    }

    private Connection getConnection() throws SQLException {
        String SUrl = "jdbc:mysql://localhost:4306/java_user_database";
        String SUser = "root";
        String SPass = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(SUrl, SUser, SPass);
    }

    // --- NEW METHODS FOR LIVE SEARCH ---

    /**
     * Initializes the live search functionality on the search field.
     */
    private void setupLiveUserSearch() {
        searchResultsPopup = new ContextMenu();
        // Optional: Style the popup to match the search bar width
        searchResultsPopup.setStyle("-fx-max-width: 393px; -fx-pref-width: 393px;");

        loadAllUsersIntoCache();

        searchUserField.textProperty().addListener((obs, oldVal, newVal) -> {
            onSearchTextChanged(newVal);
        });

        // Hide popup if text field loses focus
        searchUserField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                // Use Platform.runLater to avoid immediate close
                Platform.runLater(() -> searchResultsPopup.hide());
            }
        });
    }

    /**
     * Fetches all users from the DB and stores them in a local list for fast filtering.
     */
    private void loadAllUsersIntoCache() {
        allUsersCache.clear();
        // We exclude the logged-in user from the search
        String query = "SELECT id, full_name, email, account_type FROM user WHERE id != ?";

        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, Session.getLoggedInUserId());
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                // We pass null for the ImageView, as we don't need it for the dropdown
                allUsersCache.add(new UserData(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("account_type"),
                        null
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called on every keystroke in the search field.
     * Filters the local user cache and displays matching results in the popup.
     */
    private void onSearchTextChanged(String query) {
        searchResultsPopup.getItems().clear();
        if (query == null || query.isEmpty()) {
            searchResultsPopup.hide();
            return;
        }

        String lowerCaseQuery = query.toLowerCase();

        // Filter users based on name or email
        List<UserData> filteredUsers = allUsersCache.stream()
                .filter(user -> user.getFullName().toLowerCase().contains(lowerCaseQuery) ||
                        user.getEmail().toLowerCase().contains(lowerCaseQuery))
                .limit(7) // Show only top 7 matches
                .collect(Collectors.toList());

        if (filteredUsers.isEmpty()) {
            searchResultsPopup.getItems().add(new MenuItem("No users found."));
        } else {
            for (UserData user : filteredUsers) {
                String itemText = user.getFullName() + " (" + user.getAccountType() + ")";
                MenuItem item = new MenuItem(itemText);
                // Set action to navigate when clicked
                item.setOnAction(e -> onUserSelected(user));
                searchResultsPopup.getItems().add(item);
            }
        }

        // Show the dropdown popup anchored to the bottom of the search field
        if (!searchResultsPopup.isShowing()) {
            searchResultsPopup.show(searchUserField, Side.BOTTOM, 0, 0);
        }
    }

    /**
     * Called when a user is clicked from the search results dropdown.
     * Sets the session flag and navigates to the profile scene.
     */
    private void onUserSelected(UserData user) {
        System.out.println("Selected user: " + user.getFullName() + " (ID: " + user.getId() + ")");

        // Set the ID for the ViewProfileController to pick up
        Session.setProfileToViewId(user.getId());

        // Navigate to the profile scene
        getStartedApplication.launchScene("ViewProfile.fxml");

        // Clear search field and hide popup
        searchUserField.clear();
        searchResultsPopup.hide();
    }

    // --- END OF NEW METHODS ---


    private void loadUserDataFromDB() {
        String userEmail = Session.getLoggedInUserEmail();
        if (userEmail == null || userEmail.isEmpty()) return;

        try (Connection con = getConnection()) {
            String query = "SELECT full_name, account_type, profile_pic FROM user WHERE email=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, userEmail);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String fullName = rs.getString("full_name");
                String accountType = rs.getString("account_type");
                String profilePic = rs.getString("profile_pic");

                if (profilePic != null && !profilePic.isEmpty()) {
                    profile = profilePic; // update user image
                }

                UserName.setText(fullName);
                dashBoardName.setText(fullName);
                accountDes.setText(accountType);
            }
            rs.close();
            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent e) {
        Session.clear(); // clear logged-in user
        getStartedApplication.launchScene("login.fxml");
    }

    // Keep this stage reference, but it's for the selection menu now
    private Stage gameSelectionStage;

    @FXML
    private void handleGame(ActionEvent e) {
        try {
            // Check if the game selection window is already open
            if (gameSelectionStage == null || !gameSelectionStage.isShowing()) {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("GameSelection.fxml"));
                Parent root = loader.load();

                Stage owner = (Stage) ((Node) e.getSource()).getScene().getWindow();

                gameSelectionStage = new Stage();
                gameSelectionStage.setScene(new Scene(root));
                gameSelectionStage.setTitle("Game Selection");
                gameSelectionStage.initOwner(owner);
                gameSelectionStage.initModality(Modality.WINDOW_MODAL); // Makes it block the main window
                gameSelectionStage.setResizable(false);

                // Center it on the owner window
                gameSelectionStage.setOnShown(event -> {
                    gameSelectionStage.setX(owner.getX() + (owner.getWidth() - gameSelectionStage.getWidth()) / 2);
                    gameSelectionStage.setY(owner.getY() + (owner.getHeight() - gameSelectionStage.getHeight()) / 2);
                });

                gameSelectionStage.show();

            } else {
                // If it's already open, just bring it to the front
                gameSelectionStage.toFront();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            // Show an alert if the FXML fails to load
            new Alert(Alert.AlertType.ERROR, "Could not load game selection window.").showAndWait();
        }
    }

    private Stage chatStage;
    @FXML
    private void handleChatbot(ActionEvent e) {
        try {
            if (chatStage != null) {
                if (!chatStage.isShowing()) chatStage.show();
                chatStage.toFront();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatBot.fxml"));
            Parent root = loader.load();

            Stage owner = (Stage) ((Node) e.getSource()).getScene().getWindow();

            chatStage = new Stage(StageStyle.UNDECORATED);
            chatStage.setTitle("Skill Buddy");
            chatStage.setScene(new Scene(root, 450, 550));
            chatStage.setResizable(false);
            chatStage.initOwner(owner);
            chatStage.initModality(Modality.NONE);
            chatStage.setAlwaysOnTop(true);

            Runnable positionChat = () -> {
                double x = owner.getX() + owner.getWidth() - chatStage.getWidth() - 20;
                double y = owner.getY() + owner.getHeight() - chatStage.getHeight() - 35;
                chatStage.setX(x);
                chatStage.setY(y);
            };

            chatStage.setOnShown(ev -> positionChat.run());
            owner.xProperty().addListener((obs, o, n) -> positionChat.run());
            owner.yProperty().addListener((obs, o, n) -> positionChat.run());
            owner.widthProperty().addListener((obs, o, n) -> positionChat.run());
            owner.heightProperty().addListener((obs, o, n) -> positionChat.run());

            chatStage.setOnCloseRequest(ev -> chatStage = null);
            chatStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleOpenExplore(ActionEvent e) {
        getStartedApplication.launchScene("Explore.fxml");
    }

    private Stage musicStage;
    @FXML
    public void handleMusic(ActionEvent e) {
        try {
            if (musicStage == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/aoop_project/LofiMusic.fxml"));
                Parent root = loader.load();

                musicStage = new Stage();
                musicStage.setScene(new Scene(root));
                musicStage.initOwner(((Node) e.getSource()).getScene().getWindow());
                musicStage.initModality(Modality.NONE);
                musicStage.setWidth(420);
                musicStage.setHeight(200);
                musicStage.setResizable(false);
                musicStage.initStyle(StageStyle.UNDECORATED);
                musicStage.setAlwaysOnTop(true);

                musicStage.setOnCloseRequest(event -> {
                    Boolean forceClose = Boolean.TRUE.equals(musicStage.getProperties().get("forceClose"));
                    if (!forceClose) {
                        event.consume();
                        musicStage.hide();
                    }
                });

                musicStage.setOnHidden(evt -> {
                    Boolean forceClose = Boolean.TRUE.equals(musicStage.getProperties().get("forceClose"));
                    if (Boolean.TRUE.equals(forceClose)) {
                        musicStage.getProperties().remove("forceClose");
                        musicStage = null;
                    }
                });

                LofiMusicController controller = loader.getController();
                controller.initStage(musicStage);

                musicStage.show();
            } else {
                if (!musicStage.isShowing()) musicStage.show();
                else musicStage.toFront();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleJobSearch(ActionEvent e) {
        getStartedApplication.launchScene("JobSearch.fxml");
    }

    @FXML
    private void handlePostJobs(ActionEvent e) throws IOException {
        getStartedApplication.launchScene("AdminJobManager.fxml");
    }

    @FXML
    private void handleFeed(ActionEvent e) {
        getStartedApplication.launchScene("Feed.fxml");
    }

    @FXML
    public void handleTodolist(ActionEvent e){
        getStartedApplication.launchScene("TodoList.fxml");
    }

    @FXML
    public void handleCV(ActionEvent e){
        getStartedApplication.launchScene("CVBuilder.fxml");
    }

    @FXML
    public void handleProfile(ActionEvent e){
        // This button should now go to *your own* profile.
        // We set the ID to -1 so ViewProfileController knows to load the logged-in user.
        Session.setProfileToViewId(-1); // -1 means "load self"
        getStartedApplication.launchScene("ViewProfile.fxml");
    }

    @FXML
    public void handleMessages(ActionEvent e){
        getStartedApplication.launchScene("ChatUI.fxml");
    }

    @FXML
    private void handleUserSearch() {
        // This button now opens the *old* user search popup.
        // You can keep it or remove it.
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserSearch.fxml"));
            Parent root = loader.load();

            Stage owner = (Stage) btnUserSearch.getScene().getWindow();

            Stage userSearchStage = new Stage();
            userSearchStage.setScene(new Scene(root));
            userSearchStage.setTitle("Search Users");
            userSearchStage.setResizable(false);
            userSearchStage.initOwner(owner);
            userSearchStage.initModality(Modality.NONE);
            userSearchStage.initStyle(StageStyle.UNDECORATED);
            userSearchStage.setAlwaysOnTop(true);

            double centerX = owner.getX() + (owner.getWidth() - 740) / 2;
            double centerY = owner.getY() + (owner.getHeight() - 600) / 2;
            userSearchStage.setX(centerX);
            userSearchStage.setY(centerY);

            userSearchStage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleUploadPic() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = chooser.showOpenDialog(dassprofilePicView.getScene().getWindow());
        if (file != null) {
            profile = file.toURI().toString();
            dassprofilePicView.setImage(new Image(profile));
            // You must save this to the database
            saveProfilePicToDB(profile);
            System.out.println("🖼 Selected profile image: " + profile);
        }
    }

    /**
     * Utility method to save the new profile pic path to the database.
     */
    private void saveProfilePicToDB(String picPath) {
        String sql = "UPDATE user SET profile_pic = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, picPath);
            stmt.setInt(2, Session.getLoggedInUserId());
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}