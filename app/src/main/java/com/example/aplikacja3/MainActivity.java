package com.example.aplikacja3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    Button download_info;
    Button download_file;
    EditText editText_address;
    TextView file_size_info;
    TextView file_type_info;

    private static final int WRITE_EXTERNAL_STORAGE_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_address = findViewById(R.id.editText_address);
        file_size_info = findViewById(R.id.textView_file_size_info);
        file_type_info = findViewById(R.id.textView_file_type_info);
        download_info = findViewById(R.id.button_download_info);
        download_file = findViewById(R.id.button_download_file);

        download_info.setOnClickListener(v -> downloadInfo());

        download_file.setOnClickListener(v ->
        {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                DownloadService.startService(MainActivity.this, 1, editText_address.getText().toString());
            }
            else
            {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    Toast.makeText(this, "Can't run. No permission", Toast.LENGTH_SHORT).show();
                }
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_CODE);
            }
        });

    }

    protected void downloadInfo()
    {
        DownloadInfoTask downloadInfoTask = new DownloadInfoTask();
        downloadInfoTask.execute(editText_address.getText().toString());
    }


    public class DownloadInfoTask extends AsyncTask<String, Integer, String[]> {

        @Override
        protected String[] doInBackground(String... strings) {
            Log.d("DownloadInfoTask", "Run in background");
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
                Log.d("Error: ", Objects.requireNonNull(e.getMessage()));
            }
            int size = connection.getContentLength();
            String type = connection.getContentType();

            Log.d("Info: ", size + " " + type);

            connection.disconnect();

            String[] ret = new String[2];

            ret[0] = String.format("%.3f", size / 1000000.0f);
            ret[1] = type;

            return ret;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            String size = strings[0] + " MB";
            file_size_info.setText(size);
            file_type_info.setText(strings[1]);
            super.onPostExecute(strings);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case WRITE_EXTERNAL_STORAGE_CODE:
                if(permissions.length > 0 && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    DownloadService.startService(MainActivity.this, 1, editText_address.getText().toString());
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }
}

