package com.triadss.doctrack2.config.model;

/**
 * This class represents the USER model in FireStore.
 * It contains constants for the property names to avoid misspelling when interacting with FireStore.
 */
public class VitalSignsModel extends AuditModel {
    public static final String vitalsId = "vitalsId";
    public static final String patientId = "patientId";
    public static final String bloodPressure = "bloodPressure";
    public static final String temperature = "temperature";
    public static final String pulseRate = "pulseRate";
    public static final String oxygenLevel = "oxygenLevel";
    public static final String weight = "weight";
    public static final String height = "height";
    public static final String BMI = "BMI";
    public static final String createdAt = "createdAt";
}
