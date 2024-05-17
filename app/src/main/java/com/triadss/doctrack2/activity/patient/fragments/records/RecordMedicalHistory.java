package com.triadss.doctrack2.activity.patient.fragments.records;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.dto.MedicalHistoryDto;
import com.triadss.doctrack2.repoositories.MedicalHistoryRepository;
import com.triadss.doctrack2.utils.CheckboxStringProcessor;
import com.triadss.doctrack2.utils.EditTextStringProcessor;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecordMedicalHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordMedicalHistory extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecordMedicalHistory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordMedicalHistory.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordMedicalHistory newInstance(String param1, String param2) {
        RecordMedicalHistory fragment = new RecordMedicalHistory();
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
        MedicalHistoryRepository medicalHistoryRepository = new MedicalHistoryRepository();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String patientUid = currentUser.getUid();
        
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_record_medical_history, container, false);
        
        TextView patientPastIllness = rootView.findViewById(R.id.value_pastIllness);
        TextView patientPrevHospitalization = rootView.findViewById(R.id.value_prevHospitalization);
        TextView patientFamilyHistory = rootView.findViewById(R.id.value_familyHistory);
        TextView patientOBGyneHistory = rootView.findViewById(R.id.value_OBGyneHistory);

        medicalHistoryRepository.getMedicalHistoryOfPatient(patientUid, new MedicalHistoryRepository.FetchCallback() {
            @Override
            public void onSuccess(MedicalHistoryDto medicalHistory) {
                String SplitDelimiter = "\\|";

                List<String> separatedPastIllness = CheckboxStringProcessor.StringToSeparatedValues(SplitDelimiter, medicalHistory.getPastIllness());
                String pastIllnessString = CheckboxStringProcessor.SeparatedValuesToString(", ", separatedPastIllness);
                patientPastIllness.setText(pastIllnessString);
                patientPrevHospitalization.setText(medicalHistory.getPrevOperation());

                List<String> separatedMedicalHistory = CheckboxStringProcessor.StringToSeparatedValues(SplitDelimiter, medicalHistory.getFamilyHist());
                String medicalHistoryString = CheckboxStringProcessor.SeparatedValuesToString(", ", separatedMedicalHistory);
                patientFamilyHistory.setText(medicalHistoryString);

                List<String> separatedOBGyneHistory = EditTextStringProcessor.StringToSeparatedValues(SplitDelimiter, medicalHistory.getObgyneHist());
                Map<String, String> mappedOBGyneHistory = EditTextStringProcessor.SeparatedValuesToMap(":", separatedOBGyneHistory);
                List<String> processedOBGyneHistory = EditTextStringProcessor.MapToSeparatedValues(": ", mappedOBGyneHistory);
                String obgyneString = EditTextStringProcessor.SeparatedValuesToString("\n", processedOBGyneHistory);
                patientOBGyneHistory.setText(obgyneString);
            }

            @Override
            public void onError(String message) {

            }
        });

        return rootView;
    }
}