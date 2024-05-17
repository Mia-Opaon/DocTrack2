package com.triadss.doctrack2.config.model;

/**
 * This class represents the USER model in FireStore.
 * It contains constants for the property names to avoid misspelling when interacting with FireStore.
 */
public class MedicalHistoryModel extends AuditModel {
    public static final String medHistId = "medHistId";
    public static final String patientId = "patientId";
    public static final String pastIllness = "pastIllness";
    public static final String prevOperation = "prevOperation";
    public static final String obgyneHist = "obgyneHist";
    public static final String familyHist = "familyHist";
}
