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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.skilex.customer.R;
import com.skilex.customer.activity.SplashScreenActivity;
import com.skilex.customer.bean.support.Feedback;
import com.skilex.customer.bean.support.Service;
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

import static android.util.Log.d;
import static com.android.volley.VolleyLog.TAG;

public class FeebackListAdapter extends BaseAdapter implements IServiceListener, DialogClickListener {

    //    private final Transformation transformation;
    private Context context;
    private ArrayList<Feedback> services;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    Boolean click = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ArrayList<Integer> pos = new ArrayList<>();
    private String feebackAns = "";
    private int pooos;

    public FeebackListAdapter(Context context, ArrayList<Feedback> services) {
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
        pooos = position;
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.feedback_list_item, parent, false);

            holder = new ViewHolder();
            holder.txtCatName = (TextView) convertView.findViewById(R.id.feedback_question);
            if (PreferenceStorage.getLang(context).equalsIgnoreCase("tamil")) {
                holder.txtCatName.setText(services.get(position).getfeedback_question());
            } else {
                holder.txtCatName.setText(services.get(position).getfeedback_question());
            }
            holder.yess = (RadioButton) convertView.findViewById(R.id.rad_yes);
            holder.noo = (RadioButton) convertView.findViewById(R.id.rad_no);
            holder.grp = (RadioGroup) convertView.findViewById(R.id.rad_group);
            holder.yess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == holder.yess) {
                        onRadioButtonClicked(holder.yess);
                    }
                }
            });
            holder.noo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == holder.noo) {
                        onRadioButtonClicked(holder.noo);
                    }
                }
            });

//            holder.imgCat.setImageDrawable(subCategories.get(position).ge());
//            holder.txtStatus = (TextView) convertView.findViewById(R.id.txt_mobilizer_status);
//            holder.txtStatus.setText(categories.get(position).getStatus());
            convertView.setTag(holder);
        } else {
            holder = (FeebackListAdapter.ViewHolder) convertView.getTag();
            holder.txtCatName = (TextView) convertView.findViewById(R.id.feedback_question);
            if (PreferenceStorage.getLang(context).equalsIgnoreCase("tamil")) {
                holder.txtCatName.setText(services.get(position).getfeedback_question());
            } else {
                holder.txtCatName.setText(services.get(position).getfeedback_question());
            }
            holder.yess = (RadioButton) convertView.findViewById(R.id.rad_yes);
            holder.noo = (RadioButton) convertView.findViewById(R.id.rad_no);
            holder.grp = (RadioGroup) convertView.findViewById(R.id.rad_group);
            holder.yess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == holder.yess) {
                        onRadioButtonClicked(holder.yess);
                    }
                }
            });
            holder.noo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == holder.noo) {
                        onRadioButtonClicked(holder.noo);
                    }
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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rad_yes:
                if (checked)
                    // Pirates are the best
                    feebackAns = "Yes";
                sendFeedbac(pooos);
                break;
            case R.id.rad_no:
                if (checked)
                    // Ninjas rule
                    feebackAns = "No";
                sendFeedbac(pooos);
                break;
        }
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < services.size(); i++) {
            String homeWorkTitle = services.get(i).getfeedback_question();
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
//            holder.grp.setEnabled(false);
//            holder.yess.setEnabled(false);
//            holder.noo.setEnabled(false);
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
        private TextView txtCatName;
        private RadioButton yess, noo;
        private RadioGroup grp;
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

    private void sendFeedbac(int position) {

        JSONObject jsonObject = new JSONObject();
        String idService = "";
        idService = PreferenceStorage.getRateOrderId(context);
        String idCat = "";
        idCat = services.get(position).getid();
        String idSub = "";
        idSub = PreferenceStorage.getSubCatClick(context);
        String id = "";
        id = PreferenceStorage.getUserId(context);

        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, idService);
            jsonObject.put(SkilExConstants.FEEDBAC_ID, idCat);
            jsonObject.put(SkilExConstants.FEEDBAC_tEXT, feebackAns);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.FEEDBACK_ANSWER;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

}