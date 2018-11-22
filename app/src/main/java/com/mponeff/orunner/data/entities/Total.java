package com.mponeff.orunner.data.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Total implements Parcelable {

    private String id;
    private int activities;
    private double distance;
    private long duration;

    public Total(int activities, double distance, long duration) {
        this.activities = activities;
        this.distance = distance;
        this.duration = duration;
    }

    protected Total(Parcel in) {
        id = in.readString();
        activities = in.readInt();
        distance = in.readDouble();
        duration = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(activities);
        dest.writeDouble(distance);
        dest.writeLong(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Total> CREATOR = new Creator<Total>() {
        @Override
        public Total createFromParcel(Parcel in) {
            return new Total(in);
        }

        @Override
        public Total[] newArray(int size) {
            return new Total[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getActivities() {
        return activities;
    }

    public void setActivities(int activities) {
        this.activities = activities;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
