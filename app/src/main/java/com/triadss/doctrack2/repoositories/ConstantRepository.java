package com.triadss.doctrack2.repoositories;

import android.util.Log;

import com.google.firebase.FirebaseException;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.triadss.doctrack2.config.constants.FireStoreCollection;

import java.util.HashMap;
import java.util.Map;

public class ConstantRepository {
    private static final String TAG = "ConstantRepository";
    private static final CollectionReference constantsCollection =  FirebaseFirestore.getInstance().collection(FireStoreCollection.CONSTANT_TABLE);
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseUser user = auth.getCurrentUser();
    private static final String DOC_HOLIDAY = "holidays";
    public static void getHolidays(HolidayFetchCallback callback) {
        if (user != null) {
            try {
                constantsCollection.document(DOC_HOLIDAY).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, Object> data = document.getData();
                        if (data != null) {
                            Map<String, Timestamp> holidays = new HashMap<>();
                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                if (entry.getValue() instanceof Timestamp) {
                                    holidays.put(entry.getKey(), (Timestamp) entry.getValue());
                                }
                            }
                            callback.onHolidaysFetched(holidays);
                        } else {
                            callback.onFailure("No data found in document");
                        }
                    } else {
                        callback.onFailure("Failed to fetch document: " + task.getException());
                    }
                });
            } catch (Exception e){
                Log.e(TAG, "ERROR: " + e.getMessage());
                callback.onFailure("Error: " + e.getMessage());
            }

        } else {
            callback.onFailure("User is not authenticated");
        }
    }



    public interface HolidayFetchCallback {
        void onHolidaysFetched(Map<String, Timestamp> holidays);

        void onFailure(String errorMessage);
    }
}
