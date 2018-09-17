package com.mponeff.orunner;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;
import com.mponeff.orunner.dagger.AppComponent;
import com.mponeff.orunner.dagger.AppModule;
import com.mponeff.orunner.dagger.DaggerAppComponent;
import com.mponeff.orunner.dagger.DaggerDataSourceComponent;
import com.mponeff.orunner.dagger.DataSourceComponent;
import com.mponeff.orunner.dagger.DataSourceModule;

public class ORunnerApp extends MultiDexApplication {

    private AppComponent mAppComponent;
    private DataSourceComponent mDataSourceComponent;
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = initAppComponent();
        mDataSourceComponent = initDataSourceComponent();
        /* Enable disk persistence when offline */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        /* Set default Settings on start of application */
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public DataSourceComponent getDataSourceComponent() {
        return mDataSourceComponent;
    }

    protected AppComponent initAppComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    protected DataSourceComponent initDataSourceComponent() {
        return DaggerDataSourceComponent.builder()
                .appModule(new AppModule(this))
                .dataSourceModule(new DataSourceModule())
                .build();
    }
}
