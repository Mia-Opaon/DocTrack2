 package com.triadss.doctrack2.config.model;

/**
 * This class represents the USER model in FireStore.
 * It contains constants for the property names to avoid misspelling when interacting with FireStore.
 */
public class UserModel extends AuditModel {

    public static final String fullName = "fullName";

    public static final String role = "role";

    public static final String email = "email";

    public static final String address = "address";

    public static final String phone = "phone";

    public static final String course = "course";

    public static final String idNumber = "idNumber";
    public static final String patientId = "patientId";
    public static final String dateOfBirth = "dateOfBirth";
    public static final String deviceId = "deviceId";
    public static final String year = "year";
    public static final String gender = "gender";
    public static final String status = "status";
}
