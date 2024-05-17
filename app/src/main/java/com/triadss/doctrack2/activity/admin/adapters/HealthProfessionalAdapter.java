package com.triadss.doctrack2.activity.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.triadss.doctrack2.R;
import com.triadss.doctrack2.dto.HealthProfDto;

import java.util.ArrayList;


public class HealthProfessionalAdapter extends RecyclerView.Adapter<HealthProfessionalAdapter.ViewHolder> {
    ArrayList<HealthProfDto> healthProfessional;
    Context context;
    Callbacks callback;
    String healthProfUid;
    HealthProfDto healthProfDto;

    // Constructor for initialization
    public HealthProfessionalAdapter(Context context, ArrayList<HealthProfDto> healthProfessional, Callbacks callback) {
        this.context = context;
        this.callback = callback;
        this.healthProfessional = healthProfessional;
    }
    HealthProfessionalAdapter(String healthProfUid, HealthProfDto healthProfDto)
    {
        this.healthProfUid = healthProfUid;
        this.healthProfDto = healthProfDto;
    }

    @NonNull
    @Override
    public HealthProfessionalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_admin_healthprof, parent, false);

        // Passing view to ViewHolder
        HealthProfessionalAdapter.ViewHolder viewHolder = new HealthProfessionalAdapter.ViewHolder(view);
        return viewHolder;
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull HealthProfessionalAdapter.ViewHolder holder, int position) {
        // TypeCast Object to int type
        holder.update(healthProfessional.get(position));
    }

    @Override
    public int getItemCount() {
        return healthProfessional.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView email, name;
        private Button viewBtn, updateBtn;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.textViewAdminName);
            email = (TextView) view.findViewById(R.id.textViewAdminEmail);
            viewBtn = view.findViewById(R.id.viewBtn);
            updateBtn = view.findViewById(R.id.updateBtn);
        }

        public void update(HealthProfDto healthProfDto) {
            name.setText(healthProfDto.getFullName());
            email.setText(healthProfDto.getEmail());

            viewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.OnView(healthProfDto.getHealthProfid());
                }
            });

            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.OnUpdate(healthProfDto.getHealthProfid());
                }
            });
        }
    }

    public interface Callbacks {
        public void OnView(String healthProdUid);
        public void OnUpdate(String healthProdUid);
    }
}