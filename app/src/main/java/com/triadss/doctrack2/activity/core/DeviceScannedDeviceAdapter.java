package com.triadss.doctrack2.activity.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.triadss.doctrack2.R;
import com.triadss.doctrack2.dto.BluetoothDeviceDto;

import java.util.ArrayList;

public class DeviceScannedDeviceAdapter
    extends RecyclerView.Adapter<DeviceScannedDeviceAdapter.ViewHolder>
{
    ArrayList<BluetoothDeviceDto> devices;
    Context context;

    public DeviceScannedDeviceAdapter(Context context, ArrayList<BluetoothDeviceDto> devices)
    {
        this.context = context;
        this.devices = devices;
    }

    @NonNull
    @Override
    public DeviceScannedDeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_scanned_devices, parent, false);

        // Passing view to ViewHolder
        DeviceScannedDeviceAdapter.ViewHolder viewHolder = new DeviceScannedDeviceAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceScannedDeviceAdapter.ViewHolder holder, int position) {
        holder.update(devices.get(position));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.deviceName);
            address = (TextView) view.findViewById(R.id.deviceAddress);
        }

        public void update(BluetoothDeviceDto device)
        {
            name.setText(device.getName());
            address.setText(device.getAddress());
        }
    }
}
