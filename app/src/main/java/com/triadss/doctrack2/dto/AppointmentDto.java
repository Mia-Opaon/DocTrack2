package com.triadss.doctrack2.dto;

import com.google.firebase.Timestamp;

public class AppointmentDto {
    public AppointmentDto() {
        // required for Firestore deserialization
    }

    public AppointmentDto(String patientId, String nameOfRequester, String purpose,
            Timestamp dateOfAppointment, String status) {
        this.patientId = patientId;
        this.nameOfRequester = nameOfRequester;
        this.purpose = purpose;
        this.dateOfAppointment = dateOfAppointment;
        this.status = status;
    }



    public String getNameOfRequester() {
        return nameOfRequester;
    }

    public void setNameOfRequester(String nameOfRequester) {
        this.nameOfRequester = nameOfRequester;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Timestamp getDateOfAppointment() {
        return dateOfAppointment;
    }

    public void setDateOfAppointment(Timestamp dateOfAppointment) {
        this.dateOfAppointment = dateOfAppointment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String userId) {
        this.patientId = userId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public String getPatientIdNumber() {
        return patientIdNumber;
    }

    public void setPatientIdNumber(String patientIdNumber) {
        this.patientIdNumber = patientIdNumber;
    }

    public String getHealthProfId() {
        return healthProfId;
    }

    public void setHealthProfId(String healthProfId) {
        this.healthProfId = healthProfId;
    }

    public Timestamp getPatientBirthday() {
        return patientBirthday;
    }

    public void setPatientBirthday(Timestamp patientBirthday) {
        this.patientBirthday = patientBirthday;
    }

    public int getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(int patientAge) {
        this.patientAge = patientAge;
    }

    private String uid;
    private String nameOfRequester;
    private String purpose;
    private Timestamp dateOfAppointment;
    private String status;
    private String patientId;
    private Timestamp createdAt;
    private String requester;
    private String documentId;
    private String patientIdNumber;
    private String healthProfId;
    public Timestamp patientBirthday;
    public int patientAge;
}
