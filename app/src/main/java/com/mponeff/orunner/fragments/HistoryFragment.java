package com.mponeff.orunner.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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
import com.mponeff.orunner.activities.ViewActivity;
import com.mponeff.orunner.adapters.ActivityAdapter;
import com.mponeff.orunner.data.entities.Activity;
import com.mponeff.orunner.viewmodels.ActivitiesModel;

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
        ActivitiesModel activitiesModel = ViewModelProviders.of(this).get(ActivitiesModel.class);
        activitiesModel.getActivities().observe(this, activities -> {
            this.showActivities(activities);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, rootView);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new ActivityAdapter(getContext(), new Comparator<Activity>() {
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
        return rootView;
    }

    private void showActivities(List<Activity> activities) {
        if (activities == null) {
            return;
        }

        if (activities.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mLlNoExercises.setVisibility(View.VISIBLE);
        } else {
            mLlNoExercises.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.replaceAll(activities);
        }
    }

    private void showActivityDetails(Activity activity) {
        Intent viewExercise = new Intent(getActivity(), ViewActivity.class);
        viewExercise.putExtra("data", activity);
        startActivity(viewExercise);
    }
}
