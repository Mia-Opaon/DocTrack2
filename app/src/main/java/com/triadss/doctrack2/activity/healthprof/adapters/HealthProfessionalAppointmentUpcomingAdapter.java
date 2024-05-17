package com.triadss.doctrack2.activity.healthprof.adapters;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.triadss.doctrack2.config.constants.ReportConstants;
import com.triadss.doctrack2.dto.AppointmentDto;
import com.triadss.doctrack2.dto.DateDto;
import com.triadss.doctrack2.dto.DateTimeDto;
import com.triadss.doctrack2.dto.TimeDto;
import com.triadss.doctrack2.helper.ButtonManager;
import com.triadss.doctrack2.repoositories.AppointmentRepository;
import com.triadss.doctrack2.repoositories.NotificationRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

// Extends the Adapter class to RecyclerView.Adapter
// and implement the unimplemented methods
public class HealthProfessionalAppointmentUpcomingAdapter extends RecyclerView.Adapter<HealthProfessionalAppointmentUpcomingAdapter.ViewHolder> {
    ArrayList<AppointmentDto> appointments;
    Context context;
    AppointmentCallback callback;
    AppointmentRepository appointmentRepository;
    // Constructor for initialization
    public HealthProfessionalAppointmentUpcomingAdapter(Context context,  ArrayList<AppointmentDto> appointments, AppointmentCallback callback) {
        this.context = context;
        this.callback = callback;
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public HealthProfessionalAppointmentUpcomingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        appointmentRepository = new AppointmentRepository();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_appointment_upcoming, parent, false);

        // Passing view to ViewHolder
        HealthProfessionalAppointmentUpcomingAdapter.ViewHolder viewHolder = new HealthProfessionalAppointmentUpcomingAdapter.ViewHolder(view);
        return viewHolder;
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull HealthProfessionalAppointmentUpcomingAdapter.ViewHolder holder, int position) {
        // TypeCast Object to int type
        holder.update(appointments.get(position));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView purpose,date,time,identification,name, birthday, age;
        Button accept;

        public ViewHolder(View view) {
            super(view);
            purpose = (TextView) view.findViewById(R.id.purposetext);
            date = (TextView) view.findViewById(R.id.appointment_date);
            time = (TextView) view.findViewById(R.id.appointment_time);
            identification = (TextView) view.findViewById(R.id.IDtext);
            name = (TextView) view.findViewById(R.id.nametext);
            birthday = view.findViewById(R.id.birthdaytext);
            age = view.findViewById(R.id.agetext);
        }

        public void update(AppointmentDto appointment)
        {
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

            Button reject = (Button)itemView.findViewById(R.id.reject_button);

            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_cancel_appointment_confirmation);

                    Button yes = dialog.findViewById(R.id.yesBtn);
                    Button no = dialog.findViewById(R.id.noBtn);

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ButtonManager.disableButton(yes);
                            ButtonManager.disableButton(no);

                            callback.onReject(appointments.get(getAdapterPosition()).getUid());
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });


            accept = (Button)itemView.findViewById(R.id.accept_button);

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showSimilarDialog(appointment);
                }
            });
        }
        private void showSimilarDialog(AppointmentDto dto) {
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat commonFormat = new SimpleDateFormat("HH:mm");

            TimeZone tz = TimeZone.getDefault();
            inFormat.setTimeZone(tz);

            Date similardate = null;
            Date similartime = null;
            try {
                similardate = inFormat.parse(date.getText().toString());
                similartime = commonFormat.parse(time.getText().toString());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            SimpleDateFormat outYear = new SimpleDateFormat("yyyy");
            SimpleDateFormat outMonth = new SimpleDateFormat("MM");
            SimpleDateFormat outDay = new SimpleDateFormat("dd");
            SimpleDateFormat outHour = new SimpleDateFormat("HH");
            SimpleDateFormat outMinute= new SimpleDateFormat("mm");

            String timepickYear = outYear.format(similardate);
            String timepickMonth = outMonth.format(similardate);
            String timepickDay = outDay.format(similardate);
            String timepickhour = outHour.format(similartime);
            String timepickminute = outMinute.format(similartime);

            ButtonManager.disableButton(accept);

            appointmentRepository.checkSimilarAppointmentExists(dto.getUid(),Integer.valueOf(timepickhour), Integer.valueOf(timepickminute),Integer.valueOf(timepickYear), Integer.valueOf(timepickMonth), Integer.valueOf(timepickDay),new AppointmentRepository.PatientSimilarDateUpcomingCallback() {
                @Override
                public void onSuccess(ArrayList<String> lngList) {
                  if(lngList.size() == 2){
                      Dialog dialog = new Dialog(context);
                      dialog.setContentView(R.layout.dialog_accept_appointment_confirmation);

                      Button yes = dialog.findViewById(R.id.yesBtn);
                      Button no = dialog.findViewById(R.id.noBtn);

                      no.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              ButtonManager.enableButton(accept);
                              dialog.dismiss();
                          }
                      });

                      yes.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              callback.onAccept(dto.getUid());
                              dialog.dismiss();
                          }
                      });

                      dialog.show();
                  }
                  else {
                      Dialog dialog = new Dialog(context);
                      dialog.setContentView(R.layout.similar_appointments);
                      Button cancelBtn = dialog.findViewById(R.id.showSimilarCancel);
                      Button acceptBtn = dialog.findViewById(R.id.showSimilarAccept);

                      ListView hello = dialog.findViewById(R.id.breakdown);

                      ArrayAdapter<String> adapter = new ArrayAdapter<String>(itemView.getContext(), android.R.layout.simple_list_item_1, lngList) {
                          @Override
                          public View getView(int position, View convertView, ViewGroup parent) {
                              TextView textView = (TextView) super.getView(position, convertView, parent);
                              textView.setTextSize(15);
                              return textView;
                          }
                      };

                      cancelBtn.setOnClickListener(v -> {
                          ButtonManager.enableButton(accept);
                          dialog.dismiss();
                      });

                      acceptBtn.setOnClickListener(v -> {
                          appointmentRepository.rejectSimilarAppointmentExists(dto.getUid(),Integer.valueOf(timepickhour), Integer.valueOf(timepickminute),Integer.valueOf(timepickYear), Integer.valueOf(timepickMonth), Integer.valueOf(timepickDay),
                                  new AppointmentRepository.BatchRejectCallback() {
                                      @Override
                                      public void onSuccess(List<AppointmentDto> rejectedAppointments) {

                                          callback.onRejectBulk(rejectedAppointments);
                                      }

                                      @Override
                                      public void onError(String errorMessage) {

                                      }
                                  });

                          callback.onAccept(dto.getUid());
                          dialog.dismiss();
                      });

                      hello.setAdapter(adapter);
                      adapter.notifyDataSetChanged();
                      dialog.show();
                  }
                }

                @Override
                public void onError(String errorMessage) {
                    ButtonManager.enableButton(accept);
                }
            });
        }
    }

    public interface AppointmentCallback {
        void onAccept(String appointmentUid);
        void onReject(String appointmentUid);
        void onRejectBulk(List<AppointmentDto> rejectedAppointments);
    }
}
