package com.example.lifeline.model;

public class Patient extends BaseModel {
    private String name;
    private int age;
    private Status status;
    private int oxygenPercentage;
    private int bpm;
    //private List<Vitals> vitals;
    private String destHospital;

    public TreatmentStatus getTreatmentStatus() {
        return treatmentStatus;
    }

    public void setTreatmentStatus(TreatmentStatus treatmentStatus) {
        this.treatmentStatus = treatmentStatus;
    }

    private TreatmentStatus treatmentStatus;

    public static Status getStatusFromText(String status) {
        if(status == null) return Patient.Status.UNDETERMINED;
        switch (status){
            case "Good":
                return Status.GOOD;
            case "Mild":
                return Status.MILD;
            case "Critical":
                return Status.CRITICAL;
            case "Respirated":
                return Status.RESPIRATED;
            default:
                return Status.UNDETERMINED;
        }

    }

    public String getArduinoID() {
        return arduinoID;
    }

    public void setArduinoID(String arduinoID) {
        this.arduinoID = arduinoID;
    }

    private String arduinoID;


    public enum Status{
        GOOD,
        MILD,
        CRITICAL,
        RESPIRATED,
        UNDETERMINED,
        DEAD
    }
    public enum TreatmentStatus{
        ONGOING,
        COMPLETED
    }

    public Patient(){
        this.name = "name";
        this.age = 0;
        this.status = Status.GOOD;
        this.bpm = 0;
        this.oxygenPercentage = 0;
        this.destHospital = "";
        this.arduinoID = "";
        this.treatmentStatus = TreatmentStatus.COMPLETED;
    }

    public Patient(String name, int age, Status status, int bpm, int oxygenPercentage, String destHospital, String arduinoID, TreatmentStatus treatmentStatus){
        this.name = name;
        this.age = age;
        this.status = status;
        this.bpm = bpm;
        this.oxygenPercentage = oxygenPercentage;
        this.destHospital = destHospital;
        this.arduinoID = arduinoID;
        this.treatmentStatus = treatmentStatus;
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

    public static String getStatusText(Status status){
        switch(status){
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
    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setOxygenPercentage(int oxygenPercentage) {
        this.oxygenPercentage = oxygenPercentage;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }


    public String getDestHospital() {
        return destHospital;
    }

    public void setDestHospital(String destHospital) {
        this.destHospital = destHospital;
    }

    public static String getTreatmentStatusText(TreatmentStatus status) {
        switch (status){
            case ONGOING:
                return "Ongoing";
            case COMPLETED:
                return "Completed";
        }
        return "Completed";
    }
}
