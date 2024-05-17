package com.triadss.doctrack2.activity.patient.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.triadss.doctrack2.R;
import com.triadss.doctrack2.dto.AppointmentDto;
import com.triadss.doctrack2.dto.DateTimeDto;
import com.triadss.doctrack2.dto.TimeDto;

import java.util.ArrayList;

// Extends the Adapter class to RecyclerView.Adapter
// and implement the unimplemented methods
public class PatientAppointmentStatusAdapter extends RecyclerView.Adapter<PatientAppointmentStatusAdapter.ViewHolder> {
    ArrayList<AppointmentDto> appointments;
    Context context;

    // Constructor for initialization
    public PatientAppointmentStatusAdapter(Context context, ArrayList<AppointmentDto> appointments) {
        this.context = context;
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public PatientAppointmentStatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_appointment_status, parent, false);

        // Passing view to ViewHolder
        PatientAppointmentStatusAdapter.ViewHolder viewHolder = new PatientAppointmentStatusAdapter.ViewHolder(view);
        return viewHolder;
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull PatientAppointmentStatusAdapter.ViewHolder holder, int position) {
        // TypeCast Object to int type
        holder.update(appointments.get(position));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView purpose,date,time,status, idText, nameText, age, birthday;

        public ViewHolder(View view) {
            super(view);
            Button cancel, reschedule;
            purpose = (TextView) view.findViewById(R.id.purposetext);
            date = (TextView) view.findViewById(R.id.appointment_date);
            time = (TextView) view.findViewById(R.id.appointment_time);
            status = (TextView) view.findViewById(R.id.statustext);
            idText = view.findViewById(R.id.IDtext);
            nameText = view.findViewById(R.id.nametext);
            age = view.findViewById(R.id.agetext);
            birthday = view.findViewById(R.id.birthdaytext);
        }

        public void update(AppointmentDto appointment)
        {
            purpose.setText(appointment.getPurpose());
            status.setText(appointment.getStatus());
            idText.setText(appointment.getPatientIdNumber());
            nameText.setText(appointment.getNameOfRequester());
            birthday.setText(DateTimeDto.ToDateTimeDto(appointment.getPatientBirthday()).getDate().ToString());
            age.setText(String.valueOf(appointment.getPatientAge()));


            DateTimeDto dateTime = DateTimeDto.ToDateTimeDto(appointment.getDateOfAppointment());
            date.setText(dateTime.getDate().ToString());

            DateTimeDto rangeEnd = dateTime.Clone();
            TimeDto startTime = dateTime.getTime();
            rangeEnd.setTime(new TimeDto(startTime.getHour() + 1, startTime.getMinute()));

            time.setText(String.format("%s - %s", dateTime.getTime().ToString(), rangeEnd.getTime().ToString()));
        }
    }
}
