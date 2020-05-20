package com.example.lifeline.model;

public class Connection extends BaseModel {
    private String arduinoID;
    private int rpm;
    private String patientID;
    private String emsID;
    private boolean isManual;

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getEmsID() {
        return emsID;
    }

    public void setEmsID(String emsID) {
        this.emsID = emsID;
    }

    public Connection() {
        this.arduinoID = "";
        this.rpm = 12;
        this.isManual = false;
        this.emsID = "";
        this.patientID = "";
    }

    public Connection(String arduinoID, int rpm, boolean isManual, String patientID, String emsID) {
        this.arduinoID = arduinoID;
        this.rpm = rpm;
        this.isManual = isManual;
        this.emsID = emsID;
        this.patientID = patientID;
    }

    public String getArduinoID() {
        return arduinoID;
    }

    public void setArduinoID(String arduinoID) {
        this.arduinoID = arduinoID;
    }

    public int getRpm() {
        return rpm;
    }

    public void setRpm(int rpm) {
        this.rpm = rpm;
    }

    public boolean isManual() {
        return isManual;
    }

    public void setManual(boolean manual) {
        isManual = manual;
    }

}
