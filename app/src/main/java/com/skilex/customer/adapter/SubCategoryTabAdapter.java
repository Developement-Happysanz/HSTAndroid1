package com.skilex.customer.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

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