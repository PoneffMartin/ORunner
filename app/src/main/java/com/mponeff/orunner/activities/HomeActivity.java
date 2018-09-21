package com.mponeff.orunner.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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

    private Fragment mFragment = null;
    private boolean mItemSelected = false;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mTitle = getResources().getString(R.string.frag_overview);
        mToolbarTitle.setText(mTitle);

        setupDrawer(mNavView);

        /** TODO: What to do when offline */
        if (!Network.hasNetworkConnection(this)) {
            Snackbar.make(mDrawer, "Offline", Snackbar.LENGTH_LONG).show();
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSportsDialog();
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.content_frame, new OverviewFragment());
        ft.commit();
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

        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (mItemSelected) {
                    mItemSelected = !mItemSelected;

                    /* mFragment is set for all menu items except R.id.log_out which is activity */
                    if (mFragment == null) {
                        signOut();
                    } else {
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.content_frame, mFragment);
                        ft.commit();

                        mFragment = null;
                        mToolbarTitle.setText(mTitle);
                    }

                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
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
        int id = item.getItemId();
        mItemSelected = true;
        switch (id) {
            case R.id.home:
                mFragment = new OverviewFragment();
                mTitle = "Overview";
                mFab.show();
                break;
            case R.id.activities:
                mFragment = new HistoryFragment();
                mTitle = "History";
                mFab.show();
                break;
            case R.id.log_out:
                signOut();
                break;
            case R.id.maps:
                mFragment = new MapsFragment();
                mTitle = "Maps archive";
                mFab.show();
                break;
            case R.id.settings:
                mFragment = new SettingsFragment();
                mTitle = "Settings";
                mFab.hide();
                break;
            case R.id.reports:
                mFragment = new MonthReportsFragment();
                mTitle = "Month reports";
                mFab.show();
                break;
            case R.id.about:
                mFragment = new AboutFragment();
                mTitle = "About";
                mFab.hide();
        }

        mDrawer.closeDrawer(GravityCompat.START);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        startSignInActivity();
    }

    private void startSignInActivity() {
        Intent signInActivity = new Intent(HomeActivity.this, LoginActivity.class);
        /*signInActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        signInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
        startActivity(signInActivity);
        //finish();
    }
}
