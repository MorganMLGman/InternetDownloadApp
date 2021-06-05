package com.example.aplikacja3;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class DownloadProgress implements Parcelable {

    public int file_size_bytes;
    public int downloaded_bytes;
    private enum state_enum{ DOWNLOADING, DOWNLOADED, ERROR};
    public state_enum state;

    protected DownloadProgress(Parcel in)
    {
        file_size_bytes = in.readInt();
        downloaded_bytes = in.readInt();
        switch (in.readInt()){
            case 0:
                state = state_enum.DOWNLOADING;
                break;
            case 1:
                state = state_enum.DOWNLOADED;
                break;
            case 2:
                state = state_enum.ERROR;
                break;
            default:
                Log.d("DownloadProgress", "Constructor values not valid");
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(file_size_bytes);
        dest.writeInt(downloaded_bytes);
        switch (state)
        {
            case DOWNLOADING:
                dest.writeInt(0);
                break;
            case DOWNLOADED:
                dest.writeInt(1);
                break;
            case ERROR:
                dest.writeInt(2);
                break;
        }
    }

    public static final Creator<DownloadProgress> CREATOR = new Creator<DownloadProgress>() {
        @Override
        public DownloadProgress createFromParcel(Parcel in) {
            return new DownloadProgress(in);
        }

        @Override
        public DownloadProgress[] newArray(int size) {
            return new DownloadProgress[size];
        }
    };
}
