package com.mponeff.orunner.data.entities;

import com.mponeff.orunner.utils.Calculations;
import com.mponeff.orunner.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MonthSummary {

    private final List<Activity> activities = new ArrayList<>();
    private final int month;
    private final int year;
    private long totalDuration;
    private float totalDistance;

    public MonthSummary(List<Activity> allActivities, int month, int year) {
        this.month = month;
        this.year = year;
        this.createReport(allActivities);
    }

    public MonthSummary(List<Activity> allActivities) {
        Calendar date = Calendar.getInstance();
        this.month = date.get(Calendar.MONTH);
        this.year = date.get(Calendar.YEAR);
        this.createReport(allActivities);
    }

    public int getTotalActivities() {
        return this.activities.size();
    }

    public long getTotalDuration() {
        return this.totalDuration;
    }

    public int getMonth() {
        return this.month;
    }

    public float getTotalDistance() {
        return this.totalDistance;
    }

    public float getTotalDistanceInMiles() {
        return Calculations.getDistanceInMiles(this.totalDistance);
    }

    public String getTotalPace() {
        return Calculations.calculatePace(this.totalDistance, this.totalDuration);
    }

    public String getTotalPaceInImperial() {
        return Calculations.calculatePaceInImperial(this.getTotalDistanceInMiles(), this.totalDuration);
    }

    public int getYear() {
        return this.year;
    }

    public List<Activity> getTrainings() {
        List<Activity> trainings = new ArrayList<>();
        for (Activity activity : this.activities) {
            if (activity.getType().equalsIgnoreCase(Activity.TYPE_TRAINING)) {
                trainings.add(activity);
            }
        }

        return trainings;
    }

    public List<Activity> getCompetitions() {
        List<Activity> competitions = new ArrayList<>();
        for (Activity activity : this.activities) {
            if (activity.getType().equalsIgnoreCase(Activity.TYPE_COMPETITION)) {
                competitions.add(activity);
            }
        }

        return competitions;
    }

    public int getTrainingsCount() {
        return this.getTrainings().size();
    }

    public int getCompetitionsCount() {
        return this.getCompetitions().size();
    }

    public float getTrainingsTotalDistance() {
        float totalDistance = 0;
        for(Activity activity : this.getTrainings()) {
            totalDistance += activity.getDistance();
        }

        return totalDistance;
    }

    public float getCompetitionsTotalDistance() {
        float totalDistance = 0;
        for(Activity activity : this.getCompetitions()) {
            totalDistance += activity.getDistance();
        }

        return totalDistance;
    }

    /* Update distance when adding new activity */
    private void addActivity(Activity activity) {
        this.totalDistance += activity.getDistance();
        this.totalDuration += activity.getDuration();
        this.activities.add(activity);
    }

    private void createReport(List<Activity> activities) {
        for (Activity activity : activities) {
            int startMonth = DateTimeUtils.getMonthFromMillis(activity.getStartDateTime());
            int startYear = DateTimeUtils.getYearFromMillis(activity.getStartDateTime());
            if (startMonth == this.month && startYear == this.year) {
                this.addActivity(activity);
            }
        }
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + year;
        result = 31 * result + month;
        return result;
    }
}
