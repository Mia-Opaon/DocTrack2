package com.triadss.doctrack2.activity.healthprof.fragments.appointments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.triadss.doctrack2.R;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.triadss.doctrack2.activity.healthprof.adapters.HealthProfessionalAppointmentStatusAdapter;
import com.triadss.doctrack2.contracts.IListView;
import com.triadss.doctrack2.dto.AppointmentDto;
import com.triadss.doctrack2.repoositories.AppointmentRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HealthProfessionalStatus#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthProfessionalStatus extends Fragment implements IListView {

    TextInputEditText searchAppointment;

    public HealthProfessionalStatus() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HealthProfessionalStatus.
     */
    // TODO: Rename and change types and number of parameters
    public static HealthProfessionalStatus newInstance() {
        HealthProfessionalStatus fragment = new HealthProfessionalStatus();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    RecyclerView recyclerView;
    private AppointmentRepository appointmentRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        appointmentRepository = new AppointmentRepository();
        View rootView = inflater.inflate(R.layout.fragment_health_professional_status, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        searchAppointment = rootView.findViewById(R.id.searchAppointment);
        TextWatcher inputTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                ReloadList();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        searchAppointment.addTextChangedListener(inputTextWatcher);

        ReloadList();
        return rootView;
    }

    public void ReloadList() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        String filter = searchAppointment.getText().toString();

        appointmentRepository.getHealthProfStatusAppointmentsFiltered(currentUser.getUid(), filter, new AppointmentRepository.AppointmentFetchCallback() {
            @Override
            public void onSuccess(List<AppointmentDto> appointments) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(linearLayoutManager);

                HealthProfessionalAppointmentStatusAdapter adapter = new HealthProfessionalAppointmentStatusAdapter(getContext(), (ArrayList)appointments);

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }
}