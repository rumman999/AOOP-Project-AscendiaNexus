package com.example.aoop_project;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class UserData {

    private final StringProperty fullName;
    private final StringProperty email;
    private final StringProperty accountType;
    private final ObjectProperty<ImageView> profilePic;

    public UserData(String fullName, String email, String accountType, ImageView profilePic){
        this.fullName = new SimpleStringProperty(fullName);
        this.email = new SimpleStringProperty(email);
        this.accountType = new SimpleStringProperty(accountType);
        this.profilePic = new SimpleObjectProperty<>(profilePic);
    }

    public String getFullName(){ return fullName.get(); }
    public StringProperty fullNameProperty(){ return fullName; }

    public String getEmail(){ return email.get(); }
    public StringProperty emailProperty(){ return email; }

    public String getAccountType(){ return accountType.get(); }
    public StringProperty accountTypeProperty(){ return accountType; }

    public ImageView getProfilePic(){ return profilePic.get(); }
    public ObjectProperty<ImageView> profilePicProperty(){ return profilePic; }
}
