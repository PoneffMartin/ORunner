package com.mponeff.orunner.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mponeff.orunner.R;
import com.mponeff.orunner.data.entities.Activity;
import com.mponeff.orunner.fragments.ViewActivityFragment;

import static com.google.common.base.Preconditions.checkNotNull;

public class ViewActivity extends AppCompatActivity {
    private static final String TAG = ViewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_activity);

        Bundle state = checkNotNull(getIntent().getExtras());
        String type = checkNotNull((Activity) state.getParcelable("data")).getType();
        Log.e(TAG, "Type = " + type);

        Fragment mainFragment = new ViewActivityFragment();

        mainFragment.setArguments(state);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.view_exercise_container, mainFragment);
        fragmentTransaction.commit();

    }
}
