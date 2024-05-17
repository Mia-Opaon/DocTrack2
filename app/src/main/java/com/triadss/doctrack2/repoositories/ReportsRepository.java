package com.triadss.doctrack2.repoositories;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.triadss.doctrack2.config.constants.FireStoreCollection;
import com.triadss.doctrack2.config.constants.ReportConstants;
import com.triadss.doctrack2.config.model.ReportModel;
import com.triadss.doctrack2.dto.AddPatientDto;
import com.triadss.doctrack2.dto.AppointmentDto;
import com.triadss.doctrack2.dto.DateTimeDto;
import com.triadss.doctrack2.dto.MedicationDto;
import com.triadss.doctrack2.dto.ReportDto;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class ReportsRepository {
    private final String TAG = "Reports Repository";
    FirebaseAuth mAuth;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference reportsCollection = firestore
            .collection(FireStoreCollection.REPORTS_TABLE);
    private final AppointmentRepository appointmentRepository = new AppointmentRepository();
    private final PatientRepository patientRepository = new PatientRepository();
    private final MedicationRepository medicationRepository = new MedicationRepository();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    public interface ReportCallback {
        void onReportAddedSuccessfully();
        void onReportFailed(String errorMessage);
    }

    // HEALTH PROF REPORTS
    public void addHealthProfAcceptedAppointmentReport(String createdBy, String appointmentUid, ReportCallback callback)
    {
        appointmentRepository.getAppointment(appointmentUid, new AppointmentRepository.AppointmentDataFetchCallback() {
            @Override
            public void onSuccess(AppointmentDto appointment) {
                addReport(createdBy,
                        ReportConstants.ACCEPTED_APPOINTMENT,
                           String.format("Accepted appointment of %s at %s",
                                appointment.getNameOfRequester(),
                                DateTimeDto.ToDateTimeDto(appointment.getDateOfAppointment()).ToString()),
                           callback);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    public void addHealthProfRejectedAppointmentReport(String createdBy, String appointmentUid, ReportCallback callback)
    {
        appointmentRepository.getAppointment(appointmentUid, new AppointmentRepository.AppointmentDataFetchCallback() {
            @Override
            public void onSuccess(AppointmentDto appointment) {
                addReport(createdBy,
                        ReportConstants.REJECTED_APPOINTMENT,
                        String.format("Rejected appointment of %s at %s",
                                appointment.getNameOfRequester(),
                                DateTimeDto.ToDateTimeDto(appointment.getDateOfAppointment()).ToString()),
                        callback);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    public void addHealthProfCancelledAppointmentReport(String createdBy, String appointmentUid, ReportCallback callback)
    {
        appointmentRepository.getAppointment(appointmentUid, new AppointmentRepository.AppointmentDataFetchCallback() {
            @Override
            public void onSuccess(AppointmentDto appointment) {
                addReport(createdBy,
                        ReportConstants.CANCELLED_APPOINTMENT,
                        String.format("Cancelled appointment of %s at %s",
                                appointment.getNameOfRequester(),
                                DateTimeDto.ToDateTimeDto(appointment.getDateOfAppointment()).ToString()),
                        callback);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    public void addHealthProfRescheduledAppointmentReport(String createdBy, String appointmentUid, DateTimeDto newDate, ReportCallback callback)
    {
        appointmentRepository.getAppointment(appointmentUid, new AppointmentRepository.AppointmentDataFetchCallback() {
            @Override
            public void onSuccess(AppointmentDto appointment) {
                addReport(createdBy,
                        ReportConstants.RESCHEDULED_APPOINTMENT,
                        String.format("Rescheduled appointment of %s at %s to %s",
                                appointment.getNameOfRequester(),
                                DateTimeDto.ToDateTimeDto(appointment.getDateOfAppointment()).ToString(),
                                newDate.ToString()),
                        callback);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    public void addHealthProfPatientInfoReport(String createdBy, AddPatientDto patient, ReportCallback callback)
    {
        addReport(createdBy,
                ReportConstants.ADDED_PATIENT,
                String.format("Added patient %s",
                        patient.getFullName()),
                callback);
    }

    public void addHealthProfPatientMedHistoryReport(String createdBy, String patientUid, String message, ReportCallback callback)
    {
        patientRepository.getPatient(patientUid, new PatientRepository.PatientFetchCallback() {
            @Override
            public void onSuccess(AddPatientDto patient) {
                addReport(createdBy,
                        ReportConstants.ADDED_PATIENT_MEDICAL_HISTORY,
                        String.format("Added patient medical history of %s",
                                patient.getFullName()),
                        callback);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    public void addHealthProfPatientVitalSignReport(String createdBy, String patientUid, ReportCallback callback)
    {
        patientRepository.getPatient(patientUid, new PatientRepository.PatientFetchCallback() {
            @Override
            public void onSuccess(AddPatientDto patient) {
                addReport(createdBy,
                        ReportConstants.ADDED_PATIENT_VITAL_SIGNS,
                        String.format("Added patient vital signs of %s",
                                patient.getFullName()),
                        callback);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    public void addHealthProfPatientAddMedicationReport(String createdBy, String patientUid, MedicationDto medication, ReportCallback callback)
    {

        patientRepository.getPatient(patientUid, new PatientRepository.PatientFetchCallback() {
            @Override
            public void onSuccess(AddPatientDto patient) {
                addReport(createdBy,
                        ReportConstants.ADDED_PATIENT_MEDICATION,
                        String.format("Added patient medication %s for %s",
                                medication.getMedicine(),
                                patient.getFullName()),
                        callback);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    public void addHealthProfPatientRemovedMedicationReport(String createdBy, String patientUid, String medicationUid, ReportCallback callback)
    {

        patientRepository.getPatient(patientUid, new PatientRepository.PatientFetchCallback() {
            @Override
            public void onSuccess(AddPatientDto patient) {
                medicationRepository.getMedication(medicationUid, new MedicationRepository.MedicationDataFetchCallback() {

                    @Override
                    public void onSuccess(MedicationDto medications) {
                        addReport(createdBy,
                                ReportConstants.REMOVED_PATIENT_MEDICATION,
                                String.format("Removed patient medication %s for %s",
                                        medications.getMedicine(),
                                        patient.getFullName()),
                                callback);
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });

            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    public void addHealthProfUpdatePatientInfoReport(String createdBy, AddPatientDto patient, ReportCallback callback)
    {
        addReport(createdBy,
                ReportConstants.UPDATED_PATIENT_INFO,
                String.format("Updated patient info of %s",
                        patient.getFullName()),
                callback);
    }

    public void addHealthProfUpdatePatientMedHistoryReport(String createdBy, String patientUid, String message, ReportCallback callback)
    {
        patientRepository.getPatient(patientUid, new PatientRepository.PatientFetchCallback() {
            @Override
            public void onSuccess(AddPatientDto patient) {
                addReport(createdBy,
                        ReportConstants.UPDATED_PATIENT_MEDICAL_HISTORY,
                        String.format("Updated patient medical history of %s",
                                patient.getFullName() + message),
                        callback);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    public void addHealthProfUpdatePatientVitalSignReport(String createdBy, String patientUid, ReportCallback callback)
    {
        patientRepository.getPatient(patientUid, new PatientRepository.PatientFetchCallback() {
            @Override
            public void onSuccess(AddPatientDto patient) {
                addReport(createdBy,
                        ReportConstants.UPDATED_PATIENT_VITAL_SIGNS,
                        String.format("Updated patient vital signs of %s",
                                patient.getFullName()),
                        callback);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    //PATIENT REPOSITORY
    public void addPatientRequestScheduleReport(AppointmentDto appointment, ReportCallback callback)
    {
        String userId = user.getUid();
        addReport(userId,
                ReportConstants.REQUESTED_APPOINTMENT,
                String.format("Requested Appointment for %s at %s",
                        appointment.getPurpose(),
                        DateTimeDto.ToDateTimeDto(appointment.getDateOfAppointment()).ToString()),
                callback);

    }

    public void addPatientAddedMedicationReport(MedicationDto medication, ReportCallback callback)
    {
        String userId = user.getUid();

        addReport(userId,
                ReportConstants.ADDED_MEDICATION,
                String.format("Added medication %s at %s",
                        medication.getMedicine(),
                        DateTimeDto.ToDateTimeDto(medication.getTimestamp()).ToString()),
                callback);
    }

    public void addPatientCompletedMedicationReport(MedicationDto medication, ReportCallback callback)
    {
        String userId = user.getUid();

        addReport(userId,
                ReportConstants.COMPLETED_MEDICATION,
                String.format("COMPLETED medication %s at %s",
                        medication.getMedicine(),
                        DateTimeDto.ToDateTimeDto(medication.getTimestamp()).ToString()),
                callback);
    }

    public void addPatientUpdatedMedicationReport(MedicationDto medication, ReportCallback callback)
    {
        String userId = user.getUid();

        addReport(userId,
                ReportConstants.UPDATED_MEDICATION,
                String.format("Updated medication %s at %s",
                        medication.getMedicine(),
                        DateTimeDto.ToDateTimeDto(medication.getTimestamp()).ToString()),
                callback);
    }

    public void addReport(String createdBy, String action, String message, ReportCallback callback)
    {
        LocalDateTime currentDate = LocalDateTime.now();
        Timestamp currentTimeStamp  = DateTimeDto.ToDateTimeDto(currentDate).ToTimestamp();

        Map<String, Object> recordData = new HashMap<>();
        recordData.put(ReportModel.action, action);
        recordData.put(ReportModel.createdBy, createdBy);
        recordData.put(ReportModel.message, message);
        recordData.put(ReportModel.createdDate, currentTimeStamp);
        recordData.put(ReportModel.updatedDate, currentTimeStamp);

        // Check if mAuth and mContext are not null
        if (callback != null) {
            reportsCollection.add(recordData)
                    .addOnSuccessListener(aVoid -> {
                        callback.onReportAddedSuccessfully();
                    })
                    .addOnFailureListener(e -> {
                        callback.onReportFailed("Failed to add report: " + e.getMessage());
                    });
        } else {
            callback.onReportFailed("Failed to add report");
        }
    }

    public void getReportsFromUser(String uid, ReportsFetchCallback callback) {
        if (user != null) {
            reportsCollection
                    .whereEqualTo(ReportModel.createdBy, uid)
                    .orderBy(ReportModel.createdDate, Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<ReportDto> reports = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            ReportDto report = document.toObject(ReportDto.class);
                            reports.add(report);
                        }
                        callback.onSuccess(reports);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching reports", e);
                        callback.onError(e.getMessage());
                    });
        } else {
            Log.e(TAG, "User is null");
            callback.onError("User is null");
        }
    }

    public void getReportsFromUserFilter(String uid, String find, String[] actions, ReportsFilterCallback callback) {
        reportsCollection
                .whereEqualTo(ReportModel.createdBy, uid)
                 .orderBy(ReportModel.createdDate, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ReportDto> reports = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        if((find.toLowerCase().equals("") ||
                            Objects.requireNonNull(document.get("message")).toString().toLowerCase().contains(find.toLowerCase()) ||
                            Objects.requireNonNull(document.get("action")).toString().toLowerCase().contains(find.toLowerCase())) &&
                            (actions.length == 0 ||
                            Arrays.stream(actions).anyMatch(x -> Objects.requireNonNull(document.get("action")).toString().equals(x)))
                        ){
                            ReportDto report = document.toObject(ReportDto.class);
                            reports.add(report);
                        }
                    }
                    callback.onSuccess(reports);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching medicines", e);
                    callback.onError(e.getMessage());
                });
    }

    public void getReportsFromDateRange(Timestamp before, Timestamp after, ReportsFetchCallback callback) {
        if (user != null) {
            CollectionReference usersCollection = firestore
                .collection(FireStoreCollection.USERS_TABLE);

            usersCollection
                .get()
                .addOnSuccessListener(userQueryDocumentSnapshots -> {
                        // To get user details
                        List<AddPatientDto> users = new ArrayList<>();
                        for (QueryDocumentSnapshot document : userQueryDocumentSnapshots) {
                            AddPatientDto user = document.toObject(AddPatientDto.class);
                            user.setUid(document.getId());
                            users.add(user);
                        }

                        reportsCollection
                            .whereGreaterThanOrEqualTo(ReportModel.createdDate, before)
                            .whereLessThanOrEqualTo(ReportModel.createdDate, after)
                            .orderBy(ReportModel.createdDate, Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                List<ReportDto> reports = new ArrayList<>();
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    ReportDto report = document.toObject(ReportDto.class);
                                    String fullName = users.stream()
                                            .filter(user -> user.getUid().equals(report.getCreatedBy()))
                                            .findFirst().get().getFullName();
                                    report.setCreatedByName(fullName);

                                    reports.add(report);
                                }

                                callback.onSuccess(reports);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error fetching medicines", e);
                                callback.onError(e.getMessage());
                            });

                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching medicines", e);
                        callback.onError(e.getMessage());
                    });


        } else {
            Log.e(TAG, "User is null");
            callback.onError("User is null");
        }
    }

    public void getReportsFromUserForPdf(String uid, String find, String[] actions, ReportsForPdfFetchCallback callback) {
        if (user != null) {
            reportsCollection
                    .whereEqualTo(ReportModel.createdBy, uid)
                    .orderBy(ReportModel.createdDate, Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<String> action = new ArrayList<>();
                        List<String> message = new ArrayList<>();
                        List<String> date = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            long tempDate = document.getTimestamp("createdDate").getSeconds();

                            if((find.toLowerCase().equals("") ||
                                    Objects.requireNonNull(document.get("message")).toString().toLowerCase().contains(find.toLowerCase()) ||
                                    Objects.requireNonNull(document.get("action")).toString().toLowerCase().contains(find.toLowerCase())) &&
                                    (actions.length == 0 ||
                                            Arrays.stream(actions).anyMatch(x -> Objects.requireNonNull(document.get("action")).toString().equals(x)))
                            ) {
                                DateTimeDto dateTime = DateTimeDto.ToDateTimeDto(document.getTimestamp("createdDate"));
                                action.add(String.valueOf(document.get("action")));
                                message.add(String.valueOf(document.get("message")));
                                date.add(dateTime.formatDateTime().toString());
                            }
                        }
                        callback.onSuccess(action,message,date);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching reports", e);
                        callback.onError(e.getMessage());
                    });
        } else {
            Log.e(TAG, "User is null");
            callback.onError("User is null");
        }
    }

    public interface ReportsFetchCallback {
        void onSuccess(List<ReportDto> reports);

        void onError(String errorMessage);
    }
    public interface ReportsFilterCallback {
        void onSuccess(List<ReportDto> reports);

        void onError(String errorMessage);
    }

    public interface ReportsForPdfFetchCallback {
        void onSuccess(List<String> action,List<String> message,List<String> date);

        void onError(String errorMessage);
    }
}