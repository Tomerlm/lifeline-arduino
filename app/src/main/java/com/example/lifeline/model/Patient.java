package com.example.lifeline.model;

public class Patient extends BaseModel {
    private String name;
    private int age;
    private String status;
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

    public Patient(String name, int age, String status,int bpm,  int oxygenPercentage){
        this.name = name;
        this.age = age;
        this.status = status;
        this.bpm = bpm;
        this.oxygenPercentage = oxygenPercentage;
    }



    public String getName() {
        return this.name;
    }

    public String getStatus() {
        return this.status;
    }

    public int getBpm() {
        return this.bpm;
    }

    public int getOxygenPercentage() {
        return this.oxygenPercentage;
    }
}
