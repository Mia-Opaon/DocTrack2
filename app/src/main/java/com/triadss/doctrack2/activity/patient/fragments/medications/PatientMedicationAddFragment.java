package com.triadss.doctrack2.activity.patient.fragments.medications;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.config.constants.MedicationTypeConstants;
import com.triadss.doctrack2.dto.DateDto;
import com.triadss.doctrack2.dto.DateTimeDto;
import com.triadss.doctrack2.dto.MedicationDto;
import com.triadss.doctrack2.dto.TimeDto;
import com.triadss.doctrack2.helper.ButtonManager;
import com.triadss.doctrack2.repoositories.MedicationRepository;
import com.triadss.doctrack2.repoositories.ReportsRepository;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatientMedicationAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientMedicationAddFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PatientMedicationAddFragment";

    private DateTimeDto selectedDateTime;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button button_date, button_time, add_button, clear_button;
    private TextInputEditText medicineInput, noteInput;
    private MedicationRepository medicationRepository;
    private View rootView;
    private TextView errorMedication, errorNote, errorSelecteDate, errorSelectTime;
    private ReportsRepository _reportsRepository = new ReportsRepository();

    public PatientMedicationAddFragment() {
        // Required empty public constructor
        selectedDateTime = new DateTimeDto();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PatientMedicationAddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PatientMedicationAddFragment newInstance(String param1, String param2) {
        PatientMedicationAddFragment fragment = new PatientMedicationAddFragment();
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
        rootView = inflater.inflate(R.layout.fragment_patient_medication_add, container, false);

        medicationRepository = new MedicationRepository();

        button_date = rootView.findViewById(R.id.button_date);
        button_time = rootView.findViewById(R.id.button_time);
        medicineInput = rootView.findViewById(R.id.medicineInput);
        noteInput = rootView.findViewById(R.id.noteInput);
        add_button = rootView.findViewById(R.id.add_button);
        clear_button = rootView.findViewById(R.id.clear_button);

        errorMedication  = rootView.findViewById(R.id.errorMedication);
        errorNote  = rootView.findViewById(R.id.errorNote);
        errorSelecteDate  = rootView.findViewById(R.id.errorSelecteDate);
        errorSelectTime  = rootView.findViewById(R.id.errorSelectTime);

        errorMedication.setVisibility(rootView.INVISIBLE);
        errorNote.setVisibility(rootView.INVISIBLE);
        errorSelecteDate.setVisibility(rootView.INVISIBLE);
        errorSelectTime.setVisibility(rootView.INVISIBLE);

        setupDatePicker();
        setupTimePicker();
        setupClearButton();
        setupConfirmationButton();

        return rootView;
    }

    private void setupDatePicker() {
        // Set up Date Picker Dialog
        button_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create and show the Date Picker Dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                            int monthOfYear, int dayOfMonth) {
                        // Store the selected date
                        selectedDateTime.setDate(new DateDto(year, monthOfYear, dayOfMonth));

                        // Update the text on the button
                        button_date.setText(selectedDateTime.getDate().ToString());
                    }
                }, year, month, day);

                Calendar calendar = Calendar.getInstance();
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

                // Show the Date Picker Dialog
                datePickerDialog.show();
            }
        });
    }

    private void setupTimePicker() {
        // Set up Time Picker Dialog
        button_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current time
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Create and show the Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            // Store the selected time
                            if (!((hourOfDay >= 8 && hourOfDay < 17) || (hourOfDay == 17 && minute == 0)))
                            {
                                Toast.makeText(getActivity(), "Please select a time between 8 AM and 5 PM", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            selectedDateTime.setTime(new TimeDto(hourOfDay, minute));

                            // Update the text on the button
                            button_time.setText(selectedDateTime.getTime().ToString());
                        }
                    }, hour, minute, false);

                // Show the Time Picker Dialog
                timePickerDialog.show();
            }
        });
    }

    private void setupClearButton() {
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClearButtonClick();
            }
        });
    }

    private void setupConfirmationButton() {
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String test = button_time.getText().toString();
                // Handle confirmation button click
                if(!medicineInput.getText().toString().isEmpty() && !noteInput.getText().toString().isEmpty()
                && !button_date.getText().toString().equals("Select Date") && !button_time.getText().toString().equals("Select Time")) {
                    errorMedication.setVisibility(rootView.INVISIBLE);
                    errorNote.setVisibility(rootView.INVISIBLE);
                    errorSelecteDate.setVisibility(rootView.INVISIBLE);
                    errorSelectTime.setVisibility(rootView.INVISIBLE);
                    handleConfirmationButtonClick();
                }
                else
                {
                    if(medicineInput.getText().toString().isEmpty())  errorMedication.setVisibility(rootView.VISIBLE);
                    else errorMedication.setVisibility(rootView.INVISIBLE);
                    if (noteInput.getText().toString().isEmpty()) errorNote.setVisibility(rootView.VISIBLE);
                    else errorNote.setVisibility(rootView.INVISIBLE);
                    if( button_date.getText().toString().equals("Select Date")) errorSelecteDate.setVisibility(rootView.VISIBLE);
                    else errorSelecteDate.setVisibility(rootView.INVISIBLE);
                    if ( button_time.getText().toString().equals("Select Time")) errorSelectTime.setVisibility(rootView.VISIBLE);
                    else errorSelectTime.setVisibility(rootView.INVISIBLE);
                }
            }
        });
    }

    private void handleClearButtonClick() {
        errorMedication.setVisibility(rootView.INVISIBLE);
        errorNote.setVisibility(rootView.INVISIBLE);
        errorSelecteDate.setVisibility(rootView.INVISIBLE);
        errorSelectTime.setVisibility(rootView.INVISIBLE);
        medicineInput.setText("");
        noteInput.setText("");
        selectedDateTime.setDate(null);
        selectedDateTime.setTime(null);
        button_time.setText("Select Time");
        button_date.setText("Select Date");
    }

    private void handleConfirmationButtonClick() {
        try {
            // Sample values for MedicationDto
            String medicine = medicineInput.getText().toString();
            String note = noteInput.getText().toString();

            Timestamp dateTimeOfAppointment = selectedDateTime.ToTimestamp();

            final String status = MedicationTypeConstants.ONGOING;

            String patientId = FirebaseAuth.getInstance().getUid();

            MedicationDto medication = new MedicationDto("",
                    patientId, medicine, note, dateTimeOfAppointment, status);

            ButtonManager.disableButton(add_button);

            medicationRepository.addMedication(medication, new MedicationRepository.MedicationsAddCallback() {
                @Override
                public void onSuccess(String medicationId) {
                    _reportsRepository.addPatientAddedMedicationReport(medication, new ReportsRepository.ReportCallback() {
                        @Override
                        public void onReportAddedSuccessfully() {
                            handleClearButtonClick();

                            Log.e(TAG, "Successfully added medication with the id of " + medicationId);
                            ButtonManager.enableButton(add_button);

                            ViewPager2 vp = getActivity().findViewById(R.id.viewPager); // Fetch ViewPager instance
                            vp.setCurrentItem(1);
                        }

                        @Override
                        public void onReportFailed(String errorMessage) {
                            ButtonManager.enableButton(add_button);
                        }
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Failure in adding medication in the document");
                    ButtonManager.enableButton(add_button);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            ButtonManager.enableButton(add_button);
        }
    }
}