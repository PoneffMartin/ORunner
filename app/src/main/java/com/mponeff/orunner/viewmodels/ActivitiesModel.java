package com.mponeff.orunner.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mponeff.orunner.ORunnerApp;
import com.mponeff.orunner.data.ActivitiesDataSource;
import com.mponeff.orunner.data.entities.Activity;

import java.util.List;

import javax.inject.Inject;

public class ActivitiesModel extends AndroidViewModel {
    public static final String TAG = ActivitiesModel.class.getSimpleName();

    @Inject
    ActivitiesDataSource mActivitiesDataSource;
    private MutableLiveData<List<Activity>> mActivities;
    private MutableLiveData<Boolean> mIsActivitySaved;
    private MutableLiveData<Boolean> mIsActivityDeleted;

    private ActivitiesDataSource.GetActivitiesCallback mGetActivitiesCallback;

    public ActivitiesModel(@NonNull Application application) {
        super(application);
        ((ORunnerApp) getApplication()).getDataSourceComponent().inject(this);
        this.mGetActivitiesCallback = this.createGetActivitiesCallback();
    }

    public LiveData<List<Activity>> getActivities() {
        return getActivities(-1, -1);
    }

    public LiveData<List<Activity>> getActivities(int year, int month) {
        if (this.mActivities == null) {
            this.mActivities = new MutableLiveData<>();
            loadActivities(year, month);
        }

        return this.mActivities;
    }

    public LiveData<Boolean> isActivitySaved() {
        if (this.mIsActivitySaved == null) {
            this.mIsActivitySaved = new MutableLiveData<>();
        }

        return this.mIsActivitySaved;
    }

    public LiveData<Boolean> isActivityDeleted() {
        if (this.mIsActivityDeleted == null) {
            this.mIsActivityDeleted = new MutableLiveData<>();
        }

        return this.mIsActivityDeleted;
    }

    public void saveActivity(Activity activity, boolean update) {
        this.mActivitiesDataSource.saveActivity(activity, update, new ActivitiesDataSource.SaveActivityCallback() {
            @Override
            public void onSuccess() {
                mIsActivitySaved.setValue(true);
            }

            @Override
            public void onFailure() {
                mIsActivitySaved.setValue(false);
            }
        });
    }

    public void deleteActivity(Activity activity) {
        this.mActivitiesDataSource.deleteActivity(activity, new ActivitiesDataSource.DeleteActivityCallback() {
            @Override
            public void onSuccess() {
                mIsActivityDeleted.setValue(true);
            }

            @Override
            public void onFailure() {
                mIsActivityDeleted.setValue(false);
            }
        });
    }

    // TODO Rename???
    private void loadActivities(int year, int month) {
        if (year == -1 && month == -1) {
            this.mActivitiesDataSource.getActivities(this.mGetActivitiesCallback);
        } else {
            this.mActivitiesDataSource.getActivities(this.mGetActivitiesCallback, year, month);
        }
    }

    private ActivitiesDataSource.GetActivitiesCallback createGetActivitiesCallback() {
        return new ActivitiesDataSource.GetActivitiesCallback(){
            @Override
            public void onSuccess(List<Activity> activities) {
                mActivities.setValue(activities);
            }

            @Override
            public void onFailure() {
                mActivities.setValue(null);
            }
        };
    }
}
