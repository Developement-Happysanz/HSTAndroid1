package com.skilex.customer.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
            serviceStartTime, estimatedCost, serviceRestartTime, serviceRestartdate, serviceRestartTimeText, serviceRestartdateText;
    Button track;
    private ImageView onHold;
    LinearLayout nameLay;

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

        initiateAll();

        callGetSubCategoryServiceDetails();

    }

    public void callNumber() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + servicePersonPhone.getText().toString()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                Toast.makeText(this, "You need to enable permissions to make call !", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        startActivity(callIntent);
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

        nameLay = findViewById(R.id.name_layout);
        nameLay.setOnClickListener(this);
        catName = (TextView) findViewById(R.id.category_name);
        subCatName = (TextView) findViewById(R.id.sub_category_name);
        custName = (TextView) findViewById(R.id.customer_name);
        servicedate = (TextView) findViewById(R.id.service_date);
        orderID = (TextView) findViewById(R.id.order_id);
        onHold = findViewById(R.id.img_status);
        serviceProvider = (TextView) findViewById(R.id.service_provider_name_text);
        servicePerson = (TextView) findViewById(R.id.service_person_name);
//        servicePersonPhone = (TextView) findViewById(R.id.service_person_experience);
        servicePersonPhone = (TextView) findViewById(R.id.service_person_number);
        servicePersonPhone.setOnClickListener(this);
        serviceStartTime = (TextView) findViewById(R.id.service_statring_time_text);
        serviceRestartTime = (TextView) findViewById(R.id.service_restarting_time);
        serviceRestartdate = (TextView) findViewById(R.id.service_restarting_date);
        serviceRestartTimeText = (TextView) findViewById(R.id.service_restarting_time_text);
        serviceRestartdateText = (TextView) findViewById(R.id.service_restarting_date_text);
        estimatedCost = (TextView) findViewById(R.id.service_estimate_text);
        track = (Button) findViewById(R.id.track);
        track.setOnClickListener(this);
        if (ongoingService.getorder_status().equalsIgnoreCase("Initiated")) {
            track.setVisibility(View.VISIBLE);
        } else {
            track.setVisibility(View.GONE);

        }

    }

    private boolean validateResponse(JSONObject response) {
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

                        if (PreferenceStorage.getLang(this).equalsIgnoreCase("tamil")) {
                            AlertDialogHelper.showSimpleAlertDialog(this, msg_ta);
                        } else {
                            AlertDialogHelper.showSimpleAlertDialog(this, msg_en);
                        }

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
//                "service_order_id": "45",
//    "main_category": "Appliance Repair",
//    "main_category_ta": "வீடு பழுது பார்த்தல்",
//    "sub_category": "AC Service and Repair",
//    "sub_category_ta": "AC சேவை மற்றும் பழுதுபார்க்கும்",
//    "service_name": "Insurance 3",
//    "service_ta_name": "Insurance 3",
//    "contact_person_name": "bala",
//    "contact_person_number": "9500923685",
//    "service_address": "Neelikonam Palayam, Coimbatore, India, ",
//    "order_date": "2019-07-26",
//    "time_slot": "20:00:00-21:00:00",
//    "provider_name": "Victor",
//    "person_name": "Ganesh",
//    "person_id": "24",
//    "person_number": "1565643456",
//    "pic": "",
//    "estimated_cost": 200,
//    "order_status": "Initiated"
//                "order_date": "2019-10-29",
//                        "resume_date": "2019-10-31",
//                        "time_slot": "14:00:00-15:00:00",
//                        "r_time_slot": "09:00:00-10:00:00",
                JSONObject getData = response.getJSONObject("service_list");
                if (PreferenceStorage.getLang(this).equalsIgnoreCase("tam")) {
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

                if (getData.getString("person_name").isEmpty()) {
                    servicePerson.setText("Expert yet to be assigned");
                    findViewById(R.id.service_person_abt_title).setVisibility(View.GONE);
                } else {
                    servicePerson.setText(getData.getString("person_name"));
                }

                if (getData.getString("person_number").isEmpty()) {
                    servicePersonPhone.setVisibility(View.GONE);
                    findViewById(R.id.service_person_abt_title).setVisibility(View.GONE);
                } else {
                    servicePersonPhone.setText(getData.getString("person_number"));
                }

                serviceStartTime.setText(getData.getString("time_slot"));
                estimatedCost.setText("₹"+getData.getInt("estimated_cost"));
                PreferenceStorage.savePersonId(this, getData.getString("person_id"));
                if(getData.getString("order_status").equalsIgnoreCase("Hold")) {
                    onHold.setBackgroundColor(ContextCompat.getColor(this, R.color.on_hold));
                    onHold.setImageResource( R.drawable.ic_onhold);
                    serviceRestartTime.setVisibility(View.VISIBLE);
                    serviceRestartdate.setVisibility(View.VISIBLE);
                    serviceRestartTimeText.setVisibility(View.VISIBLE);
                    serviceRestartdateText.setVisibility(View.VISIBLE);
                    serviceRestartTimeText.setText(getData.getString("r_time_slot"));
                    serviceRestartdateText.setText(getData.getString("resume_date"));
                } else {
                    onHold.setBackgroundColor(ContextCompat.getColor(this, R.color.ongoing));
                    onHold.setImageResource( R.drawable.ic_ongoing_service);
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
        if (v == track) {
            Intent i = new Intent(getApplicationContext(), ServicePersonTrackingActivity.class);
            i.putExtra("serviceObj", ongoingService);
            startActivity(i);
            finish();
        } else if (v == nameLay) {
            callNumber();
        }
    }
}
