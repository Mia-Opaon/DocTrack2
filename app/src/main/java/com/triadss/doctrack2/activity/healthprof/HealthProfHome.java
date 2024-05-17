package com.triadss.doctrack2.activity.healthprof;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.LoginActivity;
import com.triadss.doctrack2.activity.healthprof.fragments.HealthProfHomeFragment;
import com.triadss.doctrack2.activity.healthprof.fragments.appointments.HealthProfessionalAppointmentFragment;
import com.triadss.doctrack2.activity.healthprof.fragments.reports.HealthProfessionalReportFragment;
import com.triadss.doctrack2.config.constants.NotificationConstants;
import com.triadss.doctrack2.databinding.ActivityHealthProfHomeBinding;
import com.triadss.doctrack2.activity.core.DeviceFragment;
import com.triadss.doctrack2.activity.healthprof.fragments.patient.PatientFragment;
import com.triadss.doctrack2.activity.patient.fragments.records.RecordFragment;
import com.triadss.doctrack2.notification.NotificationService;

public class HealthProfHome extends AppCompatActivity {

    FirebaseAuth auth;

    FirebaseUser user;

    ActivityHealthProfHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHealthProfHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HealthProfHomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.appointment_menu) {
               // replaceFragment(new AppointmentFragment());
                replaceFragment(new HealthProfessionalAppointmentFragment());
            } else if (item.getItemId() == R.id.device_menu) {
                replaceFragment(new DeviceFragment());
            } else if (item.getItemId() == R.id.patient_menu) {
                replaceFragment(new PatientFragment());
            } else if (item.getItemId() == R.id.record_menu) {
                replaceFragment(new RecordFragment());
            } else if (item.getItemId() == R.id.report_menu) {
                //scheduleNotification(getNotification( "1 second delay" ) , 1000 ) ;
                replaceFragment(new HealthProfessionalReportFragment());
            }
            return true;
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Back is pressed... Finishing the activity
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);
                boolean isCurrentlyAtHomepage = currentFragment instanceof HealthProfHomeFragment;
                if(!isCurrentlyAtHomepage) {
                    fragmentManager.popBackStack();
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);
        boolean isCurrentlyAtHomepage = currentFragment instanceof HealthProfHomeFragment;

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        if(isCurrentlyAtHomepage) {
            fragmentTransaction.addToBackStack("toHome");
        }
        fragmentTransaction.commit();
    }

    private void scheduleNotification (Notification notification , int delay) {
        Intent notificationIntent = new Intent( this, NotificationService.class);
        notificationIntent.putExtra(NotificationService.NOTIFICATION_ID , 1 );
        notificationIntent.putExtra(NotificationService.NOTIFICATION , notification);
        try{
            PendingIntent pendingIntent = PendingIntent. getBroadcast ( this,
                    0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE );
            long futureInMillis = SystemClock.elapsedRealtime () + delay;
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE );
            assert alarmManager != null;
            alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent);
        } catch(Exception e){
            Log.e("HealthProfHome", e.getMessage());
        }


    }
    private Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, NotificationConstants.DEFAULT_NOTIFICATION_CHANNEL_ID ) ;
        builder.setContentTitle( "Scheduled Notification" ) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NotificationConstants.NOTIFICATION_CHANNEL_ID ) ;
        return builder.build();
    }
}