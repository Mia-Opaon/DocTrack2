package com.triadss.doctrack2.activity.healthprof.fragments.appointments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.triadss.doctrack2.R;

import com.triadss.doctrack2.activity.healthprof.adapters.HealthProfessionalAppointmentPendingAdapter;
import com.triadss.doctrack2.activity.patient.fragments.appointments.PatientAppointmentPending;
import com.triadss.doctrack2.config.constants.SessionConstants;
import com.triadss.doctrack2.contracts.IListView;
import com.triadss.doctrack2.dto.AppointmentDto;
import com.triadss.doctrack2.dto.DateTimeDto;
import com.triadss.doctrack2.repoositories.AppointmentRepository;
import com.triadss.doctrack2.repoositories.NotificationRepository;
import com.triadss.doctrack2.repoositories.ReportsRepository;
import com.triadss.doctrack2.utils.AppointmentFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HealthProfessionalPending#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthProfessionalPending extends Fragment implements IListView {
    private ReportsRepository reportsRepository = new ReportsRepository();
    private NotificationRepository notifcationRepository = new NotificationRepository();

    String loggedInUserId;
    private NotificationRepository notificationRepository = new NotificationRepository();
    private TextInputEditText searchAppointment;
    private Map<String, Timestamp> holidayList = Collections.synchronizedMap(new HashMap<>());

    public HealthProfessionalPending() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HealthProfessionalPending.
     */
    // TODO: Rename and change types and number of parameters
    public static HealthProfessionalPending newInstance() {
        HealthProfessionalPending fragment = new HealthProfessionalPending();
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
    private Dialog currentDialog = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(SessionConstants.SessionPreferenceKey, Context.MODE_PRIVATE);
        loggedInUserId = sharedPref.getString(SessionConstants.LoggedInUid, "");

        // Inflate the layout for this fragment
        appointmentRepository = new AppointmentRepository();
        View rootView = inflater.inflate(R.layout.fragment_health_professional_pending, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        AppointmentFunctions.FetchHolidays(holidayList);

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
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        String filter = searchAppointment.getText().toString();

        appointmentRepository.getPendingAppointmentsForHealthProfFiltered(currentUser.getUid(), filter, new AppointmentRepository.AppointmentFetchCallback() {
            @Override
            public void onSuccess(List<AppointmentDto> appointments) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                if(currentDialog != null)
                {
                    currentDialog.dismiss();
                    currentDialog = null;
                }

                HealthProfessionalAppointmentPendingAdapter adapter = new HealthProfessionalAppointmentPendingAdapter(getContext(), (ArrayList)appointments, holidayList);
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
                boolean isCurrentlyAtHealthProfAppointment = currentFragment instanceof HealthProfessionalAppointmentFragment;

                if(!isCurrentlyAtHealthProfAppointment) {
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