package com.mponeff.orunner.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.mponeff.orunner.R;
import com.mponeff.orunner.data.entities.MonthSummary;
import com.mponeff.orunner.fragments.SettingsFragment;
import com.mponeff.orunner.utils.DateTimeUtils;

import java.util.Comparator;
import java.util.List;

public class MonthReportsListAdapter extends RecyclerView.Adapter<MonthReportsListAdapter.ViewHolder> {

    private static final String LOG_TAG = ActivityAdapter.class.getSimpleName();

    private final Comparator<MonthSummary> mComparator;
    private Context mContext;
    private String  mUnits;
    private int mExpandedPosition = -1;
    private RecyclerView mRecyclerView;

    private final SortedList<MonthSummary> mMonthReportsList = new SortedList<>(MonthSummary.class,
            new SortedList.Callback<MonthSummary>() {
                @Override
                public int compare(MonthSummary month_1, MonthSummary month_2) {
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
                public boolean areContentsTheSame(MonthSummary oldItem, MonthSummary newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areItemsTheSame(MonthSummary item1, MonthSummary item2) {
                    return item1 == item2;
                }
            });


    public MonthReportsListAdapter(Context context, Comparator<MonthSummary> comparator) {
        mComparator = comparator;
        mContext = context;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUnits = sharedPreferences.getString(SettingsFragment.KEY_UNITS, "");
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public MonthReportsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View activityView = inflater.inflate(R.layout.item_month_summary, parent, false);
        ViewHolder viewHolder = new ViewHolder(activityView);
        return viewHolder;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onBindViewHolder(MonthReportsListAdapter.ViewHolder holder, int position) {

        Log.e(LOG_TAG, "onBIndViewHolder");
        MonthSummary monthInfo = mMonthReportsList.get(position);

        String monthName = DateTimeUtils.getMonthName(monthInfo.getMonth());
        long totalDuration = monthInfo.getTotalDuration();
        long totalActivities = monthInfo.getTotalActivities();
        float totalDistance;
        String totalPace;
        String units;

        if (mUnits.equalsIgnoreCase(mContext.getString(R.string.units_imperial))) {
            totalDistance = monthInfo.getTotalDistanceInMiles();
            totalPace = monthInfo.getTotalPaceInImperial();
            units = mContext.getString(R.string.unit_mil);
        } else {
            totalDistance = monthInfo.getTotalDistance();
            totalPace = monthInfo.getTotalPace();
            units = mContext.getString(R.string.unit_km);
        }

        boolean isExpanded = position == mExpandedPosition;
        holder.llSummaryDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : holder.getAdapterPosition();
                TransitionManager.beginDelayedTransition(mRecyclerView);
                notifyItemChanged(holder.getAdapterPosition());
            }
        });

        float trainingsDistance = monthInfo.getTrainingsTotalDistance();
        float competitionsDistance = monthInfo.getCompetitionsTotalDistance();
        int trainingsPercent = (int)((trainingsDistance / totalDistance) * 100);
        int competitionsPercent = 100 - trainingsPercent;

        /** Cell title */
        holder.tvMonth.setText(String.format("%s", monthName));
        holder.tvDistance.setText(String.format("%.1f", totalDistance));
        holder.tvDistanceUnit.setText(units);
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

    public void add(MonthSummary monthSummary) {
        mMonthReportsList.add(monthSummary);
    }

    public void remove(MonthSummary monthSummary) {
        mMonthReportsList.remove(monthSummary);
    }

    public void add(List<MonthSummary> monthSummaries) {
        mMonthReportsList.addAll(monthSummaries);
    }

    public void remove(List<MonthSummary> monthSummaries) {
        mMonthReportsList.beginBatchedUpdates();
        for (MonthSummary monthSummary : monthSummaries) {
            mMonthReportsList.remove(monthSummary);
        }
        mMonthReportsList.endBatchedUpdates();
    }

    public void replaceAll(List<MonthSummary> monthSummaries) {
        mMonthReportsList.beginBatchedUpdates();
        for (int i = mMonthReportsList.size() - 1; i >= 0; i--) {
            final MonthSummary monthSummary = mMonthReportsList.get(i);
            if (!monthSummaries.contains(monthSummary)) {
                mMonthReportsList.remove(monthSummary);
            }
        }
        mMonthReportsList.addAll(monthSummaries);
        mMonthReportsList.endBatchedUpdates();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        /** Cell title */
        protected TextView tvMonth;
        protected TextView tvDistance;
        protected TextView tvDistanceUnit;
        protected NumberProgressBar pbTrainings;
        protected NumberProgressBar pbCompetitions;
        protected TextView tvTrainingsDistance;
        protected TextView tvCompetitionsDistance;
        protected TextView tvTrainingsPercent;
        protected TextView tvCompetitionsPercent;
        protected LinearLayout llSummaryDetails;
        protected TextView tvActivities;
        protected TextView tvDuration;
        protected TextView tvPace;

        public ViewHolder(View itemView) {
            super(itemView);

            tvMonth = (TextView) itemView.findViewById(R.id.tv_month);
            tvDistance = (TextView) itemView.findViewById(R.id.tv_distance);
            tvDistanceUnit = (TextView) itemView.findViewById(R.id.tv_distance_unit);
            pbTrainings = (NumberProgressBar) itemView.findViewById(R.id.trainings_bar);
            pbCompetitions = (NumberProgressBar) itemView.findViewById(R.id.competitions_bar);
            tvTrainingsDistance = (TextView) itemView.findViewById(R.id.tv_trainings_distance);
            tvCompetitionsDistance = (TextView) itemView.findViewById(R.id.tv_competitions_distance);
            tvTrainingsPercent = (TextView) itemView.findViewById(R.id.tv_trainings_percent);
            tvCompetitionsPercent = (TextView) itemView.findViewById(R.id.tv_competitions_percent);
            llSummaryDetails = itemView.findViewById(R.id.ll_summary_details);
            tvActivities = itemView.findViewById(R.id.tv_activities);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvPace = itemView.findViewById(R.id.tv_pace);
        }
    }
}
