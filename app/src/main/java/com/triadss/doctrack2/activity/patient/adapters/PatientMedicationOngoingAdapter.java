package com.triadss.doctrack2.activity.patient.adapters;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.patient.PatientHome;
import com.triadss.doctrack2.config.constants.MedicationTypeConstants;
import com.triadss.doctrack2.config.constants.ToastConstants;
import com.triadss.doctrack2.dto.DateDto;
import com.triadss.doctrack2.dto.DateTimeDto;
import com.triadss.doctrack2.dto.MedicationDto;
import com.triadss.doctrack2.dto.TimeDto;
import com.triadss.doctrack2.helper.ButtonManager;
import com.triadss.doctrack2.repoositories.MedicationRepository;
import com.triadss.doctrack2.repoositories.ReportsRepository;
import com.triadss.doctrack2.utils.AppointmentFunctions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

public class PatientMedicationOngoingAdapter extends RecyclerView.Adapter<PatientMedicationOngoingAdapter.ViewHolder> {
    private final String TAG = "PatientMedicationOngoingAdapter";
    private ArrayList<MedicationDto> medications;
    private Context context;
    private MedicationRepository medicationRepository;
    private ReportsRepository _reportsRepository = new ReportsRepository();

    public PatientMedicationOngoingAdapter(Context context, ArrayList<MedicationDto> medications) {
        this.context = context;
        this.medications = medications;
        this.medicationRepository = new MedicationRepository();
    }

    @NonNull
    @Override
    public PatientMedicationOngoingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_medication_ongoing, parent, false);

        // Passing view to ViewHolder
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientMedicationOngoingAdapter.ViewHolder holder, int position) {
        holder.update(medications.get(position));
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView medicine, note, date, time;
        private Button update, complete;
        private String mediId;

        public ViewHolder(View view) {
            super(view);
            medicine = view.findViewById(R.id.medicationMedicine);
            note = view.findViewById(R.id.medicationNote);
            date = view.findViewById(R.id.medicationDate);
            time = view.findViewById(R.id.medicationTime);
            complete = view.findViewById(R.id.medicationComplete);
            update = view.findViewById(R.id.medicationUpdate);
        }

        public void update(MedicationDto medicationDto) {
            medicine.setText(medicationDto.getMedicine());
            note.setText(medicationDto.getNote());

            DateTimeDto dateTime = DateTimeDto.ToDateTimeDto(medicationDto.getTimestamp());
            date.setText(dateTime.getDate().ToString());
            time.setText(dateTime.getTime().ToString());
            mediId = medicationDto.getMediId();
            setupComplete(medicationDto);
            update.setOnClickListener(v -> openUpdateDialog(medicationDto));
        }

        private void openUpdateDialog(MedicationDto medicationDto) {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_update_medication);

            Button dateBtn = dialog.findViewById(R.id.dateBtn);
            Button selectTime = dialog.findViewById(R.id.timeBtn);
            TextView dateErrorText = dialog.findViewById(R.id.dateErrorText);
            TextView timeErrorText = dialog.findViewById(R.id.timeErrorText);

            TextView medicationDate = dialog.findViewById(R.id.updateDate);
            TextView medicationTime = dialog.findViewById(R.id.updateTime);

            DateTimeDto selectedDateTime = new DateTimeDto();
            DateTimeDto medicationTimestamp = DateTimeDto.ToDateTimeDto(medicationDto.getTimestamp());

            Button updateBtn = dialog.findViewById(R.id.updateBtn);
            dateBtn.setOnClickListener((View.OnClickListener) v -> {
                // Get the current date
                DateDto date = medicationTimestamp.getDate();

                // Create and show the Date Picker Dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        (view, year1, monthOfYear, dayOfMonth) -> {
                            // Store the selected date
                            selectedDateTime.setDate(new DateDto(year1, monthOfYear + 1, dayOfMonth));

                            // Update the text on the button
                            medicationDate.setText(selectedDateTime.getDate().ToString(false));
                        }, date.getYear(), date.getMonth(), date.getDay());
                // Show the Date Picker Dialog
                datePickerDialog.show();
            });

            selectTime.setOnClickListener(v -> {
                // Get the current time
                TimeDto time = medicationTimestamp.getTime();

                // Create and show the Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        (view, hourOfDay, minute1) -> {
                            // Store the selected time
                            selectedDateTime.setTime(new TimeDto(hourOfDay, minute1));

                            // Update the text on the button
                            medicationTime.setText(selectedDateTime.getTime().ToString());
                        }, time.getHour(), time.getMinute(), false);

                // Show the Time Picker Dialog
                timePickerDialog.show();
            });

            updateBtn.setOnClickListener(v -> {
                dateErrorText.setVisibility(View.GONE);
                timeErrorText.setVisibility(View.GONE);

                boolean inputInvalid = false;

                if (selectedDateTime.getDate() == null ||  selectedDateTime.getDate().getYear() == 0 || selectedDateTime.getDate().getMonth() == 0 || selectedDateTime.getDate().getDay() == 0) {
                    Toast.makeText(context, "Please select a valid date", Toast.LENGTH_SHORT).show();
                    dateErrorText.setVisibility(View.VISIBLE);
                    inputInvalid = true;
                }

                if (selectedDateTime.getTime() == null) {
                    Toast.makeText(context, "Please select a valid time", Toast.LENGTH_SHORT).show();
                    timeErrorText.setVisibility(View.VISIBLE);
                    inputInvalid = true;
                }

                if(inputInvalid)
                {
                    return;
                }
                medicationDto.setTimestamp(selectedDateTime.ToTimestampForTimePicker());

                ButtonManager.disableButton(updateBtn);
                medicationRepository.updateMedication(mediId, medicationDto, new MedicationRepository.MedicationUpdateCallback() {
                    @Override
                    public void onSuccess() {
                        _reportsRepository.addPatientUpdatedMedicationReport(medicationDto, new ReportsRepository.ReportCallback() {
                            @Override
                            public void onReportAddedSuccessfully() {
                                updateMedicationsList(medicationDto);
                                Toast.makeText(context, ToastConstants.UPDATED_MEDICATION, Toast.LENGTH_SHORT).show();

                                PatientHome homeActivity = (PatientHome) context;
                                homeActivity.setupNotifications();

                                dialog.dismiss();
                            }

                            @Override
                            public void onReportFailed(String errorMessage) {
                                ButtonManager.enableButton(updateBtn);
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Error updating medication: " + errorMessage);
                        ButtonManager.enableButton(updateBtn);
                    }
                });
            });

            dialog.show();
        }

        private void setupComplete(MedicationDto medication) {
            complete.setOnClickListener(v -> {
                ButtonManager.disableButton(complete);

                medicationRepository.updateMedicationStatus(mediId, MedicationTypeConstants.COMPLETED, new MedicationRepository.MedicationUpdateCallback() {
                    @Override
                    public void onSuccess() {
                        _reportsRepository.addPatientCompletedMedicationReport(medication, new ReportsRepository.ReportCallback() {
                            @Override
                            public void onReportAddedSuccessfully() {
                                Toast.makeText(context, ToastConstants.COMPLETED_MEDICATION, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Medication status updated successfully");

                                removeItem(mediId);
                            }

                            @Override
                            public void onReportFailed(String errorMessage) {
                                ButtonManager.enableButton(complete);
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Error updating medication status: " + errorMessage);
                        ButtonManager.enableButton(complete);
                    }
                });
                Toast.makeText(context, medicine.getText() + " has been completed.", Toast.LENGTH_SHORT).show();
            });
        }

        private void removeItem(String mediId) {
            Iterator<MedicationDto> iterator = medications.iterator();
            while (iterator.hasNext()) {
                MedicationDto medication = iterator.next();
                if (mediId.equals(medication.getMediId())) {
                    iterator.remove();
                    notifyDataSetChanged();
                    break;
                }
            }
        }

        private void updateMedicationsList(MedicationDto medicationDto) {
            for (int i = 0; i < medications.size(); i++) {
                MedicationDto currentMedication = medications.get(i);
                if (currentMedication.getMediId().equals(mediId)) {
                    medications.set(i, medicationDto);
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }
}
