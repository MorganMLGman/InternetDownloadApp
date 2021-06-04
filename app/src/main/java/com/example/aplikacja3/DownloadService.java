package com.example.aplikacja3;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

public class DownloadService extends IntentService {
    private static final String TASK1 = "com.example.intent_service.action.task1";
    private static final String PARAMETER1 = "com.example.intent_service.extra.parameter1";
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;

    public DownloadService(String name) {
        super(name);
    }

    public DownloadService()
    {
        super("");

    }

    public static void startService(Context context, int parameter)
    {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(TASK1);
        intent.putExtra(PARAMETER1, parameter);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        prepareNotificationChannel();
        notificationManager.notify(NOTIFICATION_ID, createNotification());
        Log.d("Service", "Service started");
    }

    private void prepareNotificationChannel()
    {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = getString(R.string.app_name);
            NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(NOTIFICATION_ID), name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private Notification createNotification()
    {
        Intent notificationIntent = new Intent(this, DownloadService.class);
        Notification.Builder notificationBuilder = new Notification.Builder(this, String.valueOf(NOTIFICATION_ID));
        notificationBuilder.setContentTitle("Downloading")
                .setProgress(100, 100, false)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setWhen(System.currentTimeMillis());

        notificationBuilder.setOngoing(false);

        return notificationBuilder.build();
    }
}
