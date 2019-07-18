package com.skilex.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.skilex.customer.R;
import com.skilex.customer.adapter.GeneralServiceListAdapter;
import com.skilex.customer.adapter.MainServiceListAdapter;
import com.skilex.customer.bean.support.CartService;
import com.skilex.customer.bean.support.CartServiceList;
import com.skilex.customer.bean.support.Service;
import com.skilex.customer.bean.support.ServiceList;
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

public class BookingSummaryAcivity extends AppCompatActivity implements IServiceListener, DialogClickListener {

    private static final String TAG = BookingSummaryAcivity.class.getName();
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ArrayList<CartService> serviceArrayList = new ArrayList<>();
    GeneralServiceListAdapter serviceListAdapter;
    ListView loadMoreListView;
    TextView advanceAmount, totalCost;
    String res = "";
    Button confrm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_summary);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCart();
            }
        });
        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddressActivity.class);
                startActivity(i);
            }
        });

        loadMoreListView = findViewById(R.id.listSumService);
        advanceAmount = (TextView) findViewById(R.id.additional_cost);
        totalCost = (TextView) findViewById(R.id.total_cost);
        confrm = (Button) findViewById(R.id.confirm);
        callGetSubCategoryService();
    }

    private boolean validateResponse(JSONObject response) {
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
                        AlertDialogHelper.showSimpleAlertDialog(this, msg);

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
        progressDialogHelper.hideProgressDialog();
        if (validateResponse(response)) {
            try {
                if (res.equalsIgnoreCase("clear")){
                    Intent i = new Intent(this, BookingSummaryAcivity.class);
                    startActivity(i);
                    finish();
                }
                else {
                    JSONArray getData = response.getJSONArray("cart_list");
//                loadMembersList(getData.length());
                    Gson gson = new Gson();
                    CartServiceList serviceList = gson.fromJson(response.toString(), CartServiceList.class);
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
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

    protected void updateListAdapter(ArrayList<CartService> serviceArrayList) {
        this.serviceArrayList.clear();
        this.serviceArrayList.addAll(serviceArrayList);
        if (serviceListAdapter == null) {
            serviceListAdapter = new GeneralServiceListAdapter(this, this.serviceArrayList);
            loadMoreListView.setAdapter(serviceListAdapter);
            advanceAmount.setText("" + serviceArrayList.get(0).getAdvance_amount());
            ArrayList<Integer> a = new ArrayList<>();
            for (int i = 0; i < serviceArrayList.size(); i++) {
//                a.add(Integer.parseInt(serviceArrayList.get(i).getRate_card()));
            }
            int sum = 0;
            for (Integer d : a) {
                sum += d;
            }
            totalCost.setText("" + sum);
        } else {
            serviceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(String error) {

    }

    public void callGetSubCategoryService() {
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadCat();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    private void loadCat() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.CART_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private void clearCart() {
        res = "clear";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.CLEAR_CART;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

}
