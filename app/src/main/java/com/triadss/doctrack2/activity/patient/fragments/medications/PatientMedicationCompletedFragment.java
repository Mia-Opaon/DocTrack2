package com.triadss.doctrack2.activity.patient.fragments.medications;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.patient.adapters.PatientMedicationCompletedAdapter;
import com.triadss.doctrack2.config.constants.MedicationTypeConstants;
import com.triadss.doctrack2.contracts.IListView;
import com.triadss.doctrack2.dto.MedicationDto;
import com.triadss.doctrack2.repoositories.MedicationRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatientMedicationCompletedFragment#newInstance} factory method
 * to
 * create an instance of this fragment.
 */
public class PatientMedicationCompletedFragment extends Fragment implements IListView {
    private static final String TAG = "PatientMedicationCompletedFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<MedicationDto> Time = new ArrayList<MedicationDto>();
    MedicationRepository medicationRepository = new MedicationRepository();
    RecyclerView recyclerView;

    public PatientMedicationCompletedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PatientMedicationCompletedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PatientMedicationCompletedFragment newInstance(String param1, String param2) {
        PatientMedicationCompletedFragment fragment = new PatientMedicationCompletedFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_patient_medication_completed, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        ReloadList();
        return rootView;
    }

    public void ReloadList() {
        medicationRepository.getAllMedications(MedicationTypeConstants.COMPLETED,
                new MedicationRepository.MedicationFetchCallback() {
                    @Override
                    public void onSuccess(List<MedicationDto> medications) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        PatientMedicationCompletedAdapter adapter = new PatientMedicationCompletedAdapter(getContext(),
                                (ArrayList<MedicationDto>) medications);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        System.out.println();
                    }
                });
    }
}