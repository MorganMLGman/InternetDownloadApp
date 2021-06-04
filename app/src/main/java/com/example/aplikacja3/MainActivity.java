package com.example.aplikacja3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    Button download_info;
    EditText editText_address;
    TextView file_size_info;
    TextView file_type_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_address = findViewById(R.id.editText_address);
        file_size_info = findViewById(R.id.textView_file_size_info);
        file_type_info = findViewById(R.id.textView_file_type_info);
        download_info = findViewById(R.id.button_download_info);

        download_info.setOnClickListener(v -> downloadInfo());
    }

    protected void downloadInfo()
    {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(editText_address.getText().toString());
    }


    public class DownloadTask extends AsyncTask<String, Integer, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String[] doInBackground(String... strings) {
            Log.d("DownloadTask", "Run in background");
            URL url;
            try {
                url = new URL(strings[0]);
            } catch (MalformedURLException e) {
                Log.d("Error: ", e.getMessage());
                return null;
            }
            HttpsURLConnection connection = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
            } catch (IOException e) {
                Log.d("Error: ", e.getMessage());
            }
            if (connection == null) {
                return null;
            }
            try {
                connection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                Log.d("Error: ", e.getMessage());
            }
            int size = connection.getContentLength();
            String type = connection.getContentType();

            Log.d("Info: ", size + " " + type);

            String[] ret = new String[2];
            ret[0] = String.valueOf(size);
            ret[1] = type;

            return ret;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            file_size_info.setText(strings[0]);
            file_type_info.setText(strings[1]);

        }
    }
}

