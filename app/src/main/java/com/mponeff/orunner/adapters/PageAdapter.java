package com.mponeff.orunner.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mponeff.orunner.fragments.ActivitiesFragment;
import com.mponeff.orunner.fragments.OverviewFragment;

public class PageAdapter extends FragmentPagerAdapter {

    private static final String[] FRAGMENT_TITLES = {
            "Overview",
            "Activities"
    };

    public PageAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return FRAGMENT_TITLES.length;
    }


    // Returns the fragment that corresponds to the current position
    @Override
    public Fragment getItem(int position) {

        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new OverviewFragment();
                break;
            default:
                fragment = ActivitiesFragment.newInstance();
                break;
        }
        return fragment;
    }

}
