package com.triadss.doctrack2.notification;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.Timestamp;
import com.triadss.doctrack2.R;
import com.triadss.doctrack2.config.constants.NotificationConstants;
import com.triadss.doctrack2.config.constants.SessionConstants;
import com.triadss.doctrack2.dto.DateTimeDto;
import com.triadss.doctrack2.dto.NotificationDTO;
import com.triadss.doctrack2.repoositories.NotificationRepository;

import java.util.List;


public class NotificationBackgroundWorker extends Worker {
    NotificationRepository getnotify = new NotificationRepository();
    private SharedPreferences sharedPref;
    Context context;
    public NotificationBackgroundWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @Override
    public Result doWork() {
        String receiverUserUid = getInputData().getString(NotificationConstants.RECEIVER_ID);
        DateTimeDto datetimedto = new DateTimeDto();

        sharedPref = context.getSharedPreferences(SessionConstants.SessionPreferenceKey, Context.MODE_PRIVATE);
        String lastRequestDate = sharedPref.getString(SessionConstants.LastRequestDate, null);
        if (lastRequestDate == null) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SessionConstants.LastRequestDate, datetimedto.GetCurrentTimeStamp().toString());
            editor.apply();
        }

        // Extract the seconds part from the lastRequestDate string
        String secondsStr = lastRequestDate.substring(lastRequestDate.indexOf("seconds=") + 8, lastRequestDate.indexOf(","));
        long seconds = Long.parseLong(secondsStr);

        // Create a new Timestamp object using the extracted seconds
        Timestamp startDate = new Timestamp(seconds, 0); // The nanoseconds part is set to 0

        Log.e("TEST", "Running Work for " + receiverUserUid + " since " + DateTimeDto.ToDateTimeDto(startDate).ToString() + " seconds " + lastRequestDate);
        // scheduleNotification(getNotification("1 second delay"), 1000);
        NotificationChannel channel = new NotificationChannel(NotificationConstants.NOTIFICATION_CHANNEL_ID,
                NotificationConstants.NOTIFICATION_TAG, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(context, NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        getnotify.fetchUserNotification(receiverUserUid, lastRequestDate, new NotificationRepository.FetchNotificationAddCallback() {
            @Override
            public void onSuccess(List<NotificationDTO> notificationList) { // fetch notification data
                for (NotificationDTO notifyDti : notificationList) {
                    showNotification(notifyDti);
                    Log.e("TEST", "Found notifcation" + notifyDti.getTitle() + " at " +
                        DateTimeDto.ToDateTimeDto(notifyDti.getDateSent()).ToString());
                }

                //Update Notification
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(SessionConstants.LastRequestDate, datetimedto.GetCurrentTimeStamp().toString());
                editor.apply();
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error if needed
                System.out.println();
            }
        });

        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }


    // Modify the scheduleNotification method to accept notification content
    @SuppressLint("MissingPermission")
    public void showNotification(NotificationDTO dto) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        int lastNotificationId = sharedPref.getInt(SessionConstants.LastNotificationId, 1);

        notificationManager.notify(lastNotificationId, getNotification(dto));

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(SessionConstants.LastNotificationId, lastNotificationId + 1);
        editor.apply();
    }

    // Modify the getNotification method to accept content parameter
    public Notification getNotification(NotificationDTO dto) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationConstants.DEFAULT_NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(dto.getTitle());
        // Use the custom content for the notification
        builder.setContentText(dto.getContent());
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setAutoCancel(true);
        builder.setChannelId(NotificationConstants.NOTIFICATION_CHANNEL_ID);
        builder.setGroup(NotificationConstants.NOTIFICATION_GROUP);
        return builder.build();
    }

}
