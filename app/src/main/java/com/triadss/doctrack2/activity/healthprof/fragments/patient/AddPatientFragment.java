package com.triadss.doctrack2.activity.healthprof.fragments.patient;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.healthprof.fragments.records.AddMedicalHistory;
import com.triadss.doctrack2.config.constants.DocTrackConstant;
import com.triadss.doctrack2.config.constants.DocTrackErrorMessage;
import com.triadss.doctrack2.config.constants.SessionConstants;
import com.triadss.doctrack2.config.constants.ToastConstants;
import com.triadss.doctrack2.dto.AddPatientDto;
import com.triadss.doctrack2.dto.DateDto;
import com.triadss.doctrack2.dto.MedicalHistoryDto;
import com.triadss.doctrack2.helper.ButtonManager;
import com.triadss.doctrack2.repoositories.MedicalHistoryRepository;
import com.triadss.doctrack2.repoositories.PatientRepository;
import com.triadss.doctrack2.repoositories.ReportsRepository;
import com.triadss.doctrack2.repoositories.VitalSignsRepository;

import java.util.Calendar;
import java.util.function.Function;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPatientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPatientFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    EditText input_Email, editTextPhone, editTextIdNumber, editTextFullName;
    AutoCompleteTextView editTextAddress;
    TextView error_patientID, error_Email, error_FullName, error_Address, error_Contact, error_DateBirth;
    Spinner input_Status, input_course, input_Year, input_Gender;
    Button getBirthDate;
    DateDto birthDate;
    FirebaseAuth mAuth;
    String loggedInUserId;
    PatientRepository _patientRepository;
    ReportsRepository _reportsRepository = new ReportsRepository();
    MedicalHistoryRepository _medicalHistoryRepository = new MedicalHistoryRepository();
    VitalSignsRepository _vitalSignsRepository = new VitalSignsRepository();

    public AddPatientFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPatientFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPatientFragment newInstance(String param1, String param2) {
        AddPatientFragment fragment = new AddPatientFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * Called to create the view for this fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. The fragment should not add the view
     *                           itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    private SharedPreferences sharedPref;
    private Button nextButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPref = getContext().getSharedPreferences(SessionConstants.SessionPreferenceKey, Context.MODE_PRIVATE);
        loggedInUserId = sharedPref.getString(SessionConstants.LoggedInUid, "");

        _patientRepository = new PatientRepository();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_patient_record_add_patient, container, false);

        // Enable the back button
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // input field
        mAuth = FirebaseAuth.getInstance();
        input_Email = rootView.findViewById(R.id.input_Email);
        editTextAddress = rootView.findViewById(R.id.input_address);
        ArrayAdapter<CharSequence> addressAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.address,
            android.R.layout.simple_dropdown_item_1line
        );
        editTextAddress.setAdapter(addressAdapter);

        editTextPhone = rootView.findViewById(R.id.input_contactNo);
        input_course = rootView.findViewById(R.id.input_course);
        ArrayAdapter<CharSequence> courseAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.course,
                android.R.layout.simple_spinner_item
        );
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        input_course.setAdapter(courseAdapter);

        editTextIdNumber = rootView.findViewById(R.id.input_patientID);
        editTextFullName = rootView.findViewById(R.id.input_fullName);
        getBirthDate = rootView.findViewById(R.id.selectBirthDate);
        input_Year = rootView.findViewById(R.id.input_Year);
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.year,
                android.R.layout.simple_spinner_item
        );
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        input_Year.setAdapter(yearAdapter);

        input_Gender = rootView.findViewById(R.id.input_Gender);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.gender,
                android.R.layout.simple_spinner_item
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        input_Gender.setAdapter(genderAdapter);

        input_Status = rootView.findViewById(R.id.input_Status);
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.civilStatus,
            android.R.layout.simple_spinner_item
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        input_Status.setAdapter(statusAdapter);

        error_patientID = rootView.findViewById(R.id.error_patientID);
        error_Email = rootView.findViewById(R.id.error_Email);
        error_FullName = rootView.findViewById(R.id.error_FullName);
        error_Address = rootView.findViewById(R.id.error_Address);
        error_Contact = rootView.findViewById(R.id.error_Contact);
        error_DateBirth = rootView.findViewById(R.id.error_DateBirth);

        error_patientID.setVisibility(rootView.INVISIBLE);
        error_Email.setVisibility(rootView.INVISIBLE);
        error_FullName.setVisibility(rootView.INVISIBLE);
        error_Address.setVisibility(rootView.INVISIBLE);
        error_Contact.setVisibility(rootView.INVISIBLE);
        error_DateBirth.setVisibility(rootView.INVISIBLE);

        getBirthDate.setOnClickListener((View.OnClickListener) v -> {
            // Get the current date
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR)-18;
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create and show the Date Picker Dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Store the selected date
                        birthDate = new DateDto(year1, monthOfYear, dayOfMonth);

                        // Update the text on the button
                        getBirthDate.setText(birthDate.ToString());
                    }, year, month, day);

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            // Show the Date Picker Dialog
            datePickerDialog.show();
        });

        nextButton = rootView.findViewById(R.id.nextBtn);
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Function<String, Boolean> isNotEmptyPredicate = (val) -> !val.isEmpty();
                Function<String, Boolean> containsDotCom = (val) -> val.contains(".com");
                Function<String, Boolean> containsAtSign = (val) -> val.contains("@");
                Function<String, Boolean> notContainsSelectDate = (val) -> !val.contains("Select Date");
                Function<String, Boolean> lengthAtleast6 = (val) -> val.length() >= 6;

                if(widgetPredicate(input_Email, isNotEmptyPredicate)
                    && widgetPredicate(input_Email, containsDotCom)
                    && widgetPredicate(input_Email, containsAtSign)
                    && widgetPredicate(editTextAddress, isNotEmptyPredicate)
                    && widgetPredicate(editTextPhone, isNotEmptyPredicate)
                    && widgetPredicate(editTextIdNumber, isNotEmptyPredicate)
                    && widgetPredicate(editTextFullName, isNotEmptyPredicate)
                    && widgetPredicate(editTextPhone, isNotEmptyPredicate)
                    && widgetPredicate(getBirthDate, notContainsSelectDate)
                    && widgetPredicate(editTextIdNumber, lengthAtleast6)
                ) {
                    createPatient();
                }
                else {
                    showTextViewWhenTrue(input_Email, (value) -> !value.contains("@")
                            || !value.contains(".com")
                            || value.isEmpty(), error_Email);
                    showTextViewWhenTrue(editTextAddress, (value) -> value.isEmpty(), error_Address);
                    showTextViewWhenTrue(editTextPhone, (value) -> value.isEmpty(), error_Contact);
                    showTextViewWhenTrue(editTextIdNumber, (value) -> value.isEmpty() || (value.length() < 6), error_patientID);
                    showTextViewWhenTrue(editTextFullName, (value) -> value.isEmpty(), error_FullName);
                    showTextViewWhenTrue(getBirthDate, (value) -> value.contains("Select Date"), error_DateBirth);
                }
            }
        });

        return rootView;
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
        if(predicate.apply(textSource))
        {
            messageWidget.setVisibility(View.VISIBLE);
        } else {
            messageWidget.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Creates a new patient by registering a user with FirebaseAuth and saving their information to Firestore.
     * It also displays appropriate toast messages based on the success or failure of the registration process.
     */
    private void createPatient() {
        AddPatientDto patientDto = new AddPatientDto(); //add gender
        patientDto.setEmail(String.valueOf(input_Email.getText()).trim());
        patientDto.setFullName(String.valueOf(editTextFullName.getText()).trim());
        patientDto.setAddress(String.valueOf(editTextAddress.getText()).trim());
        patientDto.setPhone(String.valueOf(editTextPhone.getText()).trim());
        patientDto.setCourse(String.valueOf(input_course.getSelectedItem()).trim());
        patientDto.setIdNumber(String.valueOf(editTextIdNumber.getText()).trim());
        patientDto.setDateOfBirth(birthDate.ToStartDateTimestamp());
        Integer year = input_Year.getSelectedItem().equals(DocTrackConstant.NOT_APPLICABLE) ? null :
                Integer.parseInt(String.valueOf(input_Year.getSelectedItem()));
        patientDto.setYear(year);
        patientDto.setStatus(String.valueOf(input_Status.getSelectedItem()).trim());
        patientDto.setGender(String.valueOf(input_Gender.getSelectedItem()).trim());

        String email =sharedPref.getString(SessionConstants.Email, "");
        String password = sharedPref.getString(SessionConstants.Password, "");
        ButtonManager.disableButton(nextButton);
        try {
            FirebaseAuth newAuth = FirebaseAuth.getInstance();
            newAuth.createUserWithEmailAndPassword(patientDto.getEmail(), patientDto.getIdNumber()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    try {
                        if (user != null) {
                            String userId = user.getUid();
                            patientDto.setUid(userId);
                            // create details in user table and report table
                            _patientRepository.addPatientCallback(patientDto, new PatientRepository.PatientAddUpdateCallback() {
                                @Override
                                public void onSuccess(String patientId) {
                                    _reportsRepository.addHealthProfPatientInfoReport(loggedInUserId, patientDto, new ReportsRepository.ReportCallback() {
                                        @Override
                                        public void onReportAddedSuccessfully() {

                                            //Create default Medical History
                                            _medicalHistoryRepository.createDefaultMedicalHistoryForPatient(userId, new MedicalHistoryRepository.AddUpdateCallback() {
                                                @Override
                                                public void onSuccess(String medHistoryUid) {
                                                    _vitalSignsRepository.createDefaultVitalSignsForPatient(userId, new VitalSignsRepository.AddUpdateCallback() {
                                                        @Override
                                                        public void onSuccess(String vitalSignsId) {
                                                            Toast.makeText(requireContext(), ToastConstants.ADDED_PATIENT, Toast.LENGTH_SHORT).show();

                                                            newAuth.signOut();

                                                            // Sign in the old user
                                                            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
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

                                                            showMedicalHistory(patientId, medHistoryUid, vitalSignsId);
                                                        }

                                                        @Override
                                                        public void onError(String errorMessage) {
                                                            System.out.println();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onError(String errorMessage) {
                                                    System.out.println();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onReportFailed(String errorMessage) {
                                            ButtonManager.enableButton(nextButton);
                                        }
                                    });
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    ButtonManager.enableButton(nextButton);
                                }
                            });

                        }
                    } catch (Exception e) {
                        // DELETE newly created user when there is something wrong with saveUserToFireStore()
                        if (newAuth.getCurrentUser() != null) {
                            newAuth.getCurrentUser().delete();
                            Toast.makeText(getContext(), "Failed to create user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        ButtonManager.enableButton(nextButton);
                    }
                } else {
                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
                    Toast.makeText(getContext(), "Failed to create user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    ButtonManager.enableButton(nextButton);
                }
            });
        } catch (Exception e) {
            // GENERIC ERROR HANDLER
            Toast.makeText(getContext(), DocTrackErrorMessage.GENERIC_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            ButtonManager.enableButton(nextButton);
        }
    }

    private void showMedicalHistory(String patientUId, String medHistoryUid, String vitalSignsUid) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        // TODO: Create View Record Fragment for Patient then remove // of the nextline code to use it
        transaction.replace(R.id.frame_layout, AddMedicalHistory.newInstance(patientUId, medHistoryUid, vitalSignsUid));
        transaction.addToBackStack(null);
        transaction.commit();
    }
}