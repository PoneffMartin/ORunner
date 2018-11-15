package com.mponeff.orunner.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mponeff.orunner.R;
import com.mponeff.orunner.fragments.AboutFragment;
import com.mponeff.orunner.fragments.ChooseTypeDialog;
import com.mponeff.orunner.fragments.HistoryFragment;
import com.mponeff.orunner.fragments.MapsFragment;
import com.mponeff.orunner.fragments.MonthReportsFragment;
import com.mponeff.orunner.fragments.OverviewFragment;
import com.mponeff.orunner.fragments.SettingsFragment;
import com.mponeff.orunner.utils.Network;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    public static final String TAG = HomeActivity.class.getSimpleName();

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        /* Set initial fragment title */
        String title = getString(R.string.frag_overview);
        mToolbarTitle.setText(title);

        setupDrawer(mNavView);

        /** TODO: What to do when offline */
        if (!Network.hasNetworkConnection(this)) {
            Snackbar.make(mDrawer, "Offline", Snackbar.LENGTH_LONG).show();
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.content_frame, new OverviewFragment(), title);
        ft.commit();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSportsDialog();
            }
        });
    }

    @Override
    protected void onStop() {
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSportsDialog() {
        ChooseTypeDialog sportsDialog = new ChooseTypeDialog();
        sportsDialog.show(getSupportFragmentManager(), null);
    }

    private void setupDrawer(NavigationView navigationView) {
        setProfileInfo(navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    private void setProfileInfo(NavigationView navigationView) {
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header);
        TextView profilePicPlaceholder = (TextView) headerView.findViewById(R.id.profile_picture_placeholder);
        TextView usernameTv = (TextView) headerView.findViewById(R.id.tv_username);
        TextView emailTv = (TextView) headerView.findViewById(R.id.tv_email);
        CircleImageView profileImage = (CircleImageView) headerView.findViewById(R.id.iv_profile_pic);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Uri userPhoto = user.getPhotoUrl();
            String username = user.getDisplayName();
            String email = user.getEmail();
            profilePicPlaceholder.setText(String.valueOf(username.charAt(0))); // TODO Consider exception handling
            usernameTv.setText(username);
            emailTv.setText(email);
            if (userPhoto != null) {
                Glide.with(this)
                        .load(userPhoto)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                profilePicPlaceholder.setVisibility(View.GONE);
                                profileImage.setVisibility(View.VISIBLE);
                                return false;
                            }
                        })
                        .into(profileImage);
            }
        }
    }

    private void selectDrawerItem(MenuItem item) {
        Fragment fragment;
        String title;
        boolean showFab = false;
        switch (item.getItemId()) {
            case R.id.home:
                fragment = new OverviewFragment();
                title = getString(R.string.frag_overview);
                showFab = true;
                break;
            case R.id.activities:
                fragment = new HistoryFragment();
                title = getString(R.string.frag_history);
                showFab = true;
                break;
            case R.id.log_out:
                fragment = null;
                title = "";
                FirebaseAuth.getInstance().signOut();
                break;
            case R.id.maps:
                fragment = new MapsFragment();
                title = getString(R.string.frag_maps_archive);
                showFab = true;
                break;
            case R.id.reports:
                fragment = new MonthReportsFragment();
                title = getString(R.string.frag_month_reports);
                showFab = true;
                break;
            /*case R.id.settings:
                fragment = new SettingsFragment();
                title = getString(R.string.frag_settings);
                showFab = false;
                break;
            case R.id.about:
                fragment = new AboutFragment();
                title = getString(R.string.frag_about);
                showFab = false;
                break;*/
            default:
                fragment = null;
                title = "";
        }

        /* Do not replace the fragments if the selected fragment is the current one */
        boolean replace = !getSupportFragmentManager().findFragmentById(R.id.content_frame).getTag().equals(title);

        /* fragment is set for all menu items except R.id.log_out which is activity and is started in the case block */
        if (fragment != null && replace) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_frame, fragment, title); // Set the title as tag
            ft.commit();

            mToolbarTitle.setText(title);
            if (showFab) {
                mFab.show();
            } else {
                mFab.hide();
            }
        }

        mDrawer.closeDrawer(GravityCompat.START);
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
}
