package com.example.lifeline.model;

public class Patient extends BaseModel {
    private String name;
    private int age;
    private Status status;
    private int oxygenPercentage;
    private int bpm;

    public enum Status{
        GOOD,
        MILD,
        CRITICAL,
        RESPIRATED,
        DEAD
    }
    public enum TransitStatus{
        IN_TRANSIT,
        DELIVERED
    }

    public Patient(String name, int age, Status status,int bpm,  int oxygenPercentage){
        this.name = name;
        this.age = age;
        this.status = status;
        this.bpm = bpm;
        this.oxygenPercentage = oxygenPercentage;
    }



    public String getName() {
        return this.name;
    }

    public Status getStatus() {
        return this.status;
    }

    public int getBpm() {
        return this.bpm;
    }

    public int getOxygenPercentage() {
        return this.oxygenPercentage;
    }

    public String getStatusText(){
        switch(this.status){
            case GOOD:
                return "Good";
            case MILD:
                return "Mild";
            case CRITICAL:
                return "Critical";
            case RESPIRATED:
                return "Respirated";
            default:
                return "Undetermined";
        }
    }
}
