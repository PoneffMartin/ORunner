package com.mponeff.orunner.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTimeUtils {

    private static final int MAX_SECONDS = 362439;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final int DEFAULT_YEAR = 1970;

    public static String convertMillisToDateString(long millis) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(millis);

        String month = getMonthShortName(date.get(Calendar.MONTH));
        int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);
        int year = date.get(Calendar.YEAR);

        return String.format(new Locale("bg", "BG"), "%02d-%s-%02d", dayOfMonth, month, year);
    }

    public static String convertMillisToTimeString(long millis) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(millis);

        int hourOfDay = date.get(Calendar.HOUR_OF_DAY);
        int minutes = date.get(Calendar.MINUTE);

        return String.format(new Locale("bg", "BG"), "%02d:%02d", hourOfDay, minutes);
    }

    public static long convertTimeStringToSeconds(String duration) {
        if (duration == null || duration.isEmpty()) {
            return 0;
        }

        long hours;
        long minutes;
        long seconds;
        long totalSeconds;
        String[] tokens = duration.split(":");
        hours = Long.parseLong(tokens[0]);
        minutes = Long.parseLong(tokens[1]);
        seconds = Long.parseLong(tokens[2]);
        totalSeconds = (hours * 3600) + (minutes * 60) + seconds;

        return totalSeconds;
    }

    public static String convertSecondsToTimeString(long seconds) {
        String duration;
        if (seconds >= MAX_SECONDS) {
            duration = "99:99:99";
        } else {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            seconds = seconds % 60;
            duration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        return duration;
    }

    public static int getMonthFromDateStr(String dateStr) {
        Calendar date = Calendar.getInstance();
        try {
            date.setTime(DATE_FORMAT.parse(dateStr));
        } catch (ParseException pe) {
            pe.printStackTrace();
            return -1;
        }

        return date.get(Calendar.MONTH);
    }

    public static int getMonthFromMillis(long millis) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(millis);
        return date.get(Calendar.MONTH);
    }

    public static int getYearFromMillis(long millis) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(millis);
        return date.get(Calendar.YEAR);
    }

    public static String getMonthName(int month) {
        String monthName;
        switch (month) {
            case Calendar.JANUARY:
                monthName = "January";
                break;
            case Calendar.FEBRUARY:
                monthName = "February";
                break;
            case Calendar.MARCH:
                monthName = "March";
                break;
            case Calendar.APRIL:
                monthName = "April";
                break;
            case Calendar.MAY:
                monthName = "May";
                break;
            case Calendar.JUNE:
                monthName = "June";
                break;
            case Calendar.JULY:
                monthName = "July";
                break;
            case Calendar.AUGUST:
                monthName = "August";
                break;
            case Calendar.SEPTEMBER:
                monthName = "September";
                break;
            case Calendar.OCTOBER:
                monthName = "October";
                break;
            case Calendar.NOVEMBER:
                monthName = "November";
                break;
            case Calendar.DECEMBER:
                monthName = "December";
                break;
            default:
                // ERROR
                return null;
        }

        return monthName;
    }

    public static String getMonthShortName(int month) {
        String monthName;
        switch (month) {
            case Calendar.JANUARY:
                monthName = "Jan";
                break;
            case Calendar.FEBRUARY:
                monthName = "Feb";
                break;
            case Calendar.MARCH:
                monthName = "Mar";
                break;
            case Calendar.APRIL:
                monthName = "Apr";
                break;
            case Calendar.MAY:
                monthName = "May";
                break;
            case Calendar.JUNE:
                monthName = "Jun";
                break;
            case Calendar.JULY:
                monthName = "Jul";
                break;
            case Calendar.AUGUST:
                monthName = "Aug";
                break;
            case Calendar.SEPTEMBER:
                monthName = "Sep";
                break;
            case Calendar.OCTOBER:
                monthName = "Oct";
                break;
            case Calendar.NOVEMBER:
                monthName = "Nov";
                break;
            case Calendar.DECEMBER:
                monthName = "Dec";
                break;
            default:
                // ERROR
                return null;
        }

        return monthName;
    }

    /**
     * Creates instance of Calendar object and sets the year and month.
     * The date is set to 00:00:00 on the 1-st day of the given month.
     * @param year the year to be set on Calendar.YEAR field
     * @param month the month to be set on Calendar.MONTH field
     * @return the time in milliseconds
     */
    public static long getStartMillis(int year, int month) {
        Calendar start = Calendar.getInstance();
        start.set(year, month, 1, 0, 0, 0);
        return start.getTimeInMillis();
    }


    /**
     * Creates instance of Calendar object and sets the year and month.
     * The date is set to 00:00:00 on the 1-st day of the given month.
     * To get the last day of the month use the add method from Calendar API.
     * @param year the year to be set on Calendar.YEAR field
     * @param month the month to be set on Calendar.MONTH field
     * @return the time in milliseconds
     */
    public static long getEndMillis(int year, int month) {
        Calendar end = Calendar.getInstance();
        end.set(year, month, 1, 0, 0, 0);
        end.add(Calendar.MONTH, 1);
        return end.getTimeInMillis();
    }

    public static String getTimeDiff(long winningTime, long durationTime) {
        if (winningTime == 0 || durationTime == 0) {
            return "";
        }

        return String.format("+ %s", DateTimeUtils.convertSecondsToTimeString(durationTime - winningTime));
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }
}
