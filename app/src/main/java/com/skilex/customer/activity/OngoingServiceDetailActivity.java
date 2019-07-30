package com.skilex.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.skilex.customer.R;
import com.skilex.customer.bean.support.OngoingService;
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

import static android.util.Log.d;

public class OngoingServiceDetailActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = OngoingServiceDetailActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    OngoingService ongoingService;
    private TextView catName, subCatName, custName, servicedate, orderID, serviceProvider, servicePerson, servicePersonPhone,
            serviceStartTime, estimatedCost;
    Button track;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_services_detail);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ongoingService = (OngoingService) getIntent().getSerializableExtra("serviceObj");

        callGetSubCategoryServiceDetails();

        initiateAll();

    }

    public void callGetSubCategoryServiceDetails() {
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadOnGoService();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    private void loadOnGoService() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = ongoingService.getservice_order_id();
        try {
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.ONGOING_SERVICE_DETAILS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private void initiateAll() {
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        catName = (TextView) findViewById(R.id.category_name);
        subCatName = (TextView) findViewById(R.id.sub_category_name);
        custName = (TextView) findViewById(R.id.customer_name);
        servicedate = (TextView) findViewById(R.id.service_date);
        orderID = (TextView) findViewById(R.id.order_id);
        serviceProvider = (TextView) findViewById(R.id.service_provider_name_text);
        servicePerson = (TextView) findViewById(R.id.service_person_name);
        servicePersonPhone = (TextView) findViewById(R.id.service_person_experience);
        serviceStartTime = (TextView) findViewById(R.id.service_statring_time_text);
        estimatedCost = (TextView) findViewById(R.id.service_estimate_text);
        track = (Button) findViewById(R.id.track);
        track.setOnClickListener(this);
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
//                "service_order_id": "17",
//                        "service_name": "Insurance 5",
//                        "service_ta_name": "Insurance 5",
//                        "order_date": "2019-07-24",
//                        "time_slot": "15:00:00-16:00:00",
//                        "main_category": "Appliance Repair",
//                        "main_category_ta": "வீடு பழுது பார்த்தல்",
//                        "sub_category": "Salon at home for Women",
//                        "sub_category_ta": "பெண்கள் வீட்டிற்கு வரவேற்பு",
//                        "provider_name": "",
//                        "person_name": "",
//                        "person_number": "",
//                        "estimated_cost": 1234,
//                        "contact_person_name": "nnn",
//                        "contact_person_number": "9566883430"
                JSONObject getData = response.getJSONObject("service_list");
                if(PreferenceStorage.getLang(this).equalsIgnoreCase("tam")){
                    catName.setText(getData.getString("main_category_ta"));
                    subCatName.setText(getData.getString("service_ta_name"));

                } else {
                    catName.setText(getData.getString("main_category"));
                    subCatName.setText(getData.getString("service_name"));

                }
                custName.setText(getData.getString("contact_person_name"));
                servicedate.setText(getData.getString("order_date"));
                orderID.setText(getData.getString("service_order_id"));
                serviceProvider.setText(getData.getString("provider_name"));
                servicePerson.setText(getData.getString("person_name"));
                servicePersonPhone.setText(getData.getString("person_number"));
                serviceStartTime.setText(getData.getString("time_slot"));
                estimatedCost.setText(getData.getString("estimated_cost"));



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
        if (v == track) {
            Intent i = new Intent(getApplicationContext(), ServicePersonTrackingActivity.class);
            startActivity(i);
            finish();
        }
    }
}
