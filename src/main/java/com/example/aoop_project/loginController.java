package com.example.aoop_project;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.sql.*;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class loginController  {

    @FXML private VBox loginForm;
    @FXML private VBox signupForm;
    @FXML private TextField fnameUI;
    @FXML private TextField emailUI;
    @FXML private TextField phoneNoUI;
    @FXML private PasswordField passwordUI;
    @FXML private PasswordField signUpConfirmPasswordFieldUI;
    @FXML private ComboBox accountTypeUI;
    @FXML private Button SignUpButtonUI;

    @FXML private TextField loginEmailUI;
    @FXML private PasswordField loginPasswordUI;
    @FXML private Label LoginErrorUI;
    @FXML private Button LoginButton;
    @FXML private Label signUpError;

    @FXML
    public void initialize() {

        // ✅ Hide signup msg initially
        signUpError.setVisible(false);
        signUpError.setManaged(false);

        loginEmailUI.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER, DOWN -> loginPasswordUI.requestFocus();
            }
        });

        loginPasswordUI.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER, DOWN -> handleLogin(new ActionEvent());
                case UP -> loginEmailUI.requestFocus();
            }
        });

        java.util.function.BiConsumer<TextField, TextField> nextPrevNav = (current, next) -> {
            current.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ENTER, DOWN -> next.requestFocus();
                }
            });
        };

        TextField[] signupFields = {fnameUI, emailUI, phoneNoUI, passwordUI, signUpConfirmPasswordFieldUI};
        nextPrevNav.accept(fnameUI, emailUI);
        nextPrevNav.accept(emailUI, phoneNoUI);
        nextPrevNav.accept(phoneNoUI, passwordUI);
        nextPrevNav.accept(passwordUI, signUpConfirmPasswordFieldUI);

        signUpConfirmPasswordFieldUI.setOnKeyPressed(event -> {
            switch(event.getCode()) {
                case ENTER, DOWN -> accountTypeUI.requestFocus();
                case UP -> passwordUI.requestFocus();
            }
        });

        accountTypeUI.setOnKeyPressed(event -> {
            switch(event.getCode()) {
                case ENTER, DOWN -> handleSignup(new ActionEvent());
                case UP -> signUpConfirmPasswordFieldUI.requestFocus();
            }
        });

        for (int i = 0; i < signupFields.length; i++) {
            int prevIndex = i - 1;
            int nextIndex = i + 1;
            TextField current = signupFields[i];
            current.setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case UP -> { if (prevIndex >= 0) signupFields[prevIndex].requestFocus(); }
                    case DOWN -> { if (nextIndex < signupFields.length) signupFields[nextIndex].requestFocus(); }
                }
            });
        }

        fnameUI.setOnAction(e -> emailUI.requestFocus());
        emailUI.setOnAction(e -> phoneNoUI.requestFocus());
        phoneNoUI.setOnAction(e -> passwordUI.requestFocus());
        passwordUI.setOnAction(e -> signUpConfirmPasswordFieldUI.requestFocus());
        signUpConfirmPasswordFieldUI.setOnAction(e -> accountTypeUI.requestFocus());
        accountTypeUI.setOnAction(e -> handleSignup(new ActionEvent()));
    }

    @FXML
    private void showSignupForm() {
        loginForm.setVisible(false);
        loginForm.setManaged(false);
        signupForm.setVisible(true);
        signupForm.setManaged(true);

        VBox skillPlusPlusVBox = (VBox) ((GridPane) loginForm.getParent()).getChildren().get(0);
        skillPlusPlusVBox.setStyle("-fx-border-color: blue; -fx-border-radius: 10; -fx-background-color: #B7E3F6; -fx-background-radius: 10;");
    }

    @FXML
    private void showLoginForm() {
        signupForm.setVisible(false);
        signupForm.setManaged(false);
        loginForm.setVisible(true);
        loginForm.setManaged(true);

        VBox skillPlusPlusVBox = (VBox) ((GridPane) loginForm.getParent()).getChildren().get(0);
        skillPlusPlusVBox.setStyle("-fx-padding: 20;");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = loginEmailUI.getText().trim();
        String password = loginPasswordUI.getText().trim();

        if(email.isEmpty()) { LoginErrorUI.setText("❌ Email is required!"); return; }
        if(!validateEmailAddress2(email)) { LoginErrorUI.setText("❌ Invalid Email address!"); return; }
        if(password.isEmpty()) { LoginErrorUI.setText("❌ Password is required!"); return; }
        if(!validatePassword2(password)) { LoginErrorUI.setText("❌ Password must have 6-15 chars, upper/lower & special."); return; }

        String passDB = "";
        String SUrl = "jdbc:mysql://localhost:4306/java_user_database";
        String SUser = "root";
        String SPass = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(SUrl, SUser, SPass);
            Statement st = con.createStatement();

            String query = "SELECT * FROM user WHERE email='" + email + "'";
            ResultSet rs = st.executeQuery(query);

            if(rs.next()) {
                passDB = rs.getString("password");

                if(password.equals(passDB)) {
                    int id = rs.getInt("id");
                    LoginErrorUI.setText("✅ Login successful! Welcome, " + rs.getString("full_name") + "!");

                    Session.setLoggedInUserId(id);
                    Session.setLoggedInUserEmail(email);
                    Session.setLoggedInUserName(rs.getString("full_name"));
                    Session.setLoggedInUserType(rs.getString("account_type"));

                    getStartedApplication.launchScene("JobSeekerDashboard.fxml");
                } else {
                    LoginErrorUI.setText("❌ Incorrect Email or Password!");
                }
            } else {
                LoginErrorUI.setText("❌ Incorrect Email or Password!");
            }

            rs.close();
            st.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            LoginErrorUI.setText("❌ Database connection error!");
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        String full_name, email_address, password, phone_number, account_type, query;
        String SUrl, Suser, Spass;
        SUrl = "jdbc:mysql://localhost:4306/java_user_database";
        Suser = "root";
        Spass = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(SUrl, Suser, Spass);
            Statement st = con.createStatement();

            if (validateUsername(fnameUI.getText().trim()) &&
                    validateEmailAddress(emailUI.getText().trim()) &&
                    validatePhoneNo(phoneNoUI.getText().trim()) &&
                    validatePassword(passwordUI.getText().trim()) &&
                    validateConfirmPassword(passwordUI.getText().trim(), signUpConfirmPasswordFieldUI.getText().trim()) &&
                    validateComboBox(accountTypeUI)) {

                full_name = fnameUI.getText().trim();
                email_address = emailUI.getText().trim();
                password = passwordUI.getText().trim();
                phone_number = phoneNoUI.getText().trim();
                account_type = accountTypeUI.getValue().toString().trim();

                String checkQuery = "SELECT * FROM user WHERE full_name=? OR email=? OR phone_number=?";
                PreparedStatement checkStmt = con.prepareStatement(checkQuery);
                checkStmt.setString(1, full_name);
                checkStmt.setString(2, email_address);
                checkStmt.setString(3, phone_number);

                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {

                    query = "INSERT INTO user(full_name,email,password,phone_number,account_type,UUID) VALUES(?,?,?,?,?,?)";
                    UUID id = UUID.randomUUID();
                    PreparedStatement insertStmt = con.prepareStatement(query);
                    insertStmt.setString(1, full_name);
                    insertStmt.setString(2, email_address);
                    insertStmt.setString(3, password);
                    insertStmt.setString(4, phone_number);
                    insertStmt.setString(5, account_type);
                    insertStmt.setString(6, id.toString());
                    insertStmt.executeUpdate();

                    // ✅ SHOW GREEN MESSAGE
                    signUpError.setVisible(true);
                    signUpError.setManaged(true);
                    signUpError.setStyle("-fx-text-fill: green;");
                    signUpError.setText("✅ User created successfully!");

                    // ✅ AUTO SWITCH TO LOGIN AFTER 2 SEC
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(e -> showLoginForm());
                    pause.play();

                    fnameUI.clear();
                    emailUI.clear();
                    phoneNoUI.clear();
                    passwordUI.clear();
                    signUpConfirmPasswordFieldUI.clear();
                    accountTypeUI.getSelectionModel().clearSelection();

                    insertStmt.close();
                } else {

                    signUpError.setVisible(true);
                    signUpError.setManaged(true);
                    signUpError.setStyle("-fx-text-fill: red;");

                    if (rs.getString("full_name").equals(full_name)) {
                        signUpError.setText("❌ Username already exists!");
                    } else if (rs.getString("email").equals(email_address)) {
                        signUpError.setText("❌ Email already registered!");
                    } else if (rs.getString("phone_number").equals(phone_number)) {
                        signUpError.setText("❌ Phone already registered!");
                    }
                }

                rs.close();
                checkStmt.close();
            }

            st.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            signUpError.setVisible(true);
            signUpError.setManaged(true);
            signUpError.setStyle("-fx-text-fill: red;");
            signUpError.setText("⚠ Database error!");
        }
    }

    private boolean validateUsername(String s){
        if (s == null || s.isEmpty()) {
            showError("❌ Username is required !");
            return false;
        }
        String regex = "^[a-zA-Z ]+$";
        if (!s.matches(regex)) {
            showError("❌ Username can only contain letters !");
            return false;
        }
        return true;
    }

    private boolean validateEmailAddress(String s){
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if(!matcher.matches()){
            showError("❌ Valid Email is required !");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{6,15}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        if (!matcher.matches()) {
            showError("❌ Password must have 6-15 chars, upper/lower & special");
            return false;
        }
        return true;
    }

    private boolean validateConfirmPassword(String password, String confirmPassword) {
        if (confirmPassword.isEmpty()) {
            showError("❌ Confirm password is required !");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showError("❌ Password do not match !");
            return false;
        }
        return true;
    }

    private boolean validatePhoneNo(String s) {
        String regex = "^(013|014|015|016|017|018|019)\\d{8}$";
        if ("".equals(s)) {
            showError("❌ Please enter a valid 11 digit phone number !");
            return false;
        }
        if(s.length()>11){
            showError("❌ Please enter a valid 11 digit phone number !");
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if (!matcher.matches()) {
            showError("❌ Phone must start with 013–019");
            return false;
        }
        return true;
    }

    private boolean validateComboBox(ComboBox c){
        if(c.getSelectionModel().isEmpty()){
            showError("❌ Please select an option from the dropbox !");
            return false;
        }
        return true;
    }

    private boolean validateEmailAddress2(String s){
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }

    private boolean validatePassword2(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{6,15}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    // ✅ Helper to show red error messages
    private void showError(String msg){
        signUpError.setVisible(true);
        signUpError.setManaged(true);
        signUpError.setStyle("-fx-text-fill: red;");
        signUpError.setText(msg);
    }
}
