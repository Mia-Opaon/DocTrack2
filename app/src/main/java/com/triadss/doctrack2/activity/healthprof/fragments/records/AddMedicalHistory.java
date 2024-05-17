package com.triadss.doctrack2.activity.healthprof.fragments.records;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.healthprof.fragments.medications.AddMedication;
import com.triadss.doctrack2.config.constants.SessionConstants;
import com.triadss.doctrack2.config.constants.ToastConstants;
import com.triadss.doctrack2.dto.MedicalHistoryDto;
import com.triadss.doctrack2.helper.ButtonManager;
import com.triadss.doctrack2.repoositories.MedicalHistoryRepository;
import com.triadss.doctrack2.repoositories.ReportsRepository;
import com.triadss.doctrack2.utils.CheckboxStringProcessor;
import com.triadss.doctrack2.utils.EditTextName;
import com.triadss.doctrack2.utils.EditTextStringProcessor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddMedicalHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddMedicalHistory extends Fragment {
    EditText editPrevHospitalization;
    CheckboxStringProcessor pastIllnessProcessor, familyHistoryProcessor;
    EditTextStringProcessor obgyneHistoryProcessor;
    EditText otherPastIllnessText, otherFamilyHistoryText;
    CheckBox pastillness, cb_famHist_others;
    TextView errorPassIllness, errorFamilyHistory;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PATIENT_UID = "patientUid";
    private static final String MEDHISTORY_UID = "medHistoryUid";
    private static final String VITALSIGNS_UID = "vitalSignsUid";

    // TODO: Rename and change types of parameters
    String patientUid;
    String vitalsignsUid;
    String medicalHistoryUid;
    String loggedInUserId;
    ReportsRepository _reportsRepository = new ReportsRepository();
    private Button nextButton;

    public AddMedicalHistory() {
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
    public static AddMedicalHistory newInstance(String patientUid, String medicalHistoryId, String vitalSignsId) {
        AddMedicalHistory fragment = new AddMedicalHistory();
        Bundle args = new Bundle();
        args.putString(PATIENT_UID, patientUid);
        args.putString(MEDHISTORY_UID, medicalHistoryId);
        args.putString(VITALSIGNS_UID, vitalSignsId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patientUid = getArguments().getString(PATIENT_UID);
            medicalHistoryUid = getArguments().getString(MEDHISTORY_UID);
            vitalsignsUid = getArguments().getString(VITALSIGNS_UID);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(SessionConstants.SessionPreferenceKey, Context.MODE_PRIVATE);
        loggedInUserId = sharedPref.getString(SessionConstants.LoggedInUid, "");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_patient_record_add_medical_history, container, false);
        nextButton = rootView.findViewById(R.id.nxtButton);

        pastIllnessProcessor = new CheckboxStringProcessor(
                rootView.findViewById(R.id.otherPastIllness),
                rootView.findViewById(R.id.otherPastIllnessText),
                rootView.findViewById(R.id.tuborculosis),
                rootView.findViewById(R.id.hypertension),
                rootView.findViewById(R.id.heartDiseases),
                rootView.findViewById(R.id.nervousBreakdown),
                rootView.findViewById(R.id.pelpticUlcer),
                rootView.findViewById(R.id.kidneyDiseases),
                rootView.findViewById(R.id.bronchialAsthma),
                rootView.findViewById(R.id.hernia),
                rootView.findViewById(R.id.seizuresEpilepsy),
                rootView.findViewById(R.id.venerealDiseases),
                rootView.findViewById(R.id.allergicReaction),
                rootView.findViewById(R.id.insomnia)
        );
        pastillness = rootView.findViewById(R.id.otherPastIllness);
        cb_famHist_others = rootView.findViewById(R.id.cb_famHist_others);

        otherPastIllnessText = rootView.findViewById(R.id.otherPastIllnessText);
        otherFamilyHistoryText = rootView.findViewById(R.id.otherFamilyHistoryText);

        errorPassIllness = rootView.findViewById(R.id.errorPassIllness);
        errorFamilyHistory = rootView.findViewById(R.id.errorFamilyHistory);

        errorPassIllness.setVisibility(rootView.INVISIBLE);
        errorFamilyHistory.setVisibility(rootView.INVISIBLE);

        familyHistoryProcessor = new CheckboxStringProcessor(
                rootView.findViewById(R.id.cb_famHist_others),
                rootView.findViewById(R.id.otherFamilyHistoryText),
                rootView.findViewById(R.id.cb_famHist_diabetes),
                rootView.findViewById(R.id.cb_famHist_hyper),
                rootView.findViewById(R.id.cb_famHist_mentalHealthDisorder),
                rootView.findViewById(R.id.cb_famHist_asthma),
                rootView.findViewById(R.id.cb_famHist_bleedingDisorder)
        );

        editPrevHospitalization = rootView.findViewById(R.id.input_previous_hospitalization);

        obgyneHistoryProcessor = new EditTextStringProcessor(
            new EditTextName("Menarche",rootView.findViewById(R.id.input_menarche)),
            new EditTextName("LMP",rootView.findViewById(R.id.input_lmp)),
            new EditTextName("Gravida",rootView.findViewById(R.id.input_gravida)),
            new EditTextName("Para",rootView.findViewById(R.id.input_para)),
            new EditTextName("Abortion",rootView.findViewById(R.id.input_abortion)),
            new EditTextName("Menopause",rootView.findViewById(R.id.input_menopause)),
            new EditTextName("PAP Smear",rootView.findViewById(R.id.input_papSmear))
        );

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((pastillness.isChecked() && !otherPastIllnessText.getText().toString().isEmpty())
                && (cb_famHist_others.isChecked() && !otherFamilyHistoryText.getText().toString().isEmpty()))
                || (!pastillness.isChecked() && (cb_famHist_others.isChecked() && !otherFamilyHistoryText.getText().toString().isEmpty()))
                || (!cb_famHist_others.isChecked() && (pastillness.isChecked() && !otherPastIllnessText.getText().toString().isEmpty()))
                || (!pastillness.isChecked() && !cb_famHist_others.isChecked())
                ) {
                    errorPassIllness.setVisibility(rootView.INVISIBLE);
                    errorFamilyHistory.setVisibility(rootView.INVISIBLE);
                    createMedicalHistory(patientUid);
                }
                else {
                    if(pastillness.isChecked() && otherPastIllnessText.getText().toString().isEmpty()) errorPassIllness.setVisibility(rootView.VISIBLE);
                    else errorPassIllness.setVisibility(rootView.INVISIBLE);
                    if(cb_famHist_others.isChecked() && otherFamilyHistoryText.getText().toString().isEmpty()) errorFamilyHistory.setVisibility(rootView.VISIBLE);
                    else errorFamilyHistory.setVisibility(rootView.INVISIBLE);
                }
            }
        });
        return rootView;
    }

    private void createMedicalHistory(String userId){
        MedicalHistoryDto medicalHistoryDto= new MedicalHistoryDto();

        String pastIllness = pastIllnessProcessor.getString();
        medicalHistoryDto.setPastIllness(pastIllness);

        //Previous Hospitalization
        medicalHistoryDto.setPrevOperation(String.valueOf(editPrevHospitalization.getText()).trim());

        String familyHistory = familyHistoryProcessor.getString();
        medicalHistoryDto.setFamilyHist(familyHistory);

        //ObGyne History
        String obgyneHistory = obgyneHistoryProcessor.getString();
        medicalHistoryDto.setObgyneHist(obgyneHistory);

        medicalHistoryDto.setUid(medicalHistoryUid);

        MedicalHistoryRepository medicalHistoryRepo = new MedicalHistoryRepository();
        ButtonManager.disableButton(nextButton);

        medicalHistoryRepo.updateMedicalHistory(medicalHistoryDto, new MedicalHistoryRepository.AddUpdateCallback() {
            @Override
            public void onSuccess(String medicalHistoryId) {
                // Construct the message to include in the report
                String message = "\n" + "\n" +
                        "Past Illness: " + pastIllness + "\n" +
                        "Previous Hospitalization: " + medicalHistoryDto.getPrevOperation() + "\n" +
                        "Family History: " + familyHistory + "\n" +
                        "ObGyne History: " + obgyneHistory;
                _reportsRepository.addHealthProfPatientMedHistoryReport(loggedInUserId, userId, message, new ReportsRepository.ReportCallback() {
                    @Override
                    public void onReportAddedSuccessfully() {
                        Toast.makeText(requireContext(), ToastConstants.ADDED_MEDICAL_HISTORY, Toast.LENGTH_SHORT).show();
                        showMedication();
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

    private void showMedication() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        // TODO: Create View Record Fragment for Patient then remove // of the nextline code to use it
        transaction.replace(R.id.frame_layout, AddMedication.newInstance(patientUid, vitalsignsUid));
        transaction.commit();
    }
}