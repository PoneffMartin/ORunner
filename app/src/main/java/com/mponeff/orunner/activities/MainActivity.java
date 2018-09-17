package com.mponeff.orunner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.e(TAG, "Not logged in");
            intent = new Intent(this, LoginActivity.class);
        } else {
            Log.e(TAG, "Logged in");
            intent = new Intent(this, HomeActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
