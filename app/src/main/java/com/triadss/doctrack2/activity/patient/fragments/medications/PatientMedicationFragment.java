package com.triadss.doctrack2.activity.patient.fragments.medications;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.healthprof.fragments.HealthProfHomeFragment;
import com.triadss.doctrack2.activity.patient.adapters.PatientMedicationFragmentPageAdapter;
import com.triadss.doctrack2.activity.patient.fragments.PatientHomeFragment;
import com.triadss.doctrack2.contracts.IListView;
import com.triadss.doctrack2.utils.FragmentFunctions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatientMedicationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientMedicationFragment extends Fragment {
    private static final String TAG = "PatientMedicationFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PatientMedicationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PatientMedicationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PatientMedicationFragment newInstance(String param1, String param2) {
        PatientMedicationFragment fragment = new PatientMedicationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_patient_medication, container, false);
        FloatingActionButton homeBtn = rootView.findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> {
            FragmentFunctions.ChangeFragmentNoStack(requireActivity(), new PatientHomeFragment());
        });

        TabLayout tabLayout = rootView.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = rootView.findViewById(R.id.viewPager);

        PatientMedicationFragmentPageAdapter pageAdapter = new PatientMedicationFragmentPageAdapter(getActivity().getSupportFragmentManager(), getLifecycle());

        viewPager.setAdapter(pageAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Tab selected logic here
                if(tab != null)
                {
                    Log.e(TAG, tab.getPosition() + " position...");
                    viewPager.setCurrentItem(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Tab unselected logic here
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Tab reselected logic here
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public  void onPageSelected(int position)
            {
                super.onPageSelected(position);
                tabLayout.selectTab(tabLayout.getTabAt(position));

                Fragment fragment = getParentFragmentManager().findFragmentByTag("f" + position);
                boolean fragmentIsListView = fragment instanceof IListView;
                boolean fragmentIsNotNull = fragment != null;
                if (fragmentIsNotNull && fragmentIsListView) {
                    ((IListView) fragment).ReloadList();
                }
            }
        });
        return rootView;
    }
}