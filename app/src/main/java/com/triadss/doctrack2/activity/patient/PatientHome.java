package com.triadss.doctrack2.activity.patient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.triadss.doctrack2.activity.LoginActivity;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.core.DeviceFragment;
import com.triadss.doctrack2.activity.patient.fragments.appointments.PatientAppointmentFragment;
import com.triadss.doctrack2.activity.patient.fragments.PatientHomeFragment;
import com.triadss.doctrack2.activity.patient.fragments.medications.PatientMedicationFragment;
import com.triadss.doctrack2.activity.patient.fragments.records.PatientReportFragment;
import com.triadss.doctrack2.activity.patient.fragments.records.RecordFragment;
import com.triadss.doctrack2.config.constants.DocTrackConstant;
import com.triadss.doctrack2.config.constants.NotificationConstants;
import com.triadss.doctrack2.config.constants.SessionConstants;
import com.triadss.doctrack2.databinding.ActivityPatientHomeBinding;
import com.triadss.doctrack2.dto.DateTimeDto;
import com.triadss.doctrack2.dto.MedicationDto;
import com.triadss.doctrack2.notification.NotificationMedicationScheduleWorker;
import com.triadss.doctrack2.repoositories.MedicationRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PatientHome extends AppCompatActivity {

    FirebaseAuth auth;

    Button button;

    FirebaseUser user;
    private String loggedInUserId;
    MedicationRepository medicationRepository = new MedicationRepository();

    ActivityPatientHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);
        replaceFragment(new PatientHomeFragment());

        SharedPreferences sharedPref = getSharedPreferences(SessionConstants.SessionPreferenceKey,
                Context.MODE_PRIVATE);
        loggedInUserId = sharedPref.getString(SessionConstants.LoggedInUid, "");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        button = findViewById(R.id.logout_btn);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        button.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        binding = ActivityPatientHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.record_menu) {
                replaceFragment(new RecordFragment());
            }
            else if(item.getItemId() == R.id.device_menu)
            {
                replaceFragment(new DeviceFragment());
            }
            else if (item.getItemId() == R.id.appointment_menu) {
                replaceFragment(new PatientAppointmentFragment());
            } else if (item.getItemId() == R.id.medication_menu) {
                replaceFragment(new PatientMedicationFragment());
            } else if (item.getItemId() == R.id.report_menu) {
                replaceFragment(new PatientReportFragment());
            }
            return true;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Back is pressed... Finishing the activity
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);
                boolean isCurrentlyAtHomepage = currentFragment instanceof PatientHomeFragment;
                if(!isCurrentlyAtHomepage) {
                    fragmentManager.popBackStack();
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);
        boolean isCurrentlyAtHomepage = currentFragment instanceof PatientHomeFragment;

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        if(isCurrentlyAtHomepage) {
            fragmentTransaction.addToBackStack("toHome");
        }
        fragmentTransaction.commit();
    }

    public void setupNotifications() {
        medicationRepository.getUncompletedMedications(loggedInUserId, new MedicationRepository.MedicationFetchCallback() {
            @Override
            public void onSuccess(List<MedicationDto> medications) {
                List<OneTimeWorkRequest> requests = new ArrayList<OneTimeWorkRequest>();
                int delayForPastMedications = 1;
                HashMap<Long, Boolean> delays = new HashMap<Long, Boolean>();

                for(MedicationDto dto : medications) {
                    Log.e("TEST", "Running Medication Work for " + loggedInUserId +
                            " for " + dto.getMedicine() + " at " + DateTimeDto.ToDateTimeDto(dto.getTimestamp()).ToString());

                    long distance = DateTimeDto.GetTimestampDiffInSeconds(dto.getTimestamp());

                    if(distance < DocTrackConstant.MEDICATION_NOTIFICATION_NEGATIVE_SECOND_TRESHOLD)
                    {
                        continue;
                    }

                    distance = distance < 0 ? delayForPastMedications++: distance;

                    //Maintance Uniqueness of distance
                    if(!delays.containsKey(distance)) {
                        delays.put(distance, true);
                    } else {
                        long nextDistance = distance + 1;
                        while (delays.containsKey(nextDistance)) {
                            nextDistance++;
                        }
                        delays.put(nextDistance, true);
                        distance = nextDistance;
                    }

                    OneTimeWorkRequest notifWorkRequest = new OneTimeWorkRequest.Builder(
                            NotificationMedicationScheduleWorker.class)
                            .setInputData(new Data.Builder()
                                    .putString(NotificationConstants.RECEIVER_ID, loggedInUserId)
                                    .putString(NotificationConstants.TITLE_ID, String.format("Please Take your medication %s", dto.getMedicine()))
                                    .putString(NotificationConstants.CONTENT_ID, String.format("Medication should be taken at %s", DateTimeDto.ToDateTimeDto(dto.getTimestamp()).ToString()))
                                    .build())
                            .addTag(NotificationConstants.MEDICATION_NOTIFICATION_TAG)
                            .setInitialDelay(distance, TimeUnit.SECONDS)
                            .build();

                    requests.add(notifWorkRequest);
                }
                WorkManager.getInstance(PatientHome.this)
                        .cancelAllWorkByTag(NotificationConstants.MEDICATION_NOTIFICATION_TAG);

                Log.e("TEST", "Medication notif count " + String.valueOf(requests.size()));

                for(int i = 0; i < requests.size(); i++) {
                    WorkManager.getInstance(PatientHome.this)
                            .enqueue(requests.get(i));
                }
            }

            @Override
            public void onError(String errorMessage) {

            }
        });
    }
}