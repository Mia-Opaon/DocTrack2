package com.triadss.doctrack2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.dto.AuthDto;

import java.util.HashMap;
import java.util.Map;

import com.triadss.doctrack2.helper.ButtonManager;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;

    TextInputLayout passwordTextInputLayout, emailTextInputLayout;

    Button buttonReg;

    FirebaseAuth mAuth;

    ProgressBar progressBar;

    TextView textView;

    @Override
    @Deprecated
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);

        editTextPassword = findViewById(R.id.password);
        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);

        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        // Attach TextWatcher to email EditText
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEmail(s.toString());
            }
        });

        // Attach TextWatcher to password EditText
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePassword(s.toString());
            }
        });

        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        buttonReg.setOnClickListener(v -> {
            ButtonManager.disableButton(buttonReg);
            progressBar.setVisibility(View.VISIBLE);
            AuthDto authDto = new AuthDto();
            authDto.setEmail(String.valueOf(editTextEmail.getText()).trim());
            authDto.setPassword(String.valueOf(editTextPassword.getText()).trim());
            authDto.setRole("PATIENT");

            if (validateInputs(authDto)) {
                register(authDto);
            } else {
                progressBar.setVisibility(View.GONE);
            }

            ButtonManager.enableButton(buttonReg);
        });
    }

    /**
     * Registers a new user with the provided authentication information using
     * Firebase Authentication.
     * Upon successful registration, the user's account is created, and the role is
     * set to "USER" in the Firestore database.
     * If registration is unsuccessful, an authentication failed message is
     * displayed.
     *
     * @param authDto An AuthDto object containing the user's email and password for
     *                registration.
     */
    private void register(AuthDto authDto) {
        mAuth.createUserWithEmailAndPassword(authDto.getEmail(), authDto.getPassword())
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            saveUserRoleToFireStore(user.getUid(), authDto.getRole());
                        }

                        Toast.makeText(RegisterActivity.this, "Account Created.",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserRoleToFireStore(String userId, String role) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a reference to the "users" collection and the specific user document
        DocumentReference userRef = db.collection("users").document(userId);

        // Create a Map to represent the user data, including the "ROLE" field
        Map<String, Object> userData = new HashMap<>();
        userData.put("role", role);

        // Update the user document with the new data
        userRef.set(userData, SetOptions.merge());
    }

    /**
     * Validates the Password input.
     *
     * @param password The password to be validated.
     */
    private void validatePassword(String password) {
        if (password.isEmpty()) {
            showValidationError("Password is required", editTextPassword, passwordTextInputLayout);
        } else if (password.length() < 6) {
            showValidationError("Password should be at least 6 characters", editTextPassword, passwordTextInputLayout);
        } else if (password.length() > 30) {
            showValidationError("Password cannot exceed 30 characters", editTextPassword, passwordTextInputLayout);
        } else {
            hideValidationError(passwordTextInputLayout);
        }

    }

    /**
     * Validates the email input.
     *
     * @param email The email to be validated.
     */
    private void validateEmail(String email) {
        if (email.isEmpty()) {
            showValidationError("Email is required", editTextEmail, emailTextInputLayout);
        } else {
            hideValidationError(emailTextInputLayout);
        }
    }

    /**
     * Validates the input fields of the authentication data.
     *
     * @param authDto The authentication data to be validated.
     * @return True if all inputs are valid, false otherwise.
     */
    private boolean validateInputs(@NonNull AuthDto authDto) {
        boolean isValidated = true;

        if (authDto.getPassword().isEmpty()) {
            showValidationError("Password is required", editTextPassword, passwordTextInputLayout);
            isValidated = false;
        } else if (authDto.getPassword().length() < 6) {
            showValidationError("Password should be at least 6 characters", editTextPassword, passwordTextInputLayout);
            isValidated = false;
        } else if (authDto.getPassword().length() > 30) {
            showValidationError("Password cannot exceed 30 characters", editTextPassword, passwordTextInputLayout);
            isValidated = false;
        } else {
            hideValidationError(passwordTextInputLayout);
        }

        if (authDto.getEmail().isEmpty()) {
            showValidationError("Email is required", editTextEmail, emailTextInputLayout);
            isValidated = false;
        } else {
            hideValidationError(emailTextInputLayout);
        }
        return isValidated;
    }

    /**
     * Displays a validation error message for a TextInputEditText within a
     * TextInputLayout.
     *
     * @param errorMessage      The error message to be displayed.
     * @param textInputEditText The TextInputEditText associated with the error.
     * @param textInputLayout   The TextInputLayout containing the
     *                          TextInputEditText.
     */
    private void showValidationError(String errorMessage,
            TextInputEditText textInputEditText,
            TextInputLayout textInputLayout) {
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(errorMessage);
        textInputEditText.requestFocus();
    }

    /**
     * Hides the validation error for a TextInputLayout by disabling error display
     * and setting the error text to null.
     *
     * @param textInputLayout The TextInputLayout for which the validation error is
     *                        to be hidden.
     */
    private void hideValidationError(TextInputLayout textInputLayout) {
        textInputLayout.setErrorEnabled(false);
        textInputLayout.setError(null);
    }
}