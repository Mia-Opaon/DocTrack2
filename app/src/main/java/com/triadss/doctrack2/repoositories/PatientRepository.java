package com.triadss.doctrack2.repoositories;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.triadss.doctrack2.config.constants.DocTrackConstant;
import com.triadss.doctrack2.config.constants.FireStoreCollection;
import com.triadss.doctrack2.config.enums.UserRole;
import com.triadss.doctrack2.config.model.UserModel;
import com.triadss.doctrack2.dto.AddPatientDto;
import com.triadss.doctrack2.config.constants.UserRoleConstants;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientRepository {
    private final String TAG = "PatientRepository";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference usersCollection = firestore
            .collection(FireStoreCollection.USERS_TABLE);
    /**
     * Saves user information to Firestore.
     *
     * @param userId     The ID of the user.
     * @param patientDto The DTO (Data Transfer Object) containing patient information.
     */
    public boolean AddPatient(String userId, AddPatientDto patientDto)
    {
        try
        {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection(FireStoreCollection.USERS_TABLE).document(userId);
            LocalDateTime currentDate = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DocTrackConstant.AUDIT_DATE_FORMAT);
            String dateNow = currentDate.format(formatter);

            Map<String, Object> userData = new HashMap<>();
            userData.put(UserModel.role, UserRole.PATIENT);
            userData.put(UserModel.email, patientDto.getEmail());
            userData.put(UserModel.fullName, patientDto.getFullName());
            userData.put(UserModel.address, patientDto.getAddress());
            userData.put(UserModel.phone, patientDto.getPhone());
            userData.put(UserModel.course, patientDto.getCourse());
            userData.put(UserModel.idNumber, patientDto.getIdNumber());
            userData.put(UserModel.createdBy, dateNow);
            userData.put(UserModel.updatedDate, dateNow);
            userRef.set(userData, SetOptions.merge());
        } catch(Exception ex)
        {
            return false;
        }

        return true;
    }

    public void addPatientCallback(AddPatientDto patient, PatientAddUpdateCallback callback) {
        FirebaseAuth user = FirebaseAuth.getInstance();
        if (user != null) {
            String userId = user.getUid();
            Map<String, Object> patientMap = new HashMap<>();
            patientMap.put(UserModel.idNumber, patient.getIdNumber());
            patientMap.put(UserModel.email, patient.getEmail());
            patientMap.put(UserModel.fullName, patient.getFullName());
            patientMap.put(UserModel.gender, patient.getGender());
            patientMap.put(UserModel.address, patient.getAddress());
            patientMap.put(UserModel.dateOfBirth, patient.getDateOfBirth());
            patientMap.put(UserModel.status, patient.getStatus());
            patientMap.put(UserModel.phone, patient.getPhone());
            patientMap.put(UserModel.course, patient.getCourse());
            patientMap.put(UserModel.year, patient.getYear());
            patientMap.put(UserModel.role, UserRoleConstants.Patient);

            usersCollection
                    .document(userId)
                    .set(patientMap)
                    .addOnSuccessListener(documentReference -> {
                        callback.onSuccess(userId);
                    })
                    .addOnFailureListener(e -> {
                        callback.onError(e.getMessage());
                    });
        } else {
            callback.onError("User is null");
        }

    }

    public void getPatientList(PatientListCallback callback){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("role", "PATIENT")
                .get()
                .addOnCompleteListener(task->{
                    if (task.isSuccessful()){
                        List<AddPatientDto> addPatientDtoList = new ArrayList<>();

                        for (QueryDocumentSnapshot document: task.getResult()){
                            String role = document.getString("role");
                            if ("PATIENT".equals(role)) {
                                AddPatientDto patients = document.toObject(AddPatientDto.class);
                                patients.setUid(document.getId().toString());
                                addPatientDtoList.add(patients);
                            }
                        }
                        callback.onSuccess(addPatientDtoList);
                    }
                    else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void updatePatient(AddPatientDto patient, PatientAddUpdateCallback callback) {
        DocumentReference patientRef = usersCollection.document(patient.getUid());

        patientRef
                .update(
                        UserModel.address, patient.getAddress(),
                        UserModel.status, patient.getStatus(),
                        UserModel.phone, patient.getPhone(),
                        UserModel.course, patient.getCourse(),
                        UserModel.year, patient.getYear())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Patient Info updated successfully");
                    callback.onSuccess(patient.getUid());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating Patient Info", e);
                    callback.onError(e.getMessage());
                });
    }

    public void getPatient(String patientUid, PatientFetchCallback callback)
    {
        usersCollection.document(patientUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        AddPatientDto patient = documentSnapshot.toObject(AddPatientDto.class);
                        callback.onSuccess(patient);
                    } else {
                        callback.onError("Patient not found");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError(e.getMessage());
                });
    }

    public void searchPatientsByName(String nameQuery, PatientSearchCallback callback) {
        // Retrieve the list of patients from the data source
        getPatientList(new PatientListCallback() {
            @Override
            public void onSuccess(List<AddPatientDto> patients) {
                // Filter patients whose name contains the query string
                List<AddPatientDto> searchResult = new ArrayList<>();
                for (AddPatientDto patient : patients) {
                    if (patient.getFullName().toLowerCase().contains(nameQuery.toLowerCase())) {
                        searchResult.add(patient);
                    }
                }
                // Invoke the callback with the search result
                callback.onSuccess(searchResult);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle failure to retrieve patient list
                callback.onError(errorMessage);
            }
        });
    }
    // Callback interface for patient search
    public interface PatientSearchCallback {
        void onSuccess(List<AddPatientDto> patients);

        void onError(String errorMessage);
    }


    public interface PatientAddUpdateCallback {
        void onSuccess(String patientId);
        void onError(String errorMessage);
    }

    public interface PatientFetchCallback {
        void onSuccess(AddPatientDto patient);
        void onError(String errorMessage);
    }

    public interface PatientListCallback {
        void onSuccess(List<AddPatientDto> patients);
        void onFailure(String errorMessage);
    }
}
