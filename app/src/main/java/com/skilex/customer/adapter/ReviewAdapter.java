package com.skilex.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.skilex.customer.R;
import com.skilex.customer.app.AppController;
import com.skilex.customer.bean.support.Review;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class ReviewAdapter  extends BaseAdapter {

    private static final String TAG = ReviewAdapter.class.getName();
    private final Transformation transformation;
    private Context context;
    private ArrayList<Review> reviews;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();
    private ImageLoader imageLoader = AppController.getInstance().getUniversalImageLoader();

    public ReviewAdapter(Context context, ArrayList<Review> reviews) {

        this.context = context;
        this.reviews = reviews;

        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(5)
                .oval(false)
                .build();
        mSearching = false;
    }


    @Override
    public int getCount() {
        if (mSearching) {
            // Log.d("Event List Adapter","Search count"+mValidSearchIndices.size());
            if (!mAnimateSearch) {
                mAnimateSearch = true;
            }
            return mValidSearchIndices.size();

        } else {
            // Log.d(TAG,"Normal count size");
            return reviews.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return reviews.get(mValidSearchIndices.get(position));
        } else {
            return reviews.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.reviews_list_item, parent, false);

            holder = new ViewHolder();
            holder.txtComments = convertView.findViewById(R.id.txtComments);
            holder.rtbRating = convertView.findViewById(R.id.ratingBar);
            holder.txtUsernameDisp = convertView.findViewById(R.id.username_disp);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Review review = reviews.get(position);

        holder.txtComments.setText(reviews.get(position).getReview());
        holder.txtUsernameDisp.setText(reviews.get(position).getCustomer_name());
        holder.rtbRating.setRating(Integer.parseInt(reviews.get(position).getRating()));

        return convertView;
    }

    public class ViewHolder {
        public TextView txtComments, txtUsernameDisp;
        public RatingBar rtbRating;
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < reviews.size(); i++) {
            String planName = reviews.get(i).getCustomer_name();
            if ((planName != null) && !(planName.isEmpty())) {
                if (planName.toLowerCase().contains(eventName.toLowerCase())) {
                    mValidSearchIndices.add(i);
                }

            }

        }
        Log.d("Event List Adapter", "notify" + mValidSearchIndices.size());
        //notifyDataSetChanged();

    }

    public void exitSearch() {
        mSearching = false;
        mValidSearchIndices.clear();
        mAnimateSearch = false;
        // notifyDataSetChanged();
    }

    public void clearSearchFlag() {
        mSearching = false;
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
