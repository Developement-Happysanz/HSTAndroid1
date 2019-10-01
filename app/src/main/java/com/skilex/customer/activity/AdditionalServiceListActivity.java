package com.skilex.customer.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.skilex.customer.R;
import com.skilex.customer.adapter.AdditionalServiceListAdapter;
import com.skilex.customer.adapter.MainServiceListAdapter;
import com.skilex.customer.bean.support.AdditionalService;
import com.skilex.customer.bean.support.AdditionalServiceList;
import com.skilex.customer.bean.support.CartService;
import com.skilex.customer.bean.support.Service;
import com.skilex.customer.bean.support.ServiceList;
import com.skilex.customer.fragment.DynamicSubCatFragment;
import com.skilex.customer.helper.AlertDialogHelper;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.CommonUtils;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class AdditionalServiceListActivity extends AppCompatActivity implements IServiceListener, DialogClickListener {

    private static final String TAG = AdditionalServiceListActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ArrayList<AdditionalService> serviceArrayList = new ArrayList<>();
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    AdditionalServiceListAdapter serviceListAdapter;
    ListView loadMoreListView;
    String ser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_service);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        ser = getIntent().getStringExtra("serv");

        loadMoreListView = findViewById(R.id.listSumService);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        callGetSubCategoryService();
    }

    public void callGetSubCategoryService() {
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadCart();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private void loadCart() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = ser;
        try {
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.ADDITIONAL_SERVICE;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                JSONArray getData = response.getJSONArray("service_list");
//                loadMembersList(getData.length());
                Gson gson = new Gson();
                AdditionalServiceList serviceList = gson.fromJson(response.toString(), AdditionalServiceList.class);
                if (serviceList.getserviceArrayList() != null && serviceList.getserviceArrayList().size() > 0) {
                    totalCount = serviceList.getCount();
//                    this.categoryArrayList.addAll(subCategoryList.getCategoryArrayList());
                    isLoadingForFirstTime = false;
                    updateListAdapter(serviceList.getserviceArrayList());
                } else {
                    if (serviceArrayList != null) {
                        serviceArrayList.clear();
                        updateListAdapter(serviceList.getserviceArrayList());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(SkilExConstants.PARAM_MESSAGE);
                String msg_en = response.getString(SkilExConstants.PARAM_MESSAGE_ENG);
                String msg_ta = response.getString(SkilExConstants.PARAM_MESSAGE_TAMIL);
                d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d(TAG, "Show error dialog");
//                        if (msg.equalsIgnoreCase("Services not found")) {
//                            msgErr = true;
//                        }
                        if (PreferenceStorage.getLang(this).equalsIgnoreCase("tamil")) {
                            AlertDialogHelper.showSimpleAlertDialog(this, msg_ta);
                        } else {
                            AlertDialogHelper.showSimpleAlertDialog(this, msg_en);
                        }
//                        if (msg.equalsIgnoreCase("Service not found")){
//                            noService = true;
//                        }

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

    protected void updateListAdapter(ArrayList<AdditionalService> serviceArrayList) {
        this.serviceArrayList.clear();
        this.serviceArrayList.addAll(serviceArrayList);
        if (serviceListAdapter == null) {
            serviceListAdapter = new AdditionalServiceListAdapter(this, this.serviceArrayList);
            loadMoreListView.setAdapter(serviceListAdapter);
        } else {
            serviceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(String error) {

    }
}
