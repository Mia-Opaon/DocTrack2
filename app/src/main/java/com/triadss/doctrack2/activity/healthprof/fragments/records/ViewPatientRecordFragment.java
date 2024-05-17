package com.triadss.doctrack2.activity.healthprof.fragments.records;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.type.DateTime;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.healthprof.adapters.ViewMedicationAdapter;
import com.triadss.doctrack2.activity.healthprof.fragments.HealthProfHomeFragment;
import com.triadss.doctrack2.config.constants.DocTrackConstant;
import com.triadss.doctrack2.dto.AddPatientDto;
import com.triadss.doctrack2.dto.DateTimeDto;
import com.triadss.doctrack2.dto.MedicalHistoryDto;
import com.triadss.doctrack2.dto.MedicationDto;
import com.triadss.doctrack2.dto.VitalSignsDto;
import com.triadss.doctrack2.repoositories.MedicalHistoryRepository;
import com.triadss.doctrack2.repoositories.MedicationRepository;
import com.triadss.doctrack2.repoositories.PatientRepository;
import com.triadss.doctrack2.repoositories.VitalSignsRepository;
import com.triadss.doctrack2.utils.CheckboxStringProcessor;
import com.triadss.doctrack2.utils.EditTextStringProcessor;
import com.triadss.doctrack2.utils.FragmentFunctions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewPatientRecordFragment extends Fragment {
    private static final String PATIENT_UID = "patientUid";
    private String patientUid;

    public ViewPatientRecordFragment(){
        //Required empty public constructor
    }
    public static ViewPatientRecordFragment newInstance(String patientUid) {
        ViewPatientRecordFragment fragment = new ViewPatientRecordFragment();
        Bundle args = new Bundle();
        args.putString(PATIENT_UID, patientUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patientUid = getArguments().getString(PATIENT_UID);
        }
    }
    private void setVitalSignsValues(TextView bloodPressure, TextView temperature, TextView sp,
                                     TextView pulseRate, TextView weight, TextView height, TextView BMI,
                                     TextView date, VitalSignsDto v) {

        if (v.getCreatedAt() != null) {
            DateTimeDto dt = DateTimeDto.ToDateTimeDto(v.getCreatedAt());

            bloodPressure.setText(v.getBloodPressure());
            temperature.setText(String.valueOf(v.getTemperature()));
            sp.setText(String.valueOf(v.getOxygenLevel()));
            pulseRate.setText(String.valueOf(v.getPulseRate()));
            weight.setText(String.valueOf(v.getWeight()));
            height.setText(String.valueOf(v.getHeight()));
            BMI.setText(String.valueOf(v.getBMI()));
            date.setText(dt.ToString());
        } else {
            bloodPressure.setText("");
            temperature.setText("");
            sp.setText("");
            pulseRate.setText("");
            weight.setText("");
            height.setText("");
            BMI.setText("");
            date.setText("");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_patientrecord_viewrecord, container, false);

        FloatingActionButton homeBtn = rootView.findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> {
            FragmentFunctions.ChangeFragmentNoStack(requireActivity(), new HealthProfHomeFragment());
        });

        // Retrieve patient data from arguments
        Bundle args = getArguments();
        if (args != null) {
            MedicalHistoryRepository medicalHistoryRepository = new MedicalHistoryRepository();
            VitalSignsRepository vitalSignsRepository = new VitalSignsRepository();
            MedicationRepository medicationRepostory = new MedicationRepository();
            PatientRepository patientRepository = new PatientRepository();

            //Patient Personal Information
            TextView patientId = rootView.findViewById(R.id.value_patientID);
            TextView patientName = rootView.findViewById(R.id.value_Name);
            TextView patientEmail = rootView.findViewById(R.id.value_Email);
            TextView patientAddress = rootView.findViewById(R.id.value_Address);
            TextView patientPhone = rootView.findViewById(R.id.value_ContactNo);
            TextView patientCourse = rootView.findViewById(R.id.value_Course);
            TextView patientGender = rootView.findViewById(R.id.value_Gender);
            TextView patientYear = rootView.findViewById(R.id.value_Year);
            TextView patientStatus = rootView.findViewById(R.id.value_Status);
            TextView patientdateofBirth = rootView.findViewById(R.id.value_dateOfBirth);

            patientRepository.getPatient(patientUid, new PatientRepository.PatientFetchCallback(){

                @Override
                public void onSuccess(AddPatientDto patient) {
                    patientId.setText(patient.getIdNumber());
                    patientName.setText(patient.getFullName());
                    patientEmail.setText(patient.getEmail());
                    patientAddress.setText(patient.getAddress());
                    patientPhone.setText(patient.getPhone());
                    patientCourse.setText(patient.getCourse());
                    String yearVal = patient.getYear() == null ? DocTrackConstant.NOT_APPLICABLE :
                            String.valueOf(patient.getYear());
                    patientYear.setText(yearVal);
                    patientGender.setText(patient.getGender());
                    patientStatus.setText(patient.getStatus());
                    patientdateofBirth.setText(DateTimeDto.ToDateTimeDto(patient.getDateOfBirth()).getDate().ToString());
                }

                @Override
                public void onError(String errorMessage) {
                    System.out.println();
                }
            });

            //Patient Medical History
            TextView patientPastIllness = rootView.findViewById(R.id.value_pastIllness);
            TextView patientPrevHospitalization = rootView.findViewById(R.id.value_prevHospitalization);
            TextView patientFamilyHistory = rootView.findViewById(R.id.value_familyHistory);
            TextView patientOBGyneHistory = rootView.findViewById(R.id.value_OBGyneHistory);
            medicalHistoryRepository.getMedicalHistoryOfPatient(patientUid, new MedicalHistoryRepository.FetchCallback() {
                @Override
                public void onSuccess(MedicalHistoryDto medicalHistory) {
                    String SplitDelimiter = "\\|";

                    List<String> separatedPastIllness = CheckboxStringProcessor.StringToSeparatedValues(SplitDelimiter, medicalHistory.getPastIllness());
                    String pastIllnessString = CheckboxStringProcessor.SeparatedValuesToString(", ", separatedPastIllness);
                    patientPastIllness.setText(pastIllnessString);
                    patientPrevHospitalization.setText(medicalHistory.getPrevOperation());

                    List<String> separatedMedicalHistory = CheckboxStringProcessor.StringToSeparatedValues(SplitDelimiter, medicalHistory.getFamilyHist());
                    String medicalHistoryString = CheckboxStringProcessor.SeparatedValuesToString(", ", separatedMedicalHistory);
                    patientFamilyHistory.setText(medicalHistoryString);

                    List<String> separatedOBGyneHistory = EditTextStringProcessor.StringToSeparatedValues(SplitDelimiter, medicalHistory.getObgyneHist());
                    Map<String, String> mappedOBGyneHistory = EditTextStringProcessor.SeparatedValuesToMap(":", separatedOBGyneHistory);
                    List<String> processedOBGyneHistory = EditTextStringProcessor.MapToSeparatedValues(": ", mappedOBGyneHistory);
                    String obgyneString = EditTextStringProcessor.SeparatedValuesToString("\n", processedOBGyneHistory);
                    patientOBGyneHistory.setText(obgyneString);
                }

                @Override
                public void onError(String message) {
                    System.out.println();
                }
            });

            //Patient Vital Signs Latest
            TextView bloodPressure1 = rootView.findViewById(R.id.value_bp1);
            TextView temperature1 = rootView.findViewById(R.id.value_temp1);
            TextView sp1 = rootView.findViewById(R.id.value_spo2_1);
            TextView pulseRate1 = rootView.findViewById(R.id.value_pulseRate1);
            TextView weight1 = rootView.findViewById(R.id.value_weight1);
            TextView height1 = rootView.findViewById(R.id.value_height1);
            TextView BMI1 = rootView.findViewById(R.id.value_BMI1);
            TextView date1 = rootView.findViewById(R.id.value_latest1);

            //Patient Vital Signs 2nd Latest
            TextView bloodPressure2 = rootView.findViewById(R.id.value_bp2);
            TextView temperature2 = rootView.findViewById(R.id.value_temp2);
            TextView sp2 = rootView.findViewById(R.id.value_spo2_2);
            TextView pulseRate2 = rootView.findViewById(R.id.value_pulseRate2);
            TextView weight2 = rootView.findViewById(R.id.value_weight2);
            TextView height2 = rootView.findViewById(R.id.value_height2);
            TextView BMI2 = rootView.findViewById(R.id.value_BMI2);
            TextView date2 = rootView.findViewById(R.id.value_latest2);

            //Patient Vital Signs
            TextView bloodPressure3 = rootView.findViewById(R.id.value_bp3);
            TextView temperature3 = rootView.findViewById(R.id.value_temp3);
            TextView sp3 = rootView.findViewById(R.id.value_spo2_3);
            TextView pulseRate3 = rootView.findViewById(R.id.value_pulseRate3);
            TextView weight3 = rootView.findViewById(R.id.value_weight3);
            TextView height3 = rootView.findViewById(R.id.value_height3);
            TextView BMI3 = rootView.findViewById(R.id.value_BMI3);
            TextView date3 = rootView.findViewById(R.id.value_latest3);

            //Patient Vital Signs
            TextView bloodPressure4 = rootView.findViewById(R.id.value_bp4);
            TextView temperature4 = rootView.findViewById(R.id.value_temp4);
            TextView sp4 = rootView.findViewById(R.id.value_spo2_4);
            TextView pulseRate4 = rootView.findViewById(R.id.value_pulseRate4);
            TextView weight4 = rootView.findViewById(R.id.value_weight4);
            TextView height4 = rootView.findViewById(R.id.value_height4);
            TextView BMI4 = rootView.findViewById(R.id.value_BMI4);
            TextView date4 = rootView.findViewById(R.id.value_latest4);

            //Patient Vital Signs
            TextView bloodPressure5 = rootView.findViewById(R.id.value_bp5);
            TextView temperature5 = rootView.findViewById(R.id.value_temp5);
            TextView sp5 = rootView.findViewById(R.id.value_spo2_5);
            TextView pulseRate5 = rootView.findViewById(R.id.value_pulseRate5);
            TextView weight5 = rootView.findViewById(R.id.value_weight5);
            TextView height5 = rootView.findViewById(R.id.value_height5);
            TextView BMI5 = rootView.findViewById(R.id.value_BMI5);
            TextView date5 = rootView.findViewById(R.id.value_latest5);

            vitalSignsRepository.getVitalSignsOfPatient(patientUid, new VitalSignsRepository.FetchListCallback() {
                @Override
                public void onSuccess(List<VitalSignsDto> vitalSigns) {
                    int index = 0;

                    for (VitalSignsDto v : vitalSigns) {

                        switch (index) {
                            case 0:
                                setVitalSignsValues(bloodPressure1, temperature1, sp1, pulseRate1, weight1, height1, BMI1, date1, v);
                                break;
                            case 1:
                                setVitalSignsValues(bloodPressure2, temperature2, sp2, pulseRate2, weight2, height2, BMI2, date2, v);
                                break;
                            case 2:
                                setVitalSignsValues(bloodPressure3, temperature3, sp3, pulseRate3, weight3, height3, BMI3, date3, v);
                                break;
                            case 3:
                                setVitalSignsValues(bloodPressure4, temperature4, sp4, pulseRate4, weight4, height4, BMI4, date4, v);
                                break;
                            case 4:
                                setVitalSignsValues(bloodPressure5, temperature5, sp5, pulseRate5, weight5, height5, BMI5, date5, v);
                                break;
                            default:
                                break;
                        }

                        index++;
                    }
                }


                @Override
                public void onError(String errorMessage) {
                    Log.e("ERROR", errorMessage);
                }
            });

            //Patient Medication
            RecyclerView medicationRecyclerView = rootView.findViewById(R.id.recyclerView_medication);
            medicationRepostory.getAllMedicationsFromUser(patientUid, new MedicationRepository.MedicationFetchCallback() {
                @Override
                public void onSuccess(List<MedicationDto> medications) {
                    ViewMedicationAdapter viewMedicationAdapter = new ViewMedicationAdapter(getContext(), (ArrayList)medications);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    medicationRecyclerView.setLayoutManager(linearLayoutManager);
                    medicationRecyclerView.setAdapter(viewMedicationAdapter);
                }

                @Override
                public void onError(String message) {
                    System.out.println();
                }
            });
        }

        Button button_update = rootView.findViewById(R.id.button_update);
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Redirect
                showPatientRecordsUpdate();
            }
        });

        return rootView;
    }
    private void showPatientRecordsUpdate() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        // TODO: Create View Record Fragment for Patient then remove // of the nextline code to use it
        transaction.replace(R.id.frame_layout, UpdatePersonalInfo.newInstance(patientUid));
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
