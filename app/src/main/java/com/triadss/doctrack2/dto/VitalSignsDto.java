package com.triadss.doctrack2.dto;

import com.google.firebase.Timestamp;
import com.google.gson.Gson;
import com.google.type.DateTime;

public class VitalSignsDto {
    public String toJsonData() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public int getVitalsId() {
        return vitalsId;
    }

    public void setVitalsId(int vitalsId) {
        this.vitalsId = vitalsId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getPulseRate() {
        return pulseRate;
    }

    public void setPulseRate(int pulseRate) {
        this.pulseRate = pulseRate;
    }

    public int getOxygenLevel() {
        return oxygenLevel;
    }

    public void setOxygenLevel(int oxygenLevel) {
        this.oxygenLevel = oxygenLevel;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getBMI() {
        return BMI;
    }

    public void setBMI(double BMI) {
        this.BMI = BMI;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public Timestamp getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt){
        this.createdAt = createdAt;
    }

    private int vitalsId;
    private String patientId;
    private String bloodPressure;
    private double temperature;
    private int pulseRate;
    private int oxygenLevel;
    private double weight;
    private double height;
    private double BMI;
    private Timestamp createdAt;

    private String uid;

    public VitalSignsDto(){
    }
}
