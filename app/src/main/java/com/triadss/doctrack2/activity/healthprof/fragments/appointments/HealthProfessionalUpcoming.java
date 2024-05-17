package com.triadss.doctrack2.activity.healthprof.fragments.appointments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.triadss.doctrack2.R;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.triadss.doctrack2.activity.healthprof.adapters.HealthProfessionalAppointmentUpcomingAdapter;
import com.triadss.doctrack2.config.constants.SessionConstants;
import com.triadss.doctrack2.config.constants.ToastConstants;
import com.triadss.doctrack2.contracts.IListView;
import com.triadss.doctrack2.dto.AppointmentDto;
import com.triadss.doctrack2.helper.ButtonManager;
import com.triadss.doctrack2.repoositories.AppointmentRepository;
import com.triadss.doctrack2.repoositories.NotificationRepository;
import com.triadss.doctrack2.repoositories.ReportsRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HealthProfessionalUpcoming#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthProfessionalUpcoming extends Fragment implements IListView {
    private ReportsRepository reportsRepository = new ReportsRepository();

    String loggedInUserId;

    TextInputEditText searchAppointment;
    NotificationRepository notificationRepository = new NotificationRepository();
    public HealthProfessionalUpcoming() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HealthProfessionalUpcoming.
     */
    // TODO: Rename and change types and number of parameters
    public static HealthProfessionalUpcoming newInstance() {
        HealthProfessionalUpcoming fragment = new HealthProfessionalUpcoming();
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
        SharedPreferences sharedPref = getContext().getSharedPreferences(SessionConstants.SessionPreferenceKey,
                Context.MODE_PRIVATE);
        loggedInUserId = sharedPref.getString(SessionConstants.LoggedInUid, "");

        appointmentRepository = new AppointmentRepository();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_health_professional_upcoming, container, false);
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

        appointmentRepository.getOngoingAppointmentsFiltered(filter, new AppointmentRepository.AppointmentFetchCallback() {
            @Override
            public void onSuccess(List<AppointmentDto> appointments) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(linearLayoutManager);

                HealthProfessionalAppointmentUpcomingAdapter adapter = new HealthProfessionalAppointmentUpcomingAdapter(getContext(), (ArrayList)appointments, 
                    new HealthProfessionalAppointmentUpcomingAdapter.AppointmentCallback()
                    {
                        @Override
                        public void onAccept(String appointmentUid) {
                            appointmentRepository.acceptAppointment(appointmentUid, currentUser.getUid(), new AppointmentRepository.AppointmentAddCallback() {
                                @Override
                                public void onSuccess(String appointmentId) {
                                    notificationRepository.NotifyAcceptedAppointment(appointmentId);    

                                    reportsRepository.addHealthProfAcceptedAppointmentReport(loggedInUserId, appointmentId, new ReportsRepository.ReportCallback() {
                                        @Override
                                        public void onReportAddedSuccessfully() {
                                            Toast.makeText(requireContext(), ToastConstants.ACCEPTED, Toast.LENGTH_SHORT).show();

                                            ReloadList();
                                        }

                                        @Override
                                        public void onReportFailed(String errorMessage) {
                                            System.out.println();
                                        }
                                    });
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    System.out.println();
                                }
                            });
                        }

                        @Override
                        public void onReject(String appointmentUid) {
                            reportsRepository.addHealthProfRejectedAppointmentReport(loggedInUserId, appointmentUid, new ReportsRepository.ReportCallback() {
                                @Override
                                public void onReportAddedSuccessfully() {
                                    notificationRepository.NotifyRejectedAppointment(appointmentUid, new NotificationRepository.NotificationPushedCallback() {
                                        @Override
                                        public void onNotificationDone() {
                                            appointmentRepository.rejectAppointment(appointmentUid, new AppointmentRepository.AppointmentAddCallback() {
                                                @Override
                                                public void onSuccess(String appointmentId) {
                                                    Toast.makeText(requireContext(), ToastConstants.REJECTED, Toast.LENGTH_SHORT).show();

                                                    ReloadList();
                                                }

                                                @Override
                                                public void onError(String errorMessage) {
                                                    System.out.println();
                                                }
                                            });
                                        }
                                    });
                                }

                                @Override
                                public void onReportFailed(String errorMessage) {
                                    System.out.println();
                                }
                            });
                        }

                        @Override
                        public void onRejectBulk(List<AppointmentDto> rejectedAppointments) {
                            for(AppointmentDto appointment: rejectedAppointments) {
                                reportsRepository.addHealthProfRejectedAppointmentReport(loggedInUserId, appointment.getUid(), new ReportsRepository.ReportCallback() {
                                    @Override
                                    public void onReportAddedSuccessfully() {
                                        notificationRepository.NotifyRejectedAppointment(appointment.getUid(), new NotificationRepository.NotificationPushedCallback() {
                                            @Override
                                            public void onNotificationDone() {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onReportFailed(String errorMessage) {
                                        System.out.println();
                                    }
                                });
                            }
                        }
                    });

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }

    private void rejectAppointment(String appointmentUid)
    {
        reportsRepository.addHealthProfRejectedAppointmentReport(loggedInUserId, appointmentUid, new ReportsRepository.ReportCallback() {
            @Override
            public void onReportAddedSuccessfully() {
                notificationRepository.NotifyRejectedAppointment(appointmentUid, new NotificationRepository.NotificationPushedCallback() {
                    @Override
                    public void onNotificationDone() {
                        appointmentRepository.rejectAppointment(appointmentUid, new AppointmentRepository.AppointmentAddCallback() {
                            @Override
                            public void onSuccess(String appointmentId) {
                                ReloadList();
                            }

                            @Override
                            public void onError(String errorMessage) {
                                System.out.println();
                            }
                        });
                    }
                });
            }

            @Override
            public void onReportFailed(String errorMessage) {
                System.out.println();
            }
        });
    }
}