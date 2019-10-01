package com.skilex.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.skilex.customer.R;
import com.skilex.customer.bean.support.OngoingService;
import com.skilex.customer.bean.support.ServiceHistory;
import com.skilex.customer.fragment.DynamicSubCatFragment;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.utils.PreferenceStorage;

import java.util.ArrayList;
import java.util.Collections;

public class ServiceHistoryListAdapter extends BaseAdapter {

    //    private final Transformation transformation;
    private Context context;
    private ArrayList<ServiceHistory> services;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    Boolean click = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    DynamicSubCatFragment dsf = new DynamicSubCatFragment();

    public ServiceHistoryListAdapter(Context context, ArrayList<ServiceHistory> services) {
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
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.service_history_list_item, parent, false);

            holder = new ViewHolder();
            holder.txtCatName = (TextView) convertView.findViewById(R.id.service_category);
            holder.txtSubCatName = (TextView) convertView.findViewById(R.id.service_name);
            holder.rtbRating = convertView.findViewById(R.id.ratingBar);
            if(PreferenceStorage.getLang(context).equalsIgnoreCase("tamil")) {
                holder.txtCatName.setText(services.get(position).getmain_category_ta());
                holder.txtSubCatName.setText(services.get(position).getservice_ta_name());
            } else {
                holder.txtCatName.setText(services.get(position).getmain_category());
                holder.txtSubCatName.setText(services.get(position).getservice_name());
            }
            holder.txtDate = (TextView) convertView.findViewById(R.id.service_date);
            holder.txtDate.setText(services.get(position).getorder_date());
            holder.txtTime = (TextView) convertView.findViewById(R.id.service_time_slot);
            holder.txtTime.setText(services.get(position).gettime_slot());

            holder.txtStatus = (TextView) convertView.findViewById(R.id.status);
            holder.imgStatus = (ImageView) convertView.findViewById(R.id.status_image);
            holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.rl_hist);
            if(services.get(position).getorder_status().equalsIgnoreCase("Paid")) {
                holder.txtStatus.setText(services.get(position).getorder_status());
                holder.imgStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.button_circle_completed));
                holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_completed));
                holder.rtbRating.setVisibility(View.VISIBLE);
                holder.rtbRating.setRating(Integer.parseInt(services.get(position).getRating()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.relativeLayout.setForeground(ContextCompat.getDrawable(context, R.drawable.transparent_round));
                }
            } else if(services.get(position).getorder_status().equalsIgnoreCase("Cancelled")) {
                holder.txtStatus.setText(services.get(position).getorder_status());
                holder.imgStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.button_circle_failed));
                holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cancel_service));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.relativeLayout.setForeground(ContextCompat.getDrawable(context, R.drawable.transparent_round));
                }
            } else if(services.get(position).getorder_status().equalsIgnoreCase("Completed")) {
                holder.txtStatus.setText(services.get(position).getorder_status());
                holder.imgStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.button_requested_services_filled));
                holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_requested));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    holder.relativeLayout.setForeground(ContextCompat.getDrawable(context, R.drawable.transparent_round));
//                }
            }
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.txtCatName = (TextView) convertView.findViewById(R.id.service_category);
            holder.txtSubCatName = (TextView) convertView.findViewById(R.id.service_name);
            if(PreferenceStorage.getLang(context).equalsIgnoreCase("tamil")) {
                holder.txtCatName.setText(services.get(position).getmain_category_ta());
                holder.txtSubCatName.setText(services.get(position).getservice_ta_name());
            } else {
                holder.txtCatName.setText(services.get(position).getmain_category());
                holder.txtSubCatName.setText(services.get(position).getservice_name());
            }
            holder.txtDate = (TextView) convertView.findViewById(R.id.service_date);
            holder.txtDate.setText(services.get(position).getorder_date());
            holder.txtTime = (TextView) convertView.findViewById(R.id.service_time_slot);
            holder.txtTime.setText(services.get(position).gettime_slot());

            holder.txtStatus = (TextView) convertView.findViewById(R.id.status);
            holder.imgStatus = (ImageView) convertView.findViewById(R.id.status_image);
            holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.rl_hist);
            if(services.get(position).getorder_status().equalsIgnoreCase("Paid")) {
                holder.txtStatus.setText(services.get(position).getorder_status());
                holder.imgStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.button_circle_completed));
                holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_completed));
                holder.rtbRating.setVisibility(View.VISIBLE);
                holder.rtbRating.setRating(Integer.parseInt(services.get(position).getRating()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.relativeLayout.setForeground(ContextCompat.getDrawable(context, R.drawable.transparent_round));
                }
            } else if(services.get(position).getorder_status().equalsIgnoreCase("Cancelled")) {
                holder.txtStatus.setText(services.get(position).getorder_status());
                holder.imgStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.button_circle_failed));
                holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cancel_service));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.relativeLayout.setForeground(ContextCompat.getDrawable(context, R.drawable.transparent_round));
                }
            } else if(services.get(position).getorder_status().equalsIgnoreCase("Completed")) {
                holder.txtStatus.setText(services.get(position).getorder_status());
                holder.imgStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.button_requested_services_filled));
                holder.imgStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_requested));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    holder.relativeLayout.setForeground(ContextCompat.getDrawable(context, R.drawable.transparent_round));
//                }
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
        private TextView txtCatName, txtSubCatName, txtDate, txtTime, txtStatus;
        private ImageView imgStatus, serviceImage;
        private RelativeLayout relativeLayout;
        public RatingBar rtbRating;
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