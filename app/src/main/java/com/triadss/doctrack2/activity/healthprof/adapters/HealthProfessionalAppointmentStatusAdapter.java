package com.triadss.doctrack2.activity.healthprof.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class HealthProfessionalAppointmentStatusAdapter extends RecyclerView.Adapter<HealthProfessionalAppointmentStatusAdapter.ViewHolder> {
    ArrayList<AppointmentDto> appointments;
    Context context;

    // Constructor for initialization
    public HealthProfessionalAppointmentStatusAdapter(Context context, ArrayList<AppointmentDto> appointments) {
        this.context = context;
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public HealthProfessionalAppointmentStatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml
        // layout file into View object)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_appointment_status, parent, false);

        // Passing view to ViewHolder
        HealthProfessionalAppointmentStatusAdapter.ViewHolder viewHolder = new HealthProfessionalAppointmentStatusAdapter.ViewHolder(view);
        return viewHolder;
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull HealthProfessionalAppointmentStatusAdapter.ViewHolder holder, int position) {
        // TypeCast Object to int type
        holder.update(appointments.get(position));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }


    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView purpose,date,time,status,identification,name, age, birthday;

        public ViewHolder(View view) {
            super(view);
            purpose = (TextView) view.findViewById(R.id.purposetext);
            date = (TextView) view.findViewById(R.id.appointment_date);
            time = (TextView) view.findViewById(R.id.appointment_time);
            status = (TextView) view.findViewById(R.id.statustext);
            identification = (TextView) view.findViewById(R.id.IDtext);
            name = (TextView) view.findViewById(R.id.nametext);
            age = view.findViewById(R.id.agetext);
            birthday = view.findViewById(R.id.birthdaytext);
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

            status.setText(appointment.getStatus());
        }
    }
}
