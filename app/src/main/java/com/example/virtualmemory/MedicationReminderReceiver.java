package com.example.virtualmemory;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

public class MedicationReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String reminderText = intent.getStringExtra("reminderText");

        if (reminderText == null) {
            reminderText = "Default reminder text";
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Add logs
        android.util.Log.d("MedicationReminder", "Received alarm for reminder: " + reminderText);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "medication_channel";
            NotificationChannel channel = new NotificationChannel(channelId, "Medication Alerts", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle("Reminder")
                .setContentText(reminderText)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("medication_channel");
        }

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

}
