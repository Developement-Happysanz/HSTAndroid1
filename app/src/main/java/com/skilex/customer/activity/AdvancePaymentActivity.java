package com.skilex.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.skilex.customer.R;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class AdvancePaymentActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {

    private Handler handler = new Handler();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private int a = 1;

    Button goHome;


//    public void startTimer() {
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                checkProviderAssign();
//                handler.postDelayed(this, 60000);
//            }
//        }, 60000);
//    }

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

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.SERVICE_ALLOCATION;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_payment);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        goHome = findViewById(R.id.home_booking);
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(newIntent);
                finish();
            }
        });


        // timer for seekbar
//        final int oneMin = 3 * 60 * 1000; // 1 minute in milli seconds
//
//        /** CountDownTimer starts with 1 minutes and every onTick is 1 second */
//        new CountDownTimer(oneMin, 1000) {
//            public void onTick(long millisUntilFinished) {
//
//                //forward progress
//                long finishedSeconds = oneMin - millisUntilFinished;
//                int total = (int) (((float)finishedSeconds / (float)oneMin) * 100.0);
//
////                //backward progress
////                int total = (int) (((float) millisUntilFinished / (float) oneMin) * 100.0);
////                progressBar.setProgress(total);
//
//            }
//
//            public void onFinish() {
//                // DO something when 1 minute is up
//                handler.removeCallbacksAndMessages(null);
//
//            }
//        }.start();
        checkProviderAssign();
//        startTimer();
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

    @Override
    public void onResponse(JSONObject response) {

    }

    @Override
    public void onError(String error) {

    }
}
