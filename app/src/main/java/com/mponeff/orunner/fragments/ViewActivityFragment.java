package com.mponeff.orunner.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mponeff.orunner.R;
import com.mponeff.orunner.activities.SaveActivity;
import com.mponeff.orunner.data.entities.Activity;
import com.mponeff.orunner.utils.DateTimeUtils;
import com.mponeff.orunner.viewmodels.ActivitiesModel;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;

public class ViewActivityFragment extends Fragment {
    private static final String TAG = ViewActivityFragment.class.getSimpleName();

    /* Common views */
    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.tv_date)
    TextView mTvStartDate;
    @BindView(R.id.tv_time)
    TextView mTvStartTime;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_location)
    TextView mTvLocation;
    @BindView(R.id.tv_distance)
    TextView mTvDistance;
    @BindView(R.id.tv_duration)
    TextView mTvDuration;
    @BindView(R.id.tv_controls)
    TextView mTvControls;
    @BindView(R.id.tv_pace)
    TextView mTvPace;
    @BindView(R.id.iv_map)
    ImageView mIvMap;
    @BindView(R.id.tv_no_map)
    TextView mTvNoMap;
    @BindView(R.id.tv_details)
    TextView mTvDetails;

    /* Competition views */
    @BindView(R.id.tv_position)
    TextView mTvPosition;
    @BindView(R.id.tv_winning_time)
    TextView mTvWinningTime;
    @BindView(R.id.tv_time_diff)
    TextView mTvTimeDiff;
    @BindView(R.id.tv_class)
    TextView mTvClass;
    @BindView(R.id.pb_load_map)
    ProgressBar mProgressBar;
    @BindView(R.id.ll_winning_time_class)
    LinearLayout mLlWinningTimeClass;
    @BindView(R.id.ll_position)
    LinearLayout mLlPosition;
    @BindView(R.id.horizontal_divider_competition_1)
    View mHorizontalDivider_1;
    @BindView(R.id.horizontal_divider_competition_2)
    View mHorizontalDivider_2;

    private ProgressDialog mProgressDialog;
    private ActivitiesModel mActivitiesModel;
    private Activity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mActivitiesModel = ViewModelProviders.of(this).get(ActivitiesModel.class);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_view_activity, container, false);
        ButterKnife.bind(this, rootView);

        Bundle args = checkNotNull(getArguments());
        mActivity = checkNotNull((Activity)args.getParcelable("data"));

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(mToolbar);
        appCompatActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbarTitle.setText(mActivity.getTitle());
        mToolbar.setNavigationIcon(R.drawable.ic_back_black);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        long startDateTimeMillis = mActivity.getStartDateTime();
        mTvStartDate.setText(DateTimeUtils.convertMillisToDateString(startDateTimeMillis));
        mTvStartTime.setText(DateTimeUtils.convertMillisToTimeString(startDateTimeMillis));
        mTvTitle.setText(mActivity.getTitle());
        mTvLocation.setText(mActivity.getLocation());
        mTvDistance.setText(String.valueOf(mActivity.getDistance()));
        mTvDuration.setText(DateTimeUtils.convertSecondsToTimeString(mActivity.getDuration()));
        mTvControls.setText(String.valueOf(mActivity.getControls()));
        mTvPace.setText(mActivity.getPace());

        if (mActivity.getType().equals(getString(R.string.type_competition))) {
            showCompetitionViews();
            mTvPosition.setText(mActivity.getPosition() > 0 ?
                    String.valueOf(mActivity.getPosition()) : "-");
            mTvWinningTime.setText(mActivity.getWinningTime() > 0 ?
                    DateTimeUtils.convertSecondsToTimeString(mActivity.getWinningTime()) : "-");
            mTvTimeDiff.setText(DateTimeUtils.getTimeDiff(mActivity.getWinningTime(), mActivity.getDuration()));
            mTvClass.setText(String.valueOf(mActivity.getAgeGroup() != null ? mActivity.getAgeGroup() : "-"));
        }

        if (mActivity.getMap() != null) {
            mTvNoMap.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mIvMap.setVisibility(View.VISIBLE);
            String downloadUri = mActivity.getMap().getDownloadUri();
            Glide.with(this)
                    .load(Uri.parse(downloadUri))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(mIvMap);
        }
        mTvDetails.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_SCROLL:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                }
                return false;
            }
        });

        mTvDetails.setText((mActivity.getDetails() == null || mActivity.getDetails().isEmpty()) ?
                "-" : mActivity.getDetails());

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.delete_edit_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            showDeleteConfirmationDialog();
        } else if (item.getItemId() == R.id.edit) {
            editActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    public void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void showDeleteConfirmationDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext())
                .customView(R.layout.dialog_delete_confirmation, false)
                .positiveText("Delete")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteActivity(mActivity);
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    private void editActivity() {
        Intent addExercise = new Intent(getContext(), SaveActivity.class);
        Bundle outState = new Bundle();
        outState.putParcelable("data", mActivity);
        outState.putString("mode", getResources().getString(R.string.edit_mode));
        outState.putString("type", mActivity.getType());
        addExercise.putExtras(outState);
        startActivity(addExercise);
        getActivity().finish();
    }

    private void deleteActivity(Activity activity) {
        mActivitiesModel.deleteActivity(activity);
        mActivitiesModel.isActivityDeleted().observe(this, deleted -> {
            if (deleted) {
                showActivityDeletedMessage("Activity deleted.");
            } else {
                showActivityDeletedMessage("Failed to delete activity.");
            }
            hideProgress();
        });
    }

    public void showActivityDeletedMessage(String message) {
        /* TODO What happens after successful deleting */
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        Handler delay = new Handler();
        delay.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
            }
        }, 500);
    }

    private void showCompetitionViews() {
        mLlWinningTimeClass.setVisibility(View.VISIBLE);
        mLlPosition.setVisibility(View.VISIBLE);
        mHorizontalDivider_1.setVisibility(View.VISIBLE);
        mHorizontalDivider_2.setVisibility(View.VISIBLE);
    }
}

