package com.example.lifeline.model;

import com.google.firebase.auth.FirebaseUser;

public class User extends BaseModel {
    private String email;
    private boolean isEms;
    private String username;
    private String hospitalName = null;
    private String arduinoID = null;
    private String currentPatient;

    public User(){
        this.email = "";
        this.isEms = false;
        this.username = "";
        this.hospitalName = "";
        this.arduinoID = "";
        this.currentPatient = "";
    }

    public User(FirebaseUser fbUser, boolean isEms, String username, String hospitalNameOrArduinoId) {
        this.email = fbUser.getEmail();
        this.isEms = isEms;
        this.username = username;
        if(!isEms) {
            this.hospitalName = hospitalNameOrArduinoId;
        }
        else{
            this.arduinoID = hospitalNameOrArduinoId;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEms() {
        return isEms;
    }

    public void setEms(boolean ems) {
        isEms = ems;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getArduinoID() {
        return arduinoID;
    }

    public void setArduinoID(String arduinoID) {
        this.arduinoID = arduinoID;
    }

    public String getCurrentPatient() {
        return currentPatient;
    }

    public void setCurrentPatient(String currentPatient) {
        this.currentPatient = currentPatient;
    }
}
