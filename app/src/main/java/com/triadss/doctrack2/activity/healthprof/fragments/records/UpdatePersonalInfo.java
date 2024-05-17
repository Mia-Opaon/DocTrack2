package com.triadss.doctrack2.activity.healthprof.fragments.records;

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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.triadss.doctrack2.R;
import com.triadss.doctrack2.config.constants.DocTrackConstant;
import com.triadss.doctrack2.config.constants.SessionConstants;
import com.triadss.doctrack2.config.constants.ToastConstants;
import com.triadss.doctrack2.dto.AddPatientDto;
import com.triadss.doctrack2.helper.ButtonManager;
import com.triadss.doctrack2.repoositories.PatientRepository;
import com.triadss.doctrack2.repoositories.ReportsRepository;

import java.util.function.Function;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdatePersonalInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdatePersonalInfo extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PATIENT_UID = "patientUid";

    // TODO: Rename and change types of parameters
    private String patientUid;

    EditText editTextPhone,
        editTextEmail, editTextFullname, editTextIdNumber;
    AutoCompleteTextView editTextAddress;
    Spinner edit_course, input_Year;
    ArrayAdapter<CharSequence> courseAdapter, yearAdapter;
    TextView errorAddress, errorPhone;
    PatientRepository patientRepository = new PatientRepository();
    ReportsRepository _reportsRepository = new ReportsRepository();
    String loggedInUserId;
    Spinner input_Status;
    private Button nextButton;
    public UpdatePersonalInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param patientUid Parameter 1.
     * @return A new instance of fragment addMedicalRecord.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdatePersonalInfo newInstance(String patientUid) {
        UpdatePersonalInfo fragment = new UpdatePersonalInfo();
        Bundle args = new Bundle();
        args.putString(PATIENT_UID, patientUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patientUid = getArguments().getString(PATIENT_UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(SessionConstants.SessionPreferenceKey, Context.MODE_PRIVATE);
        loggedInUserId = sharedPref.getString(SessionConstants.LoggedInUid, "");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_update_record, container, false);

        input_Status = rootView.findViewById(R.id.input_Status);
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.civilStatus,
                android.R.layout.simple_spinner_item
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        input_Status.setAdapter(statusAdapter);

        editTextAddress = rootView.findViewById(R.id.input_address);
        ArrayAdapter<CharSequence> addressAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.address,
                android.R.layout.simple_dropdown_item_1line
        );
        editTextAddress.setAdapter(addressAdapter);

        editTextPhone = rootView.findViewById(R.id.input_contactNo);
        edit_course = rootView.findViewById(R.id.input_course);
        courseAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.course,
                android.R.layout.simple_spinner_item
        );
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edit_course.setAdapter(courseAdapter);

        input_Year = rootView.findViewById(R.id.input_Year);
        yearAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.year,
                android.R.layout.simple_spinner_item
        );
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        input_Year.setAdapter(yearAdapter);

        editTextEmail = rootView.findViewById(R.id.email);
        editTextFullname = rootView.findViewById(R.id.name);
        editTextIdNumber = rootView.findViewById(R.id.idNumber);

        errorAddress = rootView.findViewById(R.id.errorAddress);
        errorPhone = rootView.findViewById(R.id.errorPhone);

        errorAddress.setVisibility(rootView.INVISIBLE);
        errorPhone.setVisibility(rootView.INVISIBLE);

        populatePersonalInfo();

        nextButton = rootView.findViewById(R.id.nxtButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Function<String, Boolean> isNotEmptyPredicate = (val) -> !val.isEmpty();
                if(widgetPredicate(editTextAddress, isNotEmptyPredicate)
                && widgetPredicate(editTextPhone, isNotEmptyPredicate)
                )
                {
                    updatePersonalInfo();
                }
                else
                {
                    showTextViewWhenTrue(editTextAddress, (value) -> value.isEmpty(), errorAddress);
                    showTextViewWhenTrue(editTextPhone, (value) -> value.isEmpty(), errorPhone);
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

    private void populatePersonalInfo()
    {
        patientRepository.getPatient(patientUid, new PatientRepository.PatientFetchCallback(){

            @Override
            public void onSuccess(AddPatientDto patient) {
                editTextEmail.setText(patient.getEmail());
                editTextFullname.setText(patient.getFullName());
                editTextAddress.setText(patient.getAddress());
                editTextPhone.setText(patient.getPhone());
                int coursePosition = courseAdapter.getPosition(patient.getCourse());
                edit_course.setSelection(coursePosition);

                if(patient.getYear() == null) {
                    int yearNAPosition = yearAdapter.getPosition(DocTrackConstant.NOT_APPLICABLE);
                    input_Year.setSelection(yearNAPosition);
                } else {
                    int yearPosition = yearAdapter.getPosition(String.valueOf(patient.getYear()));
                    input_Year.setSelection(yearPosition);
                }

                editTextIdNumber.setText(patient.getIdNumber());
            }

            @Override
            public void onError(String errorMessage) {
                System.out.println();
            }
        });
    }

    private void updatePersonalInfo()
    {
        AddPatientDto patientDto = new AddPatientDto();
        patientDto.setUid(patientUid);
        patientDto.setFullName(editTextFullname.getText().toString());
        patientDto.setAddress(String.valueOf(editTextAddress.getText()).trim());
        patientDto.setPhone(String.valueOf(editTextPhone.getText()).trim());
        patientDto.setCourse(String.valueOf(edit_course.getSelectedItem()).trim());
        patientDto.setStatus(String.valueOf(input_Status.getSelectedItem()).trim());
        Integer year = input_Year.getSelectedItem().equals(DocTrackConstant.NOT_APPLICABLE) ? null :
                Integer.parseInt(String.valueOf(input_Year.getSelectedItem()));
        patientDto.setYear(year);

        ButtonManager.disableButton(nextButton);

        patientRepository.updatePatient(patientDto, new PatientRepository.PatientAddUpdateCallback() {
            @Override
            public void onSuccess(String patientId) {
                _reportsRepository.addHealthProfUpdatePatientInfoReport(loggedInUserId, patientDto, new ReportsRepository.ReportCallback() {
                    @Override
                    public void onReportAddedSuccessfully() {
                        Toast.makeText(requireContext(), ToastConstants.UPDATED_PATIENT, Toast.LENGTH_SHORT).show();
                        showMedicalHistory();
                    }

                    @Override
                    public void onReportFailed(String errorMessage) {
                        ButtonManager.enableButton(nextButton);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), "Failed to update patient information: " + errorMessage, Toast.LENGTH_SHORT).show();
                ButtonManager.enableButton(nextButton);
            }
        });
    }

    private void showMedicalHistory() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        // TODO: Create View Record Fragment for Patient then remove // of the nextline code to use it
        transaction.replace(R.id.frame_layout, UpdateMedicalHistory.newInstance(patientUid));
        transaction.addToBackStack(null);
        transaction.commit();
    }
}