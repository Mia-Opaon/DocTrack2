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
import com.triadss.doctrack2.activity.healthprof.fragments.medications.UpdateMedications;
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
 * Use the {@link UpdateMedicalHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateMedicalHistory extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PATIENT_UID = "patientUid";

    // TODO: Rename and change types of parameters
    private String patientUid;

    EditText editPrevHospitalization;

    CheckboxStringProcessor pastIllnessProcessor, familyHistoryProcessor;
    EditTextStringProcessor obgyneHistoryProcessor;
    MedicalHistoryRepository medicalHistoryRepo = new MedicalHistoryRepository();
    EditText otherPastIllnessText, otherFamilyHistoryText;
    CheckBox pastillness, cb_famHist_others;
    TextView errorPassIllness, errorFamilyHistory;
    String loggedInUserId;
    ReportsRepository _reportsRepository = new ReportsRepository();
    Button nextButton;

    public UpdateMedicalHistory() {
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
    public static UpdateMedicalHistory newInstance(String patientUid) {
        UpdateMedicalHistory fragment = new UpdateMedicalHistory();
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
        View rootView = inflater.inflate(R.layout.fragment_update_medical_history, container, false);

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
        editPrevHospitalization = rootView.findViewById(R.id.input_previous_hospitalization);

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

        obgyneHistoryProcessor = new EditTextStringProcessor(
                new EditTextName("Menarche",rootView.findViewById(R.id.input_menarche)),
                new EditTextName("LMP",rootView.findViewById(R.id.input_lmp)),
                new EditTextName("Gravida",rootView.findViewById(R.id.input_gravida)),
                new EditTextName("Para",rootView.findViewById(R.id.input_para)),
                new EditTextName("Abortion",rootView.findViewById(R.id.input_abortion)),
                new EditTextName("Menopause",rootView.findViewById(R.id.input_menopause)),
                new EditTextName("PAP Smear",rootView.findViewById(R.id.input_papSmear))
        );

        nextButton = rootView.findViewById(R.id.nxtButton);
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
                    updateMedicalHistory();
                }
                else {
                    if(pastillness.isChecked() && otherPastIllnessText.getText().toString().isEmpty()) errorPassIllness.setVisibility(rootView.VISIBLE);
                    else errorPassIllness.setVisibility(rootView.INVISIBLE);
                    if(cb_famHist_others.isChecked() && otherFamilyHistoryText.getText().toString().isEmpty()) errorFamilyHistory.setVisibility(rootView.VISIBLE);
                    else errorFamilyHistory.setVisibility(rootView.INVISIBLE);
                }
            }
        });

        populateData();
        return rootView;
    }

    private void populateData()
    {
        medicalHistoryRepo.getMedicalHistoryOfPatient(patientUid, new MedicalHistoryRepository.FetchCallback() {
            @Override
            public void onSuccess(MedicalHistoryDto medHistory) {
                pastIllnessProcessor.PopulateFromString(medHistory.getPastIllness());
                familyHistoryProcessor.PopulateFromString(medHistory.getFamilyHist());
                editPrevHospitalization.setText(medHistory.getPrevOperation());
                obgyneHistoryProcessor.PopulateFromString(medHistory.getObgyneHist());
            }

            @Override
            public void onError(String errorMessage) {
                
            }
        });
    }

    private MedicalHistoryDto extractDto() {
        MedicalHistoryDto medicalHistoryDto= new MedicalHistoryDto();

        //Past Illness
        String pastIllness = pastIllnessProcessor.getString();
        medicalHistoryDto.setPastIllness(pastIllness);

        //Previous Hospitalization
        medicalHistoryDto.setPrevOperation(String.valueOf(editPrevHospitalization.getText()).trim());

        //Family History
        String familyHistory = familyHistoryProcessor.getString();
        medicalHistoryDto.setFamilyHist(familyHistory);

        String obgyneHistory = obgyneHistoryProcessor.getString();
        medicalHistoryDto.setObgyneHist(obgyneHistory);

        medicalHistoryDto.setPatientId(patientUid);

        return medicalHistoryDto;
    }

    private void updateMedicalHistory()
    {
        MedicalHistoryDto dto = extractDto();
        ButtonManager.disableButton(nextButton);
        medicalHistoryRepo.getMedicalHistoryIdOfUser(patientUid, new MedicalHistoryRepository.StringFetchCallback()
        {
            @Override
            public void onSuccess(String medHistoryId) {
                dto.setUid(medHistoryId);
                medicalHistoryRepo.updateMedicalHistory(dto, new MedicalHistoryRepository.AddUpdateCallback() {
                    @Override
                    public void onSuccess(String medicalHistoryId) {
                        // Construct the message to include in the report
                        String message = "\n" + "\n" +
                                "Past Illness: " + dto.getPastIllness() + "\n" +
                                        "Previous Hospitalization: " + dto.getPrevOperation() + "\n" +
                                        "Family History: " + dto.getFamilyHist() + "\n" +
                                        "ObGyne History: " + dto.getObgyneHist();
                        _reportsRepository.addHealthProfUpdatePatientMedHistoryReport(loggedInUserId, patientUid, message, new ReportsRepository.ReportCallback() {
                            @Override
                            public void onReportAddedSuccessfully() {
                                Toast.makeText(requireContext(), ToastConstants.UPDATED_MEDICAL_HISTORY, Toast.LENGTH_SHORT).show();

                                showMedications();
                            }

                            @Override
                            public void onReportFailed(String errorMessage) {
                                ButtonManager.enableButton(nextButton);
                            }
                        });
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        ButtonManager.enableButton(nextButton);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                ButtonManager.enableButton(nextButton);

            }
        });
    }

    private void showMedications() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        // TODO: Create View Record Fragment for Patient then remove // of the nextline code to use it
        transaction.replace(R.id.frame_layout, UpdateMedications.newInstance(patientUid));
        transaction.commit();
    }
}