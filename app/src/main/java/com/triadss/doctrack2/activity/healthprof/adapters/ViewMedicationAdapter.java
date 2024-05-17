package com.triadss.doctrack2.activity.healthprof.adapters;

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

public class ViewMedicationAdapter
    extends RecyclerView.Adapter<ViewMedicationAdapter.ViewHolder>
{
    ArrayList<MedicationDto> medications;
    Context context;

    public ViewMedicationAdapter(Context context, ArrayList<MedicationDto> medications)
    {
        this.context = context;
        this.medications= medications;
    }

    @NonNull
    @Override
    public ViewMedicationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_medication_view, parent, false);

        // Passing view to ViewHolder
        ViewMedicationAdapter.ViewHolder viewHolder = new ViewMedicationAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewMedicationAdapter.ViewHolder holder, int position) {
        holder.update(medications.get(position));
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView medicine, note, date;
        public ViewHolder(View view) {
            super(view);
            medicine = (TextView) view.findViewById(R.id.input_medicineName);
            note = (TextView) view.findViewById(R.id.input_note);
            date = (TextView) view.findViewById(R.id.input_dateGivin);
        }

        public void update(MedicationDto medication)
        {
            medicine.setText(medication.getMedicine());
            note.setText(medication.getNote());
            DateTimeDto datetime = DateTimeDto.ToDateTimeDto(medication.getTimestamp());
            date.setText(datetime.formatDate());
        }
    }
}
