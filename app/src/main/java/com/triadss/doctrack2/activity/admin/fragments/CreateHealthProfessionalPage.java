package com.triadss.doctrack2.activity.admin.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.config.constants.SessionConstants;
import com.triadss.doctrack2.dto.HealthProfDto;
import com.triadss.doctrack2.repoositories.HealthProfRepository;

import java.util.function.Function;
import com.triadss.doctrack2.helper.ButtonManager;
import com.triadss.doctrack2.utils.FragmentFunctions;

public class CreateHealthProfessionalPage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private HealthProfRepository healthProfRepository;
    private static final String TAG = "PatientMedicationAddFragment";
    private Button buttonSubmit;
    TextView errorTextEmail, errorTextHWN, errorTextPosition, errorTextUser, errorTextPassword, errorTextGender;
    View rootView;
    private EditText editTextPositionInput, editTextUserNameInput, editTextPasswordInput, editTextAppointmentIDInput,
            editTextGenderInput;
    private EditText editTextEmailInput, editTextNameInput;
    private SharedPreferences sharedPref;

    public CreateHealthProfessionalPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateHealthProfessionalPage.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateHealthProfessionalPage newInstance(String param1, String param2) {
        CreateHealthProfessionalPage fragment = new CreateHealthProfessionalPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_admin_manage_user_accounts_create_health_professional, container,
                false);

        FloatingActionButton homeBtn = rootView.findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> {
            FragmentFunctions.ChangeFragmentNoStack(requireActivity(), new AdminHomeFragment());
        });

        sharedPref = getContext().getSharedPreferences(SessionConstants.SessionPreferenceKey, Context.MODE_PRIVATE);
        healthProfRepository = new HealthProfRepository();
        editTextNameInput = rootView.findViewById(R.id.editTextHWN);
        editTextPositionInput = rootView.findViewById(R.id.editTextPosition);
        editTextUserNameInput = rootView.findViewById(R.id.editTextUserName);
        editTextPasswordInput = rootView.findViewById(R.id.editTextPassword);
        editTextGenderInput = rootView.findViewById(R.id.editTextGender);
        editTextEmailInput = rootView.findViewById(R.id.editTextEmail);

        errorTextEmail = rootView.findViewById(R.id.errorTextEmail);
        errorTextHWN = rootView.findViewById(R.id.errorTextHWN);
        errorTextPosition = rootView.findViewById(R.id.errorTextPosition);
        errorTextUser = rootView.findViewById(R.id.errorTextUser);
        errorTextPassword = rootView.findViewById(R.id.errorTextPassword);
        errorTextGender = rootView.findViewById(R.id.errorTextGender);

        errorTextEmail.setVisibility(rootView.GONE);
        errorTextHWN.setVisibility(rootView.GONE);
        errorTextPosition.setVisibility(rootView.GONE);
        errorTextUser.setVisibility(rootView.GONE);
        errorTextPassword.setVisibility(rootView.GONE);
        errorTextGender.setVisibility(rootView.GONE);

        buttonSubmit = rootView.findViewById(R.id.buttonSubmit);

        setupConfirmationButton();
        return rootView;
    }

    private void setupConfirmationButton() {
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Function<String, Boolean> isNotEmptyPredicate = (val) -> !val.isEmpty();
                Function<String, Boolean> containsDotCom = (val) -> val.contains(".com");
                Function<String, Boolean> containsAtSign = (val) -> val.contains("@");

                if (widgetPredicate(editTextEmailInput, containsDotCom)
                        && widgetPredicate(editTextEmailInput, containsAtSign)
                        && widgetPredicate(editTextEmailInput, isNotEmptyPredicate)
                        && widgetPredicate(editTextNameInput, isNotEmptyPredicate)
                        && widgetPredicate(editTextPositionInput, isNotEmptyPredicate)
                        && widgetPredicate(editTextUserNameInput, isNotEmptyPredicate)
                        && widgetPredicate(editTextPasswordInput, isNotEmptyPredicate)
                        && widgetPredicate(editTextGenderInput, isNotEmptyPredicate)) {
                    handleConfirmationButtonClick();
                } else {
                    showTextViewWhenTrue(editTextEmailInput, (value) -> !value.contains("@")
                            || !value.contains(".com")
                            || value.isEmpty(), errorTextEmail);
                    showTextViewWhenTrue(editTextNameInput, (value) -> value.isEmpty(), errorTextHWN);
                    showTextViewWhenTrue(editTextPositionInput, (value) -> value.isEmpty(), errorTextPosition);
                    showTextViewWhenTrue(editTextUserNameInput, (value) -> value.isEmpty(), errorTextUser);
                    showTextViewWhenTrue(editTextPasswordInput, (value) -> value.isEmpty(), errorTextPassword);
                    showTextViewWhenTrue(editTextGenderInput, (value) -> value.isEmpty(), errorTextGender);
                }
            }
        });
    }

    boolean widgetPredicate(Button textSource, Function<String, Boolean> predicate) {
        return predicate.apply(textSource.getText().toString());
    }

    boolean widgetPredicate(EditText textSource, Function<String, Boolean> predicate) {
        return predicate.apply(textSource.getText().toString());
    }

    void showTextViewWhenTrue(EditText textSource, Function<String, Boolean> predicate, TextView messageWidget) {
        showTextViewWhenTrue(textSource.getText().toString(), predicate, messageWidget);
    }

    void showTextViewWhenTrue(Button buttonSource, Function<String, Boolean> predicate, TextView messageWidget) {
        showTextViewWhenTrue(buttonSource.getText().toString(), predicate, messageWidget);
    }

    void showTextViewWhenTrue(String textSource, Function<String, Boolean> predicate, TextView messageWidget) {
        if (predicate.apply(textSource)) {
            messageWidget.setVisibility(View.VISIBLE);
        } else {
            messageWidget.setVisibility(View.GONE);
        }
    }

    private void handleConfirmationButtonClick() {
        try {
            ButtonManager.disableButton(buttonSubmit);
            String Position = editTextPositionInput.getText().toString();
            String UserName = editTextUserNameInput.getText().toString();
            String Password = editTextPasswordInput.getText().toString();
            String Gender = editTextGenderInput.getText().toString();
            String fullName = editTextNameInput.getText().toString();
            String email = editTextEmailInput.getText().toString();

            String currentEmail = sharedPref.getString(SessionConstants.Email, "");
            String currentPassword = sharedPref.getString(SessionConstants.Password, "");

            HealthProfDto healthProfdto = new HealthProfDto(fullName, Position, UserName, email, Password, Gender);
            healthProfRepository.addHealthProf(healthProfdto, new HealthProfRepository.HealthProAddCallback() {

                @Override
                public void onSuccess(String healthProfId) {
                    // Sign in the old user
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(currentEmail, currentPassword)
                            .addOnCompleteListener(signInTask -> {
                                if (signInTask.isSuccessful()) {
                                    FirebaseUser oldUser = signInTask.getResult().getUser();
                                    if (oldUser != null) {
                                        // Old user signed in successfully, do something
                                    }
                                } else {
                                    // Handle sign-in failure
                                }
                            });
                    Log.e(TAG, "Successfully added medication with the id of " + healthProfId);
                    editTextNameInput.setText("");
                    editTextPositionInput.setText("");
                    editTextUserNameInput.setText("");
                    editTextPasswordInput.setText("");
                    editTextGenderInput.setText("");
                    editTextEmailInput.setText("");
                    Toast.makeText(getContext(), "Added Professional Health Account Created", Toast.LENGTH_SHORT)
                            .show();
                    ButtonManager.enableButton(buttonSubmit);
                    getActivity().onBackPressed();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e(TAG, "Failure in adding medication in the document");
                    ButtonManager.enableButton(buttonSubmit);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            ButtonManager.enableButton(buttonSubmit);
        }
    }
}