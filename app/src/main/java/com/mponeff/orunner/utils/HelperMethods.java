package com.mponeff.orunner.utils;

import com.mponeff.orunner.data.entities.Activity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HelperMethods {

    // Helper method used to sort all Exercises in list by StartDate
    public static void sortListByDate(List<Activity> exercisesList) {
        Collections.sort(exercisesList, new Comparator<Activity>() {
            @Override
            public int compare(Activity activity1, Activity activity2) {
                return Long.valueOf(activity2.getStartDateTime()).compareTo((activity1.getStartDateTime()));
            }
        });
    }


}
