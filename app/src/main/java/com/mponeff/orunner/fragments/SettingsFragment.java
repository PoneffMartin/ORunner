package com.mponeff.orunner.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.EditTextPreference;
import android.util.Log;

import com.mponeff.orunner.R;
import com.mponeff.orunner.data.FirebaseDB;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = SettingsFragment.class.getSimpleName();
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
                if (pref instanceof ListPreference){
                    ListPreference listPref = (ListPreference) pref;
                    pref.setSummary(listPref.getEntry());
                } else if (pref instanceof EditTextPreference) {
                    EditTextPreference editTextPref = (EditTextPreference) pref;
                    pref.setSummary(editTextPref.getText());
                }
            }
        };

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
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
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
