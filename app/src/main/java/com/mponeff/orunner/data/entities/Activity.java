package com.mponeff.orunner.data.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Activity implements Parcelable {

    protected static final String TYPE_COMPETITION = "Competition";
    protected static final String TYPE_TRAINING = "Training";

    private String _ID;
    private String type;
    private String title;
    private String location;
    private String details;
    private String pace;
    private String ageGroup;
    private Map map;
    private int controls;
    private int position;
    private long duration;
    private long startDateTimeMillis;
    private long winningTime;
    private float distance;

    /** Required empty constructor */
    public Activity() {
    }

    /** Required arguments */
    public Activity(long startDateTimeMillis, String title, String location, float distance,
                    long duration, int controls, String pace) {
        this.startDateTimeMillis = startDateTimeMillis;
        this.title = title;
        this.location = location;
        this.distance = distance;
        this.duration = duration;
        this.controls = controls;
        this.pace = pace;
    }

    public String get_ID() {
        return this._ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
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

    public long getStartDateTime() {
        return this.startDateTimeMillis;
    }

    public void setStartDateTime(long startDateTimeMillis) {
        this.startDateTimeMillis = startDateTimeMillis;
    }

    public String getDetails() {
        return this.details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPace() {
        return this.pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public float getDistance() {
        return this.distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getAgeGroup() {
        return this.ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getWinningTime() {
        return this.winningTime;
    }

    public void setWinningTime(long winningTime) {
        this.winningTime = winningTime;
    }

    public Map getMap() {
        return this.map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public int getControls() {
        return this.controls;
    }

    public void setControls(int controls) {
        this.controls = controls;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._ID);
        dest.writeString(this.type);
        dest.writeString(this.title);
        dest.writeString(this.location);
        dest.writeLong(this.startDateTimeMillis);
        dest.writeString(this.details);
        dest.writeString(this.pace);
        dest.writeFloat(this.distance);
        dest.writeLong(this.duration);
        dest.writeString(this.ageGroup);
        dest.writeParcelable(this.map, flags);
        dest.writeInt(this.controls);
        dest.writeInt(this.position);
        dest.writeLong(this.winningTime);
    }

    protected Activity(Parcel in) {
        this._ID = in.readString();
        this.type = in.readString();
        this.title = in.readString();
        this.location = in.readString();
        this.startDateTimeMillis = in.readLong();
        this.details = in.readString();
        this.pace = in.readString();
        this.distance = in.readFloat();
        this.duration = in.readLong();
        this.ageGroup = in.readString();
        this.map = in.readParcelable(getClass().getClassLoader());
        this.controls = in.readInt();
        this.position = in.readInt();
        this.winningTime = in.readLong();
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel source) {
            return new Activity(source);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };
}