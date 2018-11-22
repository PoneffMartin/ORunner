package com.mponeff.orunner.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mponeff.orunner.ORunnerApp;
import com.mponeff.orunner.data.ActivitiesDataSource;
import com.mponeff.orunner.data.entities.Activity;
import com.mponeff.orunner.data.entities.Total;

import java.util.List;

import javax.inject.Inject;

public class TotalsViewModel extends AndroidViewModel {

    public static final String TAG = ActivitiesViewModel.class.getSimpleName();

    @Inject
    ActivitiesDataSource mActivitiesDataSource;
    private MutableLiveData<Total> mTotals;

    public TotalsViewModel(@NonNull Application application) {
        super(application);
        ((ORunnerApp) getApplication()).getDataSourceComponent().inject(this);
    }

    public LiveData<Total> getTotals() {
        if (this.mTotals == null) {
            this.mTotals = new MutableLiveData<>();
            fetchTotals();
        }

        return this.mTotals;
    }

    private void fetchTotals() {
        this.mActivitiesDataSource.getActivities(new ActivitiesDataSource.GetActivitiesCallback() {
            @Override
            public void onSuccess(List<Activity> activities) {
                double distance = activities.stream().mapToDouble(Activity::getDistance).sum();
                long duration = activities.stream().mapToLong(Activity::getDuration).sum();
                Total totals = new Total(activities.size(), distance, duration);
                mTotals.setValue(totals);
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "Failed to fetch all activities");
            }
        });
    }
}
