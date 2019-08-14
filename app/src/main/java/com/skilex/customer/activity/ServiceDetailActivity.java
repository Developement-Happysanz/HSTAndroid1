package com.skilex.customer.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.skilex.customer.R;
import com.skilex.customer.bean.support.Category;
import com.skilex.customer.bean.support.Service;
import com.skilex.customer.helper.AlertDialogHelper;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.CommonUtils;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class ServiceDetailActivity extends AppCompatActivity implements IServiceListener, View.OnClickListener, DialogClickListener {
    private static final String TAG = ServiceDetailActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ImageView serviceImage;
    private TextView serviceCost, costText, serviceIncludes, serviceExcludes, serviceProcedure, serviceOthers;
    private ScrollView scrollView;
    Service service;
    Button bookNow;
    String res = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        service = (Service) getIntent().getSerializableExtra("serviceObj");
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        serviceCost = (TextView) findViewById(R.id.cost);
        costText = (TextView) findViewById(R.id.cost_text);
        serviceIncludes = (TextView) findViewById(R.id.include_text);
        serviceExcludes = (TextView) findViewById(R.id.exclude_text);
        serviceProcedure = (TextView) findViewById(R.id.procedure_text);
        serviceOthers = (TextView) findViewById(R.id.others_text);
        scrollView = (ScrollView) findViewById(R.id.extras);
        serviceImage = (ImageView) findViewById(R.id.service_image);
        bookNow = (Button) findViewById(R.id.book_now);
        bookNow.setOnClickListener(this);

        callGetSubCategoryService();
    }

    public void callGetSubCategoryService() {
//        if (classTestArrayList != null)
//            classTestArrayList.clear();

        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            getServiceDetail();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    private void getServiceDetail() {
        res = "detail";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = service.getservice_id();
        try {
            jsonObject.put(SkilExConstants.SERVICE_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.GET_SERVICE_DETAIL;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void bookService() {
//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        res = "cart";
        JSONObject jsonObject = new JSONObject();

        String idService = "";
        idService = service.getservice_id();
        String idCat = "";
        idCat = PreferenceStorage.getCatClick(this);
        String idSub = "";
        idSub = PreferenceStorage.getSubCatClick(this);
        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ID, idService);
            jsonObject.put(SkilExConstants.CATEGORY_ID, idCat);
            jsonObject.put(SkilExConstants.SUB_CAT_ID, idSub);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.ADD_TO_CART;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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
                if (res.equalsIgnoreCase("detail")) {
                    JSONObject data = response.getJSONObject("service_details");
                    serviceCost.setText("₹" + data.getString("rate_card"));
                    if (PreferenceStorage.getLang(this).equalsIgnoreCase("tam")) {
                        costText.setText(data.getString("rate_card_details"));
                    } else {
                        costText.setText(data.getString("rate_card_details_ta"));
                    }
                    if (!data.getString("inclusions").isEmpty() ||
                            !data.getString("exclusions").isEmpty() ||
                            !data.getString("service_procedure").isEmpty() ||
                            !data.getString("others").isEmpty()) {
                        if (PreferenceStorage.getLang(this).equalsIgnoreCase("tam")) {
                            serviceIncludes.setText(data.getString("inclusions_ta"));
                            serviceExcludes.setText(data.getString("exclusions_ta"));
                            serviceProcedure.setText(data.getString("service_procedure_ta"));
                            serviceOthers.setText(data.getString("others_ta"));
                        } else {
                            serviceIncludes.setText(data.getString("inclusions"));
                            serviceExcludes.setText(data.getString("exclusions"));
                            serviceProcedure.setText(data.getString("service_procedure"));
                            serviceOthers.setText(data.getString("others"));
                        }
                        scrollView.setVisibility(View.VISIBLE);
                    }
                    String url = "";
                    url = data.getString("service_pic_url");
                    if (!url.isEmpty()) {
                        Picasso.get().load(url).into(serviceImage);
                    }
                } else if (res.equalsIgnoreCase("cart")) {

                    JSONObject data = response.getJSONObject("cart_total");

                    String rate = data.getString("total_amt");
                    String count = data.getString("service_count");

                    PreferenceStorage.saveRate(this, rate);
                    PreferenceStorage.saveServiceCount(this, count);
                    PreferenceStorage.savePurchaseStatus(this, true);

                    Intent newIntent = new Intent(this, BookingSummaryAcivity.class);
                    startActivity(newIntent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onClick(View v) {
        if (v == bookNow) {
            if (PreferenceStorage.getUserId(this).equalsIgnoreCase("")) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
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
            } else {
                bookService();
            }

        }
    }

    private void doLogout() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().clear().apply();
//        TwitterUtil.getInstance().resetTwitterRequestToken();

        Intent homeIntent = new Intent(this, SplashScreenActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }
}
