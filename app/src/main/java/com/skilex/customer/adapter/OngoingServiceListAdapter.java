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

import androidx.core.content.ContextCompat;

import com.skilex.customer.R;
import com.skilex.customer.bean.support.OngoingService;
import com.skilex.customer.bean.support.Service;
import com.skilex.customer.fragment.DynamicSubCatFragment;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class OngoingServiceListAdapter extends BaseAdapter {

    //    private final Transformation transformation;
    private Context context;
    private ArrayList<OngoingService> services;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    Boolean click = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    DynamicSubCatFragment dsf = new DynamicSubCatFragment();

    public OngoingServiceListAdapter(Context context, ArrayList<OngoingService> services) {
        this.context = context;
        this.services = services;
//        Collections.reverse(services);
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
        final OngoingServiceListAdapter.ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.ongoing_service_list_item, parent, false);

            holder = new OngoingServiceListAdapter.ViewHolder();
            holder.txtCatName = (TextView) convertView.findViewById(R.id.category_name);
            holder.txtSubCatName = (TextView) convertView.findViewById(R.id.sub_category_name);
            holder.onHold = (ImageView) convertView.findViewById(R.id.img_hol);
            holder.txtStatus = (TextView) convertView.findViewById(R.id.service_status);
            holder.txtStatus.setText(services.get(position).getorder_status());
            if(PreferenceStorage.getLang(context).equalsIgnoreCase("tamil")) {
                holder.txtCatName.setText(services.get(position).getmain_category_ta());
                holder.txtSubCatName.setText(services.get(position).getservice_ta_name());
            } else {
                holder.txtCatName.setText(services.get(position).getmain_category());
                holder.txtSubCatName.setText(services.get(position).getservice_name());
            }
            holder.txtDate = (TextView) convertView.findViewById(R.id.customer_name);
            holder.txtDate.setText(services.get(position).getContact_person_name());
            holder.txtTime = (TextView) convertView.findViewById(R.id.service_date);
            holder.txtTime.setText(services.get(position).getOrder_date());
            holder.txtStatus.setText(services.get(position).getorder_status());
            if(services.get(position).getorder_status().equalsIgnoreCase("Hold")){
                holder.onHold.setImageResource(R.drawable.ic_onhold);
                holder.onHold.setBackgroundColor(ContextCompat.getColor(context, R.color.on_hold));
            } else {
                holder.onHold.setImageResource(R.drawable.ic_ongoing_service);
                holder.onHold.setBackgroundColor(ContextCompat.getColor(context, R.color.ongoing));
            }
            convertView.setTag(holder);
        } else {
            holder = (OngoingServiceListAdapter.ViewHolder) convertView.getTag();
            holder.txtCatName = (TextView) convertView.findViewById(R.id.category_name);
            holder.txtSubCatName = (TextView) convertView.findViewById(R.id.sub_category_name);
            if(PreferenceStorage.getLang(context).equalsIgnoreCase("tamil")) {
                holder.txtCatName.setText(services.get(position).getmain_category_ta());
                holder.txtSubCatName.setText(services.get(position).getservice_ta_name());
            } else {
                holder.txtCatName.setText(services.get(position).getmain_category());
                holder.txtSubCatName.setText(services.get(position).getservice_name());
            }
            holder.txtDate = (TextView) convertView.findViewById(R.id.customer_name);
            holder.txtDate.setText(services.get(position).getContact_person_name());
            holder.txtTime = (TextView) convertView.findViewById(R.id.service_date);
            holder.txtTime.setText(services.get(position).getOrder_date());
            if(services.get(position).getorder_status().equalsIgnoreCase("Hold")){
                holder.onHold.setImageResource(R.drawable.ic_onhold);
                holder.onHold.setBackgroundColor(ContextCompat.getColor(context, R.color.on_hold));
            } else {
                holder.onHold.setImageResource(R.drawable.ic_ongoing_service);
                holder.onHold.setBackgroundColor(ContextCompat.getColor(context, R.color.ongoing));
            }
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
        public TextView txtCatName, txtSubCatName, txtDate, txtTime, txtStatus;
        private ImageView onHold;
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