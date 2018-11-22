package com.mponeff.orunner.dagger;

import com.mponeff.orunner.viewmodels.ActivitiesViewModel;
import com.mponeff.orunner.viewmodels.MapViewModel;
import com.mponeff.orunner.viewmodels.MonthReportViewModel;
import com.mponeff.orunner.viewmodels.TotalsViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, DataSourceModule.class})
public interface DataSourceComponent {
    void inject(ActivitiesViewModel activitiesViewModel);
    void inject(MapViewModel mapViewModelModel);
    void inject(MonthReportViewModel monthReportViewModel);
    void inject(TotalsViewModel totalsViewModel);
}
