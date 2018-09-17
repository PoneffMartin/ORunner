package com.mponeff.orunner.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mponeff.orunner.R;
import com.mponeff.orunner.data.FirebaseDB;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static final String TOOLBAR_TITLE = "Settings";

    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        title.setText(TOOLBAR_TITLE);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new PreferencesFragment())
                .commit();
    }

    public static class PreferencesFragment extends PreferenceFragment {
        private static final String TAG = PreferencesFragment.class.getSimpleName();
        public static final String KEY_UNITS = "units";
        public static final String KEY_GENDER = "gender";
        public static final String KEY_WEIGHT = "weight";
        public static final String KEY_ACCOUNT = "account";
        private SharedPreferences.OnSharedPreferenceChangeListener mChangeListener;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    Preference pref = findPreference(key);
                    Log.e(TAG, "Preference change: preference is " + key);
                    if (pref instanceof ListPreference) {
                        ListPreference listPref = (ListPreference) pref;
                        pref.setSummary(listPref.getEntry());
                    } else if (pref instanceof EditTextPreference) {
                        EditTextPreference editTextPref = (EditTextPreference) pref;
                        pref.setSummary(editTextPref.getText());
                    }
                }
            };

            addPreferencesFromResource(R.xml.preferences);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String units = preferences.getString(KEY_UNITS, "");
            String gender = preferences.getString(KEY_GENDER, "");
            String weight = preferences.getString(KEY_WEIGHT, "");
            String userEmail = FirebaseDB.getFirebaseUserEmail();
            Preference accountPref = findPreference(KEY_ACCOUNT);
            Preference unitsPref = findPreference(KEY_UNITS);
            Preference genderPref = findPreference(KEY_GENDER);
            Preference weightPref = findPreference(KEY_WEIGHT);
            Log.e(TAG, "Preference change: preference is: " + accountPref.toString());
            accountPref.setSummary(userEmail);
            unitsPref.setSummary(units);
            genderPref.setSummary(gender);
            weightPref.setSummary(weight);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(
                    mChangeListener);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                    mChangeListener);
        }
    }
}
