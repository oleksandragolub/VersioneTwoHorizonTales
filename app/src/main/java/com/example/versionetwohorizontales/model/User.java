package com.example.versionetwohorizontales.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class User implements Parcelable {
    private String userId;
    private String name;
    private String dob;
    private String gender;
    private String email;
    private String authMethod;
    private String userType;
    private Boolean emailVerificato;
    private String profileImage;

    // Costruttore vuoto richiesto da Firebase
    public User() {}

    // Costruttori vari per diverse situazioni
    public User(String name, String email, String userId) {
        this.name = name;
        this.email = email;
        this.userId = userId;
    }

    public User(String userId, String name, String email, String dob, String gender, Boolean emailVerificato, String authMethod, String userType, String profileImage) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
        this.emailVerificato = emailVerificato;
        this.authMethod = authMethod;
        this.userType = userType;
        this.profileImage = profileImage;
    }

    // Getters e Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getEmailVerificato() {
        return emailVerificato;
    }

    public void setEmailVerificato(Boolean emailVerificato) {
        this.emailVerificato = emailVerificato;
    }

    public String getAuthMethod() {
        return authMethod;
    }

    public void setAuthMethod(String authMethod) {
        this.authMethod = authMethod;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", dob='" + dob + '\'' +
                ", gender='" + gender + '\'' +
                ", authMethod='" + authMethod + '\'' +
                ", userType='" + userType + '\'' +
                ", emailVerificato=" + emailVerificato +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }

    // Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.dob);
        dest.writeString(this.gender);
        dest.writeValue(this.emailVerificato);
        dest.writeString(this.authMethod);
        dest.writeString(this.userType);
        dest.writeString(this.profileImage);
    }

    protected User(Parcel in) {
        this.userId = in.readString();
        this.name = in.readString();
        this.email = in.readString();
        this.dob = in.readString();
        this.gender = in.readString();
        this.emailVerificato = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.authMethod = in.readString();
        this.userType = in.readString();
        this.profileImage = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
