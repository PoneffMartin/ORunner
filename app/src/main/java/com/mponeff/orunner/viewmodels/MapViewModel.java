package com.mponeff.orunner.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mponeff.orunner.ORunnerApp;
import com.mponeff.orunner.data.ActivitiesDataSource;
import com.mponeff.orunner.data.entities.Activity;
import com.mponeff.orunner.data.entities.Map;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MapViewModel extends AndroidViewModel {

    @Inject
    ActivitiesDataSource mActivitiesDataSource;
    private MutableLiveData<List<Map>> mMaps;

    public MapViewModel(@NonNull Application application) {
        super(application);
        ((ORunnerApp) getApplication()).getDataSourceComponent().inject(this);
    }

    public LiveData<List<Map>> getMaps() {
        if (this.mMaps == null) {
            this.mMaps = new MutableLiveData<>();
            loadMaps();
        }

        return this.mMaps;
    }

    private void loadMaps() {
        this.mActivitiesDataSource.getActivities(new ActivitiesDataSource.GetActivitiesCallback(){
            @Override
            public void onSuccess(List<Activity> activities) {
                List<Map> maps = new ArrayList<>();
                for (Activity activity : activities) {
                    if (activity.getMap() != null) {
                        maps.add(activity.getMap());
                    }
                }
                mMaps.setValue(maps);
            }

            @Override
            public void onFailure() {
                mMaps.setValue(null);
            }
        });
    }
}
