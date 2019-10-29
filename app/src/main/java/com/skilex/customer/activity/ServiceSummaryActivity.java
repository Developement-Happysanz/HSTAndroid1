package com.skilex.customer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.skilex.customer.R;
import com.skilex.customer.bean.support.ServiceHistory;
import com.skilex.customer.bean.support.StoreTimeSlot;
import com.skilex.customer.ccavenue.activity.InitialScreenActivity;
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

public class ServiceSummaryActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = ServiceSummaryActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ServiceHistory serviceHistory;
    String res = "", serviceOrder = "";

    Button additionalService;
    ImageView go;

    TextView catName, serviceName, custName, serviceDate, timeSlot, providerName, servicePersonName, couponAmt,
            serviceStartTime, serviceEndTime, materials, serviceCharge, additionalCharge, subTotal, advanceAmt, advanceAmtLayout, total, viewBill,
            chooseCoupon, applyCoupon, couponContent;
    LinearLayout startEndLayout, additionalServiceLayout;
    RelativeLayout paymentLayout, materialsLayout, applyCouponLayout, couponAppliedLayout;
    Button shareInvoice, payBill;

    ArrayAdapter<StoreTimeSlot> timeSlotAdapter = null;
    ArrayList<StoreTimeSlot> timeList;
    String timeSlotId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_summary);
        initVals();
        serviceHistory = (ServiceHistory) getIntent().getSerializableExtra("serviceObj");

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        callGetServiceSummary();
    }

    private void initVals() {
        catName = (TextView) findViewById(R.id.service_category);
        serviceName = (TextView) findViewById(R.id.service_name);
        custName = (TextView) findViewById(R.id.customer_name_txt);
        serviceDate = (TextView) findViewById(R.id.service_date_txt);
        timeSlot = (TextView) findViewById(R.id.service_time_slot_txt);
        providerName = (TextView) findViewById(R.id.service_provider_name_txt);
        servicePersonName = (TextView) findViewById(R.id.service_person_txt);
        serviceStartTime = (TextView) findViewById(R.id.service_start_time);
        serviceEndTime = (TextView) findViewById(R.id.service_end_time);
        materials = (TextView) findViewById(R.id.material_list);
        serviceCharge = (TextView) findViewById(R.id.service_charge_amount);
        additionalCharge = (TextView) findViewById(R.id.additional_service_charge_amount);
        subTotal = (TextView) findViewById(R.id.sub_total_amount);
        advanceAmt = (TextView) findViewById(R.id.advance_charge_amount);
        advanceAmtLayout = (TextView) findViewById(R.id.advance_charge);
        total = (TextView) findViewById(R.id.grand_total_amount);
        viewBill = (TextView) findViewById(R.id.view_bills);
        viewBill.setOnClickListener(this);
        couponAmt = (TextView) findViewById(R.id.coupon_applied_amount);
        chooseCoupon = (TextView) findViewById(R.id.coupon_select);
        chooseCoupon.setOnClickListener(this);
        applyCoupon = (TextView) findViewById(R.id.apply_coupon);
        applyCoupon.setOnClickListener(this);
        couponContent = (TextView) findViewById(R.id.coupon_content);
        shareInvoice = (Button) findViewById(R.id.invoice);
        shareInvoice.setOnClickListener(this);
        payBill = (Button) findViewById(R.id.pay);
        payBill.setOnClickListener(this);

        startEndLayout = (LinearLayout) findViewById(R.id.start_end_layout);
        additionalServiceLayout = (LinearLayout) findViewById(R.id.additional_layout);
        paymentLayout = (RelativeLayout) findViewById(R.id.payment_layout);
        materialsLayout = (RelativeLayout) findViewById(R.id.material_layout);
        couponAppliedLayout = (RelativeLayout) findViewById(R.id.coupon_applied_layoout);
        applyCouponLayout = (RelativeLayout) findViewById(R.id.choose_coupon_layout);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCoupon();
            }
        });

        additionalService = (Button) findViewById(R.id.add_service);
        additionalService.setOnClickListener(this);

        go = (ImageView) findViewById(R.id.go);
        go.setOnClickListener(this);
    }

    public void callGetServiceSummary() {
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            getServiceSummary();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    private void getServiceSummary() {
        res = "summary";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, serviceHistory.getservice_order_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.SERVICE_ORDER_SUMMARY;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    public void callGetServiceStatus() {
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            getServiceStatus();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    private void getServiceStatus() {
        res = "status";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, serviceHistory.getservice_order_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.SERVICE_ORDER_STATUS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void getCouponList() {
        res = "coupon";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.COUPON_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void cancelCoupon() {
        res = "remove_coupon";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, serviceHistory.getservice_order_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.REMOVE_COUPON;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void applyCoupon() {
        res = "apply_coupon";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);

        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, serviceHistory.getservice_order_id());
            jsonObject.put(SkilExConstants.COUPON_ID, timeSlotId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.APPLY_COUPON;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void proceedPay() {
        res = "proceed_pay";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);
        PreferenceStorage.saveRateOrderId(this, serviceHistory.getservice_order_id());
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, serviceHistory.getservice_order_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.PROCEED_TO_PAY;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

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
                if (res.equalsIgnoreCase("summary")) {
                    JSONObject getData = response.getJSONObject("service_list");

                    if (PreferenceStorage.getLang(this).equalsIgnoreCase("tam")) {
                        catName.setText(getData.getString("main_category_ta"));
                        serviceName.setText(getData.getString("service_ta_name"));
                    } else {
                        catName.setText(getData.getString("main_category"));
                        serviceName.setText(getData.getString("service_name"));
                    }
                    serviceOrder = getData.getString("service_order_id");
                    custName.setText(getData.getString("contact_person_name"));
                    serviceDate.setText(getData.getString("order_date"));
                    timeSlot.setText(getData.getString("time_slot"));
                    providerName.setText(getData.getString("provider_name"));
                    servicePersonName.setText(getData.getString("person_name"));
                    serviceStartTime.setText(getData.getString("service_start_time"));
                    serviceEndTime.setText(getData.getString("service_end_time"));
                    materials.setText(getData.getString("material_notes"));
                    serviceCharge.setText(getData.getString("service_amount"));
                    additionalCharge.setText(getData.getString("additional_service_amt"));
                    additionalService.setText("Additional services - " + getData.getString("additional_service"));
                    if (getData.getString("additional_service").equalsIgnoreCase("0") ||
                            getData.getString("additional_service_amt").isEmpty()) {
                        additionalServiceLayout.setVisibility(View.GONE);
                    }
                    subTotal.setText(getData.getString("total_service_cost"));
                    advanceAmt.setText(getData.getString("paid_advance_amt"));
                    couponAmt.setText(getData.getString("discount_amt"));
//                    if (couponAmt.getText().toString().equalsIgnoreCase("0") ||
//                            couponAmt.getText().toString().isEmpty()) {
//                        total.setText(getData.getString("net_service_amount"));
//                    } else {
//                        total.setText(getData.getString("payable_amount"));
//                    }


                    callGetServiceStatus();
                }
                if (res.equalsIgnoreCase("status")) {
                    String status = response.getString("order_status");
                    if (status.equalsIgnoreCase("Cancelled")) {
                        paymentLayout.setVisibility(View.GONE);
                        additionalServiceLayout.setVisibility(View.GONE);
                        materialsLayout.setVisibility(View.GONE);
                    } else if (status.equalsIgnoreCase("Completed")) {

                        if (PreferenceStorage.getCoupon(this).isEmpty() || PreferenceStorage.getCoupon(this).equalsIgnoreCase("")) {
                            couponAppliedLayout.setVisibility(View.GONE);
                            applyCouponLayout.setVisibility(View.VISIBLE);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.setMargins(40, 20, 0, 0);
                            params.addRule(RelativeLayout.BELOW, R.id.choose_coupon_layout);
//                        params.addRule(RelativeLayout.BELOW, applyCouponLayout);
                            advanceAmtLayout.setLayoutParams(params);
                        } else {
                            couponAppliedLayout.setVisibility(View.VISIBLE);
                            applyCouponLayout.setVisibility(View.GONE);
                            couponContent.setText(PreferenceStorage.getCoupon(this));
                        }
                        payBill.setVisibility(View.VISIBLE);
                        proceedPay();
                    } else if (status.equalsIgnoreCase("Paid")) {
//                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        params.addRule(RelativeLayout.BELOW, R.id.coupon_applied_layoout);
//                        advanceAmt.setLayoutParams(params);
                        couponAppliedLayout.setVisibility(View.VISIBLE);
                        if (!PreferenceStorage.getCoupon(this).equalsIgnoreCase("") &&
                                !PreferenceStorage.getCoupon(this).isEmpty()) {
                            couponContent.setText(PreferenceStorage.getCoupon(this));
                        } else {
                            couponContent.setVisibility(View.GONE);
                        }
                        applyCouponLayout.setVisibility(View.GONE);
//                        shareInvoice.setVisibility(View.VISIBLE);
                        shareInvoice.setVisibility(View.GONE);

                        proceedPay();
                    }
                }
                if (res.equalsIgnoreCase("coupon")) {
                    JSONArray getData = response.getJSONArray("offer_details");
                    int getLength = getData.length();
                    String timeId = "";
                    String timeName = "";
                    timeList = new ArrayList<>();

                    for (int i = 0; i < getLength; i++) {

                        timeId = getData.getJSONObject(i).getString("id");
                        timeName = getData.getJSONObject(i).getString("offer_code");
                        timeList.add(new StoreTimeSlot(timeId, timeName));
                    }

                    timeSlotAdapter = new ArrayAdapter<StoreTimeSlot>(getApplicationContext(), R.layout.time_slot_layout, R.id.time_slot_range, timeList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            Log.d(TAG, "getview called" + position);
                            View view = getLayoutInflater().inflate(R.layout.time_slot_layout, parent, false);
                            TextView gendername = (TextView) view.findViewById(R.id.time_slot_range);
                            gendername.setText(timeList.get(position).getTimeName());

                            // ... Fill in other views ...
                            return view;
                        }
                    };
                }
                if (res.equalsIgnoreCase("apply_coupon")) {
                    couponContent.setText(response.getString("msg") + "%");
                    PreferenceStorage.saveCoupon(this, couponContent.getText().toString());
                    Intent i = new Intent(this, ServiceSummaryActivity.class);
                    i.putExtra("serviceObj", serviceHistory);
                    startActivity(i);
                    finish();
                }if (res.equalsIgnoreCase("remove_coupon")) {

                    PreferenceStorage.saveCoupon(getApplicationContext(), "");
                    finish();
                }
                if (res.equalsIgnoreCase("proceed_pay")) {
                    if (response.getString("msg").equalsIgnoreCase("Service status") &&
                            response.getString("order_status").equalsIgnoreCase("Cancelled")) {

                    } else {
                        JSONObject getData = response.getJSONObject("payment_details");
                        total.setText(String.valueOf(Float.valueOf(getData.getString("payable_amount"))));
                        PreferenceStorage.saveOrderId(this, getData.getString("order_id"));
                        getCouponList();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {

    }

    private void showTimeSlotList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.header);
        header.setText(R.string.coupon_applied);
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(timeSlotAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StoreTimeSlot cty = timeList.get(which);
                        chooseCoupon.setText(cty.getTimeName());
                        timeSlotId = cty.getTimeId();
                    }
                });
        builderSingle.show();
    }

    @Override
    public void onClick(View v) {
        if (v == viewBill) {
            Intent i = new Intent(this, ViewBillActivity.class);
            i.putExtra("serv", serviceOrder);
            startActivity(i);
        }
        if (v == payBill) {
            Intent i = new Intent(this, InitialScreenActivity.class);
            PreferenceStorage.saveCoupon(this, "");
            i.putExtra("advpay", total.getText().toString());
            i.putExtra("page", "service_pay");
            startActivity(i);
            finish();
        }
        if (v == shareInvoice) {

        }
        if (v == chooseCoupon) {
            showTimeSlotList();
        }
        if (v == applyCoupon) {
            if (timeSlotId.isEmpty()) {
                AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.select_coupon));
            } else {
                applyCoupon();
            }
        }
        if (v == additionalService) {
            Intent i = new Intent(this, AdditionalServiceListActivity.class);
            i.putExtra("serv", serviceOrder);
            startActivity(i);
        }
        if (v == go) {
            Intent i = new Intent(this, AdditionalServiceListActivity.class);
            i.putExtra("serv", serviceOrder);
            startActivity(i);
        }
    }
}
