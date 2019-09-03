package com.skilex.customer.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.skilex.customer.R;
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

import static android.util.Log.d;

public class AssignProviderActivity extends AppCompatActivity implements IServiceListener, DialogClickListener {

    private static final String TAG = AssignProviderActivity.class.getName();

    private ProgressBar progressBar;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    boolean doubleBackToExitPressedOnce = false;

    private Handler handler = new Handler();


    private int a = 1;

    //To start timer
    public void startTimer() {
        handler.postDelayed(new Runnable() {
            public void run() {
                checkProviderAssign();
                handler.postDelayed(this, 60000);
            }
        }, 60000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_assign_activity);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

//        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        progressBar = (ProgressBar) findViewById(R.id.progress);

        // timer for seekbar
        final int oneMin = 3 * 60 * 1000; // 1 minute in milli seconds

        /** CountDownTimer starts with 1 minutes and every onTick is 1 second */
        new CountDownTimer(oneMin, 1000) {
            public void onTick(long millisUntilFinished) {

                //forward progress
                long finishedSeconds = oneMin - millisUntilFinished;
                int total = (int) (((float)finishedSeconds / (float)oneMin) * 100.0);
                progressBar.setProgress(total);

//                //backward progress
//                int total = (int) (((float) millisUntilFinished / (float) oneMin) * 100.0);
//                progressBar.setProgress(total);

            }

            public void onFinish() {
                // DO something when 1 minute is up
                handler.removeCallbacksAndMessages(null);
                finish();

            }
        }.start();
        checkProviderAssign();
        startTimer();
    }

    private void checkProviderAssign() {
        JSONObject jsonObject = new JSONObject();

        String id = "";
        id = PreferenceStorage.getUserId(this);

        String orderId = "";
        orderId = PreferenceStorage.getOrderId(this);

        String aa = String.valueOf(a);
        a++;

        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.ORDER_ID, orderId);
            jsonObject.put(SkilExConstants.TIME_INTERVAL, aa);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.SERVICE_ALLOCATION;
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
    public void onBackPressed() {
        //Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);;
        } else {
            super.onBackPressed();
            return;
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateResponse(response)) {
            try {
                response.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {

    }
}
