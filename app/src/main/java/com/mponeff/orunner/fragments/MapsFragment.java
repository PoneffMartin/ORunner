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
import com.mponeff.orunner.activities.ViewMapActivity;
import com.mponeff.orunner.adapters.MapsAdapter;
import com.mponeff.orunner.data.entities.Map;
import com.mponeff.orunner.viewmodels.MapViewModel;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsFragment extends Fragment {

    @BindView(R.id.rv_maps)
    RecyclerView mRecyclerView;
    @BindView(R.id.ll_no_maps)
    LinearLayout mLlNoMaps;

    private MapsAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapViewModel mapsViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        mapsViewModel.getMaps().observe(this, maps -> {
            this.showMaps(maps);
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_maps_history, container, false);
        ButterKnife.bind(this, rootView);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new MapsAdapter(getContext(), new Comparator<Map>() {
            @Override
            public int compare(Map map1, Map map2) {
                return Long.valueOf(map2.getDate()).compareTo(map1.getDate());
            }
        });

        mAdapter.setOnItemClickListener(new MapsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Map map) {
                showMap(map);
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    public void showMaps(List<Map> maps) {
        if (maps.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mLlNoMaps.setVisibility(View.VISIBLE);
        } else {
            mLlNoMaps.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.replaceAll(maps);
        }
    }

    private void showMap(Map map) {
        Bundle outState = new Bundle();
        outState.putParcelable("data", map);
        Intent viewMap = new Intent(getActivity(), ViewMapActivity.class);
        viewMap.putExtras(outState);
        startActivity(viewMap);
    }
}
