package com.triadss.doctrack2.utils;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.triadss.doctrack2.config.constants.FireStoreCollection;
import com.triadss.doctrack2.config.model.UserModel;

import java.util.concurrent.ExecutionException;

public class DocTrackUtils {

    /**
     * Displays a validation error message for a TextInputEditText within a TextInputLayout.
     *
     * @param errorMessage      The error message to be displayed.
     * @param textInputEditText The TextInputEditText associated with the error.
     * @param textInputLayout   The TextInputLayout containing the TextInputEditText.
     */
    public static void showValidationError(String errorMessage,
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
    public static void hideValidationError(TextInputLayout textInputLayout) {
        textInputLayout.setErrorEnabled(false);
        textInputLayout.setError(null);
    }
}
