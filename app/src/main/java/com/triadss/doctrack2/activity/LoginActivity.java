package com.triadss.doctrack2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.admin.AdminHome;
import com.triadss.doctrack2.activity.healthprof.HealthProfHome;
import com.triadss.doctrack2.activity.patient.PatientHome;
import com.triadss.doctrack2.config.constants.NotificationConstants;
import com.triadss.doctrack2.config.constants.SessionConstants;
import com.triadss.doctrack2.config.enums.UserRole;
import com.triadss.doctrack2.helper.ButtonManager;
import com.triadss.doctrack2.notification.NotificationBackgroundWorker;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText editTextEmail;

    TextInputEditText editTextPassword;

    Button buttonLogin;

    FirebaseAuth mAuth;

    ProgressBar progressBar;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            fetchUserRole(currentUser.getUid());
        } else
        {
            WorkManager.getInstance(this).cancelAllWorkByTag(NotificationConstants.NOTIFICATION_WORKER_TAG);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);

        buttonLogin.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email, password;
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(LoginActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            ButtonManager.disableButton(buttonLogin);
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        SharedPreferences sharedPref = getSharedPreferences(SessionConstants.SessionPreferenceKey,
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();

                        editor.putString(SessionConstants.LoggedInUid, user.getUid());
                        editor.putString(SessionConstants.Password, password);
                        editor.putString(SessionConstants.Email, email);
                        editor.apply();

                        if (user != null) {
                            ButtonManager.enableButton(buttonLogin);
                            if (user.isEmailVerified()) {
                                //Email is verified
                                fetchUserRole(user.getUid());
                                Toast.makeText(LoginActivity.this, "Login Successfully",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // Email is not verified, prompt user to verify email
                                Toast.makeText(LoginActivity.this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                                sendEmailVerification(user);
                            }
                        }

                    } else {
                        FirebaseAuthException e = (FirebaseAuthException) task.getException();
                        if (e != null) {
                            // Get the error code
                            String errorCode = e.getErrorCode();
                            // Check the error code for specific cases
                            if (errorCode != null) {
                                switch (errorCode) {
                                        //User not found
                                    case "ERROR_USER_NOT_FOUND":
                                        // Incorrect email format
                                        Toast.makeText(LoginActivity.this, "Email is incorrect.", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "ERROR_INVALID_EMAIL":
                                        // Incorrect email format
                                        Toast.makeText(LoginActivity.this, "Email is incorrect.", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "ERROR_WRONG_PASSWORD":
                                        // Incorrect password
                                        Toast.makeText(LoginActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        // Other errors
                                        Toast.makeText(LoginActivity.this, "Failed To Login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            } else {
                                // Unknown error
                                Toast.makeText(LoginActivity.this, "Failed To Login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            ButtonManager.enableButton(buttonLogin);
                        }
                    }

                }).addOnFailureListener(e -> {
                    Log.e("ERROR TAG", e.getMessage());
                    ButtonManager.enableButton(buttonLogin);

                });
        });
    }

    private void fetchUserRole(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a reference to the "users" collection and the specific user document
        DocumentReference userRef = db.collection("users").document(userId);

        // Fetch the user document
        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Retrieve the user role from the document
                            String userRoleString = document.getString("role");
                            if (userRoleString != null) {
                                // Prepare periodic background
                                Constraints constraints = new Constraints.Builder()
                                        .setRequiredNetworkType(NetworkType.CONNECTED)
                                        .build();

                                PeriodicWorkRequest notifWorkRequest = new PeriodicWorkRequest.Builder(
                                        NotificationBackgroundWorker.class, 15, TimeUnit.MINUTES)
                                        .setInputData(new Data.Builder()
                                                .putString(NotificationConstants.RECEIVER_ID, userId)
                                                .build())
                                        .addTag(NotificationConstants.NOTIFICATION_WORKER_TAG)
                                        .setConstraints(constraints)
                                        .build();
                                WorkManager.getInstance(this)
                                        .enqueueUniquePeriodicWork(NotificationConstants.NOTIFICATION_TAG,
                                                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, notifWorkRequest);

                                UserRole userRole = UserRole.valueOf(userRoleString);
                                redirectBasedOnUserRole(userRole);
                            }
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Error fetching user role.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void redirectBasedOnUserRole(UserRole userRole) {
        switch (userRole) {
            case ADMIN:
                Intent adminIntent = new Intent(getApplicationContext(), AdminHome.class);
                startActivity(adminIntent);
                break;
            case PROF:
                Intent profIntent = new Intent(getApplicationContext(), HealthProfHome.class);
                startActivity(profIntent);
                break;
            case PATIENT:
                Intent userIntent = new Intent(getApplicationContext(), PatientHome.class);
                startActivity(userIntent);
                break;
            default:
                Toast.makeText(LoginActivity.this, "Unknown or invalid user role.",
                        Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmailVerification(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}