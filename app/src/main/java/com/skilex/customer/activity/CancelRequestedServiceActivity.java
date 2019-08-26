package com.skilex.customer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.skilex.customer.R;
import com.skilex.customer.bean.support.OngoingService;
import com.skilex.customer.bean.support.StoreTimeSlot;
import com.skilex.customer.helper.AlertDialogHelper;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import static android.util.Log.d;

public class CancelRequestedServiceActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = RequestedServicesDetailActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    EditText reason, comment;
    Button submit;
    OngoingService ongoingService;

    String res = "";


    ArrayAdapter<StoreTimeSlot> timeSlotAdapter = null;
    ArrayList<StoreTimeSlot> timeList;
    String timeSlotId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_service);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        initvals();
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initvals() {
        ongoingService = (OngoingService) getIntent().getSerializableExtra("serviceObj");

        reason = (EditText) findViewById(R.id.edt_user_reason);
        reason.setFocusable(false);
        reason.setOnClickListener(this);
        comment = (EditText) findViewById(R.id.edt_user_comment);
        submit = (Button) findViewById(R.id.submit_reason);
        submit.setOnClickListener(this);
        loadReason();
    }

    private void loadReason() {
        res = "reason";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = SkilExConstants.BUILD_URL + SkilExConstants.CANCEL_REASON;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void cencelOrder() {
        res = "cancel";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = ongoingService.getservice_order_id();
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserId(this));
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, id);
            jsonObject.put(SkilExConstants.CANCEL_ID, timeSlotId);
            jsonObject.put(SkilExConstants.CANCEL_COMMENTS, comment.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.CANCEL_SERVICE;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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
                if (res.equalsIgnoreCase("reason")) {
                    JSONArray getData = response.getJSONArray("reason_list");
                    int getLength = getData.length();
                    String timeId = "";
                    String timeName = "";
                    timeList = new ArrayList<>();

                    for (int i = 0; i < getLength; i++) {

                        timeId = getData.getJSONObject(i).getString("id");
                        timeName = getData.getJSONObject(i).getString("cancel_reason");
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


                } else if (res.equalsIgnoreCase("cancel")) {
                    Intent intent = new Intent (this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showTimeSlotList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.header);
        if (PreferenceStorage.getLang(this).equalsIgnoreCase("tamil")) {
            header.setText("காரணத்தைத் தேர்ந்தெடுக்கவும்");
        } else {
            header.setText("Select Reason");
        }
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(timeSlotAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StoreTimeSlot cty = timeList.get(which);
                        reason.setText(cty.getTimeName());
                        timeSlotId = cty.getTimeId();
                    }
                });
        builderSingle.show();
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onClick(View v) {
        if (v == reason) {
            showTimeSlotList();
        }
        if (v == submit) {
            cencelOrder();
        }
    }
}
