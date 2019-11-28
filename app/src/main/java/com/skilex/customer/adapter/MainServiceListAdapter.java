package com.skilex.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.skilex.customer.R;
import com.skilex.customer.activity.SplashScreenActivity;
import com.skilex.customer.activity.SubCategoryActivity;
import com.skilex.customer.bean.support.Service;
import com.skilex.customer.bean.support.SubCategory;
import com.skilex.customer.fragment.DynamicSubCatFragment;
import com.skilex.customer.helper.AlertDialogHelper;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import static android.util.Log.d;
import static com.android.volley.VolleyLog.TAG;

public class MainServiceListAdapter extends BaseAdapter implements IServiceListener, DialogClickListener {

    //    private final Transformation transformation;
    private Context context;
    private ArrayList<Service> services;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    Boolean click = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ArrayList<Integer> pos = new ArrayList<>();
    private String res = "";
    DynamicSubCatFragment dsf = new DynamicSubCatFragment();

    public MainServiceListAdapter(Context context, ArrayList<Service> services) {
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
        serviceHelper = new ServiceHelper(context);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(context);
        final MainServiceListAdapter.ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.category_list_item, parent, false);

            holder = new MainServiceListAdapter.ViewHolder();
            holder.txtCatName = (TextView) convertView.findViewById(R.id.sub_category_name);
            if(PreferenceStorage.getLang(context).equalsIgnoreCase("tamil")) {
                holder.txtCatName.setText(services.get(position).getservice_ta_name());
            } else {
                holder.txtCatName.setText(services.get(position).getservice_name());
            }
            holder.imgCat = (ImageView) convertView.findViewById(R.id.sub_category_image);
            String url = services.get(position).getservice_pic_url();
            if (((url != null) && !(url.isEmpty()))) {
                Picasso.get().load(url).into(holder.imgCat);
            }
            holder.addList = (ImageView) convertView.findViewById(R.id.add_to_list);
            if (services.get(position).getSelected().equalsIgnoreCase("1")) {
                holder.addList.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                holder.addList.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_completed));

            } else {
                holder.addList.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                holder.addList.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_control_point_black_24dp));
            }
            final int finalPosition = position;
            final int finalPosition1 = position;
            holder.addList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == holder.addList) {
                        if (services.get(finalPosition1).getSelected().equalsIgnoreCase("0")) {
                            if (checkLogin(finalPosition1)) {
                                holder.addList.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                                holder.addList.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_completed));
                                services.get(finalPosition1).setSelected("1");
                            }
                        }
                        else {
                            holder.addList.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                            holder.addList.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_control_point_black_24dp));
                            removeService(finalPosition1);
                            services.get(finalPosition1).setSelected("0");
                        }
                    }
                    notifyDataSetChanged();
                }
            });
//            holder.imgCat.setImageDrawable(subCategories.get(position).ge());
//            holder.txtStatus = (TextView) convertView.findViewById(R.id.txt_mobilizer_status);
//            holder.txtStatus.setText(categories.get(position).getStatus());
          convertView.setTag(holder);
        } else {
            holder = (MainServiceListAdapter.ViewHolder) convertView.getTag();
            holder.txtCatName = (TextView) convertView.findViewById(R.id.sub_category_name);
            if(PreferenceStorage.getLang(context).equalsIgnoreCase("tamil")) {
                holder.txtCatName.setText(services.get(position).getservice_ta_name());
            } else {
                holder.txtCatName.setText(services.get(position).getservice_name());
            }
            holder.imgCat = (ImageView) convertView.findViewById(R.id.sub_category_image);
            String url = services.get(position).getservice_pic_url();
            if (((url != null) && !(url.isEmpty()))) {
                Picasso.get().load(url).into(holder.imgCat);
            }
            holder.addList = (ImageView) convertView.findViewById(R.id.add_to_list);
            if (services.get(position).getSelected().equalsIgnoreCase("1")) {
                holder.addList.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                holder.addList.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_completed));

            } else {
                holder.addList.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                holder.addList.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_control_point_black_24dp));
            }
            final int finalPosition = position;
            final int finalPosition1 = position;
            holder.addList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == holder.addList) {
                        if (services.get(finalPosition1).getSelected().equalsIgnoreCase("0")) {
                            if (checkLogin(finalPosition1)) {
                                holder.addList.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                                holder.addList.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_completed));
                                services.get(finalPosition1).setSelected("1");
                            }
                        }
                        else {
                            holder.addList.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                            holder.addList.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_control_point_black_24dp));
                            removeService(finalPosition1);
                            services.get(finalPosition1).setSelected("0");
                        }
                    }
                    notifyDataSetChanged();
                }
            });
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

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(SkilExConstants.PARAM_MESSAGE);
                d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(context, msg);

                    } else {
                        signInSuccess = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return signInSuccess;
    }

    @Override
    public void onResponse(JSONObject response) {
        if (validateSignInResponse(response)) {
            try {

                JSONObject data = response.getJSONObject("cart_total");
                String rate = data.getString("total_amt");
                String count = data.getString("service_count");
                PreferenceStorage.saveRate(context, rate);
                PreferenceStorage.saveServiceCount(context, count);
//                dsf.setrates(rate , count);
                PreferenceStorage.savePurchaseStatus(context, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

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

    private boolean checkLogin(int position) {
        String id = PreferenceStorage.getUserId(context);
        boolean a = false;
        if (!id.isEmpty()) {
            loadCat(position);
            a = true;
        } else {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
            alertDialogBuilder.setTitle("Login");
            alertDialogBuilder.setMessage("Log in to continue");
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    doLogout();
                }
            });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.show();
        }
        return a;
    }

    private void doLogout() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().clear().apply();
//        TwitterUtil.getInstance().resetTwitterRequestToken();

        Intent homeIntent = new Intent(context, SplashScreenActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(homeIntent);
        ((Activity) context).finish();
    }

    private void loadCat(int position) {
        res = "add";
        JSONObject jsonObject = new JSONObject();
        String idService = "";
        idService = services.get(position).getservice_id();
        String idCat = "";
        idCat = PreferenceStorage.getCatClick(context);
        String idSub = "";
        idSub = PreferenceStorage.getSubCatClick(context);
        String id = "";
        id = PreferenceStorage.getUserId(context);

        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ID, idService);
            jsonObject.put(SkilExConstants.CATEGORY_ID, idCat);
            jsonObject.put(SkilExConstants.SUB_CAT_ID, idSub);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.ADD_TO_CART;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }
    private void removeService(int position) {
        res = "add";
        JSONObject jsonObject = new JSONObject();
        String idService = "";
        idService = services.get(position).getservice_id();
        String idCat = "";
        idCat = PreferenceStorage.getCatClick(context);
        String idSub = "";
        idSub = PreferenceStorage.getSubCatClick(context);
        String id = "";
        id = PreferenceStorage.getUserId(context);

        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ID, idService);
            jsonObject.put(SkilExConstants.CATEGORY_ID, idCat);
            jsonObject.put(SkilExConstants.SUB_CAT_ID, idSub);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.REMOVE_FROM_CART_IMMEDIATELY;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }


}