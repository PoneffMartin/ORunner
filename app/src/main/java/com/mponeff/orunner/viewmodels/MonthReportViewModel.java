package com.mponeff.orunner.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mponeff.orunner.ORunnerApp;
import com.mponeff.orunner.data.ActivitiesDataSource;
import com.mponeff.orunner.data.entities.Activity;
import com.mponeff.orunner.data.entities.MonthSummary;
import com.mponeff.orunner.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class MonthReportViewModel extends AndroidViewModel {

    private static final String TAG = MonthReportViewModel.class.getSimpleName();

    @Inject
    ActivitiesDataSource mActivitiesDataSource;
    private MutableLiveData<List<MonthSummary>> mAllSummaries;
    private MutableLiveData<MonthSummary> mSummary;

    public MonthReportViewModel(@NonNull Application application) {
        super(application);
        ((ORunnerApp) getApplication()).getDataSourceComponent().inject(this);
    }

    public LiveData<List<MonthSummary>> getSummaries(int year) {
        this.mAllSummaries = new MutableLiveData<>();
        //loadAllSummaries(year);
        return this.mAllSummaries;
    }

    public LiveData<MonthSummary> getCurrentReport() {
        if (this.mSummary == null) {
            this.mSummary = new MutableLiveData<>();
            loadCurrentReport();
        }

        return this.mSummary;
    }

    public LiveData<List<MonthSummary>> getReports() {
        if (this.mAllSummaries == null) {
            this.mAllSummaries = new MutableLiveData<>();
            //oadAllSummaries();
        }

        return this.mAllSummaries;
    }

    /*private void loadAllSummaries() {
        this.loadAllSummaries(DateTimeUtils.DEFAULT_YEAR);
    }*/

    /*private void loadAllSummaries(int year) {
        this.mActivitiesDataSource.getActivities(new ActivitiesDataSource.GetActivitiesCallback(){
            @Override
            public void onSuccess(List<Activity> activities) {
                List<MonthSummary> reports = createReports(activities);
                mAllSummaries.setValue(reports);
            }

            @Override
            public void onFailure() {
                mAllSummaries.setValue(null);
            }
        }, year);
    }*/

    private void loadCurrentReport() {
        this.mActivitiesDataSource.getActivities(new ActivitiesDataSource.GetActivitiesCallback(){
            @Override
            public void onSuccess(List<Activity> activities) {
                MonthSummary monthSummary = new MonthSummary(activities);
                mSummary.setValue(monthSummary);
            }

            @Override
            public void onFailure() {
                mSummary.setValue(null);
            }
        });
    }

    private List<MonthSummary> createReports(List<Activity> activities) {
        Map<Integer, HashMap<Integer, List<Activity>>> months = new HashMap<>();
        int month;
        int year;
        for (Activity activity : activities) {
            month = DateTimeUtils.getMonthFromMillis(activity.getStartDateTime());
            year = DateTimeUtils.getYearFromMillis(activity.getStartDateTime());

            if (!months.containsKey(year)) {
                months.put(year, new HashMap<>());
            }
            if (!months.get(year).containsKey(month)) {
                months.get(year).put(month, new ArrayList<>());
            }

            months.get(year).get(month).add(activity);
        }

        List<MonthSummary> reports = new ArrayList<>();
        for (Map.Entry<Integer, HashMap<Integer, List<Activity>>> mapEntry : months.entrySet()) {
            for (Map.Entry<Integer, List<Activity>> entry : mapEntry.getValue().entrySet()) {
                year = mapEntry.getKey();
                month = entry.getKey();
                MonthSummary report = new MonthSummary(entry.getValue(), month, year);
                reports.add(report);
            }
        }

        return reports;
    }
}
