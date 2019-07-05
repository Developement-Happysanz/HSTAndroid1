package com.skilex.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skilex.customer.R;
import com.skilex.customer.bean.support.Category;
import com.skilex.customer.bean.support.SubCategory;
import com.skilex.customer.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class SubCategoryListAdapter extends BaseAdapter {

    //    private final Transformation transformation;
    private Context context;
    private ArrayList<SubCategory> subCategories;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();

    public SubCategoryListAdapter(Context context, ArrayList<SubCategory> subCategories) {
        this.context = context;
        this.subCategories = subCategories;
        Collections.reverse(subCategories);
//        transformation = new RoundedTransformationBuilder()
//                .cornerRadiusDp(0)
//                .oval(false)
//                .build();
        mSearching = false;
    }

    @Override
    public int getCount() {
        if (mSearching) {
            if (!mAnimateSearch) {
                mAnimateSearch = true;
            }
            return mValidSearchIndices.size();
        } else {
            return subCategories.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return subCategories.get(mValidSearchIndices.get(position));
        } else {
            return subCategories.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SubCategoryListAdapter.ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.category_list_item, parent, false);

            holder = new SubCategoryListAdapter.ViewHolder();
            holder.txtCatName = (TextView) convertView.findViewById(R.id.sub_category_name);
            holder.txtCatName.setText(subCategories.get(position).getSub_cat_name());
            holder.imgCat = (ImageView) convertView.findViewById(R.id.sub_category_image);
            String url = subCategories.get(position).getSub_cat_pic_url();
            if (((url != null) && !(url.isEmpty()))) {
                Picasso.get().load(url).into(holder.imgCat);
            }
//            holder.imgCat.setImageDrawable(subCategories.get(position).ge());
//            holder.txtStatus = (TextView) convertView.findViewById(R.id.txt_mobilizer_status);
//            holder.txtStatus.setText(categories.get(position).getStatus());
//          convertView.setTag(holder);
        } else {
            holder = (SubCategoryListAdapter.ViewHolder) convertView.getTag();
        }

        if (mSearching) {
            position = mValidSearchIndices.get(position);

        } else {
            Log.d("Event List Adapter", "getview pos called" + position);
        }

        return convertView;
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < subCategories.size(); i++) {
            String homeWorkTitle = subCategories.get(i).getSub_cat_name();
            if ((homeWorkTitle != null) && !(homeWorkTitle.isEmpty())) {
                if (homeWorkTitle.toLowerCase().contains(eventName.toLowerCase())) {
                    mValidSearchIndices.add(i);
                }
            }
        }
        Log.d("Event List Adapter", "notify" + mValidSearchIndices.size());
    }

    public void exitSearch() {
        mSearching = false;
        mValidSearchIndices.clear();
        mAnimateSearch = false;
    }

    public void clearSearchFlag() {
        mSearching = false;
    }

    public class ViewHolder {
        public TextView txtCatName;
        public ImageView imgCat, addList;
    }

    public boolean ismSearching() {
        return mSearching;
    }

    public int getActualEventPos(int selectedSearchpos) {
        if (selectedSearchpos < mValidSearchIndices.size()) {
            return mValidSearchIndices.get(selectedSearchpos);
        } else {
            return 0;
        }
    }
}