package com.example.movieapp.ui.Extension;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.movieapp.ui.Activities.MovieDetailsActivity;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
        // Default constructor
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create a notification when the alarm goes off
        createNotification(context);
        Log.i("notification", "noti");
    }

    private void createNotification(Context context) {
        String channelId = "alarm_channel";
        String channelName = "Alarm Notifications";
        Log.i("calenderrrrrrrrrrrrrrrrrrrr", "noti");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(context, MovieDetailsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Movie App")
                .setContentText("Its The Time To Watch " + " Movie")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(1, builder.build());
    }
}