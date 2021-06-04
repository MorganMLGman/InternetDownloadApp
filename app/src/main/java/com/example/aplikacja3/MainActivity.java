package com.example.aplikacja3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Button download_info;
    EditText editText_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_address = findViewById(R.id.editText_address);
        download_info = findViewById(R.id.button_download_info);

        download_info.setOnClickListener(v -> downloadInfo());
    }

    protected void downloadInfo()
    {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(editText_address.getText().toString());
    }
}