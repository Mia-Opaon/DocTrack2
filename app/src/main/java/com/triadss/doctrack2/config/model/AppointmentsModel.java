package com.triadss.doctrack2.config.model;

/**
 * This class represents the USER model in FireStore.
 * It contains constants for the property names to avoid misspelling when interacting with FireStore.
 */
public class AppointmentsModel extends AuditModel {
    public static final String appointmentId = "appointmentId";
    public static final String healthProfId = "healthProfId";
    public static final String patientId = "patientId";
    public static final String nameOfRequester = "nameOfRequester";
    public static final String purpose = "purpose";
    public static final String dateOfAppointment = "dateOfAppointment";
    public static final String timeOfAppointment = "timeOfAppointment";
    public static final String status = "status";
    public static final String createdAt = "createdAt";

}
