package com.skilex.customer.activity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.skilex.customer.utils.MySMSBroadcastReceiver;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;
import com.skilex.customer.utils.SkilExValidator;

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
    private TextView tvResendOTP;
    private ImageView btnConfirm;
    private Button btnChangeNumber;
    private String mobileNo;
    private String checkVerify;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private static final int SMS_CONSENT_REQUEST = 2;  // Set to an unused request code
    SQLiteHelper database;
    private final BroadcastReceiver smsVerificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch (smsRetrieverStatus.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        // Get consent intent
                        Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_SMS_MESSAGE);
                        try {
                            // Start activity to show consent dialog to user, activity must be started in
                            // 5 minutes, otherwise you'll receive another TIMEOUT intent
                            startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                        } catch (ActivityNotFoundException e) {
                            // Handle the exception ...
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        // Time out occurred, handle the error.
                        break;
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_verification);
        database = new SQLiteHelper(getApplicationContext());

        mobileNo = PreferenceStorage.getMobileNo(getApplicationContext());
        otpEditText = (CustomOtpEditText) findViewById(R.id.otp_view);
        tvResendOTP = (TextView) findViewById(R.id.resend);
        tvResendOTP.setOnClickListener(this);
        btnConfirm = (ImageView) findViewById(R.id.sendcode);
        btnConfirm.setOnClickListener(this);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
    }

    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    public void onClick(View v) {

        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {

            if (v == tvResendOTP) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.resend_otp);
                alertDialogBuilder.setMessage(R.string.mobile_number + PreferenceStorage.getMobileNo(getApplicationContext()));
                alertDialogBuilder.setPositiveButton(R.string.alert_button_ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                checkVerify = "Resend";
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
                if (checkVerify.equalsIgnoreCase("Resend")) {

                    Toast.makeText(getApplicationContext(), "OTP resent successfully", Toast.LENGTH_SHORT).show();

                } else if (checkVerify.equalsIgnoreCase("Confirm")) {
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
                    Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
//                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
}
