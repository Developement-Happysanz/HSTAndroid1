package com.skilex.customer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.skilex.customer.bean.support.SubCategory;
import com.skilex.customer.fragment.DynamicSubCatFragment;

import java.util.ArrayList;

public class SubCategoryTabAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    ArrayList<SubCategory> subCategoryArrayList;
    public SubCategoryTabAdapter(FragmentManager fm, int NumOfTabs, ArrayList<SubCategory> categoryArrayList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.subCategoryArrayList = categoryArrayList;
    }
    @Override
    public Fragment getItem(int position) {
        return DynamicSubCatFragment.newInstance(position,subCategoryArrayList);
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}