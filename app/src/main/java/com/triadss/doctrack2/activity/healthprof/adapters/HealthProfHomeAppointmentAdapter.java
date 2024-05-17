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
public class HealthProfHomeAppointmentAdapter extends RecyclerView.Adapter<HealthProfHomeAppointmentAdapter.ViewHolder> {
    ArrayList<AppointmentDto> appointments;
    Context context;

    // Constructor for initialization
    public HealthProfHomeAppointmentAdapter(Context context, ArrayList<AppointmentDto> appointments) {
        this.context = context;
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public HealthProfHomeAppointmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_health_prof_home_page_pending, parent, false);

        // Passing view to ViewHolder
        HealthProfHomeAppointmentAdapter.ViewHolder viewHolder = new HealthProfHomeAppointmentAdapter.ViewHolder(view);
        return viewHolder;
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull HealthProfHomeAppointmentAdapter.ViewHolder holder, int position) {
        // TypeCast Object to int type
        holder.update(appointments.get(position));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, purpose, time;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.editText_name);
            purpose = (TextView) view.findViewById(R.id.editText_purpose);
            time = (TextView) view.findViewById(R.id.editText_time);
        }

        public void update(AppointmentDto appointment)
        {
            purpose.setText(appointment.getPurpose());
            name.setText(appointment.getNameOfRequester());
            DateTimeDto dateTime = DateTimeDto.ToDateTimeDto(appointment.getDateOfAppointment());

            TimeDto startTime = dateTime.getTime().Clone();
            TimeDto endTime = new TimeDto(startTime.getHour() + 1, startTime.getMinute());

            this.time.setText(String.format("%s - %s ", dateTime.ToString(), endTime.ToAMPMString()));
        }
    }
}
