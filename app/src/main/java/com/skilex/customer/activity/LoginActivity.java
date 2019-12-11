package com.skilex.customer.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.skilex.customer.R;
import com.skilex.customer.helper.AlertDialogHelper;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.CommonUtils;
import com.skilex.customer.utils.FirstTimePreference;
import com.skilex.customer.utils.LocaleHelper;
import com.skilex.customer.utils.PermissionUtil;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;
import com.skilex.customer.utils.SkilExValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.util.Log.d;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = LoginActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private EditText edtNumber;
    private Button signIn;
    private TextView skip;
    private ImageView laang;
    String IMEINo = "", resString = "";
    private static final int PERMISSION_REQUEST_CODE = 1;

    private static String[] PERMISSIONS_ALL = {Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private static final int REQUEST_PERMISSION_All = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        edtNumber = (EditText) findViewById(R.id.edtMobileNumber);
        signIn = findViewById(R.id.sendcode);
        signIn.setOnClickListener(this);
        skip = findViewById(R.id.skip);
        skip.setOnClickListener(this);
        laang = findViewById(R.id.langues);
        laang.setOnClickListener(this);
        FirstTimePreference prefFirstTime = new FirstTimePreference(getApplicationContext());

        if (prefFirstTime.runTheFirstTime("FirstTimePermit")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                requestAllPermissions();
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            TelephonyManager tm = (TelephonyManager)
                    this.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.READ_PHONE_STATE};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                IMEINo = tm.getImei(1);
                IMEINo = String.valueOf(generateRandom(12));
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_DENIED) {
                    IMEINo = "";
                } else {
                    IMEINo = String.valueOf(generateRandom(12));
                }
            }
            PreferenceStorage.saveIMEI(this, IMEINo);
        }

        if (PreferenceStorage.getLang(this).isEmpty()) {
//            showLangAlert();
        }

    }

    public static long generateRandom(int length) {
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return Long.parseLong(new String(digits));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(new View(this).getWindowToken(), 0);
        return true;
    }

    private void requestAllPermissions() {

        boolean requestPermission = PermissionUtil.requestAllPermissions(this);

        if (requestPermission) {

            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.

            ActivityCompat
                    .requestPermissions(this, PERMISSIONS_ALL,
                            REQUEST_PERMISSION_All);
        } else {

            ActivityCompat.requestPermissions(this, PERMISSIONS_ALL, REQUEST_PERMISSION_All);
        }
    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.haveNetworkConnection(getApplicationContext())) {
            if (v == signIn) {
                if (validateFields()) {
                    resString = "mob_verify";
                    String number = edtNumber.getText().toString();
                    PreferenceStorage.saveMobileNo(this, number);
                    String GCMKey = PreferenceStorage.getGCM(getApplicationContext());

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(SkilExConstants.PHONE_NUMBER, number);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    String url = SkilExConstants.BUILD_URL + SkilExConstants.MOBILE_VERIFICATION;
                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
                }
            }
            if (v == skip) {
                callGetSubCategoryService();
            }
            if (v == laang) {
                showLangAlert();
            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private boolean validateFields() {
        if (!SkilExValidator.checkMobileNumLength(this.edtNumber.getText().toString().trim())) {
            edtNumber.setError(getString(R.string.error_number));
            requestFocus(edtNumber);
            return false;
        }
        if (!SkilExValidator.checkNullString(this.edtNumber.getText().toString().trim())) {
            edtNumber.setError(getString(R.string.empty_entry));
            requestFocus(edtNumber);
            return false;
        } else {
            return true;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private boolean validateSignInResponse(JSONObject response) {
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

        if (validateSignInResponse(response)) {
            try {
                if (resString.equalsIgnoreCase("guest_user")) {
                    Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(homeIntent);
                    finish();
                } else if (resString.equalsIgnoreCase("Confirm")) {
                    PreferenceStorage.setFirstTimeLaunch(getApplicationContext(), false);
//                    database.app_info_check_insert("Y");
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

                } else {
                        String userId = response.getString("user_master_id");
                    PreferenceStorage.saveUserId(this, userId);
                    Intent homeIntent = new Intent(getApplicationContext(), NumberVerificationActivity.class);
                    startActivity(homeIntent);
//                    finish();
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

    private void showLangAlert() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.language);
        alertDialogBuilder.setMessage(R.string.choose_language);
        alertDialogBuilder.setPositiveButton("English", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                PreferenceStorage.saveLang(getApplicationContext(), "eng");
                Toast.makeText(getApplicationContext(), "App language is set to English", Toast.LENGTH_SHORT).show();
                LocaleHelper.setLocale(LoginActivity.this, "en");
//                LocaleHelper.setLocale(LoginActivity.this, "");

                //It is required to recreate the activity to reflect the change in UI.
//                recreate();
//                LoginActivity.this.recreate();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton("தமிழ்", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceStorage.saveLang(getApplicationContext(), "tamil");
                Toast.makeText(getApplicationContext(), "மொழி தமிழுக்கு அமைக்கப்பட்டுள்ளது", Toast.LENGTH_SHORT).show();
                LocaleHelper.setLocale(LoginActivity.this, "ta");

                //It is required to recreate the activity to reflect the change in UI.
//                recreate();
//                LoginActivity.this.recreate();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        alertDialogBuilder.show();
    }

    public void callGetSubCategoryService() {
//        if (classTestArrayList != null)
//            classTestArrayList.clear();

        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            guestLogin();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private void guestLogin() {
        resString = "guest_user";
        String GCMKey = PreferenceStorage.getGCM(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.UNIQUE_NUMBER, IMEINo);
            jsonObject.put(SkilExConstants.MOBILE_TYPE, "1");
            jsonObject.put(SkilExConstants.MOBILE_KEY, GCMKey);
            jsonObject.put(SkilExConstants.USER_STATUS, "Guest");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.GUEST_LOGIN;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

}


