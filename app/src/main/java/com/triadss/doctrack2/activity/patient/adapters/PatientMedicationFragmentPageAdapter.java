package com.triadss.doctrack2.activity.patient.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.triadss.doctrack2.activity.patient.fragments.medications.PatientMedicationAddFragment;
import com.triadss.doctrack2.activity.patient.fragments.medications.PatientMedicationCompletedFragment;
import com.triadss.doctrack2.activity.patient.fragments.medications.PatientMedicationOngoingFragment;

public class PatientMedicationFragmentPageAdapter extends FragmentStateAdapter {
    private static final String TAG = "PatientMedicationFragmentPageAdapter";

    public PatientMedicationFragmentPageAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new PatientMedicationCompletedFragment();
            default:
                return new PatientMedicationOngoingFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
