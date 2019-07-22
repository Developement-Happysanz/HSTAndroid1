package com.skilex.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.Collections;

public class CategoryListAdapter extends BaseAdapter {

    //    private final Transformation transformation;
    private Context context;
    private ArrayList<SubCategory> categories;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();

    public CategoryListAdapter(Context context, ArrayList<SubCategory> categories) {
        this.context = context;
        this.categories = categories;
        Collections.reverse(categories);
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
            return categories.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return categories.get(mValidSearchIndices.get(position));
        } else {
            return categories.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.category_list_item, parent, false);

            holder = new ViewHolder();
            holder.categoryName = (TextView) convertView.findViewById(R.id.sub_category_name);
            if(PreferenceStorage.getLang(context).equalsIgnoreCase("tamil")) {
                holder.categoryName.setText(categories.get(position).getSub_cat_ta_name());
            } else {
                holder.categoryName.setText(categories.get(position).getSub_cat_name());
            }
            holder.categoryName.setText(categories.get(position).getSub_cat_name());
            holder.categoryImage = (ImageView) convertView.findViewById(R.id.sub_category_image);
            holder.add = (ImageView) convertView.findViewById(R.id.add_to_list);

//            holder.txtStatus.setText(categories.get(position).getStatus());
//          convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
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
        for (int i = 0; i < categories.size(); i++) {
            String homeWorkTitle = categories.get(i).getSub_cat_name();
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
        public TextView categoryName;
        public ImageView categoryImage, add;
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