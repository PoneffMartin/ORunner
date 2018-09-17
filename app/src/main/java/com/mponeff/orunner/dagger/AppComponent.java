package com.mponeff.orunner.dagger;

import com.mponeff.orunner.ORunnerApp;
import com.mponeff.orunner.activities.HomeActivity;
import com.mponeff.orunner.data.ActivitiesRepository;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(ORunnerApp oRunnerApp);
    void inject(HomeActivity homeActivity);
    void inject(ActivitiesRepository activitiesRepository);
}
