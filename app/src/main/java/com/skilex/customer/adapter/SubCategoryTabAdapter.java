package com.skilex.customer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.skilex.customer.fragment.DynamicSubCatFragment;

public class SubCategoryTabAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public SubCategoryTabAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }
    @Override
    public Fragment getItem(int position) {
        return DynamicSubCatFragment.newInstance(position);
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}