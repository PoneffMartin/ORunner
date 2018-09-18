package com.mponeff.orunner.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.mponeff.orunner.R;
import com.mponeff.orunner.activities.ViewActivity;
import com.mponeff.orunner.adapters.ActivityAdapter;
import com.mponeff.orunner.data.entities.Activity;
import com.mponeff.orunner.data.entities.CustomView;
import com.mponeff.orunner.data.entities.MonthSummary;
import com.mponeff.orunner.utils.DateTimeUtils;
import com.mponeff.orunner.utils.OnSwipeTouchListener;
import com.mponeff.orunner.viewmodels.ActivitiesModel;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OverviewFragment extends Fragment {
    public static final String TAG = OverviewFragment.class.getSimpleName();

    @BindView(R.id.rv_history)
    RecyclerView mRecyclerView;
    @BindView(R.id.ll_custom_view)
    LinearLayout llCustomView;
    @BindView(R.id.tv_custom_field_1)
    TextView tvCustomField_1;
    @BindView(R.id.tv_custom_field_2)
    TextView tvCustomField_2;
    @BindView(R.id.tv_trainings_count)
    TextView tvTrainingsCount;
    @BindView(R.id.tv_competitions_count)
    TextView tvCompetitionsCount;
    @BindView(R.id.month_report_chart)
    DecoView mChart;
    @BindView(R.id.ll_dots_slider)
    LinearLayout llDotsSlider;

    private ActivityAdapter mAdapter;
    private int mCustomViewPos = 0;
    private int mCustomViewsSize = 3;
    private CustomView[] mCustomViews = new CustomView[mCustomViewsSize];

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int currentYear = DateTimeUtils.getCurrentYear();
        int currentMonth = DateTimeUtils.getCurrentMonth();
        ActivitiesModel activitiesModel = ViewModelProviders.of(this).get(ActivitiesModel.class);
        activitiesModel.getActivities(currentYear, currentMonth).observe(this, activities -> {
            this.showOverview(activities);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);
        ButterKnife.bind(this, rootView);

        /* Draw background chart ring */
        mChart.configureAngles(300, 0);
        mChart.addSeries(new SeriesItem.Builder(Color.parseColor("#565B77"))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setLineWidth(10f)
                .build());

        mAdapter = new ActivityAdapter(getContext(), new Comparator<Activity>() {
            /* Order activities by date starting from latest */
            @Override
            public int compare(Activity activity1, Activity activity2) {
                return Long.valueOf(activity2.getStartDateTime()).compareTo(activity1.getStartDateTime());
            }
        });

        mAdapter.setOnItemClickListener(new ActivityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Activity activity) {
                showActivityDetails(activity);
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        /* Add swipe listener to the custom views layout */
        llCustomView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeRight() {
                swipeRight();
            }
        });

        return rootView;
    }

    private void loadChart(MonthSummary monthSummary) {

        int total = monthSummary.getTotalActivities();
        if (total > 0) {
            /* Competitions */
            int competitions = monthSummary.getCompetitions().size();
            SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#F67280"))
                    .setRange(0, total, 0)
                    .setLineWidth(18f)
                    .setShowPointWhenEmpty(false)
                    .build();

            int series1Index = mChart.addSeries(seriesItem1);

            /* Trainings */
            int trainings = monthSummary.getTrainings().size();
            SeriesItem seriesItem2 = new SeriesItem.Builder(Color.parseColor("#4286f4"))
                    .setRange(0, total, 0)
                    .setLineWidth(18f)
                    .setShowPointWhenEmpty(false)
                    .build();

            int series2Index = mChart.addSeries(seriesItem2);

            if (competitions > 0) {
                mChart.addEvent(new DecoEvent.Builder(total).setIndex(series1Index).setDelay(100).setDuration(2000).build());
            }

            mChart.addEvent(new DecoEvent.Builder(trainings).setIndex(series2Index).setDelay(200).setDuration(2000).build());
        }
    }

    private void showOverview(List<Activity> activities) {
        MonthSummary monthSummary = new MonthSummary(activities);
        loadChart(monthSummary);

        /* Initialize custom views array */
        /* TODO Export string resources */
        mCustomViews[0] = new CustomView(String.valueOf(monthSummary.getTotalActivities()), "Activities");
        mCustomViews[1] = new CustomView(String.valueOf(monthSummary.getTotalDistance()), "Distance");
        mCustomViews[2] = new CustomView(DateTimeUtils.convertSecondsToTimeString(monthSummary.getTotalDuration()), "Duration");

        /* Set initial custom view and slider position */
        tvCustomField_1.setText(mCustomViews[0].getField_1());
        tvCustomField_2.setText(mCustomViews[0].getField_2());
        slideDot(0);

        tvTrainingsCount.setText(String.valueOf(monthSummary.getTrainingsCount()));
        tvCompetitionsCount.setText(String.valueOf(monthSummary.getCompetitionsCount()));

        if (activities.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.replaceAll(activities);
        }
    }

    private void showActivityDetails(Activity activity) {
        Intent viewExercise = new Intent(getActivity(), ViewActivity.class);
        viewExercise.putExtra("data", activity);
        startActivity(viewExercise);
    }

    private void slideDot(int position) {
        for (int i = 0; i < mCustomViewsSize; i++) {
            if (i == position) {
                ((ImageView)llDotsSlider.getChildAt(i)).setImageDrawable(getContext().getDrawable(R.drawable.selected_dot));
            } else {
                ((ImageView)llDotsSlider.getChildAt(i)).setImageDrawable(getContext().getDrawable(R.drawable.default_dot));
            }
        }
    }

    private void swipeRight() {
        Log.e(TAG, "onSwipeRight"); // TODO Remove
        mCustomViewPos++;
        mCustomViewPos %= mCustomViewsSize;
        slideDot(mCustomViewPos);
        CustomView cv = mCustomViews[mCustomViewPos];
        tvCustomField_1.setText(cv.getField_1());
        tvCustomField_2.setText(cv.getField_2());
    }
}
