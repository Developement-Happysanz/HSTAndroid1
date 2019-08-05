package com.skilex.customer.adapter;

import android.content.Context;
import android.preference.PreferenceScreen;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.skilex.customer.R;
import com.skilex.customer.bean.support.CartService;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;
import com.skilex.customer.utils.SkilExValidator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CartServiceDeleteListAdapter extends RecyclerView.Adapter<CartServiceDeleteListAdapter.ViewHolder> implements IServiceListener {

    private ArrayList<CartService> categoryArrayList;
    private Context context;
    private CartServiceDeleteListAdapter.OnItemClickListener onItemClickListener;
    private View.OnClickListener onClickListener;
    private final Transformation transformation;
    CartService mRecentlyDeletedItem;
    int mRecentlyDeletedItemPosition;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;


    @Override
    public void onResponse(JSONObject response) {
        try {
            String status = response.getString("status");
            if (status.equalsIgnoreCase("success")) {
                Toast.makeText(context, "Service Removed", Toast.LENGTH_SHORT).show();
                categoryArrayList.remove(mRecentlyDeletedItemPosition);
                notifyItemRemoved(mRecentlyDeletedItemPosition);
                if (categoryArrayList.size() == 0) {
                    PreferenceStorage.savePurchaseStatus(context,false);
                } else {
                    PreferenceStorage.saveCartStatus(context, true);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String error) {

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
            mImageView = (ImageView) v.findViewById(R.id.sub_category_image);
            mPrefTextView = (TextView) v.findViewById(R.id.sub_category_name);
            Selecttick = (ImageView) v.findViewById(R.id.add_to_list);
            Selecttick.setVisibility(View.GONE);

//            if (viewType == 1) {
//                rlPref = (RelativeLayout) v.findViewById(R.id.rlPref);
//            } else {
//                rlPref = (RelativeLayout) v;
//            }
//
//            rlPref.setOnClickListener(this);
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
    public CartServiceDeleteListAdapter(Context context, ArrayList<CartService> categoryArrayList, CartServiceDeleteListAdapter.OnItemClickListener onItemClickListener) {
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
    public CartServiceDeleteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
        // create a new view
        View parentView;
        //Log.d("CategoryAdapter","viewType is"+ viewType);
        //if (viewType == 1) {
        parentView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_list_item, parent, false);

//        }
//        else {
//            parentView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.preference_view_type2, parent, false);
//        }
        // set the view's size, margins, paddings and layout parameters
        CartServiceDeleteListAdapter.ViewHolder vh = new CartServiceDeleteListAdapter.ViewHolder(parentView, viewType);
        serviceHelper = new ServiceHelper(context);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(context);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CartServiceDeleteListAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (PreferenceStorage.getLang(context).equalsIgnoreCase("tamil")) {
            holder.mPrefTextView.setText(categoryArrayList.get(position).getservice_ta_name());
        } else {
            holder.mPrefTextView.setText(categoryArrayList.get(position).getservice_name());
        }

        //imageLoader.displayImage(events.get(position).getEventLogo(), holder.imageView, AppController.getInstance().getLogoDisplayOptions());
        if (SkilExValidator.checkNullString(categoryArrayList.get(position).getService_picture())) {
            Picasso.get().load(categoryArrayList.get(position).getService_picture()).into(holder.mImageView);
        } else {
            holder.mImageView.setImageResource(R.drawable.ic_user_profile_image);
        }

    }


    @Override
    public int getItemCount() {
        return categoryArrayList.size();

    }

    public CartService getItem(int position) {
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

    public void deleteItem(int position) {
        mRecentlyDeletedItem = categoryArrayList.get(position);
        mRecentlyDeletedItemPosition = position;
        removeService(position);

    }

    private void removeService(int position) {

        JSONObject jsonObject = new JSONObject();
        String idService = "";
        idService = categoryArrayList.get(position).getCart_id();
        try {
            jsonObject.put(SkilExConstants.CART_ID, idService);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.REMOVE_FROM_CART;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

}
