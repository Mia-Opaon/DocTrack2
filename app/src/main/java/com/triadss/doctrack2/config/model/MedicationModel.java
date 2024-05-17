package com.triadss.doctrack2.config.model;

/**
 * This class represents the USER model in FireStore.
 * It contains constants for the property names to avoid misspelling when interacting with FireStore.
 */
public class MedicationModel extends AuditModel {
    public static final String mediId = "mediId";
    public static final String patientId = "patientId";
    public static final String medicine = "medicine";
    public static final String note = "note";
    public static final String status = "status";

    public static final String timestamp = "timestamp";
}
