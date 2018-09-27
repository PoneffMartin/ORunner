package com.mponeff.orunner.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.mponeff.orunner.R;
import com.mponeff.orunner.fragments.SaveActivityFragment;

import static com.google.common.base.Preconditions.checkNotNull;

public class SaveActivity extends AppCompatActivity {
    private static final String TAG = SaveActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);

        Fragment fragment = new SaveActivityFragment();
        Bundle state = checkNotNull(getIntent().getExtras());
        fragment.setArguments(state);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.add_activity_fragment_container, fragment);
        fragmentTransaction.commit();
    }
}


