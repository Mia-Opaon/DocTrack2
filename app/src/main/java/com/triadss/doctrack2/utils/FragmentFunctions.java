package com.triadss.doctrack2.utils;

import android.annotation.SuppressLint;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.healthprof.fragments.patient.AddPatientFragment;

public class FragmentFunctions {
    public static void ChangeFragmentNoStack(FragmentActivity activity, Fragment fragment) {
        @SuppressLint("CommitTransaction")
        FragmentTransaction transaction = activity.getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }
}
