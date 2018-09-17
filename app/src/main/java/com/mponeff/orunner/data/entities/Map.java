package com.mponeff.orunner.data.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Map implements Parcelable {

    private String downloadUri;
    private String fileUri;
    private String title;
    private String location;
    private long dateMillis;

    /** Required empty constructor */
    public Map() {
    }

    public Map(String downloadUri, String fileUri, String title, String location, long dateMillis) {
        this.downloadUri = downloadUri;
        this.fileUri = fileUri;
        this.title = title;
        this.location = location;
        this.dateMillis = dateMillis;
    }

    public long getDate() {
        return this.dateMillis;
    }

    public void setDate(long dateMillis) {
        this.dateMillis = dateMillis;
    }

    public String getFileUri() {
        return this.fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDownloadUri() {
        return this.downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.downloadUri);
        dest.writeString(this.fileUri);
        dest.writeString(this.title);
        dest.writeString(this.location);
        dest.writeLong(this.dateMillis);
    }

    protected Map(Parcel in) {
        this.downloadUri = in.readString();
        this.fileUri = in.readString();
        this.title = in.readString();
        this.location = in.readString();
        this.dateMillis = in.readLong();
    }

    public static final Parcelable.Creator<Map> CREATOR = new Parcelable.Creator<Map>() {
        @Override
        public Map createFromParcel(Parcel source) {
            return new Map(source);
        }

        @Override
        public Map[] newArray(int size) {
            return new Map[size];
        }
    };
}
