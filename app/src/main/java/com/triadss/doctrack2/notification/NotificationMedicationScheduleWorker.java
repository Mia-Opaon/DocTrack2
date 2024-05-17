 package com.triadss.doctrack2.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
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
import com.triadss.doctrack2.dto.MedicationDto;
import com.triadss.doctrack2.dto.NotificationDTO;
import com.triadss.doctrack2.repoositories.MedicationRepository;

import java.util.List;

public class NotificationMedicationScheduleWorker extends Worker {
    private SharedPreferences sharedPref;
    Context context;
    public NotificationMedicationScheduleWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }
    @NonNull
    @Override
    public Result doWork() {
        if(isStopped())
        {
            return Result.success();
        }

        //Get Medications
        String receiverUserUid = getInputData().getString(NotificationConstants.RECEIVER_ID);
        String title = getInputData().getString(NotificationConstants.TITLE_ID);
        String content = getInputData().getString(NotificationConstants.CONTENT_ID);

        sharedPref = context.getSharedPreferences(SessionConstants.SessionPreferenceKey, Context.MODE_PRIVATE);

        Log.e("TEST", "Notified Medication Work: " + title +
                " at: " + DateTimeDto.ToDateTimeDto(Timestamp.now()).ToString() +
                " with content: " + content);

        NotificationDTO notif = new NotificationDTO();
        notif.setTitle(title);
        notif.setContent(content);

        notify(notif);

        return Result.success();
    }

    @SuppressLint("MissingPermission")

    public void notify(NotificationDTO dto) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        int lastNotificationId = sharedPref.getInt(SessionConstants.LastNotificationId, 1);
        notificationManager.notify(lastNotificationId, getNotification(dto));
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(SessionConstants.LastNotificationId, lastNotificationId + 1);
        editor.apply();
    }

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
