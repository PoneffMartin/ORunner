package com.mponeff.orunner.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;

import com.mponeff.orunner.R;
import com.mponeff.orunner.fragments.SaveActivityFragment;

import static com.google.common.base.Preconditions.checkNotNull;

public class SaveActivity extends AppCompatActivity {
    private static final String TAG = SaveActivity.class.getSimpleName();

    private Fragment mMainFragment;
    private Fragment mDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
            // Reuqest permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2); // TODO Export
        }

        mMainFragment = new SaveActivityFragment();

        /*Bundle state = checkNotNull(getIntent().getExtras());
        String type = checkNotNull(state.getString("type"));
        if (type.equalsIgnoreCase(getString(R.string.type_competition))) {
            mDetailsFragment = SaveCompetitionDetailsFragment.newInstance();
        } else {
            mDetailsFragment = SaveTrainingDetailsFragment.newInstance();
        }*/

        Bundle state = checkNotNull(getIntent().getExtras());
        mMainFragment.setArguments(state);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.add_activity_fragment_container, mMainFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}


