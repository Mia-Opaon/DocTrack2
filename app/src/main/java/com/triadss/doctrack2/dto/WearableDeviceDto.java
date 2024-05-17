package com.triadss.doctrack2.dto;

import com.google.type.Date;
import com.google.type.DateTime;

import java.sql.Time;

public class WearableDeviceDto {
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getTimeSynced() {
        return timeSynced;
    }

    public void setTimeSynced(String timeSynced) {
        this.timeSynced = timeSynced;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public int getRemainingBattery() {
        return remainingBattery;
    }

    public void setRemainingBattery(int remainingBattery) {
        this.remainingBattery = remainingBattery;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public boolean getIsNearby() { return isNearby; }
    public void setIsNearby(boolean isNearby){
        this.isNearby = isNearby;
    }
    public String getOwnerId(){
        return ownerId;
    }
    public void setOwnerId(String ownerId){
        this.ownerId = ownerId;
    }

    private String deviceId;
    private String deviceName;
    private String timeSynced;
    private String firmwareVersion;
    private String appVersion;
    private int remainingBattery;
    private boolean isNearby;
    private String ownerId;
}
