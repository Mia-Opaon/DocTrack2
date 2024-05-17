package com.triadss.doctrack2.config;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.triadss.doctrack2.dto.AuthDto;

public class DocTrackUtils {

    /**
     * Validates the Password input.
     *
     * @param password The password to be validated.
     */
    private static void validatePassword(String password, TextInputEditText editTextPassword, TextInputLayout passwordTextInputLayout) {
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
//    private void validateEmail(String email) {
//        if (email.isEmpty()) {
//            showValidationError("Email is required", editTextEmail, emailTextInputLayout);
//        } else {
//            hideValidationError(emailTextInputLayout);
//        }
//    }

    /**
     * Displays a validation error message for a TextInputEditText within a TextInputLayout.
     *
     * @param errorMessage      The error message to be displayed.
     * @param textInputEditText The TextInputEditText associated with the error.
     * @param textInputLayout   The TextInputLayout containing the TextInputEditText.
     */
    private static void showValidationError(String errorMessage,
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
     * @param textInputLayout The TextInputLayout for which the validation error is to be hidden.
     */
    private static void hideValidationError(TextInputLayout textInputLayout) {
        textInputLayout.setErrorEnabled(false);
        textInputLayout.setError(null);
    }

}
