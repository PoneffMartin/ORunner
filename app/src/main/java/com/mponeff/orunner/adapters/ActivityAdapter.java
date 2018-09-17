package com.mponeff.orunner.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mponeff.orunner.R;
import com.mponeff.orunner.data.entities.Activity;
import com.mponeff.orunner.utils.DateTimeUtils;

import java.util.Comparator;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {
    private static final String TAG = ActivityAdapter.class.getSimpleName();

    private final Context mContext;
    private final Comparator<Activity> mComparator;
    private OnItemClickListener mOnClickListener;

    public interface OnItemClickListener {
        void onItemClick(Activity activity);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnClickListener = listener;
    }

    private final SortedList<Activity> mSortedList = new SortedList<>(Activity.class,
            new SortedList.Callback<Activity>() {
        @Override
        public int compare(Activity activity_1, Activity activity_2) {
            return mComparator.compare(activity_1, activity_2);
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
        public boolean areContentsTheSame(Activity oldItem, Activity newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Activity item1, Activity item2) {
            return item1 == item2;
        }
    });

    public ActivityAdapter(Context context, Comparator<Activity> comparator) {
        mComparator = comparator;
        mContext = context;
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView ivActivity;
        View typeIndicator;
        //TextView tvLocation;
        TextView tvTitle;
        TextView tvDuration;
        TextView tvDate;

        ActivityViewHolder(View itemView) {
            super(itemView);
            //ivActivity = (ImageView) itemView.findViewById(R.id.iv_type);
            //tvLocation = (TextView) itemView.findViewById(R.id.tv_location);
            typeIndicator = itemView.findViewById(R.id.type_indicator);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvDuration = (TextView) itemView.findViewById(R.id.tv_duration);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            Activity activity = mSortedList.get(position);
            mOnClickListener.onItemClick(activity);
        }
    }

    @Override
    public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View activityView = inflater.inflate(R.layout.item_activity, parent, false);

        return new ActivityViewHolder(activityView);
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position) {
        // Get the data model
        Activity activity = mSortedList.get(position);

        if(position % 2 == 1) {
            holder.itemView.setBackgroundColor(Color.parseColor("#f8f8f8"));
        }
        else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        // Get each View item
        //TextView  tvLocation = holder.tvLocation;
        View typeIndicator = holder.typeIndicator;
        TextView  tvTitle = holder.tvTitle;
        TextView  tvDuration = holder.tvDuration;
        TextView  tvDate = holder.tvDate;

        // Set each View item
        int color;
        if (activity.getType().equals("Training")) {
            color = R.color.training;
        } else {
            color = R.color.competition;
        }
        //ivExercise.setImageResource(image);
        //tvLocation.setText(activity.getLocation());
        typeIndicator.setBackgroundResource(color);
        tvTitle.setText(activity.getTitle());
        tvDuration.setText(DateTimeUtils.convertSecondsToTimeString(activity.getDuration()));
        tvDate.setText(DateTimeUtils.convertMillisToDateString(activity.getStartDateTime())); // TODO Change
    }

    public void add(Activity activity) {
        mSortedList.add(activity);
    }

    public void remove(Activity activity) {
        mSortedList.remove(activity);
    }

    public void remove(int position) {
        mSortedList.removeItemAt(position);
    }

    public void add(List<Activity> activities) {
        mSortedList.addAll(activities);
    }

    public void remove(List<Activity> activities) {
        mSortedList.beginBatchedUpdates();
        for (Activity activity : activities) {
            mSortedList.remove(activity);
        }
        mSortedList.endBatchedUpdates();
    }

    public void replaceAll(List<Activity> activities) {
        mSortedList.beginBatchedUpdates();
        for (int i = mSortedList.size() - 1; i >= 0; i--) {
            final Activity activity = mSortedList.get(i);
            if (!activities.contains(activity)) {
                mSortedList.remove(activity);
            }
        }
        mSortedList.addAll(activities);
        mSortedList.endBatchedUpdates();
    }

    public Context getContext() {
        return this.mContext;
    }

    @Override
    public int getItemCount() {
        return this.mSortedList.size();
    }
}

