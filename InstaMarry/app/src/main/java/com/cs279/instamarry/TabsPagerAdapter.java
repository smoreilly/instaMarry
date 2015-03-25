package com.cs279.instamarry;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by pauljs on 1/29/2015.
 * With help from http://www.androidhive.info/2013/10/android-tab-layout-with-swipeable-views-1/
 */

public class TabsPagerAdapter extends FragmentPagerAdapter {
    private FragmentExploreTab fragExplore;
    private FragmentPersonalTab fragPersonal;
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
        fragExplore = new FragmentExploreTab();
        fragPersonal = new FragmentPersonalTab();
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return fragExplore;
            case 1:
                // Games fragment activity
                return fragPersonal;
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

    public FragmentExploreTab getFragExplore() {
        return fragExplore;
    }

    public FragmentPersonalTab getFragPersonal() {
        return fragPersonal;
    }

}
