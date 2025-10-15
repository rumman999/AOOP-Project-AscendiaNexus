package com.example.aoop_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class loginController  {

    @FXML private VBox loginForm;   // Matches fx:id="loginForm"
    @FXML private VBox signupForm;  // Matches fx:id="signupForm"
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
    private void showSignupForm() {
        // Hide the login form
        loginForm.setVisible(false);
        loginForm.setManaged(false);

        // Show the signup form
        signupForm.setVisible(true);
        signupForm.setManaged(true);

        // Apply the login form style to the left column (SkillPlusPlus pane)
        VBox skillPlusPlusVBox = (VBox) ((GridPane) loginForm.getParent()).getChildren().get(0); // Access the left column
        skillPlusPlusVBox.setStyle("-fx-border-color: blue; -fx-border-radius: 10; -fx-background-color: #B7E3F6; -fx-background-radius: 10;");
    }



    @FXML
    private void showLoginForm() {
        // Hide the signup form
        signupForm.setVisible(false);
        signupForm.setManaged(false);

        // Show the login form
        loginForm.setVisible(true);
        loginForm.setManaged(true);

        // Revert the style to the original if needed, you can specify different styles here if desired
        VBox skillPlusPlusVBox = (VBox) ((GridPane) loginForm.getParent()).getChildren().get(0); // Access the left column
        skillPlusPlusVBox.setStyle("-fx-padding: 20;");
    }


    @FXML
    private void handleLogin(ActionEvent event)  {
        String email = loginEmailUI.getText().trim();
        String password = loginPasswordUI.getText().trim();

        // Basic input validations first
        if(email.isEmpty()) {
            LoginErrorUI.setText("❌ Email is required!");
            return;
        }
        if(!validateEmailAddress2(email)) {
            LoginErrorUI.setText("❌ Invalid Email address!");
            return;
        }
        if(password.isEmpty()) {
            LoginErrorUI.setText("❌ Password is required!");
            return;
        }
        if(!validatePassword2(password)) {
            LoginErrorUI.setText("❌ Password must have 6-15 chars, upper/lower & special.");
            return;
        }

        String passDB = "";
        String SUrl = "jdbc:mysql://localhost:4306/java_user_database"; // fix capitalization
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
                // Check password
                if(password.equals(passDB)) {
                    int id = rs.getInt("id");
                    LoginErrorUI.setText(""); // Clear error
                    Session.setLoggedInUserId(id);
                    Session.setLoggedInUserEmail(email);// store email globally
                    Session.setLoggedInUserName(rs.getString("full_name"));
                    Session.setLoggedInUserType(rs.getString("account_type"));
                    Session.setLoggedInUserPassword(passDB);
                    // Use your existing scene launcher
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

                // 🔎 Check for duplicates
                String checkQuery = "SELECT * FROM user WHERE full_name=? OR email=? OR phone_number=?";
                PreparedStatement checkStmt = con.prepareStatement(checkQuery);
                checkStmt.setString(1, full_name);
                checkStmt.setString(2, email_address);
                checkStmt.setString(3, phone_number);

                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    // ✅ No duplicates → insert
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
                    signUpError.setText("✅ User created successfully!");

                    // clear fields
                    fnameUI.clear();
                    emailUI.clear();
                    phoneNoUI.clear();
                    passwordUI.clear();
                    signUpConfirmPasswordFieldUI.clear();
                    accountTypeUI.getSelectionModel().clearSelection();

                    insertStmt.close();
                } else {
                    // ❌ Duplicate found
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
            signUpError.setText("⚠ Database error!");
        }
    }

    private boolean validateUsername(String s){
        if (s == null || s.isEmpty()) {
            signUpError.setText("❌ Username is required !");
            return false;
        }

        // Regex to allow only letters (both uppercase and lowercase) and spaces
        String regex = "^[a-zA-Z ]+$";
        if (!s.matches(regex)) {
            signUpError.setText("❌ Username can only contain letters !");
            return false;
        }

        return true;
    }

    private boolean validateEmailAddress(String s){
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if(!matcher.matches()){
            signUpError.setText("❌ Valid Email is required !");
            return false;
        }
        else return true;
    }
    private boolean validatePassword(String password) {
        // Regex pattern to match the password requirements
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{6,15}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        if (!matcher.matches()) {
            signUpError.setText("❌ Password must have 6-15 chars, upper/lower & special");
            return false;
        } else {
            return true;
        }
    }
    private boolean validateConfirmPassword(String password, String confirmPassword) {
        if (confirmPassword.isEmpty()) {
            signUpError.setText("❌ Confirm password is required !");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            signUpError.setText("❌ Password do not match !");
            return false;
        }
        return true;  // ✅ no extra validation here
    }
    private boolean validatePhoneNo(String s) {
        // Regular expression that ensures the number starts with one of the specified prefixes (013, 014, etc.)
        String regex = "^(013|014|015|016|017|018|019)\\d{8}$";  // Ensures valid prefixes and 7 more digits

        // Check if the phone number field is empty
        if ("".equals(s)) {
            signUpError.setText("❌ Please enter a valid 11 digit phone number !");
            return false;
        }

        if(s.length()>11){
            signUpError.setText("❌ Please enter a valid 11 digit phone number !");
        }

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);

        // If the number doesn't match the regex pattern
        if (!matcher.matches()) {
            signUpError.setText("❌ Phone must start with 013–019");
            return false;
        }


        // If the number is valid, return true
        return true;
    }
    private boolean validateComboBox(ComboBox c){
        if(c.getSelectionModel().isEmpty()){
            signUpError.setText("❌ Please select an option from the dropbox !");
            return false;
        }
        else return true;
    }
    private boolean validateEmailAddress2(String s){
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if(!matcher.matches()){
            return false;
        }
        else return true;
    }
    private boolean validatePassword2(String password) {
        // Regex pattern to match the password requirements
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{6,15}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        if (!matcher.matches()) {
            return false;
        } else {
            return true;
        }
    }
}

