package com.skilex.customer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.skilex.customer.R;
import com.skilex.customer.bean.support.Category;
import com.skilex.customer.utils.SkilExValidator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Created by nandhakumar.k on 01/01/16.
 * * Edited by Narendar on 01/07/19.
 */
public class PreferenceListAdapter extends RecyclerView.Adapter<PreferenceListAdapter.ViewHolder> implements Filterable {

    private ArrayList<Category> categoryArrayList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private View.OnClickListener onClickListener;
    private final Transformation transformation;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();
    @Override
    public Filter getFilter() {
        return new Filter() {
            private ArrayList<Category> filtered = new ArrayList<Category>();
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase();
                filtered.clear();
                if(charString.isEmpty()){
                    filtered = categoryArrayList;
                    //filteredCUG = CUG;
                }
                else{
                    for (Category cug : categoryArrayList){
                        if( cug.getCat_name().toLowerCase().contains(charString) || cug.getCat_ta_name().toLowerCase().contains(charString) ){
                            filtered.add(cug);
                        }
                    }
                    //filteredCUG = filtered;
                }
                FilterResults filterResults = new FilterResults();

                filterResults.values = filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //filteredCUG.clear();
                categoryArrayList = (ArrayList<Category>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public ImageView mImageView, Selecttick;
        public CheckBox checkTick;
        public TextView mPrefTextView;
        public RelativeLayout rlPref;
        public RelativeLayout slPref;

        public ViewHolder(View v, int viewType) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.txt_preference_name);
            mPrefTextView = (TextView) v.findViewById(R.id.txt_pref_category_name);
            Selecttick = (ImageView) v.findViewById(R.id.pref_tick);
            if (viewType == 1) {
                rlPref = (RelativeLayout) v.findViewById(R.id.rlPref);
            } else {
                rlPref = (RelativeLayout) v;
            }

            rlPref.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
//            else {
//                onClickListener.onClick(Selecttick);
//            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PreferenceListAdapter(Context context, ArrayList<Category> categoryArrayList, OnItemClickListener onItemClickListener) {
        this.categoryArrayList = categoryArrayList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;

        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(5)
                .oval(false)
                .build();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PreferenceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View parentView;
        //Log.d("CategoryAdapter","viewType is"+ viewType);
        //if (viewType == 1) {
        parentView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.preference_view, parent, false);

//        }
//        else {
//            parentView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.preference_view_type2, parent, false);
//        }
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(parentView, viewType);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mPrefTextView.setText(categoryArrayList.get(position).getCat_name());

        //imageLoader.displayImage(events.get(position).getEventLogo(), holder.imageView, AppController.getInstance().getLogoDisplayOptions());
        if (SkilExValidator.checkNullString(categoryArrayList.get(position).getCat_pic_url())) {
            Picasso.get().load(categoryArrayList.get(position).getCat_pic_url()).into(holder.mImageView);
        } else {
            holder.mImageView.setImageResource(R.drawable.ic_user_profile_image);
        }

    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();

    }

    public Category getItem(int position) {
        return categoryArrayList.get(position);
    }


    @Override
    public int getItemViewType(int position) {
        /*if ((position + 1) % 7 == 4 || (position + 1) % 7 == 0) {
            return 2;
        } else {
            return 1;
        }*/
        if (categoryArrayList.get(position) != null || categoryArrayList.get(position).getSize() > 0)
            return categoryArrayList.get(position).getSize();
        else
            return 1;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < categoryArrayList.size(); i++) {
            String eventname = categoryArrayList.get(i).getCat_name();
            if ((eventname != null) && !(eventname.isEmpty())) {
                if (eventname.toLowerCase().contains(eventName.toLowerCase())) {
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
        // notifyDataSetChanged();
    }

    public void clearSearchFlag() {
        mSearching = false;
    }

}
