package com.triadss.doctrack2.activity.healthprof.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.triadss.doctrack2.R;
import com.triadss.doctrack2.dto.DateTimeDto;
import com.triadss.doctrack2.dto.ReportDto;

import java.util.ArrayList;
import java.util.List;

public class HealthProfessionalReportAdapter extends RecyclerView.Adapter<HealthProfessionalReportAdapter.ViewHolder> {
    private final String TAG = "HealthProfessionalReportAdapter";
    private List<ReportDto> reports;
    private Context context;

    public HealthProfessionalReportAdapter(Context context, List<ReportDto> reports) {
        this.context = context;
        this.reports = reports;
    }

    @NonNull
    @Override
    public HealthProfessionalReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_reports, parent, false);

        // Passing view to ViewHolder
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HealthProfessionalReportAdapter.ViewHolder holder, int position) {
        holder.update(reports.get(position));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView description, action, date;
        public ViewHolder(View view) {
            super(view);
            description = view.findViewById(R.id.descriptionValue);
            action = view.findViewById(R.id.actionValue);
            date = view.findViewById(R.id.dateValue);
        }

        public void update(ReportDto reportDto) {
            description.setText(reportDto.getMessage());
            action.setText(reportDto.getAction());

            DateTimeDto dateTime = DateTimeDto.ToDateTimeDto(reportDto.getCreatedDate());
            date.setText(dateTime.formatDateTime());
        }
    }
}
