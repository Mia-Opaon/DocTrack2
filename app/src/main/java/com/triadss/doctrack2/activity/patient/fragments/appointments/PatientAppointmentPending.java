package com.triadss.doctrack2.activity.patient.fragments.appointments;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.patient.adapters.PatientAppointmentPendingAdapter;
import com.triadss.doctrack2.contracts.IListView;
import com.triadss.doctrack2.dto.AppointmentDto;
import com.triadss.doctrack2.repoositories.AppointmentRepository;
import com.triadss.doctrack2.utils.AppointmentFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientAppointmentPending extends Fragment implements IListView {
    RecyclerView recyclerView;
    private AppointmentRepository appointmentRepository;
    TextInputEditText searchAppointment;
    private Map<String, Timestamp> holidayList = Collections.synchronizedMap(new HashMap<>());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //storage of patient appointment on fire store
        appointmentRepository = new AppointmentRepository();

        AppointmentFunctions.FetchHolidays(holidayList);

        View rootView = inflater.inflate(R.layout.fragment_patient_appointment_pending, container, false);
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
        CallSomething();
        return rootView;
    }

   public void ReloadList() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        String filter = searchAppointment.getText().toString();

       appointmentRepository.getAllPatientPendingAppointmentsFiltered(currentUser.getUid(), filter, new AppointmentRepository.AppointmentPatientPendingFetchCallback() {
               @Override
               public void onSuccess(List<AppointmentDto> appointments) {
                   LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                   recyclerView.setLayoutManager(linearLayoutManager);

                   PatientAppointmentPendingAdapter adapter = new PatientAppointmentPendingAdapter(getContext(), (ArrayList) appointments, holidayList);

                   recyclerView.setAdapter(adapter);
               }
               @Override
               public void onError(String errorMessage) {

               }
       });
    }

    public void CallSomething(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            int carl = 2;
            boolean stop = false;
            public void run() {
                //When you are not in fragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);
                boolean isCurrentlyAtPatientAppointmentPending = currentFragment instanceof PatientAppointmentFragment;
                if(!isCurrentlyAtPatientAppointmentPending) {
                    stop = true;
                }

                SharedPreferences sh = getActivity().getApplicationContext().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);

                int a = sh.getInt("PatientPending",9);
                carl = a;
                if(carl == 10){
                    SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                    myEdit.putInt("PatientPending", Integer.parseInt("0"));

                    myEdit.apply();
                    ReloadList();

                }
                if (!stop) handler.postDelayed(this,1000);
            }
        }, 1000);
    }
}