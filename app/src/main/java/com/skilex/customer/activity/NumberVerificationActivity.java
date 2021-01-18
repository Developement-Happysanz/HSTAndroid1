package com.skilex.customer.activity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.skilex.customer.R;
import com.skilex.customer.bean.database.SQLiteHelper;
import com.skilex.customer.customview.CustomOtpEditText;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.util.Log.d;

/**
 * Created by Narendar on 16/10/17.
 */

public class NumberVerificationActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = NumberVerificationActivity.class.getName();

    private CustomOtpEditText otpEditText;
    private TextView tvResendOTP, tvCountDown;
    private ImageView btnConfirm;
    private Button btnChangeNumber;
    private String mobileNo;
    private String checkVerify;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private static final int SMS_CONSENT_REQUEST = 2;  // Set to an unused request code
    SQLiteHelper database;
    private SmsBrReceiver smsReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_verification);
        database = new SQLiteHelper(getApplicationContext());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mobileNo = PreferenceStorage.getMobileNo(getApplicationContext());
        otpEditText = (CustomOtpEditText) findViewById(R.id.otp_view);
        tvResendOTP = (TextView) findViewById(R.id.resend);
        tvResendOTP.setOnClickListener(this);
        tvCountDown = findViewById(R.id.contentresend);
        btnConfirm = (ImageView) findViewById(R.id.sendcode);
        btnConfirm.setOnClickListener(this);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        // Start listening for SMS User Consent broadcasts from senderPhoneNumber
        // The Task<Void> will be successful if SmsRetriever was able to start
        // SMS User Consent, and will error if there was an error starting.
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();
//        Task<Void> task = SmsRetriever.getClient(this).startSmsUserConsent(senderPhoneNumber /* or null */);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent
                // ...
                Toast.makeText(NumberVerificationActivity.this, "Listening for otp...", Toast.LENGTH_SHORT).show();
                IntentFilter filter = new IntentFilter();
                filter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
                if (smsReceiver == null) {
                    smsReceiver = new NumberVerificationActivity.SmsBrReceiver();
                }
                getApplicationContext().registerReceiver(smsReceiver, filter);
//                            startActivity(homeIntent);
//                            finish();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                // ...
                Toast.makeText(NumberVerificationActivity.this, "Failed listening for otp...", Toast.LENGTH_SHORT).show();
            }
        });

        countDownTimers();

    }

    void countDownTimers() {
        new CountDownTimer(30 * 1000 + 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvResendOTP.setVisibility(View.GONE);
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tvCountDown.setText("Resend in " + String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds) + " seconds");
            }

            public void onFinish() {
                tvCountDown.setText("Try again...");
                tvCountDown.setVisibility(View.GONE);
                tvResendOTP.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(new View(this).getWindowToken(), 0);
        return true;
    }

    @Override
    public void onClick(View v) {

        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {

            if (v == tvResendOTP) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.resend_otp);
                alertDialogBuilder.setMessage(getString(R.string.mobile_number) + getString(R.string.mobile_number_tag) + PreferenceStorage.getMobileNo(getApplicationContext()));
                alertDialogBuilder.setPositiveButton(R.string.alert_button_ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                checkVerify = "Resend";
                                countDownTimers();
                                tvCountDown.setVisibility(View.VISIBLE);
                                JSONObject jsonObject = new JSONObject();
                                try {

                                    jsonObject.put(SkilExConstants.PHONE_NUMBER, PreferenceStorage.getMobileNo(getApplicationContext()));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                                String url = SkilExConstants.BUILD_URL + SkilExConstants.MOBILE_VERIFICATION;
                                serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

                            }
                        });
                alertDialogBuilder.setNegativeButton(R.string.alert_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

//                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialogBuilder.show();

            } else if (v == btnConfirm) {
                if (otpEditText.hasValidOTP()) {
                    checkVerify = "Confirm";
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserId(getApplicationContext()));
                        jsonObject.put(SkilExConstants.PHONE_NUMBER, PreferenceStorage.getMobileNo(getApplicationContext()));
                        jsonObject.put(SkilExConstants.OTP, otpEditText.getOTP());
                        jsonObject.put(SkilExConstants.DEVICE_TOKEN, PreferenceStorage.getGCM(getApplicationContext()));
                        jsonObject.put(SkilExConstants.MOBILE_TYPE, "1");
                        jsonObject.put(SkilExConstants.UNIQUE_NUMBER, PreferenceStorage.getIMEI(getApplicationContext()));
                        jsonObject.put(SkilExConstants.REFERRAL_CODE, PreferenceStorage.getReferralNo(getApplicationContext()));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    String url = SkilExConstants.BUILD_URL + SkilExConstants.USER_LOGIN;
                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
                } else {
                    if (PreferenceStorage.getLang(this).equalsIgnoreCase("tamil")) {
                        AlertDialogHelper.showSimpleAlertDialog(this, "தவறான கடவுச்சொல்!");
                    } else {
                        AlertDialogHelper.showSimpleAlertDialog(this, "Invalid OTP");
                    }
                }

            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
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
//                String msg = response.getString(SkilExConstants.PARAM_MESSAGE);
                String msg_en = response.getString(SkilExConstants.PARAM_MESSAGE_ENG);
                String msg_ta = response.getString(SkilExConstants.PARAM_MESSAGE_TAMIL);
                d(TAG, "status val" + status + "msg" + msg_en);

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
                if (checkVerify.equalsIgnoreCase("Resend")) {

                    Toast.makeText(getApplicationContext(), "OTP resent successfully", Toast.LENGTH_SHORT).show();

                } else if (checkVerify.equalsIgnoreCase("Confirm") || checkVerify.equalsIgnoreCase("verified")) {
                    PreferenceStorage.setFirstTimeLaunch(getApplicationContext(), false);
                    database.app_info_check_insert("Y");
//                    Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_SHORT).show();
                    JSONObject data = response.getJSONObject("userData");

                    String userId = data.getString("user_master_id");
                    String fullName = data.getString("full_name");
                    String gender = data.getString("gender");
                    String mobileVerify = data.getString("mobile_verify");
                    String phoneNo = data.getString("phone_no");
                    String profilePic = data.getString("profile_pic");
                    String email = data.getString("email");
                    String emailVerifyStatus = data.getString("email_verify");
                    String userType = data.getString("user_type");

                    PreferenceStorage.saveUserId(getApplicationContext(), userId);
                    PreferenceStorage.saveName(getApplicationContext(), fullName);
                    PreferenceStorage.saveGender(getApplicationContext(), gender);
//                    PreferenceStorage.saveAddress(getApplicationContext(), address);
                    PreferenceStorage.saveProfilePic(getApplicationContext(), profilePic);
                    PreferenceStorage.saveEmail(getApplicationContext(), email);
                    PreferenceStorage.saveEmailVerify(getApplicationContext(), emailVerifyStatus);
                    PreferenceStorage.saveUserType(getApplicationContext(), userType);

//                    PreferenceStorage.saveUserId(getApplicationContext(), userId);
//                    PreferenceStorage.saveCheckFirstTimeProfile(getApplicationContext(), "new");
                    setSda();


                } else if (checkVerify.equalsIgnoreCase("set_lang")) {
                    Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                    homeIntent.putExtra("profile_state", "new");
                    startActivity(homeIntent);
//                    this.finish();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    class SmsBrReceiver extends BroadcastReceiver {

        private String parseCode(String message) {
            Pattern p = Pattern.compile("\\b\\d{4}\\b");
            Matcher m = p.matcher(message);
            String code = "";
            while (m.find()) {
                code = m.group(0);
            }
            return code;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch (status.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        // Get SMS message contents
                        String smsMessage = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                        // Extract one-time code from the message and complete verification
                        // by sending the code back to your server.
                        Log.d(TAG, "Retrieved sms code: " + smsMessage);
                        if (smsMessage != null) {
                            String sms = parseCode(smsMessage);
                            verifyMessage(sms);
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        // Waiting for SMS timed out (5 minutes)
                        // Handle the error ...
                        break;
                }
            }
        }

    }

    public void verifyMessage(String otp) {
        checkVerify = "verified";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserId(getApplicationContext()));
            jsonObject.put(SkilExConstants.PHONE_NUMBER, PreferenceStorage.getMobileNo(getApplicationContext()));
            jsonObject.put(SkilExConstants.OTP, otp);
            jsonObject.put(SkilExConstants.DEVICE_TOKEN, PreferenceStorage.getGCM(getApplicationContext()));
            jsonObject.put(SkilExConstants.MOBILE_TYPE, "1");
            jsonObject.put(SkilExConstants.UNIQUE_NUMBER, PreferenceStorage.getIMEI(getApplicationContext()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.USER_LOGIN;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void setSda() {
        checkVerify = "set_lang";
        String langID = "";
        if (PreferenceStorage.getLang(getApplicationContext()).equalsIgnoreCase("eng")) {
            langID = "2";
        } else {
            langID = "1";
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserId(getApplicationContext()));
            jsonObject.put(SkilExConstants.LANG_ID, langID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.SET_USER_LANG;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

}
