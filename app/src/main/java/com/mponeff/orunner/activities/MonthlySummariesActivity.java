package com.mponeff.orunner.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.mponeff.orunner.R;
import com.mponeff.orunner.fragments.MonthlySummariesFragment;

public class MonthlySummariesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_summaries);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.monthly_summaries_fragment_container, MonthlySummariesFragment.getInstance());
        ft.commit();
    }
}
