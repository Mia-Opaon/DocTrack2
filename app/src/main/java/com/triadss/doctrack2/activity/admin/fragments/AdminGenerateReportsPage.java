package com.triadss.doctrack2.activity.admin.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.admin.adapters.AdminGenerateReportAdapter;
import com.triadss.doctrack2.activity.patient.fragments.PatientHomeFragment;
import com.triadss.doctrack2.dto.DateDto;
import com.triadss.doctrack2.dto.ReportDto;
import com.triadss.doctrack2.repoositories.ReportsRepository;
import com.triadss.doctrack2.utils.FragmentFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminGenerateReportsPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminGenerateReportsPage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    List<ReportDto> retrievedReports = new ArrayList<>();

    public AdminGenerateReportsPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminGenerateReportsPage.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminGenerateReportsPage newInstance(String param1, String param2) {
        AdminGenerateReportsPage fragment = new AdminGenerateReportsPage();
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
        View rootView = inflater.inflate(R.layout.fragment_admin_generate_reports_page, container, false);

        FloatingActionButton homeBtn = rootView.findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> {
            FragmentFunctions.ChangeFragmentNoStack(requireActivity(), new AdminHomeFragment());
        });

        Button generateReports = rootView.findViewById(R.id.generate_report_button);
        DatePicker startDatePicker = rootView.findViewById(R.id.start_date_picker);
        DatePicker endDatePicker = rootView.findViewById(R.id.end_date_picker);

        generateReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timestamp startDate = DateDto.fromDatePicker(startDatePicker).ToStartDateTimestamp();
                Timestamp endDate = DateDto.fromDatePicker(endDatePicker).ToEndDateTimestamp();
                showReportDialog(startDate, endDate);
            }
        });

        return rootView;
    }

    private void showReportDialog(Timestamp startDate, Timestamp endDate) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_generate_reports);
        
        RecyclerView recyclerView = dialog.findViewById(R.id.reports_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        ReportsRepository reportsRepository = new ReportsRepository();

        reportsRepository.getReportsFromDateRange(startDate, endDate, new ReportsRepository.ReportsFetchCallback() {
            @Override
            public void onSuccess(List<ReportDto> reports) {
                retrievedReports = reports;
                recyclerView.setAdapter(new AdminGenerateReportAdapter(getContext(), (ArrayList)retrievedReports));
                dialog.show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        TextInputEditText editTextSearch = dialog.findViewById(R.id.search_bar_patient);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {String filter = s.toString();
                ArrayList<ReportDto> filteredDto = retrievedReports.stream()
                        .filter(report -> report.getMessage().contains(filter) ||
                                report.getAction().contains(filter) ||
                                report.getCreatedByName().contains(filter)
                        ).collect(Collectors.toCollection(ArrayList::new));
                recyclerView.setAdapter(new AdminGenerateReportAdapter(getContext(), 
                    filteredDto));
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });

        Button closeBtn = dialog.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });

    }
}