package com.mponeff.orunner.utils;

import java.util.Locale;

public class Calculations {

    public static final float MILE_IN_KM = 1.60934F;

    public static String calculatePace (float distance, long duration) {
        String pace = null;
        int minutes;
        int seconds;
        if (distance == 0 || duration == 0) {
            pace = String.format(Locale.US, "%02d:%02d", 0, 0);
        } else {
            double secondsPerKm = duration / distance;
            minutes = (int) secondsPerKm / 60;
            seconds = (int) secondsPerKm % 60;
            if (minutes > 99) {
                pace = "-";
            } else {
                pace = String.format(Locale.US, "%02d:%02d", minutes, seconds);
            }
        }

        return pace;
    }

    public static String calculateSpeed (float distance, long duration) {
        String speed = null;
        if (distance == 0 || duration == 0) {
            speed = String.format(Locale.US, "%d", 0);
        } else {
            double hours = duration / 3600D;
            double kilometersPerHour = distance / hours;
            if (kilometersPerHour > 9999.9D) {
                speed = "-";
            } else {
                speed = String.format(Locale.US, "%.1f", kilometersPerHour);
            }
        }

        return speed;
    }

    public static String calculatePaceInImperial (Float distance, Long duration) {
        String pace = null;
        distance = distance/MILE_IN_KM;
        int minutes;
        int seconds;
        if (distance == 0 || duration == 0) {
            pace = String.format(Locale.US, "%02d:%02d", 0, 0);
        } else {
            double secondsPerMil = duration / distance;
            minutes = (int) secondsPerMil / 60;
            seconds = (int) secondsPerMil % 60;
            if (minutes > 99) {
                pace = "-";
            } else {
                pace = String.format(Locale.US, "%02d:%02d", minutes, seconds);
            }
        }

        return pace;
    }

    public static String calculateSpeedInImperial (Float distance, Long duration) {
        String speed = null;
        distance = distance/MILE_IN_KM;
        if (distance == 0 || duration == 0) {
            speed = String.format(Locale.US, "%d", 0);
        } else {
            double hours = duration / 3600D;
            double milesPerHour = distance / hours;
            if (milesPerHour > 9999.9D) {
                speed = "-";
            } else {
                speed = String.format(Locale.US, "%.1f", milesPerHour);
            }
        }

        return speed;
    }

    public static float getDistanceInMiles(float distance) {
        return distance / MILE_IN_KM;
    }

}
