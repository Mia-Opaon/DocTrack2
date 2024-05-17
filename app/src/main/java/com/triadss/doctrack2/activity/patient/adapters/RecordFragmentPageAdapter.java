package com.triadss.doctrack2.activity.patient.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.triadss.doctrack2.activity.patient.fragments.records.RecordMedicalHistory;
import com.triadss.doctrack2.activity.patient.fragments.records.RecordMedication;
import com.triadss.doctrack2.activity.patient.fragments.records.RecordPersonalInfo;
import com.triadss.doctrack2.activity.patient.fragments.records.RecordVitalSigns;

public class RecordFragmentPageAdapter extends FragmentStateAdapter
{
    public RecordFragmentPageAdapter(FragmentManager fragmentManager, Lifecycle lifecycle)
    {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
                return new RecordPersonalInfo();
            case 1:
                return new RecordMedicalHistory();
            case 2:
                return new RecordMedication();
            default:
                return new RecordVitalSigns();
        }
    }


    @Override
    public int getItemCount() {
        return 4;
    }
}
