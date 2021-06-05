package com.example.aplikacja3;

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
        state = Enum.valueOf(state_enum.class, in.readString());
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
        dest.writeString(state.toString());
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
