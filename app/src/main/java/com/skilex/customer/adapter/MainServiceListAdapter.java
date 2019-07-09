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
import com.skilex.customer.bean.support.Service;
import com.skilex.customer.bean.support.SubCategory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class MainServiceListAdapter extends BaseAdapter {

    //    private final Transformation transformation;
    private Context context;
    private ArrayList<Service> services;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    Boolean click = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();

    public MainServiceListAdapter(Context context, ArrayList<Service> services) {
        this.context = context;
        this.services = services;
        Collections.reverse(services);
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
            return services.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return services.get(mValidSearchIndices.get(position));
        } else {
            return services.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MainServiceListAdapter.ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.category_list_item, parent, false);

            holder = new MainServiceListAdapter.ViewHolder();
            holder.txtCatName = (TextView) convertView.findViewById(R.id.sub_category_name);
            holder.txtCatName.setText(services.get(position).getservice_name());
            holder.imgCat = (ImageView) convertView.findViewById(R.id.sub_category_image);
            String url = services.get(position).getservice_pic_url();
            if (((url != null) && !(url.isEmpty()))) {
                Picasso.get().load(url).into(holder.imgCat);
            }
            holder.addList = (ImageView) convertView.findViewById(R.id.add_to_list);
            holder.addList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == holder.addList) {
                        if (!click) {
                            holder.addList.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                            holder.addList.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_completed));

                        } else {
                            holder.addList.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                            holder.addList.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_control_point_black_24dp));
                        }
                    }
                }
            });
//            holder.imgCat.setImageDrawable(subCategories.get(position).ge());
//            holder.txtStatus = (TextView) convertView.findViewById(R.id.txt_mobilizer_status);
//            holder.txtStatus.setText(categories.get(position).getStatus());
//          convertView.setTag(holder);
        } else {
            holder = (MainServiceListAdapter.ViewHolder) convertView.getTag();
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
        for (int i = 0; i < services.size(); i++) {
            String homeWorkTitle = services.get(i).getservice_name();
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