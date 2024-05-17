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
import com.triadss.doctrack2.dto.DateTimeDto;
import com.triadss.doctrack2.dto.MedicationDto;

import java.util.ArrayList;

// Extends the Adapter class to RecyclerView.Adapter
// and implement the unimplemented methods
public class PatientHomeMedicationAdapter extends RecyclerView.Adapter<PatientHomeMedicationAdapter.ViewHolder> {
    ArrayList<MedicationDto> medication;
    Context context;

    // Constructor for initialization
    public PatientHomeMedicationAdapter(Context context, ArrayList<MedicationDto> medication) {
        this.context = context;
        this.medication = medication;
    }

    @NonNull
    @Override
    public PatientHomeMedicationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_patient_homepage_medication, parent, false);

        // Passing view to ViewHolder
        PatientHomeMedicationAdapter.ViewHolder viewHolder = new PatientHomeMedicationAdapter.ViewHolder(view);
        return viewHolder;
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull PatientHomeMedicationAdapter.ViewHolder holder, int position) {
        // TypeCast Object to int type
        holder.update(medication.get(position));
    }

    @Override
    public int getItemCount() {
        return medication.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView medName, time;

        public ViewHolder(View view) {
            super(view);
            Button cancel, reschedule;
            medName = (TextView) view.findViewById(R.id.editText_medName);
            time = (TextView) view.findViewById(R.id.editText_time);
        }

        public void update(MedicationDto medication)
        {
            medName.setText(medication.getMedicine());

            DateTimeDto dateTime = DateTimeDto.ToDateTimeDto(medication.getTimestamp());
            this.time.setText(dateTime.ToString());
        }
    }
}
