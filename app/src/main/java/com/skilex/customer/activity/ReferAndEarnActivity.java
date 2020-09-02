package com.skilex.customer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.skilex.customer.R;
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

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class ReferAndEarnActivity extends AppCompatActivity implements IServiceListener, View.OnClickListener, DialogClickListener {
    private static final String TAG = ReferAndEarnActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ImageView serviceImage;
    private TextView pointsEarned, referralCode, referFriend, pointsText;
    private ScrollView scrollView;
    Service service;
    Button claimReward, done;
    String res = "";
    private LinearLayout addMOneeey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_and_earn);

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

        pointsEarned = (TextView) findViewById(R.id.points);
        referralCode = (TextView) findViewById(R.id.referral_code);

        claimReward = (Button) findViewById(R.id.claim);
        done = (Button) findViewById(R.id.done);
        referFriend = (TextView) findViewById(R.id.btn_refer);
        claimReward.setOnClickListener(this);
        done.setOnClickListener(this);
        referFriend.setOnClickListener(this);
        addMOneeey = (LinearLayout) findViewById(R.id.alerere);
        addMOneeey.setVisibility(View.GONE);
        pointsText = (TextView) findViewById(R.id.teett);
        callGetSubCategoryService();
    }

    public void callGetSubCategoryService() {
//        if (classTestArrayList != null)
//            classTestArrayList.clear();

        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            getServiceDetail();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private void getServiceDetail() {
        res = "detail";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.GET_REFERRAL_DETAIL;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void bookService() {
//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        res = "cart";
        JSONObject jsonObject = new JSONObject();

        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.GET_CLAIM;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void claimPoint() {
//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        res = "claim";
        JSONObject jsonObject = new JSONObject();

        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.CLAIM_POINTS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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
                            AlertDialogHelper.showSimpleAlertDialog(this, msg);
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
                if (res.equalsIgnoreCase("detail")) {
                    JSONObject data = response.getJSONObject("points_code");
                    referralCode.setText(data.getString("referral_code"));
                    pointsEarned.setText(data.getString("points_to_claim"));

                } else if (res.equalsIgnoreCase("cart")) {
                    pointsText.setText(getString(R.string.points_earned_success) + response.getString("amount_to_be_claim"));
                    addMOneeey.setVisibility(View.VISIBLE);
                } else if (res.equalsIgnoreCase("claim")) {
                    Toast.makeText(this, getString(R.string.points_claim_success), Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
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
        if (v == claimReward) {
            if (PreferenceStorage.getUserId(this).equalsIgnoreCase("")) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.login);
                alertDialogBuilder.setMessage(R.string.login_to_continue);
                alertDialogBuilder.setPositiveButton(R.string.alert_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        doLogout();
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.alert_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
            } else {
                bookService();
            }

        } if (v == referFriend) {
            Intent i = new Intent(android.content.Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Refer and Earn");
            i.putExtra(android.content.Intent.EXTRA_TEXT, "SkilEx by Skilex Multiservices Private Limited\n\nDownload SkilEx – The ultimate service app for all your home and office service needs. Enter my code "+ referralCode.getText() +" to earn 50 points worth ₹25 in your SkilEx wallet redeemed during your service payment. Download our app now:\nPlay store: https://bit.ly/3c5Vr0h \n" +
                    "App store: https://apple.co/2Ya8AjS");
            startActivity(Intent.createChooser(i, "Share via"));
        } if (v == done) {
            claimPoint();
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