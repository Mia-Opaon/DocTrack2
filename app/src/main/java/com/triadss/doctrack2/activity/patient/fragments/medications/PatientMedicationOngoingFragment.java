package com.triadss.doctrack2.activity.patient.fragments.medications;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.patient.PatientHome;
import com.triadss.doctrack2.activity.patient.adapters.PatientMedicationOngoingAdapter;
import com.triadss.doctrack2.config.constants.MedicationTypeConstants;
import com.triadss.doctrack2.contracts.IListView;
import com.triadss.doctrack2.dto.MedicationDto;
import com.triadss.doctrack2.repoositories.MedicationRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatientMedicationOngoingFragment#newInstance} factory method
 * to
 * create an instance of this fragment.
 */
public class PatientMedicationOngoingFragment extends Fragment implements IListView {
    private static final String TAG = "PatientMedicationOngoingFragment";

    ArrayList<MedicationDto> Time = new ArrayList<MedicationDto>();
    RecyclerView recyclerView;
    private MedicationRepository medicationRepository = new MedicationRepository();;
    private List<MedicationDto> ongoingMedications;

    public PatientMedicationOngoingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PatientMedicationOngoingCompleted.
     */
    // TODO: Rename and change types and number of parameters
    public static PatientMedicationOngoingFragment newInstance() {
        PatientMedicationOngoingFragment fragment = new PatientMedicationOngoingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_patient_medication_ongoing, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        ReloadList();
        return rootView;
    }

    public void ReloadList() {
        PatientHome homeActivity = (PatientHome) getContext();
        homeActivity.setupNotifications();

        medicationRepository.getAllMedications(MedicationTypeConstants.ONGOING,
                new MedicationRepository.MedicationFetchCallback() {
                    @Override
                    public void onSuccess(List<MedicationDto> medications) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        PatientMedicationOngoingAdapter adapter = new PatientMedicationOngoingAdapter(getContext(),
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