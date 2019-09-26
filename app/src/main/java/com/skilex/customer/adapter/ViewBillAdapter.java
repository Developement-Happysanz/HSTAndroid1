package com.skilex.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.skilex.customer.R;
import com.skilex.customer.bean.support.Bill;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExValidator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class ViewBillAdapter extends RecyclerView.Adapter<ViewBillAdapter.ViewHolder> {

    private ArrayList<Bill> categoryArrayList;
    private Context context;
    private ViewBillAdapter.OnItemClickListener onItemClickListener;
    private View.OnClickListener onClickListener;
    private final Transformation transformation;

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
    public ViewBillAdapter(Context context, ArrayList<Bill> categoryArrayList, ViewBillAdapter.OnItemClickListener onItemClickListener) {
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
    public ViewBillAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View parentView;
        //Log.d("CategoryAdapter","viewType is"+ viewType);
        //if (viewType == 1) {
        parentView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_bill_list_item, parent, false);

//        }
//        else {
//            parentView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.preference_view_type2, parent, false);
//        }
        // set the view's size, margins, paddings and layout parameters
        ViewBillAdapter.ViewHolder vh = new ViewBillAdapter.ViewHolder(parentView, viewType);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewBillAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(PreferenceStorage.getLang(context).equalsIgnoreCase("tamil")) {
            holder.mPrefTextView.setText(categoryArrayList.get(position).getId());
        } else {
            holder.mPrefTextView.setText(categoryArrayList.get(position).getId());
        }

        //imageLoader.displayImage(events.get(position).getEventLogo(), holder.imageView, AppController.getInstance().getLogoDisplayOptions());
        if (SkilExValidator.checkNullString(categoryArrayList.get(position).getFile_bill())) {
            Picasso.get().load(categoryArrayList.get(position).getFile_bill()).into(holder.mImageView);
        } else {
            holder.mImageView.setImageResource(R.drawable.ic_user_profile_image);
        }

    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();

    }

    public Bill getItem(int position) {
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

}