package com.skilex.customer.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.skilex.customer.R;
import com.skilex.customer.bean.support.Category;
import com.skilex.customer.bean.support.TrendingServices;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExValidator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class TrendingServiceListAdapter extends RecyclerView.Adapter<TrendingServiceListAdapter.ViewHolder> implements Filterable {

    private ArrayList<TrendingServices> categoryArrayList;
    private Context context;
    private TrendingServiceListAdapter.OnItemClickListener onItemClickListener;
    private View.OnClickListener onClickListener;
    private final Transformation transformation;

    @Override
    public Filter getFilter() {
        return new Filter() {
            private ArrayList<TrendingServices> filtered = new ArrayList<TrendingServices>();
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase();
                filtered.clear();
                if(charString.isEmpty()){
                    filtered = categoryArrayList;
                    //filteredCUG = CUG;
                }
                else{
                    for (TrendingServices cug : categoryArrayList){
                        if( cug.getservice_name().toLowerCase().contains(charString) || cug.getservice_ta_name().toLowerCase().contains(charString) ){
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
                categoryArrayList = (ArrayList<TrendingServices>) filterResults.values;
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
        public TextView mPrefTextView, mtrick;
        public RelativeLayout rlPref;
        public RelativeLayout slPref;

        public ViewHolder(View v, int viewType) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.txt_preference_name);
            mPrefTextView = (TextView) v.findViewById(R.id.txt_pref_category_name);
//            mtrick = (TextView) v.findViewById(R.id.trick);
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
                onItemClickListener.onItemCslick(v, getAdapterPosition());
            }
//            else {
//                onClickListener.onClick(Selecttick);
//            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TrendingServiceListAdapter(Context context, ArrayList<TrendingServices> categoryArrayList, TrendingServiceListAdapter.OnItemClickListener onItemClickListener) {
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
    public TrendingServiceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View parentView;
        //Log.d("CategoryAdapter","viewType is"+ viewType);
        //if (viewType == 1) {
        parentView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trend_services_list_item, parent, false);

//        }
//        else {
//            parentView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.preference_view_type2, parent, false);
//        }
        // set the view's size, margins, paddings and layout parameters
        TrendingServiceListAdapter.ViewHolder vh = new TrendingServiceListAdapter.ViewHolder(parentView, viewType);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TrendingServiceListAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(PreferenceStorage.getLang(context).equalsIgnoreCase("tamil")) {
            holder.mPrefTextView.setText(categoryArrayList.get(position).getservice_ta_name());
        } else {
            holder.mPrefTextView.setText(categoryArrayList.get(position).getservice_name());
        }

        //imageLoader.displayImage(events.get(position).getEventLogo(), holder.imageView, AppController.getInstance().getLogoDisplayOptions());
        if (SkilExValidator.checkNullString(categoryArrayList.get(position).getservice_pic_url())) {
            Picasso.get().load(categoryArrayList.get(position).getservice_pic_url()).into(holder.mImageView);
        } else {
            holder.mImageView.setImageResource(R.drawable.ic_user_profile_image);
        }
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(4, ViewGroup.LayoutParams.MATCH_PARENT);
//        params.setMargins(0,0,0,0);
//        params.addRule(RelativeLayout.ALIGN_PARENT_END);
//        TextView line1 = new TextView(context);
//        line1.setLayoutParams(new RelativeLayout.LayoutParams(4, ViewGroup.LayoutParams.MATCH_PARENT));
//        line1.setLayoutParams(params);
//        line1.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
//        holder.rlPref.addView(line1);
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();

    }

    public TrendingServices getItem(int position) {
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
        public void onItemCslick(View view, int position);
    }

}