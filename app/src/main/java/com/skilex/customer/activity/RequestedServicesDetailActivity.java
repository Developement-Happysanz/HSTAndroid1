package com.skilex.customer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class RequestedServicesDetailActivity extends AppCompatActivity implements IServiceListener, DialogClickListener {

    private static final String TAG = RequestedServicesDetailActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    OngoingService ongoingService;
    private TextView catName, subCatName, custName, servicedate, serviceTimeSlot, orderID, custNumber, custAddress, estimatedCost;
    Button cancel;
    String res = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_services_detail);

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
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private void loadOnGoService() {
        res = "load";
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

    private void cencelOrder() {
//        res = "cancel";
//        JSONObject jsonObject = new JSONObject();
//        String id = "";
//        id = ongoingService.getservice_order_id();
//        try {
//            jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserId(this));
//            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, id);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
////        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
//        String url = SkilExConstants.BUILD_URL + SkilExConstants.CANCEL_SERVICE;
//        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
        if (ongoingService.getAdvance_payment_status().equalsIgnoreCase("NA")) {
            Intent intent = new Intent(this, CancelRequestedServiceActivity.class);
            intent.putExtra("serviceObj", ongoingService);
            startActivity(intent);
            finish();
        } else {
            showCancelAlert();
        }

    }

    private void showCancelAlert() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(RequestedServicesDetailActivity.this);
        alertDialogBuilder.setTitle(R.string.alert_button_cancel);
        alertDialogBuilder.setMessage(R.string.cancel_service_alert);
        alertDialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(getApplicationContext(), CancelRequestedServiceActivity.class);
                intent.putExtra("serviceObj", ongoingService);
                startActivity(intent);
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
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
        custNumber = (TextView) findViewById(R.id.contact_number_text);
        custAddress = (TextView) findViewById(R.id.contact_address_text);
        catName = (TextView) findViewById(R.id.category_name);
        subCatName = (TextView) findViewById(R.id.sub_category_name);
        custName = (TextView) findViewById(R.id.contact_name_text);
        servicedate = (TextView) findViewById(R.id.service_date);
        serviceTimeSlot = (TextView) findViewById(R.id.service_time_slot);
        orderID = (TextView) findViewById(R.id.order_id);

        estimatedCost = (TextView) findViewById(R.id.service_estimate_text);

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(RequestedServicesDetailActivity.this);
                alertDialogBuilder.setTitle(R.string.cancel_service);
                alertDialogBuilder.setMessage(R.string.cancel_service_noadvance_alert1);
                alertDialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        cencelOrder();
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
            }
        });
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

                if (res.equalsIgnoreCase("cancel")) {
//                    Toast.makeText(this, "Service cancellation initiated.", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(this, CancelRequestedServiceActivity.class);
//                    startActivity(intent);
//                    finish();
                } else {

                    JSONObject getData = response.getJSONObject("service_list");
                    if (PreferenceStorage.getLang(this).equalsIgnoreCase("tam")) {
                        catName.setText(getData.getString("main_category_ta"));
                        subCatName.setText(getData.getString("service_ta_name"));

                    } else {
                        catName.setText(getData.getString("main_category"));
                        subCatName.setText(getData.getString("service_name"));

                    }
                    custName.setText(getData.getString("contact_person_name"));
                    custNumber.setText(getData.getString("contact_person_number"));
                    custAddress.setText(getData.getString("service_address"));
                    servicedate.setText(getData.getString("order_date"));
                    orderID.setText(getData.getString("service_order_id"));
//                serviceProvider.setText(getData.getString("provider_name"));
//                servicePerson.setText(getData.getString("person_name"));
//                servicePersonPhone.setText(getData.getString("person_number"));
                    serviceTimeSlot.setText(getData.getString("time_slot"));
                    estimatedCost.setText("Rs." + getData.getInt("estimated_cost"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {

    }
}