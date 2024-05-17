package com.triadss.doctrack2.config.model;

/**
 * This class represents the USER model in FireStore.
 * It contains constants for the property names to avoid misspelling when interacting with FireStore.
 */
public class WearableDeviceModel extends AuditModel {
    public static final String deviceId = "deviceId";
    public static final String deviceName = "deviceName";
    public static final String timeSynced = "timeSynced";
    public static final String firmwareVersion = "firmwareVersion";
    public static final String appVersion = "appVersion";
    public static final String remainingBattery = "remainingBattery";
}
