package com.triadss.doctrack2.activity.healthprof.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.triadss.doctrack2.R;
import com.triadss.doctrack2.dto.AddPatientDto;

import java.util.List;

public class PatientFragmentAdapter extends RecyclerView.Adapter<PatientFragmentAdapter.PatientFragmentViewHolder> {
    List<AddPatientDto> addPatientDtoList;

    private Callbacks callback;

    public interface OnItemClickListener {
        void onItemClick(AddPatientDto patient);
    }


    public PatientFragmentAdapter(List<AddPatientDto> patientList, Callbacks callback) {
        this.addPatientDtoList = patientList;
        this.callback = callback;
    }

    public void updateList(List<AddPatientDto> newData) {
        addPatientDtoList.clear();
        addPatientDtoList.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PatientFragmentAdapter.PatientFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_healthprof_patient, parent, false);
        return new PatientFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientFragmentAdapter.PatientFragmentViewHolder holder, int position) {
        holder.update(addPatientDtoList.get(position));
    }

    @Override
    public int getItemCount() {
        return addPatientDtoList.size();
    }

    public class PatientFragmentViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView idNumber;
        Button viewRecord;
        View view;

        public PatientFragmentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView_patientName);
            idNumber = itemView.findViewById(R.id.textView_patientId);
            view = itemView;
        }

        public void update(AddPatientDto patient)
        {
            name.setText(patient.getFullName());
            idNumber.setText(patient.getIdNumber());

            view.setOnClickListener(v -> {
                callback.onPatientView(patient.getUid());
            });
        }
    }

    public interface Callbacks {
        void onPatientView(String patientUid);

    }
}
