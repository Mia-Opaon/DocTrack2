package com.triadss.doctrack2.activity.patient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.triadss.doctrack2.R;
import com.triadss.doctrack2.dto.DateTimeDto;
import com.triadss.doctrack2.dto.MedicationDto;

import java.util.ArrayList;

public class RecordMedicationAdapter
    extends RecyclerView.Adapter<RecordMedicationAdapter.ViewHolder>
{
    ArrayList<MedicationDto> medications;
    Context context;

    public RecordMedicationAdapter(Context context, ArrayList<MedicationDto> medications)
    {
        this.context = context;
        this.medications= medications;
    }

    @NonNull
    @Override
    public RecordMedicationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_medication_record, parent, false);

        // Passing view to ViewHolder
        RecordMedicationAdapter.ViewHolder viewHolder = new RecordMedicationAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordMedicationAdapter.ViewHolder holder, int position) {
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
