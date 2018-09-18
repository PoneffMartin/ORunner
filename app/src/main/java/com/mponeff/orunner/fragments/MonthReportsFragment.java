package com.mponeff.orunner.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mponeff.orunner.R;
import com.mponeff.orunner.adapters.MonthReportsListAdapter;
import com.mponeff.orunner.data.entities.MonthSummary;
import com.mponeff.orunner.viewmodels.MonthReportViewModel;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MonthReportsFragment extends Fragment {

    private static final String TAG = MonthReportsFragment.class.getSimpleName();

    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rl_no_month_reports)
    RelativeLayout mRlNoMonthReports;
    @BindView(R.id.month_reports_recycler_view)
    RecyclerView mMonthReportsRecyclerView;
    @BindView(R.id.iv_previous_year)
    ImageView mPreviousYearIv;
    @BindView(R.id.tv_current_year)
    TextView mCurrentYearTv;
    @BindView(R.id.iv_next_year)
    ImageView mNextYearIv;

    private MonthReportsListAdapter mAdapter;
    private MonthReportViewModel mMonthReportViewModel;
    private int year;

    public static Fragment getInstance() {
        return new MonthReportsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mMonthReportViewModel = ViewModelProviders.of(this).get(MonthReportViewModel.class);
        this.year = Calendar.getInstance().get(Calendar.YEAR);
        this.getSummaries(this.year);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_monthly_summaries, container, false);
        ButterKnife.bind(this, rootView);
        setToolbar();
        setupYearPicker();

        mMonthReportsRecyclerView.setHasFixedSize(true);
        mMonthReportsRecyclerView.setItemAnimator(null);
        mMonthReportsRecyclerView.setNestedScrollingEnabled(false);
        mMonthReportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new MonthReportsListAdapter(getContext(), new Comparator<MonthSummary>() {
            @Override
            public int compare(MonthSummary monthSummary1, MonthSummary monthSummary2) {
                int r = Integer.valueOf(monthSummary2.getYear())
                        .compareTo(Integer.valueOf(monthSummary1.getYear()));
                if (r != 0) return r;
                return Integer.valueOf(monthSummary2.getMonth())
                        .compareTo(Integer.valueOf(monthSummary1.getMonth()));
            }
        });

        mAdapter.setHasStableIds(true);
        mMonthReportsRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private void showReports(List<MonthSummary> reports) {
        if (reports.isEmpty()) {
            mRlNoMonthReports.setVisibility(View.VISIBLE);
            mMonthReportsRecyclerView.setVisibility(View.GONE);
        } else {
            mAdapter.replaceAll(reports);
            mRlNoMonthReports.setVisibility(View.GONE);
            mMonthReportsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setToolbar() {
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(mToolbar);
        appCompatActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Monthly summaries");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }

    private void setupYearPicker() {
        /** TODO Add min year */
        mCurrentYearTv.setText(String.valueOf(year));

        mPreviousYearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNextYearIv.setEnabled(true);
                int currentYear = Integer.parseInt(mCurrentYearTv.getText().toString()) - 1;
                mCurrentYearTv.setText(String.valueOf(currentYear));
                getSummaries(currentYear);
            }
        });

        mNextYearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentYear = Integer.parseInt(mCurrentYearTv.getText().toString()) + 1;
                mCurrentYearTv.setText(String.valueOf(currentYear));
                getSummaries(currentYear);
                if (currentYear == year) {
                    mNextYearIv.setEnabled(false);
                }
            }
        });

        /** Disable the next year iv by default */
        mNextYearIv.setEnabled(false);
    }

    private void getSummaries(int year) {
        this.mMonthReportViewModel.getSummaries(year).observe(this, summaries -> {
            this.showReports(summaries);
        });
    }

}
