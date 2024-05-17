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
import com.triadss.doctrack2.config.model.MedicalHistoryModel;
import com.triadss.doctrack2.dto.AddPatientDto;
import com.triadss.doctrack2.dto.MedicalHistoryDto;
import com.triadss.doctrack2.dto.WearableDeviceDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicalHistoryRepository {
    private final String TAG = "Medical History Repository";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference medHistoryCollection = firestore
            .collection(FireStoreCollection.MEDICALHISTORY_TABLE);

    /**
     * Saves user information to Firestore.
     *
     * @param patientId     The ID of the user.
     * @param medicalHistoryDto The DTO (Data Transfer Object) containing patient information.
     */
    public boolean AddMedicalHistory(String patientId, MedicalHistoryDto medicalHistoryDto, AddUpdateCallback callback)
    {
        try
        {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // TODO: EDIT THIS
            DocumentReference userRef = db.collection(FireStoreCollection.USERS_TABLE).document(patientId);
            LocalDateTime currentDate = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DocTrackConstant.AUDIT_DATE_FORMAT);
            String dateNow = currentDate.format(formatter);

            Map<String, Object> medicalHistories = new HashMap<>();
            medicalHistories.put(MedicalHistoryModel.medHistId, 0);
            medicalHistories.put(MedicalHistoryModel.patientId, patientId);
            medicalHistories.put(MedicalHistoryModel.pastIllness, medicalHistoryDto.getPastIllness());
            medicalHistories.put(MedicalHistoryModel.prevOperation, medicalHistoryDto.getPrevOperation());
            medicalHistories.put(MedicalHistoryModel.obgyneHist, medicalHistoryDto.getObgyneHist());
            medicalHistories.put(MedicalHistoryModel.familyHist, medicalHistoryDto.getFamilyHist());
            // TODO: EDIT THIS
            medHistoryCollection
                    .add(medicalHistories)
                    .addOnSuccessListener(documentReference -> {
                        callback.onSuccess(documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        callback.onError(e.getMessage());
                    });
        } catch(Exception ex)
        {
            callback.onError(ex.getMessage());
            return false;
        }

        return true;
    }

    public void createDefaultMedicalHistoryForPatient(String patientId, AddUpdateCallback callback) {
        MedicalHistoryDto medicalHistoryDto = new MedicalHistoryDto();
        medicalHistoryDto.setPastIllness("");
        medicalHistoryDto.setPrevOperation("");
        medicalHistoryDto.setFamilyHist("");
        medicalHistoryDto.setObgyneHist("Para:|Menopause:|Gravida:|Menarche:|PAP Smear:|Abortion:|LMP:");
        AddMedicalHistory(patientId, medicalHistoryDto, callback);
    }

    public void getMedicalHistoryOfPatient(String patientUid, FetchCallback callback) {
        medHistoryCollection.whereEqualTo(MedicalHistoryModel.patientId, patientUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        MedicalHistoryDto medicalHistory = null;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            medicalHistory = document.toObject(MedicalHistoryDto.class);
                            medicalHistory.setUid(document.getId().toString());
                        }
                        callback.onSuccess(medicalHistory);
                    } else {
                        Log.e(TAG, "Error getting medicalHistory", task.getException());
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

     public void updateMedicalHistory(MedicalHistoryDto medicalHistoryDto, AddUpdateCallback callback) {
        if (user != null) {
            medHistoryCollection
                    .document(medicalHistoryDto.getUid())
                    .update(MedicalHistoryModel.pastIllness, medicalHistoryDto.getPastIllness(),
                            MedicalHistoryModel.prevOperation, medicalHistoryDto.getPrevOperation(),
                            MedicalHistoryModel.obgyneHist, medicalHistoryDto.getObgyneHist(),
                            MedicalHistoryModel.familyHist, medicalHistoryDto.getFamilyHist())
                    .addOnSuccessListener(documentReference -> {
                        callback.onSuccess(medicalHistoryDto.getUid());
                    })
                    .addOnFailureListener(e -> {
                        callback.onError(e.getMessage());
                    });
        } else {
            callback.onError("User is null");
        }
    }

    public void getMedicalHistoryIdOfUser(String userUid, StringFetchCallback callback) {
        if (user != null) {
            medHistoryCollection
                    .whereEqualTo(MedicalHistoryModel.patientId, userUid)
                    .get()
                    .addOnCompleteListener(task->{
                        if (task.isSuccessful()){
                            MedicalHistoryDto medHistory = null;

                            for (QueryDocumentSnapshot document: task.getResult()){
                                medHistory = document.toObject(MedicalHistoryDto.class);
                                medHistory.setUid(document.getId().toString());
                                break;
                            }
                            callback.onSuccess(medHistory.getUid());
                        }
                        else {
                            callback.onError(task.getException().getMessage());
                        }
                    })
                    .addOnFailureListener(e -> {
                        callback.onError(e.getMessage());
                    });
        } else {
            callback.onError("User is null");
        }
    }

    public interface AddUpdateCallback {
        void onSuccess(String medHistoryUid);
        void onError(String errorMessage);
    }

    public interface StringFetchCallback {
        void onSuccess(String stringValue);
        void onError(String errorMessage);
    }

    public interface FetchCallback {
        void onSuccess(MedicalHistoryDto medHistory);
        void onError(String errorMessage);
    }
}
