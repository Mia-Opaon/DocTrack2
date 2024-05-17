package com.triadss.doctrack2.activity.healthprof.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.config.constants.ErrorMessageConstants;
import com.triadss.doctrack2.config.constants.ReportConstants;
import com.triadss.doctrack2.config.constants.ToastConstants;
import com.triadss.doctrack2.dto.AppointmentDto;
import com.triadss.doctrack2.dto.DateDto;
import com.triadss.doctrack2.dto.DateTimeDto;
import com.triadss.doctrack2.dto.TimeDto;
import com.triadss.doctrack2.helper.ButtonManager;
import com.triadss.doctrack2.repoositories.AppointmentRepository;
import com.triadss.doctrack2.repoositories.NotificationRepository;
import com.triadss.doctrack2.utils.AppointmentFunctions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class HealthProfessionalAppointmentPendingAdapter extends RecyclerView.Adapter<HealthProfessionalAppointmentPendingAdapter.ViewHolder> {
    ArrayList<AppointmentDto> healthProfessional;
    Context context;
    AppointmentRepository appointmentRepository;
    NotificationRepository notificationRepository = new NotificationRepository();
    private String TimePick;
    private Map<String, Timestamp> holidayList = Collections.synchronizedMap(new HashMap<>());

    public String getTimePick(){
        return TimePick;
    }

    public void setTimePick(String TimePick){
        if(TimePick.equals("8:00 am - 9:00 am")){
            TimePick = "8";
        }
        if(TimePick.equals("9:00 am - 10:00 am")){
            TimePick = "9";
        }
        if(TimePick.equals("10:00 am - 11:00 am")){
            TimePick ="10";
        }
        if(TimePick.equals("11:00 am - 12:00 pm")){
            TimePick = "11";
        }
        if(TimePick.equals("12:00 pm - 1:00 pm")){
            TimePick = "12";
        }
        if(TimePick.equals("1:00 pm - 2:00 pm")){
            TimePick = "13";
        }
        if(TimePick.equals("2:00 pm - 3:00 pm")){
            TimePick = "14";
        }
        if(TimePick.equals("3:00 pm - 4:00 pm")){
            TimePick = "15";
        }
        if(TimePick.equals("4:00 pm - 5:00 pm")){
            TimePick = "16";
        }
        this.TimePick = TimePick;
    }

    // Constructor for initialization
    public HealthProfessionalAppointmentPendingAdapter(Context context,  ArrayList<AppointmentDto> healthProfessional, Map<String, Timestamp> holidayList) {
        this.context = context;
        this.healthProfessional = healthProfessional;
        this.holidayList = holidayList;
    }

    @NonNull
    @Override
    public HealthProfessionalAppointmentPendingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        appointmentRepository = new AppointmentRepository();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_appointment_pending, parent, false);

        // Passing view to ViewHolder
        HealthProfessionalAppointmentPendingAdapter.ViewHolder viewHolder = new HealthProfessionalAppointmentPendingAdapter.ViewHolder(view);
        return viewHolder;
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull HealthProfessionalAppointmentPendingAdapter.ViewHolder holder, int position) {
        // TypeCast Object to int type
        holder.update(healthProfessional.get(position));
    }

    @Override
    public int getItemCount() {
        return healthProfessional.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView purpose, date, time, identification, name, documentId,patientName,  DocId, age, birthday;
        private Button reschedule, cancel, info;

        public ViewHolder(View view) {
            super(view);
            purpose = (TextView) view.findViewById(R.id.purposetext);
            date = (TextView) view.findViewById(R.id.appointment_date);
            time = (TextView) view.findViewById(R.id.appointment_time);
            identification = (TextView) view.findViewById(R.id.IDtext);
            name = (TextView) view.findViewById(R.id.nametext);
            cancel = (Button) itemView.findViewById(R.id.cancel_button);
            reschedule = (Button) itemView.findViewById(R.id.reschedule_button);
            documentId = (TextView) view.findViewById(R.id.IDtext);
            patientName = view.findViewById(R.id.nametext);
            age = view.findViewById(R.id.agetext);
            birthday = view.findViewById(R.id.birthdaytext);

            DocId = (TextView) view.findViewById(R.id.DocumentID);
        }

        public void update(AppointmentDto appointment) {
            purpose.setText(appointment.getPurpose());
            identification.setText(appointment.getPatientIdNumber());
            name.setText(appointment.getNameOfRequester());
            birthday.setText(DateTimeDto.ToDateTimeDto(appointment.getPatientBirthday()).getDate().ToString());
            age.setText(String.valueOf(appointment.getPatientAge()));

            DateTimeDto dateTime = DateTimeDto.ToDateTimeDto(appointment.getDateOfAppointment());
            date.setText(dateTime.getDate().ToString());

            TimeDto startTime = dateTime.getTime();
            TimeDto rangeEnd = new TimeDto(startTime.getHour() + 1, startTime.getMinute());

            time.setText(String.format("%s - %s", dateTime.getTime().ToString(), rangeEnd.ToString()));

            Button reschedule = (Button)itemView.findViewById(R.id.reschedule_button);

            reschedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), purpose.getText(), Toast.LENGTH_SHORT).show();
                    showUpdateDialog(appointment);
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), appointment.getDocumentId(), Toast.LENGTH_SHORT).show();
                    android.app.AlertDialog.Builder alertDialog = new AlertDialog.Builder(itemView.getContext());

                    alertDialog.setTitle("Canceling");
                    alertDialog.setMessage("Are you sure?");
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            android.app.AlertDialog.Builder progressDialog = new AlertDialog.Builder(
                                    itemView.getContext());

                            getBindingAdapterPosition();
                            SharedPreferences sharedPreferences = itemView.getContext()
                                    .getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
                            SharedPreferences.Editor myEdit = sharedPreferences.edit();

                            AlertDialog alertDialog = (AlertDialog) dialog;

                            Button yesButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            ButtonManager.disableButton(yesButton);

                            appointmentRepository.cancelAppointment(appointment.getDocumentId(),
                                    new AppointmentRepository.AppointmentCancelCallback() {
                                        @Override
                                        public void onSuccess(String appointmentId) {
                                            notificationRepository
                                                .NotifyCancelledAppointmentToHealthProf(appointmentId);
                                            Toast.makeText(context, ToastConstants.CANCELLED, Toast.LENGTH_SHORT).show();

                                            myEdit.putInt("PatientPending", Integer.parseInt("10"));
                                            myEdit.putInt("PatientStatus", Integer.parseInt("10"));
                                            myEdit.apply();
                                        }

                                        @Override
                                        public void onError(String errorMessage) {
                                            ButtonManager.enableButton(yesButton);
                                        }
                                    });

                            appointmentRepository.addReport(appointment.getDocumentId(), ReportConstants.CANCELLED_APPOINTMENT,
                                new AppointmentRepository.ReportCallback() {
                                    @Override
                                    public void onSuccess(String appointmentId) {
                                    }

                                    @Override
                                    public void onError(String errorMessage) {

                                    }
                                });
                        }
                    });
                    alertDialog.show();
                }
            });
        }

        private void showUpdateDialog(AppointmentDto dto) {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.fragment_patient_appointment_reschedule);

            Button dateBtn = dialog.findViewById(R.id.dateBtn);
            TextView updateDate = dialog.findViewById(R.id.updateDate);

            Button timeBtn = dialog.findViewById(R.id.timeBtn);

            TextView updateTime = dialog.findViewById(R.id.updateTime);
            TextView dateErrorText = dialog.findViewById(R.id.dateErrorText);
            TextView timeErrorText = dialog.findViewById(R.id.timeErrorText);

            Button confirmBtn = dialog.findViewById(R.id.confirmbutton);
            DateTimeDto selectedDateTime = new DateTimeDto();

            DateTimeDto dateOfAppointment = DateTimeDto.ToDateTimeDto(dto.getDateOfAppointment());

            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
            TimeZone tz = TimeZone.getDefault();
            inFormat.setTimeZone(tz);

            dateBtn.setOnClickListener((View.OnClickListener) v -> {
                // Get the current date
                DateDto date = dateOfAppointment.getDate();

                // Create and show the Date Picker Dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        (view, year1, monthOfYear, dayOfMonth) -> {
                            if (DateDto.isDayWeekend(year1, monthOfYear, dayOfMonth)) {
                                Toast.makeText(context, ErrorMessageConstants.CANNOT_SELECT_WEEKEND_APPOINTMENTS, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(DateDto.checkDayIfHoliday(holidayList, year1, monthOfYear, dayOfMonth)){
                                Toast.makeText(context, "Cannot select a holiday", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Store the selected date
                            selectedDateTime.setDate(new DateDto(year1, monthOfYear + 1, dayOfMonth));

                            updateDate.setText(selectedDateTime.getDate().ToString(false));

                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            String currentDate = df.format(Calendar.getInstance().getTime());

                            Date sunday = null;
                            try {
                                sunday = inFormat.parse(updateDate.getText().toString());
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
                            String goal = outFormat.format(sunday);
                            // Update the text on the button
                            if (currentDate.compareTo(updateDate.getText().toString()) > 0) {
                                updateDate.setText("Date");
                                Toast.makeText(itemView.getContext(), "Cannot select past date", Toast.LENGTH_SHORT)
                                        .show();
                            }
                            if (goal.equals("Sunday")) {
                                updateDate.setText("Date");
                                Toast.makeText(itemView.getContext(), "No Appointments on Sunday", Toast.LENGTH_SHORT)
                                        .show();
                            }

                        }, date.getYear(), date.getMonth(), date.getDay());
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                // Show the Date Picker Dialog
                datePickerDialog.show();
                updateTime.setText("Time");
            });

            timeBtn.setOnClickListener(v -> {
                if (updateDate.getText().toString().equals("Date") || updateDate.getText().toString().equals(" ")) {

                    Toast.makeText(itemView.getContext(), "Error: must select date first", Toast.LENGTH_SHORT)
                            .show();
                } else {

                    selectedDateTime.setTime(new TimeDto(0, 00));
                    Date date = null;
                    try {
                        date = inFormat.parse(updateDate.getText().toString());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
                    SimpleDateFormat outYear = new SimpleDateFormat("yyyy");
                    SimpleDateFormat outMonth = new SimpleDateFormat("MM");
                    SimpleDateFormat outDay = new SimpleDateFormat("dd");

                    String goal = outFormat.format(date);
                    String timepickYear = outYear.format(date);
                    String timepickMonth = outMonth.format(date);
                    String timepickDay = outDay.format(date);

                    appointmentRepository.checkAppointmentExists(goal, selectedDateTime.ToTimestampForTimePicker(), Integer.valueOf(timepickYear), Integer.valueOf(timepickMonth), Integer.valueOf(timepickDay), new AppointmentRepository.CheckAppointmentExistFetchCallback() {
                        public void onSuccess(ArrayList<String> lngList) {

                            //AnotherPickTimeSlot(lngList);
                            Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.time_slots_picker);
                            Button cancelBtn = dialog.findViewById(R.id.timePickCancel);

                            ListView hello = dialog.findViewById(R.id.breakdown);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(itemView.getContext(), android.R.layout.simple_list_item_1, lngList) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {

                                    TextView textView = (TextView) super.getView(position, convertView, parent);
                                    textView.setTextSize(15);
                                    return textView;
                                }
                            };

                            hello.setOnItemClickListener((adapterView, view, i, l) -> {
                                String itemValue = (String) hello.getItemAtPosition(i);
                                Toast.makeText(itemView.getContext(), itemValue, Toast.LENGTH_LONG).show();
                                if (!itemValue.equals("Not available")) {
                                    setTimePick(itemValue);
                                    updateTime.setText(itemValue);
                                    selectedDateTime.setTime(new TimeDto(Integer.parseInt(getTimePick()), 00));
                                    dialog.dismiss();
                                }
                            });
                            cancelBtn.setOnClickListener(v -> {
                                dialog.dismiss();
                            });

                            hello.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            dialog.show();
                        }

                        @Override
                        public void onError(String errorMessage) {

                        }

                    });
                }
            });

            confirmBtn.setOnClickListener(v -> {
                dateErrorText.setVisibility(View.GONE);
                timeErrorText.setVisibility(View.GONE);

                boolean inputInvalid = false;

                if (selectedDateTime.getDate() == null ||  selectedDateTime.getDate().getYear() == 0 || selectedDateTime.getDate().getMonth() == 0 || selectedDateTime.getDate().getDay() == 0) {
                    Toast.makeText(context, "Please select a valid date", Toast.LENGTH_SHORT).show();
                    dateErrorText.setVisibility(View.VISIBLE);
                    inputInvalid = true;
                }

                if (selectedDateTime.getTime() == null || AppointmentFunctions.IsValidHour(selectedDateTime.getTime())) {
                    Toast.makeText(context, "Please select a valid time", Toast.LENGTH_SHORT).show();
                    timeErrorText.setVisibility(View.VISIBLE);
                    inputInvalid = true;
                }

                if(inputInvalid)
                {
                    return;
                }

                if (updateDate.getText().toString().equals("Date") || updateTime.getText().toString().equals("Time") || updateTime.getText().toString().equals(" ")) {
                    Toast.makeText(itemView.getContext(), "Error: must select date and time", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("MySharedPref",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    ButtonManager.disableButton(confirmBtn);

                    notificationRepository.NotifyRescheduledAppointmentToPatient(dto.getDocumentId(),
                            DateTimeDto.ToDateTimeDto(selectedDateTime.ToTimestampForTimePicker()),
                            new NotificationRepository.NotificationPushedCallback() {
                                @Override
                                public void onNotificationDone() {
                                    appointmentRepository.rescheduleAppointment(dto.getDocumentId(),
                                            selectedDateTime.ToTimestampForTimePicker(),
                                            new AppointmentRepository.AppointmentRescheduleCallback() {

                                                @Override
                                                public void onSuccess(String appointmentId) {
                                                    Toast.makeText(context, ToastConstants.RESCHEDULED, Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();

                                                    myEdit.putInt("PatientPending", Integer.parseInt("10"));
                                                    myEdit.putInt("PatientStatus", Integer.parseInt("10"));
                                                    myEdit.apply();
                                                }

                                                @Override
                                                public void onError(String errorMessage) {
                                                    ButtonManager.enableButton(confirmBtn);
                                                }
                                            });
                                }
                            });

                    appointmentRepository.addReport(dto.getDocumentId(), ReportConstants.RESCHEDULED_APPOINTMENT,
                            new AppointmentRepository.ReportCallback() {
                                @Override
                                public void onSuccess(String appointmentId) {

                                }

                                @Override
                                public void onError(String errorMessage) {

                                }
                            });
                }
            });
            dialog.show();
        }
    }
}