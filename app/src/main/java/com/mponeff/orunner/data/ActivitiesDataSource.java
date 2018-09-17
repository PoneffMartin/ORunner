package com.mponeff.orunner.data;

import android.support.annotation.NonNull;

import com.mponeff.orunner.data.entities.Activity;

import java.util.List;

public interface ActivitiesDataSource {

    interface GetActivitiesCallback {
        void onSuccess(List<Activity> activities);
        void onFailure();
    }

    interface SaveActivityCallback {
        void onSuccess();
        void onFailure();
    }

    interface DeleteActivityCallback {
        void onSuccess();
        void onFailure();
    }

    void getActivities(@NonNull GetActivitiesCallback callback);
    void getActivities(@NonNull GetActivitiesCallback callback, int year, int month);

    void saveActivity(@NonNull Activity activity, boolean update, @NonNull SaveActivityCallback callback);
    void deleteActivity(@NonNull Activity activity, @NonNull DeleteActivityCallback callback);
}
