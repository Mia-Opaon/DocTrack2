package com.triadss.doctrack2.activity.healthprof.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.LoginActivity;
import com.triadss.doctrack2.activity.healthprof.adapters.HealthProfHomeAppointmentAdapter;
import com.triadss.doctrack2.activity.patient.adapters.PatientHomeAppointmentAdapter;
import com.triadss.doctrack2.config.constants.DocTrackConstant;
import com.triadss.doctrack2.config.constants.SessionConstants;
import com.triadss.doctrack2.dto.AppointmentDto;
import com.triadss.doctrack2.repoositories.AppointmentRepository;
import com.triadss.doctrack2.repoositories.ReportsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HealthProfHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthProfHomeFragment extends Fragment {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();

    AppointmentRepository appointmentRepository = new AppointmentRepository();
    ReportsRepository reportsRepository = new ReportsRepository();
    RecyclerView recyclerView;
    TextView pendingAppointmentCountVal;
    String loggedInUserId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HealthProfHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HealthProfHomeFragment newInstance(String param1, String param2) {
        HealthProfHomeFragment fragment = new HealthProfHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(SessionConstants.SessionPreferenceKey, Context.MODE_PRIVATE);
        loggedInUserId = sharedPref.getString(SessionConstants.LoggedInUid, "");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_health_prof_home_page, container, false);
        recyclerView= rootView.findViewById(R.id.recycler_view_pending_appointments);
        pendingAppointmentCountVal = rootView.findViewById(R.id.pendingAppointmentCountVal);

        ImageView menuImageView = rootView.findViewById(R.id.menu);
        menuImageView.setOnClickListener(v -> showPopupMenu(v));

        ReloadList();

        return rootView;
    }

    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_item1) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
                return true;
            } else {
                return false;
            }
        });
        popupMenu.inflate(R.menu.healthprof_popup_menu);
        popupMenu.show();
    }

    public void ReloadList() {
        appointmentRepository.getPendingAppointmentsForHealthProfRecent(loggedInUserId,  DocTrackConstant.HOME_PAGE_APPOINTMENT_COUNT, new AppointmentRepository.AppointmentFetchCallback() {
            @Override
            public void onSuccess(List<AppointmentDto> appointments) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                pendingAppointmentCountVal.setText(String.valueOf(appointments.size()));

                HealthProfHomeAppointmentAdapter adapter = new HealthProfHomeAppointmentAdapter(getContext(),
                        (ArrayList)appointments.stream().limit(DocTrackConstant.HOME_PAGE_APPOINTMENT_COUNT)
                                .collect(Collectors.toList()));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }
}