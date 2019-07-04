package com.swim.recipees;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    private int num_of_tabs;

    PageAdapter(FragmentManager fm, int num_of_tabs){
        super(fm);
        this.num_of_tabs = num_of_tabs;
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                return new FragFavourites();
            case 1:
                return new FragBrowse();
            case 2:
                return new FragNewRecipe();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return num_of_tabs;
    }
}
