package com.triadss.doctrack2.repoositories;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.triadss.doctrack2.config.constants.FireStoreCollection;
import com.triadss.doctrack2.config.constants.UserRoleConstants;
import com.triadss.doctrack2.config.model.UserModel;
import com.triadss.doctrack2.dto.HealthProfDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.Timestamp;

public class HealthProfRepository {
    private static final String TAG = "HealthProfRepository";
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference healProfCollection = firestore
            .collection(FireStoreCollection.USERS_TABLE);
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    public void addHealthProf(HealthProfDto healthProf, HealthProAddCallback callback) {
        String newEmail = healthProf.getEmail();
        String newPassword = healthProf.getPassword();

        FirebaseAuth newUserAuth = FirebaseAuth.getInstance();

        newUserAuth.createUserWithEmailAndPassword(newEmail, newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            healthProf.setHealthProfid(userId);

                            Map<String, Object> healthprofMap = new HashMap<>();
                            healthprofMap.put("idNumber", userId);
                            healthprofMap.put("fullName", healthProf.getFullName());
                            healthprofMap.put("position", healthProf.getPosition());

                            healthprofMap.put("userName", healthProf.getUserName());
                            healthprofMap.put("gender", healthProf.getGender());
                            healthprofMap.put("createdDate", Timestamp.now());
                            healthprofMap.put("email", newEmail);

                            healthprofMap.put("role", UserRoleConstants.HealthProf);

                            healProfCollection
                                    .document(userId)
                                    .set(healthprofMap)
                                    .addOnSuccessListener(documentReference -> {
                                        Log.d(TAG, "Health Professional added with ID: " + userId);
                                        callback.onSuccess(userId);

                                        // Sign out the new user from the separate FirebaseAuth instance
                                        newUserAuth.signOut();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error adding health professional to Firestore", e);
                                        callback.onFailure(e.getMessage());
                                    });
                        } else {
                            Log.e(TAG, "Failed to get current user after creating account");
                            callback.onFailure("Failed to get current user after creating account");
                        }
                    } else {
                        Log.e(TAG, "Error creating user in Firebase Authentication", task.getException());
                        callback.onFailure("Error creating user in Firebase Authentication: " + task.getException().getMessage());
                    }
                });
    }



    public void getHealthProfList(HealthProListCallback callback){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
            .whereEqualTo(UserModel.role, UserRoleConstants.HealthProf)
            .get()
            .addOnCompleteListener(task->{
                if (task.isSuccessful()){
                    List<HealthProfDto> healthProfListDto = new ArrayList<>();

                    for (QueryDocumentSnapshot document: task.getResult()){
                        String role = document.getString(UserModel.role);
                        if (UserRoleConstants.HealthProf.equals(role)) {
                            HealthProfDto patients = document.toObject(HealthProfDto.class);
                            patients.setHealthProfid(document.getId());
                            healthProfListDto.add(patients);
                        }
                    }
                    callback.onSuccess(healthProfListDto);
                }
                else {
                    callback.onFailure(task.getException().getMessage());
                }
            });
    }

    //Update HealthProfessional data
    public void updateHealthProfessional(HealthProfDto healthProfDto, HealthProUpdateCallback callback)
    {
        if(healthProfDto.getHealthProfid() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(healthProfDto.getHealthProfid())
                    .update("position", healthProfDto.getPosition())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Update Health Professional updated successfully");
                        callback.onSuccess(healthProfDto);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating Health Professional", e);
                        callback.onFailure(e.getMessage());
                    });
        } else {
            Log.e(TAG, "Health Prof id is null");
            callback.onFailure("Health Prof id is null");
        }
    }
    public void getHealthProfessional(String healthprofUid, HealthProGetCallback callback)
    {
        healProfCollection.document(healthprofUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        HealthProfDto healthprof = documentSnapshot.toObject(HealthProfDto.class);
                        healthprof.setHealthProfid(documentSnapshot.getId());
                        callback.onSuccess(healthprof);
                    } else {
                        callback.onFailure("Patient not found");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e.getMessage());
                });
    }

    public interface HealthProListCallback
    {
        public void onSuccess(List<HealthProfDto> dto);
        public void onFailure(String errorMessage);

    }
    public interface HealthProUpdateCallback
    {
        public void onSuccess(HealthProfDto dto);
        public void onFailure(String errorMessage);

    }
    public interface HealthProGetCallback
    {
        public void onSuccess(HealthProfDto dto);
        public void onFailure(String errorMessage);

    }
    public interface HealthProAddCallback
    {
        public void onSuccess(String healthProfId);
        public void onFailure(String errorMessage);
    }
}
