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

public class PatientMedicationCompletedAdapter
    extends RecyclerView.Adapter<PatientMedicationCompletedAdapter.ViewHolder>
{
    ArrayList<MedicationDto> medications;
    Context context;

    public PatientMedicationCompletedAdapter(Context context, ArrayList<MedicationDto> medications)
    {
        this.context = context;
        this.medications= medications;
    }

    @NonNull
    @Override
    public PatientMedicationCompletedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_medication_completed, parent, false);

        // Passing view to ViewHolder
        PatientMedicationCompletedAdapter.ViewHolder viewHolder = new PatientMedicationCompletedAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PatientMedicationCompletedAdapter.ViewHolder holder, int position) {
        holder.update(medications.get(position));
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView medicine, note, date, time;

        public ViewHolder(View view) {
            super(view);
            Button complete, update;

            medicine = (TextView) view.findViewById(R.id.medicationMedicine);
            note = (TextView) view.findViewById(R.id.medicationNote);
            date = (TextView) view.findViewById(R.id.medicationDate);
            time = (TextView) view.findViewById(R.id.medicationTime);
        }

        public void update(MedicationDto medication)
        {
            medicine.setText(medication.getMedicine());
            note.setText(medication.getNote());

            DateTimeDto dateTime = DateTimeDto.ToDateTimeDto(medication.getTimestamp());
            date.setText(dateTime.getDate().ToString());
            time.setText(dateTime.getTime().ToString());
        }
    }
}
