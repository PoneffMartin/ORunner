package com.mponeff.orunner.adapters;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.mponeff.orunner.R;
import com.mponeff.orunner.data.entities.MonthReport;
import com.mponeff.orunner.utils.DateTimeUtils;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MonthReportsListAdapter extends RecyclerView.Adapter<MonthReportsListAdapter.ViewHolder> {
    private static final String TAG = ActivityAdapter.class.getSimpleName();

    private final Comparator<MonthReport> mComparator;
    private final SortedList<MonthReport> mMonthReportsList = new SortedList<>(MonthReport.class,
            new SortedList.Callback<MonthReport>() {
                @Override
                public int compare(MonthReport month_1, MonthReport month_2) {
                    return mComparator.compare(month_1, month_2);
                }

                @Override
                public void onInserted(int position, int count) {
                    notifyItemRangeInserted(position, count);
                }

                @Override
                public void onRemoved(int position, int count) {
                    notifyItemRangeRemoved(position, count);
                }

                @Override
                public void onMoved(int fromPosition, int toPosition) {
                    notifyItemMoved(fromPosition, toPosition);
                }

                @Override
                public void onChanged(int position, int count) {
                    notifyItemRangeChanged(position, count);
                }

                @Override
                public boolean areContentsTheSame(MonthReport oldItem, MonthReport newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areItemsTheSame(MonthReport item1, MonthReport item2) {
                    return item1 == item2;
                }
            });


    public MonthReportsListAdapter(Comparator<MonthReport> comparator) {
        mComparator = comparator;
    }

    @Override
    public MonthReportsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View activityView = inflater.inflate(R.layout.item_month_summary, parent, false);
        return new ViewHolder(activityView);
    }

    @Override
    public void onBindViewHolder(MonthReportsListAdapter.ViewHolder holder, int position) {
        MonthReport monthInfo = mMonthReportsList.get(position);

        String monthName = DateTimeUtils.getMonthName(monthInfo.getMonth());
        long totalDuration = monthInfo.getTotalDuration();
        long totalActivities = monthInfo.getTotalActivities();
        float totalDistance = monthInfo.getTotalDistance();
        String totalPace = monthInfo.getTotalPace();
        float trainingsDistance = monthInfo.getTrainingsTotalDistance();
        float competitionsDistance = monthInfo.getCompetitionsTotalDistance();
        int trainingsPercent = (int)((trainingsDistance / totalDistance) * 100);
        int competitionsPercent = 100 - trainingsPercent;

        holder.tvMonth.setText(String.format("%s", monthName));
        holder.tvDistance.setText(String.format("%.1fkm", totalDistance));
        holder.tvTrainingsDistance.setText(String.format("%.1fkm", trainingsDistance));
        holder.tvCompetitionsDistance.setText(String.format("%.1fkm", competitionsDistance));
        holder.pbTrainings.setMax((int)(totalDistance * 10));
        holder.pbCompetitions.setMax((int)(totalDistance * 10));
        holder.pbTrainings.setProgress((int)(trainingsDistance * 10));
        holder.pbCompetitions.setProgress((int)(competitionsDistance * 10));
        holder.tvTrainingsPercent.setText(String.format("%d", trainingsPercent));
        holder.tvCompetitionsPercent.setText(String.format("%d", competitionsPercent));
        holder.tvActivities.setText(String.valueOf(totalActivities));
        holder.tvDuration.setText(DateTimeUtils.convertSecondsToTimeString(totalDuration));
        holder.tvPace.setText(totalPace);
    }

    @Override
    public int getItemCount() {
        return mMonthReportsList.size();
    }

    @Override
    public long getItemId(int position) {
        return mMonthReportsList.get(position).hashCode();
    }

    public void add(MonthReport monthReport) {
        mMonthReportsList.add(monthReport);
    }

    public void remove(MonthReport monthReport) {
        mMonthReportsList.remove(monthReport);
    }

    public void add(List<MonthReport> monthSummaries) {
        mMonthReportsList.addAll(monthSummaries);
    }

    public void remove(List<MonthReport> monthSummaries) {
        mMonthReportsList.beginBatchedUpdates();
        for (MonthReport monthReport : monthSummaries) {
            mMonthReportsList.remove(monthReport);
        }
        mMonthReportsList.endBatchedUpdates();
    }

    public void replaceAll(List<MonthReport> monthSummaries) {
        mMonthReportsList.beginBatchedUpdates();
        for (int i = mMonthReportsList.size() - 1; i >= 0; i--) {
            final MonthReport monthReport = mMonthReportsList.get(i);
            if (!monthSummaries.contains(monthReport)) {
                mMonthReportsList.remove(monthReport);
            }
        }
        mMonthReportsList.addAll(monthSummaries);
        mMonthReportsList.endBatchedUpdates();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_month)
        TextView tvMonth;
        @BindView(R.id.tv_distance)
        TextView tvDistance;
        @BindView(R.id.trainings_bar)
        NumberProgressBar pbTrainings;
        @BindView(R.id.competitions_bar)
        NumberProgressBar pbCompetitions;
        @BindView(R.id.tv_trainings_distance)
        TextView tvTrainingsDistance;
        @BindView(R.id.tv_competitions_distance)
        TextView tvCompetitionsDistance;
        @BindView(R.id.tv_trainings_percent)
        TextView tvTrainingsPercent;
        @BindView(R.id.tv_competitions_percent)
        TextView tvCompetitionsPercent;
        @BindView(R.id.ll_summary_details)
        LinearLayout llSummaryDetails;
        @BindView(R.id.tv_activities)
        TextView tvActivities;
        @BindView(R.id.tv_duration)
        TextView tvDuration;
        @BindView(R.id.tv_pace)
        TextView tvPace;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
