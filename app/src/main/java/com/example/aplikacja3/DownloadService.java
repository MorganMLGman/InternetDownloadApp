package com.example.aplikacja3;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class DownloadService extends IntentService {
    private static final String TASK1 = "com.example.intent_service.action.task1";
    private static final String PARAMETER1 = "com.example.intent_service.extra.parameter1";
    private static final String URL_PARAM = "com.example.intent_service.extra.url_param";
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;

    public static final String PARCELABLE_NOTIFICATION = "com.example.intent_service.receiver";
    public static final String FILE_SIZE = "file_size";
    public static final String DOWNLOADED_SIZE = "downloaded_size";
    public static final String DOWNLOAD_STATUS = "download_status";


    private int downloadedBytes = 0;
    private int progress = 0;

    public DownloadService(String name) {
        super(name);
    }

    public DownloadService()
    {
        super("");

    }

    public static void startService(Context context, int parameter, String url)
    {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(TASK1);
        intent.putExtra(PARAMETER1, parameter);
        intent.putExtra(URL_PARAM, url);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        Log.d("Service", "Service started");
        prepareNotificationChannel();
        notificationManager.notify(NOTIFICATION_ID, createNotification(progress));
        assert intent != null;
        String url = intent.getStringExtra(URL_PARAM);
        DownloadFileTask downloadFileTask = new DownloadFileTask();
        downloadFileTask.execute(url);
    }

    private void prepareNotificationChannel()
    {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        CharSequence name = getString(R.string.app_name);
        NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(NOTIFICATION_ID), name, NotificationManager.IMPORTANCE_LOW);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private Notification createNotification(int progress)
    {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification.Builder notificationBuilder = new Notification.Builder(this, String.valueOf(NOTIFICATION_ID));
        if(progress == 100)
        {
            notificationBuilder.setContentTitle("Download complete")
                    .setProgress(0, 0, false)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent);
        }
        else
        {
            notificationBuilder.setContentTitle("Downloading")
                    .setProgress(100, progress, false)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent);
        }


        notificationBuilder.setOngoing(false);

        return notificationBuilder.build();
    }

    private void sendInfoBroadcast(int total_size, int progress, int current_state)
    {
        Intent intent= new Intent(PARCELABLE_NOTIFICATION);
        intent.putExtra(FILE_SIZE, total_size);
        intent.putExtra(DOWNLOADED_SIZE, progress);
        intent.putExtra(DOWNLOAD_STATUS, current_state);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public class DownloadFileTask extends AsyncTask<String, Integer, Integer>
    {

        @Override
        protected Integer doInBackground(String... strings) {
            Log.d("DownloadFileTask", "Run in background");
            URL url;
            try {
                url = new URL(strings[0]);
            } catch (MalformedURLException e) {
                Log.d("Error: ", Objects.requireNonNull(e.getMessage()));
                return null;
            }
            HttpsURLConnection connection = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
            } catch (IOException e) {
                Log.d("Error: ", Objects.requireNonNull(e.getMessage()));
            }
            if (connection == null) {
                return null;
            }
            try {
                connection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                Log.d("Error: ", Objects.requireNonNull(e.getMessage()));
            }

            int fileSize = connection.getContentLength();

            File temp = new File(url.getFile());
            File outputFile = new File(Environment.getExternalStorageDirectory() + File.separator + temp.getName());

            if(outputFile.exists())
            {
                outputFile.delete();
            }

            FileOutputStream fileStream = null;
            try {
                fileStream = new FileOutputStream(outputFile);
            } catch (FileNotFoundException e) {
                Log.d("File output: ", Objects.requireNonNull(e.getMessage()));
            }

            OutputStream outputStream = fileStream;
            InputStream inputStream = null;

            try {
                inputStream = new BufferedInputStream(url.openStream(), 8192);
            } catch (IOException e) {
                Log.d("Input Stream: ", Objects.requireNonNull(e.getMessage()));
            }

            byte[] data = new byte[1024];
            int downloaded;
            try
            {
                while ((downloaded = inputStream.read(data)) != -1)
                {
                    outputStream.write(data, 0, downloaded);
                    publishProgress(downloaded, fileSize);
                }
            }
            catch (IOException e){
                Log.d("Download error: ", Objects.requireNonNull(e.getMessage()));
            }

            try {
                outputStream.flush();
            } catch (IOException e) {
                Log.d("OutputStream", Objects.requireNonNull(e.getMessage()));
            }

            try {
                outputStream.close();
            } catch (IOException e) {
                Log.d("Output Stream", Objects.requireNonNull(e.getMessage()));
            }


            try {
                inputStream.close();
            } catch (IOException e) {
                Log.d("Input Stream", Objects.requireNonNull(e.getMessage()));
            }

            connection.disconnect();

            Log.d("INFO: ", "File downloaded");

            return 0;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            downloadedBytes += values[0];
            int percentage = (downloadedBytes * 100) / values[1];
            if(percentage >= progress + 5)
            {
                Log.d("Progress: ", String.valueOf(percentage));
                progress = percentage;
                notificationManager.notify(NOTIFICATION_ID, createNotification(progress));
                sendInfoBroadcast(values[1], downloadedBytes, (percentage == 100) ? 0 : 1 );
            }
            super.onProgressUpdate(values);
        }
    }
}
