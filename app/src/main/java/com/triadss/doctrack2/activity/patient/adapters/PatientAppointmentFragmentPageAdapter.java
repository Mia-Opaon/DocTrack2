package com.triadss.doctrack2.activity.patient.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.triadss.doctrack2.activity.patient.fragments.appointments.PatientAppointmentPending;
import com.triadss.doctrack2.activity.patient.fragments.appointments.PatientAppointmentRequest;
import com.triadss.doctrack2.activity.patient.fragments.appointments.PatientAppointmentStatus;

public class PatientAppointmentFragmentPageAdapter extends FragmentStateAdapter {

    public PatientAppointmentFragmentPageAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new PatientAppointmentRequest();
            case 1:
                return new PatientAppointmentPending();
            case 2:
                return new PatientAppointmentStatus();
            default:
                return new PatientAppointmentStatus();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
