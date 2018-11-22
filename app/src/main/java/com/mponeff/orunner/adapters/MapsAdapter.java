package com.mponeff.orunner.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mponeff.orunner.R;
import com.mponeff.orunner.data.entities.Map;
import com.mponeff.orunner.utils.DateTimeUtils;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsAdapter extends RecyclerView.Adapter<MapsAdapter.CardViewViewHolder> {

    private static final String TAG = MapsAdapter.class.getSimpleName();
    private Comparator<Map> mComparator;
    private Context mContext;
    private OnItemClickListener mOnClickListener;

    public interface OnItemClickListener {
        void onItemClick(Map map);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnClickListener = listener;
    }

    private final SortedList<Map> mSortedList = new SortedList<>(Map.class,
            new SortedList.Callback<Map>() {
                @Override
                public int compare(Map map_1, Map map_2) {
                    return mComparator.compare(map_1, map_2);
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
                public boolean areContentsTheSame(Map oldItem, Map newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areItemsTheSame(Map item1, Map item2) {
                    return item1 == item2;
                }
            });

    class CardViewViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        @BindView(R.id.iv_map)
        ImageView ivMap;
        @BindView(R.id.tv_location)
        TextView tvLocation;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_date)
        TextView tvDate;

        CardViewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            Map map = mSortedList.get(position);
            mOnClickListener.onItemClick(map);
        }
    }

    public MapsAdapter(Context context, Comparator<Map> comparator) {
        mContext = context;
        mComparator = comparator;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public MapsAdapter.CardViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View activityView = inflater.inflate(R.layout.card_view_map, parent, false);
        CardViewViewHolder viewHolder = new CardViewViewHolder(activityView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MapsAdapter.CardViewViewHolder holder, int position) {

        Map map = mSortedList.get(position);
        String downloadUri = map.getDownloadUri();

        ImageView ivMap = holder.ivMap;
        TextView  tvLocation = holder.tvLocation;
        TextView  tvTitle = holder.tvTitle;
        TextView  tvDate = holder.tvDate;

        Glide.with(getContext())
                .load(Uri.parse(downloadUri))
                .into(ivMap);

        tvLocation.setText(map.getLocation());
        tvTitle.setText(map.getTitle());
        tvDate.setText(DateTimeUtils.convertMillisToDateString(map.getDate()));
    }

    public void add(Map map) {
        mSortedList.add(map);
    }

    public void remove(Map map) {
        mSortedList.remove(map);
    }

    public void add(List<Map> maps) {
        mSortedList.addAll(maps);
    }

    public void remove(List<Map> maps) {
        mSortedList.beginBatchedUpdates();
        for (Map map : maps) {
            mSortedList.remove(map);
        }
        mSortedList.endBatchedUpdates();
    }

    public void replaceAll(List<Map> maps) {
        mSortedList.beginBatchedUpdates();
        for (int i = mSortedList.size() - 1; i >= 0; i--) {
            final Map map = mSortedList.get(i);
            if (!maps.contains(map)) {
                mSortedList.remove(map);
            }
        }
        mSortedList.addAll(maps);
        mSortedList.endBatchedUpdates();
    }

    @Override
    public int getItemCount() {
        return mSortedList.size();
    }
}
