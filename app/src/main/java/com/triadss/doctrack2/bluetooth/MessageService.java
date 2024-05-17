package com.triadss.doctrack2.bluetooth;

import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.triadss.doctrack2.config.constants.BluetoothConstants;

//Extend WearableListenerService//
public class MessageService extends WearableListenerService {
    @Override
    public void onCreate() {
        super.onCreate(); // <-- don't forget this
    }
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //If the message’s path equals "/my_path"...//
        if (messageEvent.getPath().equals(BluetoothConstants.DataPath)) {
            //...retrieve the message//
            final String message = new String(messageEvent.getData());
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra(BluetoothConstants.MessageKey, message);
            //Broadcast the received Data Layer messages locally//
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }
}