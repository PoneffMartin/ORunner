package com.mponeff.orunner.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mponeff.orunner.R;
import com.mponeff.orunner.adapters.ActivityAdapter;
import com.mponeff.orunner.data.entities.Activity;
import com.mponeff.orunner.viewmodels.ActivitiesViewModel;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryFragment extends Fragment {

    @BindView(R.id.rv_history)
    RecyclerView mRecyclerView;
    @BindView(R.id.ll_no_activities)
    LinearLayout mLlNoExercises;

    private ActivityAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitiesViewModel activitiesViewModel = ViewModelProviders.of(this).get(ActivitiesViewModel.class);
        activitiesViewModel.getActivities().observe(this, activities -> {
            this.showActivities(activities);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, rootView);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new ActivityAdapter(getContext(), new Comparator<Activity>() {
            @Override
            public int compare(Activity activity1, Activity activity2) {
                return Long.valueOf(activity2.getStartDateTime()).compareTo(activity1.getStartDateTime());
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    private void showActivities(List<Activity> activities) {
        if (activities == null || activities.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mLlNoExercises.setVisibility(View.VISIBLE);
        } else {
            mLlNoExercises.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.replaceAll(activities);
        }
    }
}
