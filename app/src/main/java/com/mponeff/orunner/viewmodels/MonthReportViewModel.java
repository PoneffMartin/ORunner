package com.mponeff.orunner.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mponeff.orunner.ORunnerApp;
import com.mponeff.orunner.data.ActivitiesDataSource;
import com.mponeff.orunner.data.entities.Activity;
import com.mponeff.orunner.data.entities.MonthReport;
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
    private MutableLiveData<List<MonthReport>> mAllReports;
    private MutableLiveData<MonthReport> mCurrentReport;

    public MonthReportViewModel(@NonNull Application application) {
        super(application);
        ((ORunnerApp) getApplication()).getDataSourceComponent().inject(this);
    }

    /**
     * Load all month reports.
     * @return LiveData object containing list of all objects representing month summary.
     */
    public LiveData<List<MonthReport>> getAllReports() {
        if (this.mAllReports == null) {
            this.mAllReports = new MutableLiveData<>();
            loadAllReports();
        }

        return this.mAllReports;
    }

    /**
     * Load the report for the current month.
     * @return LiveData object containing month summary object for the current month.
     */
    public LiveData<MonthReport> getCurrentReport() {
        if (this.mCurrentReport == null) {
            this.mCurrentReport = new MutableLiveData<>();
            loadCurrentReport();
        }

        return this.mCurrentReport;
    }

    private void loadAllReports() {
        this.mActivitiesDataSource.getActivities(new ActivitiesDataSource.GetActivitiesCallback(){
            @Override
            public void onSuccess(List<Activity> activities) {
                List<MonthReport> reports = createReports(activities);
                mAllReports.setValue(reports);
            }

            @Override
            public void onFailure() {
                mAllReports.setValue(null);
            }
        });
    }

    private void loadCurrentReport() {
        int currentYear = DateTimeUtils.getCurrentYear();
        int currentMonth = DateTimeUtils.getCurrentMonth();
        this.mActivitiesDataSource.getActivities(new ActivitiesDataSource.GetActivitiesCallback(){
            @Override
            public void onSuccess(List<Activity> activities) {
                MonthReport monthReport = new MonthReport(activities, currentYear, currentMonth);
                mCurrentReport.setValue(monthReport);
            }

            @Override
            public void onFailure() {
                mCurrentReport.setValue(null);
            }
        }, currentYear, currentMonth);
    }

    private List<MonthReport> createReports(List<Activity> activities) {
        /* Year -> Month -> List of activities */
        Map<Integer, HashMap<Integer, List<Activity>>> allReportsMap = new HashMap<>();
        int month;
        int year;
        for (Activity activity : activities) {
            month = DateTimeUtils.getMonthFromMillis(activity.getStartDateTime());
            year = DateTimeUtils.getYearFromMillis(activity.getStartDateTime());

            if (!allReportsMap.containsKey(year)) {
                allReportsMap.put(year, new HashMap<>());
            }
            if (!allReportsMap.get(year).containsKey(month)) {
                allReportsMap.get(year).put(month, new ArrayList<>());
            }

            allReportsMap.get(year).get(month).add(activity);
        }

        List<MonthReport> reports = new ArrayList<>();
        for (Map.Entry<Integer, HashMap<Integer, List<Activity>>> mapEntry : allReportsMap.entrySet()) {
            for (Map.Entry<Integer, List<Activity>> innerEntry : mapEntry.getValue().entrySet()) {
                year = mapEntry.getKey();
                month = innerEntry.getKey();
                MonthReport report = new MonthReport(innerEntry.getValue(), year, month);
                reports.add(report);
            }
        }

        return reports;
    }
}
