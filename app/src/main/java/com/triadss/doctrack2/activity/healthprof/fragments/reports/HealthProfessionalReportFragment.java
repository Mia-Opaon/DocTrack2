package com.triadss.doctrack2.activity.healthprof.fragments.reports;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static com.triadss.doctrack2.config.constants.PdfConstants.READ_PERMISSION_REQUEST_CODE;
import static com.triadss.doctrack2.config.constants.PdfConstants.WRITE_PERMISSION_REQUEST_CODE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.healthprof.adapters.HealthProfessionalReportAdapter;
import com.triadss.doctrack2.activity.healthprof.fragments.HealthProfHomeFragment;
import com.triadss.doctrack2.config.constants.ReportConstants;
import com.triadss.doctrack2.config.constants.SessionConstants;
import com.triadss.doctrack2.dto.AddPatientDto;
import com.triadss.doctrack2.dto.DateTimeDto;
import com.triadss.doctrack2.dto.HealthProfDto;
import com.triadss.doctrack2.dto.ReportDto;
import com.triadss.doctrack2.helper.ButtonManager;
import com.triadss.doctrack2.repoositories.HealthProfRepository;
import com.triadss.doctrack2.repoositories.PatientRepository;
import com.triadss.doctrack2.repoositories.ReportsRepository;
import com.triadss.doctrack2.utils.FragmentFunctions;
import com.triadss.doctrack2.utils.PdfHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HealthProfessionalReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthProfessionalReportFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button exportButton;
    String loggedInUserId;
    RadioGroup radioGroup;

    public HealthProfessionalReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HealthProfessionalReportFragment newInstance(String param1, String param2) {
        HealthProfessionalReportFragment fragment = new HealthProfessionalReportFragment();
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

    RecyclerView recyclerView;
    String healthProfName;
    private ReportsRepository repository;
    private HealthProfRepository healthProfRepository = new HealthProfRepository();
    private PatientRepository patientRepository = new PatientRepository();
    private EditText search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(SessionConstants.SessionPreferenceKey, Context.MODE_PRIVATE);
        loggedInUserId = sharedPref.getString(SessionConstants.LoggedInUid, "");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_healthprof_reports_list, container, false);

        exportButton = rootView.findViewById(R.id.exportReportsBtn);

        recyclerView = rootView.findViewById(R.id.recyclerViewReports);
        search = (EditText) rootView.findViewById(R.id.search_bar);

        FloatingActionButton homeBtn = rootView.findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> {
            FragmentFunctions.ChangeFragmentNoStack(requireActivity(), new HealthProfHomeFragment());
        });

        radioGroup = rootView.findViewById(R.id.radioGroupbtn);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);

                if (checkedId == R.id.radioAll) {
                    reloadList();
                }
                if (checkedId == R.id.radioAppointments) {
                    reloadList();
                }
                if (checkedId == R.id.radioPatients) {
                    reloadList();
                }
            }
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        repository = new ReportsRepository();

        reloadList();

        search.addTextChangedListener(inputTextWatcher);
        return rootView;
    }

    TextWatcher inputTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            reloadList();
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after){
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    private void reloadList() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        ReportsRepository repository = new ReportsRepository();

        String[] list = new String[0];
        int checkedId = radioGroup.getCheckedRadioButtonId();

        if (checkedId==R.id.radioAll) {
            //* Merge all report lists into 'list'
            list = Stream.concat(
                    Arrays.stream(ReportConstants.HEALTHPROF_APPOINTMENT_REPORTS),
                            Arrays.stream(ReportConstants.HEALTHPROF_PATIENT_REPORTS))
                            .toArray(String[]::new);
        } else {
            //* Handle individual toggle button states
            if (checkedId==R.id.radioAppointments) {
                list = Stream.concat(Arrays.stream(list), Arrays.stream(ReportConstants.HEALTHPROF_APPOINTMENT_REPORTS))
                        .toArray(String[]::new);
            }

            if (checkedId==R.id.radioPatients) {
                list = Stream.concat(Arrays.stream(list), Arrays.stream(ReportConstants.HEALTHPROF_PATIENT_REPORTS))
                        .toArray(String[]::new);
            }
        }

        if(checkedId!=R.id.radioAll || checkedId!=R.id.radioAppointments || checkedId!=R.id.radioPatients){
            repository.getReportsFromUserFilter(user.getUid(),
                    search.getText().toString().toLowerCase(),
                    list,
                    new ReportsRepository.ReportsFilterCallback() {
                    @Override
                    public void onSuccess(List<ReportDto> reports) {
                        HealthProfessionalReportAdapter pageAdapter = new HealthProfessionalReportAdapter(getContext(), (ArrayList) reports);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(pageAdapter);

                        healthProfRepository.getHealthProfessional(loggedInUserId, new HealthProfRepository.HealthProGetCallback() {
                            @Override
                            public void onSuccess(HealthProfDto dto) {
                                healthProfName = dto.getFullName();
                                exportButton.setOnClickListener(v -> {
                                    boolean hasPermissions = true;
                                    if (ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(requireActivity(), new String[]{READ_EXTERNAL_STORAGE}, READ_PERMISSION_REQUEST_CODE);
                                        hasPermissions = false;
                                    }

                                    if (ContextCompat.checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(requireActivity(), new String[]{WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST_CODE);
                                        hasPermissions = false;
                                    }

                                    if(!hasPermissions) {
                                        return;
                                    }

                                    if(reports.isEmpty()) {
                                        Toast.makeText(requireContext(), "Nothing to Export", Toast.LENGTH_SHORT).show();
                                    } else {

                                        // Search for patients based on input text
                                        String searchText = search.getText().toString().trim().toLowerCase();
                                        if (!searchText.isEmpty()) {
                                            // If search text is not empty, search patients based on input text
                                            patientRepository.searchPatientsByName(searchText, new PatientRepository.PatientSearchCallback() {
                                                @Override
                                                public void onSuccess(List<AddPatientDto> patients) {
                                                    exportReports(reports, patients);
                                                }

                                                @Override
                                                public void onError(String errorMessage) {
                                                    // Handle error
                                                }
                                            });
                                        } else {
                                            // If search text is empty, export reports without patient information
                                            exportReports(reports, null);
                                        }



                                    }
                                });
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                // Handle failure
                            }
                        });
                    }

                        @Override
                        public void onError(String errorMessage) {
                            // Handle error
                        }
                    });
        } else {
            HealthProfessionalReportAdapter pageAdapter = new HealthProfessionalReportAdapter(getContext(), new ArrayList<ReportDto>());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(pageAdapter);

            exportButton.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Nothing to Export", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void exportReports(List<ReportDto> reports, List<AddPatientDto> patients) {
        DateTimeDto dateTimeDto = DateTimeDto.GetCurrentDateTimeDto();
        String filename = String.format("%s Reports %s", healthProfName, dateTimeDto.formatDateTime());
        filename = filename.replace(":", "_");
        Toast.makeText(requireContext(), "Ongoing Export", Toast.LENGTH_SHORT).show();
        ButtonManager.disableButton(exportButton);

        // Include patient information in the PDF if patients are available
        String patientInfo = (patients != null && !patients.isEmpty()) ? getPatientInfo(patients) : "N/A";
        PdfHelper.GeneratePdfFromReports(requireContext(), filename, reports, patientInfo, pdfFile -> {
            ButtonManager.enableButton(exportButton);
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(getUriFromFile(pdfFile), "application/pdf");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);
                // Start activity
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Method to generate patient information from the list of patients
    private String getPatientInfo(List<AddPatientDto> patients) {
        StringBuilder patientInfo = new StringBuilder();
        for (AddPatientDto patient : patients) {
            DateTimeDto dateTime = DateTimeDto.ToDateTimeDto(patient.getDateOfBirth());
            patientInfo.append("Patient ID: ").append(patient.getIdNumber()).append("\n")
                    .append("Patient Name: ").append(patient.getFullName()).append("\n")
                    .append("Age: ").append(DateTimeDto.ComputeAge(patient.getDateOfBirth())).append("\n")
                    .append("Date of Birth: ").append(dateTime.formatDate()).append("\n")
                    .append("Gender: ").append(patient.getGender()).append("\n")
                    .append("Address ").append(patient.getAddress()).append("\n")
                    .append("Contact No.: ").append(patient.getPhone()).append("\n\n");
        }
        return patientInfo.toString();
    }
    private Uri getUriFromFile(File file){
        return FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file);
    }

}