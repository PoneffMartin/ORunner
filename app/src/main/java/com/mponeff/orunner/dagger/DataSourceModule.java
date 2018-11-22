package com.mponeff.orunner.dagger;

import android.app.Application;

import com.mponeff.orunner.data.ActivitiesDataSource;
import com.mponeff.orunner.data.ActivitiesRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataSourceModule {

    @Provides
    @Singleton
    ActivitiesDataSource provideActivitiesDataSource(Application application) {
        return new ActivitiesRepository();
    }
}
