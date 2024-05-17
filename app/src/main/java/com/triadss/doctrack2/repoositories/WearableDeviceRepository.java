package com.triadss.doctrack2.repoositories;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.triadss.doctrack2.config.constants.FireStoreCollection;
import com.triadss.doctrack2.dto.WearableDeviceDto;

import java.util.List;

public class WearableDeviceRepository {
    private static final String TAG = "WearableDeviceRepository";
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference wearablesCollection = firestore.collection(FireStoreCollection.WEARABLEDEVICES_TABLE);
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    public void addWearableDevice(WearableDeviceDto wearableDeviceDto, WearableAddCallback callback)
    {
        if(user == null) return;

        wearablesCollection
                .add(wearableDeviceDto)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Wearable device added with ID" + documentReference.getId());
                    callback.onSuccess(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding Wearable Device", e);
                    callback.onError(e.getMessage());
                });
    }

    public void getWearableDevice(String deviceId, String userId, GetWearableDeviceCallback callback) {
        if(user == null) return;

        Query query = wearablesCollection
                .whereEqualTo("deviceId", deviceId)
                .whereEqualTo("ownerId", userId);

        query
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Assuming there's only one device for a unique combination of deviceId and ownerId
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        WearableDeviceDto wearableDevice = documentSnapshot.toObject(WearableDeviceDto.class);
                        callback.onSuccess(wearableDevice);
                    } else {
                        callback.onError("Wearable Device not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting Wearable Device", e);
                    callback.onError(e.getMessage());
                });
    }

    public void getWearableDevices(GetWearableDevicesCallback callback) {
        if(user == null) return;

        wearablesCollection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // TODO
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting Wearable Devices", e);
                    callback.onError(e.getMessage());
                });
    }

    public void setWearableDevice(String deviceId, WearableDeviceDto updatedDevice, WearableUpdateCallback callback) {
        if(user == null) return;

        DocumentReference deviceRef = wearablesCollection.document(deviceId);
        deviceRef
                .set(updatedDevice, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Wearable device updated successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating Wearable Device", e);
                    callback.onError(e.getMessage());
                });
    }

    public void updateWearableDevice(String deviceId, String ownerId, WearableDeviceDto updatedDevice, WearableUpdateCallback callback) {
        if (user == null) return;

        Query query = wearablesCollection.whereEqualTo("deviceId", deviceId)
                .whereEqualTo("ownerId", ownerId);

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DocumentReference deviceRef = wearablesCollection.document(documentSnapshot.getId());
                            deviceRef
                                    .set(updatedDevice, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Wearable device fields updated successfully");
                                        callback.onSuccess();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error updating Wearable Device fields", e);
                                        callback.onError(e.getMessage());
                                    });
                        }
                    } else {
                        Log.e(TAG, "No matching document found for deviceId: " + deviceId + " and ownerId: " + ownerId);
                        callback.onError("No matching document found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error searching for Wearable Device", e);
                    callback.onError(e.getMessage());
                });
    }



    public void deleteWearableDevice(String deviceId, WearableDeleteCallback callback) {
        if(user == null) return;

        DocumentReference deviceRef = wearablesCollection.document(deviceId);
        deviceRef
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Wearable device deleted successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting Wearable Device", e);
                    callback.onError(e.getMessage());
                });
    }

    public interface WearableAddCallback {
        void onSuccess(String medicationId);

        void onError(String errorMessage);
    }
    public interface GetWearableDevicesCallback {
        void onSuccess(List<WearableDeviceDto> wearableDevices);

        void onError(String errorMessage);
    }

    public interface WearableUpdateCallback {
        void onSuccess();

        void onError(String errorMessage);
    }

    public interface WearableDeleteCallback {
        void onSuccess();

        void onError(String errorMessage);
    }
    public interface GetWearableDeviceCallback {
        void onSuccess(WearableDeviceDto wearableDevice);

        void onError(String errorMessage);
    }
}
