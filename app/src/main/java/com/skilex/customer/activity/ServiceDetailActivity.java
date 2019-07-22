package com.skilex.customer.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.CommonUtils;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class ServiceDetailActivity extends AppCompatActivity implements IServiceListener, View.OnClickListener {
    private static final String TAG = ServiceDetailActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ImageView serviceImage;
    private TextView serviceCost, serviceIncludes, serviceExcludes, serviceProcedure;
    private ScrollView scrollView;
    Service service;
    Button bookNow;

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
        serviceIncludes = (TextView) findViewById(R.id.include_text);
        serviceExcludes = (TextView) findViewById(R.id.exclude_text);
        serviceProcedure = (TextView) findViewById(R.id.procedure_text);
        scrollView = (ScrollView) findViewById(R.id.extras);
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
                JSONObject data = response.getJSONObject("service_details");
                serviceCost.setText("₹"+data.getString("rate_card"));
                if (!data.getString("inclusions").isEmpty() ||
                        !data.getString("exclusions").isEmpty() || !data.getString("service_procedure").isEmpty()) {
                    serviceIncludes.setText(data.getString("inclusions"));
                    serviceExcludes.setText(data.getString("exclusions"));
                    serviceProcedure.setText(data.getString("service_procedure"));
                    scrollView.setVisibility(View.VISIBLE);
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
            Intent newIntent = new Intent(this, BookingSummaryAcivity.class);
            startActivity(newIntent);
        }
    }
}
