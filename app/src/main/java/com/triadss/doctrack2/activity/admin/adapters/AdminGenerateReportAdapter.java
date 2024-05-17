package com.triadss.doctrack2.activity.admin.adapters;

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

public class AdminGenerateReportAdapter extends RecyclerView.Adapter<AdminGenerateReportAdapter.ViewHolder> {
    private final String TAG = "PatientMedicationOngoingAdapter";
    private ArrayList<ReportDto> reports;
    private Context context;

    public AdminGenerateReportAdapter(Context context, ArrayList<ReportDto> reports) {
        this.context = context;
        this.reports = reports;
    }

    @NonNull
    @Override
    public AdminGenerateReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_reports_admin, parent, false);

        // Passing view to ViewHolder
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminGenerateReportAdapter.ViewHolder holder, int position) {
        holder.update(reports.get(position));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView description, action, date, createdBy;

        public ViewHolder(View view) {
            super(view);
            description = view.findViewById(R.id.descriptionValue);
            action = view.findViewById(R.id.actionValue);
            date = view.findViewById(R.id.dateValue);
            createdBy = view.findViewById(R.id.createdByValue);
        }

        public void update(ReportDto reportDto) {
            description.setText(reportDto.getMessage());
            action.setText(reportDto.getAction());
            createdBy.setText(reportDto.getCreatedBy());
            DateTimeDto dateTime = DateTimeDto.ToDateTimeDto(reportDto.getCreatedDate());
            date.setText(dateTime.ToString());
        }
    }
}
