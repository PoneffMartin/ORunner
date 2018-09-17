package com.mponeff.orunner.dagger;

import com.mponeff.orunner.viewmodels.ActivitiesModel;
import com.mponeff.orunner.viewmodels.MapViewModel;
import com.mponeff.orunner.viewmodels.MonthReportViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, DataSourceModule.class})
public interface DataSourceComponent {
    void inject(ActivitiesModel activitiesModel);
    void inject(MapViewModel mapViewModelModel);
    void inject(MonthReportViewModel monthReportViewModel);
}
