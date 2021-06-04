package com.example.aplikacja3;

import android.os.AsyncTask;
import android.util.Log;

public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    @Override
    protected Integer doInBackground(String... strings) {
        Log.d("DownloadTask", "Run in background");
        String url = strings[0];
        return 0;
    }
}
