package com.mponeff.orunner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mponeff.orunner.R;
import com.mponeff.orunner.fragments.ChooseTypeDialog;
import com.mponeff.orunner.fragments.HistoryFragment;
import com.mponeff.orunner.fragments.MapsFragment;
import com.mponeff.orunner.fragments.MonthReportsFragment;
import com.mponeff.orunner.fragments.OverviewFragment;
import com.mponeff.orunner.fragments.ProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    public static final String TAG = HomeActivity.class.getSimpleName();

    @BindView(R.id.navigation)
    BottomNavigationView mBottomNavigationView;
    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        /* Set initial fragment title */
        String title = getString(R.string.frag_overview);
        mToolbarTitle.setText(title);

        /* Set scrolling behaviour for bottom navigation bar. Hide/show on scroll */
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mBottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.content_frame, new OverviewFragment(), title);
        ft.commit();
    }

    @Override
    protected void onStop() {
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_activity:
                showSportsDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSportsDialog() {
        ChooseTypeDialog sportsDialog = new ChooseTypeDialog();
        sportsDialog.show(getSupportFragmentManager(), null);
    }

    private void selectDrawerItem(MenuItem item) {
        Fragment fragment = null;
        String title = "";
        switch (item.getItemId()) {
            case R.id.home:
                fragment = new OverviewFragment();
                title = getString(R.string.frag_overview);
                break;
            case R.id.activities:
                fragment = new HistoryFragment();
                title = getString(R.string.frag_history);
                break;
            case R.id.maps:
                fragment = new MapsFragment();
                title = getString(R.string.frag_maps_archive);
                break;
            case R.id.reports:
                fragment = new MonthReportsFragment();
                title = getString(R.string.frag_month_reports);
                break;
            case R.id.profile:
                fragment = new ProfileFragment();
                title = getString(R.string.frag_profile);
                break;
        }

        /* Do not replace the fragments if the selected fragment is the current one */
        boolean replace = !getSupportFragmentManager().findFragmentById(R.id.content_frame).getTag().equals(title);

        if (fragment != null && replace) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_frame, fragment, title); // Set the title as tag
            ft.commit();

            mToolbarTitle.setText(title);
        }
    }

    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (firebaseAuth.getCurrentUser() == null) {
                // User signed out
                startSignInActivity();
            }
        }
    };

    private void startSignInActivity() {
        Intent signInActivity = new Intent(HomeActivity.this, LoginActivity.class);
        signInActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        signInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signInActivity);
    }

    /* Helper class which hides/shows the bottom nav bar on scroll */
    private class BottomNavigationViewBehavior extends CoordinatorLayout.Behavior<BottomNavigationView> {
        private int height;

        @Override
        public boolean onLayoutChild(CoordinatorLayout parent, BottomNavigationView child, int layoutDirection) {
            height = child.getHeight();
            return super.onLayoutChild(parent, child, layoutDirection);
        }

        @Override
        public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                           @NonNull BottomNavigationView child,
                                           @NonNull View directTargetChild, @NonNull View target,
                                           int axes, int type) {
            return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
        }

        @Override
        public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomNavigationView child,
                                   @NonNull View target, int dxConsumed, int dyConsumed,
                                   int dxUnconsumed, int dyUnconsumed,
                                   @ViewCompat.NestedScrollType int type) {
            if ((dyConsumed | dyUnconsumed) > 0) {
                slideDown(child);
            } else if ((dyConsumed | dyUnconsumed) < 0) {
                slideUp(child);
            }
        }

        private void slideUp(BottomNavigationView child) {
            child.clearAnimation();
            child.animate().translationY(0).setDuration(200);
        }

        private void slideDown(BottomNavigationView child) {
            child.clearAnimation();
            child.animate().translationY(height).setDuration(200);
        }
    }
}
