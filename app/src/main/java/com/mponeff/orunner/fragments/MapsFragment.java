package com.mponeff.orunner.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    @BindView(R.id.rl_no_maps)
    RelativeLayout mRlNoMaps;
    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.add_activity_button)
    Button mAddActivityBtn;

    private MapsAdapter mAdapter;

    public static Fragment getInstance() {
        return new MapsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        setToolbar();

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
                Bundle outState = new Bundle();
                outState.putParcelable("data", map);
                Intent viewMap = new Intent(getActivity(), ViewMapActivity.class);
                viewMap.putExtras(outState);
                startActivity(viewMap);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        mAddActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSportsDialog();
            }
        });

        return rootView;
    }

    public void showMaps(List<Map> maps) {
        if (maps.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mRlNoMaps.setVisibility(View.VISIBLE);
        } else {
            mRlNoMaps.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.replaceAll(maps);
        }
    }

    private void setToolbar() {
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(mToolbar);
        appCompatActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Maps");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }

    private void showSportsDialog() {
        ChooseTypeDialog sportsDialog = new ChooseTypeDialog();
        sportsDialog.show(getFragmentManager(), null);
    }
}
