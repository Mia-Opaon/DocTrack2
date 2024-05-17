package com.triadss.doctrack2.activity.admin.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.dto.HealthProfDto;
import com.triadss.doctrack2.repoositories.HealthProfRepository;
import com.triadss.doctrack2.utils.FragmentFunctions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateHealthProfPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateHealthProfPage extends Fragment {
    private static final String TAG = "UpdateHealthProfPage";
    private static final String HEALTHPROF_ID = "healthProfUid";
    HealthProfDto healthProfDto;
    String healthProfUid;
    Button updateBtn;
    EditText editTextPosition, editTextPositionInput;
    TextView textHealthWorkerName, textUserName, textGenderUpdate, errorTextPosition;

    public UpdateHealthProfPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param healthProfUid Parameter 1.
     * @return A new instance of fragment UpdateHealthProfPage.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateHealthProfPage newInstance(String healthProfUid) {
        UpdateHealthProfPage fragment = new UpdateHealthProfPage();
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
        HealthProfRepository repository = new HealthProfRepository();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_admin_manage_user_accounts_update_health_prof, container, false);

        FloatingActionButton homeBtn = rootView.findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> {
            FragmentFunctions.ChangeFragmentNoStack(requireActivity(), new AdminHomeFragment());
        });

        updateBtn = rootView.findViewById(R.id.buttonUpdateHealthProf);
        editTextPosition = rootView.findViewById(R.id.editTextPosition);
        textHealthWorkerName = rootView.findViewById(R.id.healthWorkerName);
        textUserName = rootView.findViewById(R.id.UserName);
        textGenderUpdate = rootView.findViewById(R.id.Gender);

        editTextPositionInput = rootView.findViewById(R.id.editTextPosition);

        errorTextPosition = rootView.findViewById(R.id.errorTextPosition);
        errorTextPosition.setVisibility(rootView.GONE);

        repository.getHealthProfessional(healthProfUid, new HealthProfRepository.HealthProGetCallback() {
            @Override
            public void onSuccess(HealthProfDto dto) {
                textHealthWorkerName.setText(dto.getFullName());
                textUserName.setText(dto.getUserName());
                textGenderUpdate.setText(dto.getGender());
                editTextPosition.setText(dto.getPosition());

                updateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!editTextPositionInput.getText().toString().isEmpty()) {
                            dto.setPosition(editTextPosition.getText().toString());
                            repository.updateHealthProfessional(dto, new HealthProfRepository.HealthProUpdateCallback() {
                                @Override
                                public void onSuccess(HealthProfDto dto) {
                                    @SuppressLint("CommitTransaction")
                                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager()
                                            .beginTransaction();
                                    transaction.replace(R.id.frame_layout, new AdminManageUserAccount());
                                    // Add HomeFragment to the back stack with a tag
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }

                                @Override
                                public void onFailure(String errorMessage) {

                                }
                            });
                        }
                        else if(editTextPositionInput.getText().toString().isEmpty()) errorTextPosition.setVisibility(rootView.VISIBLE);
                        else errorTextPosition.setVisibility(rootView.GONE);
                    }
                });
            }
            @Override
            public void onFailure(String errorMessage) {
                System.out.println("");
            }
        });

        return rootView;
    }
}