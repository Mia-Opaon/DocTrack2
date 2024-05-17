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
import com.triadss.doctrack2.helper.ButtonManager;

import java.util.ArrayList;

public class AddMedicationAdapter
    extends RecyclerView.Adapter<AddMedicationAdapter.ViewHolder>
{
    ArrayList<MedicationDto> medications;
    Context context;
    Callback callback;

    public AddMedicationAdapter(Context context, ArrayList<MedicationDto> medications, Callback callback)
    {
        this.context = context;
        this.medications= medications;
        this.callback = callback;
    }

    @NonNull
    @Override
    public AddMedicationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_medication_add_update, parent, false);

        // Passing view to ViewHolder
        AddMedicationAdapter.ViewHolder viewHolder = new AddMedicationAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddMedicationAdapter.ViewHolder holder, int position) {
        holder.update(medications.get(position));
    }

    @Override
    public int getItemCount() {
        return medications.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView medicine, note;
        Button delete;
        public ViewHolder(View view) {
            super(view);
            medicine = (TextView) view.findViewById(R.id.input_medicineName);
            note = (TextView) view.findViewById(R.id.input_note);
            delete = view.findViewById(R.id.deleteBtn);
        }

        public void update(MedicationDto medication)
        {
            medicine.setText(medication.getMedicine());
            note.setText(medication.getNote());

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ButtonManager.disableButton(delete);
                    callback.onDelete(medication.getMediId());
                }
            });
        }
    }

    public interface Callback {
        public void onDelete(String medicationId);
    }
}
