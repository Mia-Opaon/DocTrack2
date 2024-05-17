package com.triadss.doctrack2.activity.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.triadss.doctrack2.R;
import com.triadss.doctrack2.activity.patient.fragments.PatientHomeFragment;
import com.triadss.doctrack2.config.constants.BluetoothConstants;
import com.triadss.doctrack2.dto.AppointmentDto;
import com.triadss.doctrack2.dto.DateTimeDto;
import com.triadss.doctrack2.dto.VitalSignsDto;
import com.triadss.doctrack2.dto.WearableDeviceDto;
import com.triadss.doctrack2.helper.ButtonManager;
import com.triadss.doctrack2.repoositories.AppointmentRepository;
import com.triadss.doctrack2.repoositories.VitalSignsRepository;
import com.triadss.doctrack2.repoositories.WearableDeviceRepository;
import com.triadss.doctrack2.utils.FragmentFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.google.firebase.Timestamp;
import java.util.concurrent.atomic.AtomicBoolean;

public class DeviceFragment extends Fragment {
    private static final String TAG = "DEVICE";
    private final AppointmentRepository appointmentRepository = new AppointmentRepository();

    private TextView receivedMessageTextView;
    private Button syncButton;
    private int receivedMessageNumber = 1;
    private Receiver messageReceiver;
    private int count = 0;

    private final VitalSignsRepository vitalSignsRepo = new VitalSignsRepository();
    private final WearableDeviceRepository wearableDevicesRepo = new WearableDeviceRepository();
    private WearableDeviceDto wearableDeviceDto = new WearableDeviceDto();

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseUser user = auth.getCurrentUser();

    private VitalSignsDto vitalSignsDto = new VitalSignsDto();

    //* TextViews for the Vital Signs
    private TextView bloodPressureValue, temperatureValue, spo2Value, pulseRateValue, weightValue, heightValue, BMIValue;

    //* TextViews for the Device Registered
    private TextView lastSyncVal, deviceNameVal, deviceIDVal, isNearbyVal, noDeviceFound;
    private TableLayout deviceRegisteredTable;

    private static boolean checkOnce = false;

    private DataClient.OnDataChangedListener dataListener;
    private boolean isCurrentInAnAppointment = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_device_with_device, container, false);

        FloatingActionButton homeBtn = rootView.findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> FragmentFunctions.ChangeFragmentNoStack(requireActivity(), new PatientHomeFragment()));

        initializeViews(rootView);
        initializeListeners();
        initVitalSigns();
        startContinuousCheck();
        setupDataClientListener();

        wearableDeviceDto.setOwnerId(auth.getUid());
        return rootView;
    }

    private void initializeListeners() {
        syncButton.setOnClickListener(v -> handleSyncButtonClick());
    }

    private void setupDataClientListener() {
        dataListener = dataEventBuffer -> {
            for (DataEvent event : dataEventBuffer) {
                if (event.getType() == DataEvent.TYPE_CHANGED) {
                    handleDataChangeEvent(event);
                }
            }
        };

        Wearable.getDataClient(requireActivity()).addListener(dataListener);
    }

    private void handleDataChangeEvent(DataEvent event) {
        DataItem dataItem = event.getDataItem();
        if (Objects.equals(dataItem.getUri().getPath(), "/wear_to_mobile")) {
            DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
            String jsonData = dataMap.getString("jsonData");

            updatePulseRate(jsonData);
            updateLastSync(jsonData);
        }
    }

    private void updatePulseRate(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            int pulseRate = jsonObject.getInt("pulseRate");
            vitalSignsDto.setPulseRate(pulseRate);
            vitalSignsRepo.updateVitalSigns(vitalSignsDto, new VitalSignsRepository.AddUpdateCallback() {
                @Override
                public void onSuccess(String vitalSignsId) {
                    setVitalSignsViews(vitalSignsDto);
                    Toast.makeText(requireContext(), "Updated Pulse Rate", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(requireContext(), "Failure Updating Pulse Rate", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON data: " + e.getMessage());
        }
    }

    private void updateLastSync(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String timeSynced = jsonObject.getString("timeSynced");

            wearableDeviceDto.setTimeSynced(timeSynced);
            assert user != null;
            wearableDevicesRepo.updateWearableDevice(
                    wearableDeviceDto.getDeviceId(),
                    user.getUid(),
                    wearableDeviceDto,
                    new WearableDeviceRepository.WearableUpdateCallback() {
                        @Override
                        public void onSuccess() {
                            setDeviceRegisteredViews(wearableDeviceDto);
                            Toast.makeText(getContext(), "Sync Successful", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(requireContext(), "Something's wrong with your wearable", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON data: " + e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataListener != null) {
            Wearable.getDataClient(requireActivity()).addListener(dataListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dataListener != null) {
            Wearable.getDataClient(requireActivity()).removeListener(dataListener);
        }
    }

    private void startContinuousCheck() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(this::checkIfPairedDevice, 0, 10, TimeUnit.SECONDS);
    }

    private void checkIfPairedDevice() {
        Context context = requireContext();
        Task<List<Node>> nodeListTask = Wearable.getNodeClient(context).getConnectedNodes();
        try {
            List<Node> nodes = Tasks.await(nodeListTask);
            for (Node node : nodes) {
                String nodeName = node.getDisplayName();
                String nodeId = node.getId();
                boolean isNearby = node.isNearby();

                wearableDeviceDto.setDeviceId(nodeId);
                wearableDeviceDto.setDeviceName(nodeName);
                wearableDeviceDto.setIsNearby(isNearby);
            }

            showPairedDeviceStatus(nodes.size() == 1);
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
    }

    private void showPairedDeviceStatus(boolean isPaired) {
        requireActivity().runOnUiThread(() -> {
            Handler mainThreadHandler = new Handler(Looper.getMainLooper());
            if (isPaired) {
                mainThreadHandler.post(() -> {
                    if (wearableDeviceDto.getIsNearby()) {
                        noDeviceFound.setVisibility(View.GONE);
                        deviceRegisteredTable.setVisibility(View.VISIBLE);
                    } else {
                        noDeviceFound.setVisibility(View.VISIBLE);
                        deviceRegisteredTable.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "No Wearable Device Nearby. Searching...", Toast.LENGTH_SHORT).show();
                    }
                });
                if (!checkOnce && wearableDeviceDto.getIsNearby()) {
                    wearableDevicesRepo.getWearableDevice(wearableDeviceDto.getDeviceId(), Objects.requireNonNull(user).getUid(), new WearableDeviceRepository.GetWearableDeviceCallback() {
                        @Override
                        public void onSuccess(WearableDeviceDto wearableDevice) {
                            wearableDeviceDto = wearableDevice;
                        }

                        @Override
                        public void onError(String errorMessage) {
                            wearableDevicesRepo.addWearableDevice(wearableDeviceDto, new WearableDeviceRepository.WearableAddCallback() {
                                @Override
                                public void onSuccess(String medicationId) {
                                    Toast.makeText(getContext(), "Device Successfully Registered", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(String errorMessage) {

                                }
                            });
                        }
                    });
                    Toast.makeText(requireContext(), "Paired with a smartwatch", Toast.LENGTH_SHORT).show();
                    checkOnce = true;
                }
                mainThreadHandler.post(() -> setDeviceRegisteredViews(wearableDeviceDto));
            } else {
                mainThreadHandler.post(() -> {
                    noDeviceFound.setVisibility(View.VISIBLE);
                    deviceRegisteredTable.setVisibility(View.GONE);
                });
                if (checkOnce) {
                    Toast.makeText(requireContext(), "Please pair a smartwatch", Toast.LENGTH_SHORT).show();
                    checkOnce = false;
                }
            }
        });
    }

    private void initializeViews(View rootView) {
        receivedMessageTextView = rootView.findViewById(R.id.receivedMessageTextView);
        syncButton = rootView.findViewById(R.id.SyncDeviceBtn);

        //* Vital Signs Views
        bloodPressureValue = rootView.findViewById(R.id.BloodPressureValue);
        temperatureValue = rootView.findViewById(R.id.TemperatureValue);
        spo2Value = rootView.findViewById(R.id.Spo2Value);
        pulseRateValue = rootView.findViewById(R.id.PulseRateValue);
        weightValue = rootView.findViewById(R.id.WeightValue);
        heightValue = rootView.findViewById(R.id.HeightValue);
        BMIValue = rootView.findViewById(R.id.BMIValue);

        //* Device Registered
        lastSyncVal = rootView.findViewById(R.id.LastSyncValue);
        deviceNameVal = rootView.findViewById(R.id.DeviceNameValue);
        deviceIDVal = rootView.findViewById(R.id.DeviceIDValue);
        isNearbyVal = rootView.findViewById(R.id.IsNearbyValue);
        deviceRegisteredTable = rootView.findViewById(R.id.DeviceTable);

        noDeviceFound = rootView.findViewById(R.id.NoDeviceFound);
    }

    private void initVitalSigns() {
        try {
            vitalSignsRepo.getVitalSignOfPatient(Objects.requireNonNull(user).getUid(), new VitalSignsRepository.FetchCallback() {
                @Override
                public void onSuccess(VitalSignsDto vitalSigns) {
                    vitalSignsDto = vitalSigns;
                    setVitalSignsViews(vitalSignsDto);
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Failure in fetching patient's vital signs");
                    Toast.makeText(getContext(), "Sync Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setVitalSignsViews(VitalSignsDto vitalSigns) {
        bloodPressureValue.setText(vitalSigns.getBloodPressure());
        temperatureValue.setText(String.valueOf(vitalSigns.getTemperature()));
        spo2Value.setText(String.valueOf(vitalSigns.getOxygenLevel()));
        pulseRateValue.setText(String.valueOf(vitalSigns.getPulseRate()));
        weightValue.setText(String.valueOf(vitalSigns.getWeight()));
        heightValue.setText(String.valueOf(vitalSigns.getHeight()));
        BMIValue.setText(String.valueOf(vitalSigns.getBMI()));
    }

    private void setDeviceRegisteredViews(WearableDeviceDto wearableDeviceDto) {
        lastSyncVal.setText((wearableDeviceDto.getTimeSynced() == null) ? "No Time Available" : wearableDeviceDto.getTimeSynced());
        deviceNameVal.setText(wearableDeviceDto.getDeviceName());
        deviceIDVal.setText(wearableDeviceDto.getDeviceId());
        isNearbyVal.setText(String.valueOf(wearableDeviceDto.getIsNearby()));
    }

    private boolean checkPendingAppointmentsIfCurrent(String patientId) {
        Timestamp currentTimeStamp = DateTimeDto.GetCurrentTimeStamp();
        AtomicBoolean isCurrent = new AtomicBoolean(false);
        AtomicBoolean noDataFetch = new AtomicBoolean(false);

        appointmentRepository.getAllPatientPendingAppointments(patientId, new AppointmentRepository.AppointmentPatientPendingFetchCallback() {
            @Override
            public void onSuccess(List<AppointmentDto> appointments) {
                if (!appointments.isEmpty()) {
                    for (AppointmentDto appointment : appointments) {
                        if (DateTimeDto.isToday(appointment.getDateOfAppointment()) && DateTimeDto.isCurrentTimeInRange(currentTimeStamp, appointment.getDateOfAppointment())) {
                            isCurrent.set(true);
                            isCurrentInAnAppointment = true;
                            break;
                        }
                    }
                } else {
                    noDataFetch.set(true);
                }

                // Check the conditions after the appointment data is fetched
                handleAppointmentResult(isCurrent.get(), noDataFetch.get());
            }

            @Override
            public void onError(String errorMessage) {
                noDataFetch.set(true);
                handleAppointmentResult(isCurrent.get(), noDataFetch.get());
            }
        });
        return isCurrent.get();
    }

    public interface IsValidCallback {
        void onSuccess(boolean isValid);

        void onError(boolean isNotValid);
    }

    private void handleAppointmentResult(boolean isCurrent, boolean noDataFetch) {
        if (!isCurrent && !noDataFetch) {
            Toast.makeText(getContext(), "Can't sync. You're not currently in an appointment.", Toast.LENGTH_SHORT).show();
        }
        if (noDataFetch) {
            Toast.makeText(getContext(), "There are no appointments made.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSyncButtonClick() {
        try {

            appointmentRepository.getAllPatientPendingAppointments(user.getUid(), new AppointmentRepository.AppointmentPatientPendingFetchCallback() {
                @Override
                public void onSuccess(List<AppointmentDto> appointments) {
                    Timestamp currentTimeStamp = DateTimeDto.GetCurrentTimeStamp();
                    boolean noDataFetch = false;
                    boolean isCurrent = false;
                    if (!appointments.isEmpty()) {
                        for (AppointmentDto appointment : appointments) {
                            if (DateTimeDto.isToday(appointment.getDateOfAppointment()) && DateTimeDto.isCurrentTimeInRange(currentTimeStamp, appointment.getDateOfAppointment())) {
                                isCurrent = true;

                                break;
                            }
                        }
                    } else {
                        noDataFetch = true;
                    }

                    // Check the conditions after the appointment data is fetched
                    handleAppointmentResult(isCurrent, noDataFetch);

                    if(isCurrent && !noDataFetch){
                        Toast.makeText(getContext(), "Syncing...", Toast.LENGTH_SHORT).show();

                        ButtonManager.disableButton(syncButton);

                        vitalSignsRepo.getVitalSignOfPatient(Objects.requireNonNull(user).getUid(), new VitalSignsRepository.FetchCallback() {
                            @Override
                            public void onSuccess(VitalSignsDto vitalSigns) {
                                String jsonData = vitalSigns.toJsonData();
                                sendMessage(jsonData);
                                ButtonManager.enableButton(syncButton);
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Log.e(TAG, "Failure in fetching patient's vital signs");
                                Toast.makeText(getContext(), "Sync Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                                ButtonManager.enableButton(syncButton);
                            }
                        });
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    handleAppointmentResult(false, true);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            ButtonManager.enableButton(syncButton);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (messageReceiver != null) {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(messageReceiver);
            messageReceiver = null;
        }
    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = "\nI just received a message from the wearable "
                    + receivedMessageNumber++
                    + " \n" + intent.getStringExtra(BluetoothConstants.MessageKey);
            receivedMessageTextView.append(message);
        }
    }

    private void sendMessage(String jsonData) {
        String jsonDataWithCount = jsonData + " Count: " + count;
        PutDataMapRequest dataMap = PutDataMapRequest.create("/mobile_to_wear");
        dataMap.getDataMap().putString("jsonData", jsonDataWithCount);
        PutDataRequest request = dataMap.asPutDataRequest();
        Task<DataItem> putDataTask = Wearable.getDataClient(requireContext()).putDataItem(request);

        putDataTask.addOnSuccessListener(dataItem -> count++)
                .addOnFailureListener(e -> Log.e(TAG, "Failed to send data", e));
    }
}