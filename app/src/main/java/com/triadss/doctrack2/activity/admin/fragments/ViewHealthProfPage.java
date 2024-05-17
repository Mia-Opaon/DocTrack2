package com.triadss.doctrack2.activity.admin.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.dto.HealthProfDto;
import com.triadss.doctrack2.repoositories.HealthProfRepository;
import com.triadss.doctrack2.utils.FragmentFunctions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewHealthProfPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewHealthProfPage extends Fragment {
    private static final String HEALTHPROF_ID = "healthProfUid";
    String healthProfUid;
    HealthProfRepository repository = new HealthProfRepository();

    public ViewHealthProfPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param healthProfUid Parameter 1.
     * @return A new instance of fragment ViewHealthProfPage.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewHealthProfPage newInstance(String healthProfUid) {
        ViewHealthProfPage fragment = new ViewHealthProfPage();
        Bundle args = new Bundle();
        args.putString(HEALTHPROF_ID, healthProfUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            healthProfUid = getArguments().getString(HEALTHPROF_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_admin_manage_user_accounts_view_health_prof, container, false);
        Button exitBtn = rootView.findViewById(R.id.exitBtn);

        FloatingActionButton homeBtn = rootView.findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> {
            FragmentFunctions.ChangeFragmentNoStack(requireActivity(), new AdminHomeFragment());
        });

        TextView healthWorkerName = rootView.findViewById(R.id.healthWorkerName);
        TextView userName = rootView.findViewById(R.id.UserName);
        TextView gender = rootView.findViewById(R.id.Gender);
        TextView position = rootView.findViewById(R.id.Position);

        repository.getHealthProfessional(healthProfUid, new HealthProfRepository.HealthProGetCallback() {
            @Override
            public void onSuccess(HealthProfDto dto) {
                healthWorkerName.setText(dto.getFullName());
                userName.setText(dto.getUserName());
                gender.setText(dto.getGender());
                position.setText(dto.getPosition());
            }
            @Override
            public void onFailure(String errorMessage) {
                System.out.println("");
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, AdminManageUserAccount.newInstance("", ""));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return rootView;
    }
}