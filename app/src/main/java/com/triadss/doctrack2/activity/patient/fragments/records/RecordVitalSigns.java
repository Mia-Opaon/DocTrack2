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
import com.triadss.doctrack2.dto.VitalSignsDto;
import com.triadss.doctrack2.repoositories.VitalSignsRepository;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecordVitalSigns#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordVitalSigns extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecordVitalSigns() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordVitalSigns.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordVitalSigns newInstance(String param1, String param2) {
        RecordVitalSigns fragment = new RecordVitalSigns();
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
        VitalSignsRepository vitalSignsRepository = new VitalSignsRepository();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String patientUid = currentUser.getUid();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_record_vital_signs, container, false);

        TextView bloodPressure = rootView.findViewById(R.id.value_bloodPressure);
        TextView temperature = rootView.findViewById(R.id.value_temperature);
        TextView spo2 = rootView.findViewById(R.id.value_SpO2);
        TextView pulseRate = rootView.findViewById(R.id.value_pulseRate);
        TextView weight = rootView.findViewById(R.id.value_weight);
        TextView height = rootView.findViewById(R.id.value_height);
        TextView BMI = rootView.findViewById(R.id.value_BMI);

        vitalSignsRepository.getVitalSignOfPatient(patientUid, new VitalSignsRepository.FetchCallback() {
            @Override
            public void onSuccess(VitalSignsDto vitalSigns) {
                bloodPressure.setText(vitalSigns.getBloodPressure());
                temperature.setText(String.valueOf(vitalSigns.getTemperature()));
                spo2.setText(String.valueOf(vitalSigns.getOxygenLevel()));
                pulseRate.setText(String.valueOf(vitalSigns.getPulseRate()));
                weight.setText(String.valueOf(vitalSigns.getWeight()));
                height.setText(String.valueOf(vitalSigns.getHeight()));
                BMI.setText(String.valueOf(vitalSigns.getBMI()));
            }

            @Override
            public void onError(String message) {

            }
        });

        return rootView;
    }
}