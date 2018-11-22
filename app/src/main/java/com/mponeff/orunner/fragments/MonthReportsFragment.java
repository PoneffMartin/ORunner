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
import com.mponeff.orunner.adapters.MonthReportsListAdapter;
import com.mponeff.orunner.data.entities.MonthReport;
import com.mponeff.orunner.viewmodels.MonthReportViewModel;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MonthReportsFragment extends Fragment {
    private static final String TAG = MonthReportsFragment.class.getSimpleName();

    @BindView(R.id.ll_no_month_reports)
    LinearLayout mLlNoMonthReports;
    @BindView(R.id.rv_month_reports)
    RecyclerView mMonthReportsRecyclerView;

    private MonthReportsListAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MonthReportViewModel monthReportViewModel = ViewModelProviders.of(this).get(MonthReportViewModel.class);
        monthReportViewModel.getAllReports().observe(this, reports -> {
            this.showReports(reports);
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_month_reports, container, false);
        ButterKnife.bind(this, rootView);

        mMonthReportsRecyclerView.setHasFixedSize(false);
        mMonthReportsRecyclerView.setNestedScrollingEnabled(false);
        mMonthReportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new MonthReportsListAdapter(new Comparator<MonthReport>() {
            @Override
            public int compare(MonthReport monthReport1, MonthReport monthReport2) {
                int r = Integer.valueOf(monthReport2.getYear())
                        .compareTo(Integer.valueOf(monthReport1.getYear()));
                if (r != 0) return r;
                return Integer.valueOf(monthReport2.getMonth())
                        .compareTo(Integer.valueOf(monthReport1.getMonth()));
            }
        });

        mAdapter.setHasStableIds(true);
        mMonthReportsRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private void showReports(List<MonthReport> reports) {
        if (reports == null || reports.isEmpty()) {
            mLlNoMonthReports.setVisibility(View.VISIBLE);
            mMonthReportsRecyclerView.setVisibility(View.GONE);
        } else {
            mLlNoMonthReports.setVisibility(View.GONE);
            mMonthReportsRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.replaceAll(reports);
        }
    }
}
