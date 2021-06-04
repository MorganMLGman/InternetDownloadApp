package com.example.aplikacja3;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    @Override
    protected Integer doInBackground(String... strings) {
        Log.d("DownloadTask", "Run in background");
        URL url;
        try {
            url = new URL(strings[0]);
        } catch (MalformedURLException e) {
            Log.d("Error: ", e.getMessage());
            return -1;
        }
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
        } catch (IOException e) {
            Log.d("Error: ", e.getMessage());
        }
        if (connection == null) {
            return -1;
        }
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            Log.d("Error: ", e.getMessage());
        }
        int size = connection.getContentLength();
        String type = connection.getContentType();

        Log.d("Info: ", size + " " + type);

        return 0;
    }
}
