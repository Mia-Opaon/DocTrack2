package com.triadss.doctrack2.activity.healthprof.fragments.vitalsigns;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.healthprof.fragments.patient.PatientFragment;
import com.triadss.doctrack2.config.constants.SessionConstants;
import com.triadss.doctrack2.config.constants.ToastConstants;
import com.triadss.doctrack2.dto.VitalSignsDto;
import com.triadss.doctrack2.helper.ButtonManager;
import com.triadss.doctrack2.repoositories.ReportsRepository;
import com.triadss.doctrack2.repoositories.VitalSignsRepository;

import java.util.function.Function;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddVitalSigns#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddVitalSigns extends Fragment {

    FirebaseAuth mAuth;
    EditText editBloodPressure, editTemperature, editPulseRate, editOxygenLevel, editWeight, editHeight, editBMI;
    TextView errorBloodPresure, errorTempreture, errorSp02, errorPulse, errorWeight, errorHeight, errorBMI;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PATIENT_UID = "patientUid";
    private static final String VITALSIGNS_UID = "vitalSignsUid";

    // TODO: Rename and change types of parameters
    String PatientUid;
    String vitalsignsUid;
    String loggedInUserId;
    ReportsRepository _reportsRepository = new ReportsRepository();
    private Button submit;

    public AddVitalSigns() {
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
    public static AddVitalSigns newInstance(String patientUid, String vitalsignsUid) {
        AddVitalSigns fragment = new AddVitalSigns();
        Bundle args = new Bundle();
        args.putString(PATIENT_UID, patientUid);
        args.putString(VITALSIGNS_UID, vitalsignsUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            PatientUid = getArguments().getString(PATIENT_UID);
            vitalsignsUid = getArguments().getString(VITALSIGNS_UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(SessionConstants.SessionPreferenceKey, Context.MODE_PRIVATE);
        loggedInUserId = sharedPref.getString(SessionConstants.LoggedInUid, "");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_patient_record_add_vital_signs, container, false);
        submit = rootView.findViewById(R.id.submitBtn);

        editBloodPressure = rootView.findViewById(R.id.input_bloodPressure);
        editTemperature = rootView.findViewById(R.id.input_temperature);
        editPulseRate = rootView.findViewById(R.id.input_pulseRate);
        editOxygenLevel = rootView.findViewById(R.id.input_spo2);
        editWeight = rootView.findViewById(R.id.input_weight);
        editHeight = rootView.findViewById(R.id.input_height);
        editBMI = rootView.findViewById(R.id.input_bmi);

        errorBloodPresure = rootView.findViewById(R.id.errorBloodPresure);
        errorTempreture = rootView.findViewById(R.id.errorTempreture);
        errorSp02 = rootView.findViewById(R.id.errorSp02);
        errorPulse = rootView.findViewById(R.id.errorPulse);
        errorWeight = rootView.findViewById(R.id.errorWeight);
        errorHeight = rootView.findViewById(R.id.errorHeight);
        errorBMI = rootView.findViewById(R.id.errorBMI);

        errorBloodPresure.setVisibility(rootView.INVISIBLE);
        errorTempreture.setVisibility(rootView.INVISIBLE);
        errorSp02.setVisibility(rootView.INVISIBLE);
        errorPulse.setVisibility(rootView.INVISIBLE);
        errorWeight.setVisibility(rootView.INVISIBLE);
        errorHeight.setVisibility(rootView.INVISIBLE);
        errorBMI.setVisibility(rootView.INVISIBLE);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for auto-calculation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed for auto-calculation
            }

            @Override
            public void afterTextChanged(Editable s) {
                String weightStr = editWeight.getText().toString();
                String heightStr = editHeight.getText().toString();

                double bmi = calculateBMI(weightStr, heightStr);
                editBMI.setText(String.format("%.2f", bmi));
            }
        };

        editWeight.addTextChangedListener(textWatcher);
        editHeight.addTextChangedListener(textWatcher);


        submitButton(submit);

        return rootView;
    }

    private double calculateBMI(String weightStr, String heightStr){
        if(weightStr.isEmpty() || heightStr.isEmpty()) return 0;

        double weight = Double.parseDouble(weightStr);
        double height = Double.parseDouble(heightStr);
        return (height != 0) ? (weight / Math.pow(height, 2.0) * 10000) : 0.0;
    }

    private void submitButton(Button submit){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Function<String, Boolean> isNotEmptyPredicate = (val) -> !val.isEmpty();
                if(widgetPredicate(editBloodPressure, isNotEmptyPredicate)
                        && widgetPredicate(editTemperature, isNotEmptyPredicate)
                        && widgetPredicate(editPulseRate, isNotEmptyPredicate)
                        && widgetPredicate(editOxygenLevel, isNotEmptyPredicate)
                        && widgetPredicate(editWeight, isNotEmptyPredicate)
                        && widgetPredicate(editHeight, isNotEmptyPredicate)
                        && widgetPredicate(editBMI, isNotEmptyPredicate)) {
                    createVitalSigns(PatientUid);
                }else {
                    showTextViewWhenTrue(editBloodPressure, (value) -> value.isEmpty(), errorBloodPresure);
                    showTextViewWhenTrue(editTemperature, (value) -> value.isEmpty(), errorTempreture);
                    showTextViewWhenTrue(editOxygenLevel, (value) -> value.isEmpty(), errorSp02);
                    showTextViewWhenTrue(editPulseRate, (value) -> value.isEmpty(), errorPulse);
                    showTextViewWhenTrue(editWeight, (value) -> value.isEmpty(), errorWeight);
                    showTextViewWhenTrue(editHeight, (value) -> value.isEmpty(), errorHeight);
                    showTextViewWhenTrue(editBMI, (value) -> value.isEmpty(), errorBMI);
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
        if(predicate.apply(textSource))
        {
            messageWidget.setVisibility(View.VISIBLE);
        } else {
            messageWidget.setVisibility(View.INVISIBLE);
        }
    }
    
    private void createVitalSigns(String PatientUid) {
        VitalSignsDto vitalSignsDto = new VitalSignsDto();
        vitalSignsDto.setBloodPressure(String.valueOf(editBloodPressure.getText()).trim());
        vitalSignsDto.setTemperature(Double.parseDouble(String.valueOf(editTemperature.getText()).trim()));
        vitalSignsDto.setPulseRate(Integer.parseInt(String.valueOf(editPulseRate.getText()).trim()));
        vitalSignsDto.setOxygenLevel(Integer.parseInt(String.valueOf(editOxygenLevel.getText()).trim()));
        vitalSignsDto.setWeight(Integer.parseInt(String.valueOf(editWeight.getText())));
        vitalSignsDto.setHeight(Integer.parseInt(String.valueOf(editHeight.getText()).trim()));
        vitalSignsDto.setBMI(Double.parseDouble(String.valueOf(editBMI.getText()).trim()));
        vitalSignsDto.setPatientId(PatientUid);

        ButtonManager.disableButton(submit);

        vitalSignsDto.setUid(vitalsignsUid);
        VitalSignsRepository vitalSignsRepo = new VitalSignsRepository();
        vitalSignsRepo.updateVitalSigns(vitalSignsDto, new VitalSignsRepository.AddUpdateCallback() {
            @Override
            public void onSuccess(String vitalSignsId) {
                _reportsRepository.addHealthProfPatientVitalSignReport(loggedInUserId, PatientUid, new ReportsRepository.ReportCallback() {
                    @Override
                    public void onReportAddedSuccessfully() {
                        Toast.makeText(requireContext(), ToastConstants.ADDED_VITAL_SIGNS, Toast.LENGTH_SHORT).show();

                        backToPatientList();
                    }

                    @Override
                    public void onReportFailed(String errorMessage) {
                        ButtonManager.enableButton(submit);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                ButtonManager.enableButton(submit);
            }
        });
    }

    private void backToPatientList() {
        // Replace the current fragment with the patient list fragment
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        // TODO: Create View Record Fragment for Patient then remove // of the nextline code to use it
        transaction.replace(R.id.frame_layout, new PatientFragment());
        transaction.commit();
    }
}