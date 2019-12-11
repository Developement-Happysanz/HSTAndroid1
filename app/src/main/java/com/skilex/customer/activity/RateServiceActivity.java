package com.skilex.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.skilex.customer.R;
import com.skilex.customer.helper.AlertDialogHelper;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class RateServiceActivity  extends AppCompatActivity implements DialogClickListener, IServiceListener, View.OnClickListener {

    private static final String TAG = RateServiceActivity.class.getName();
    private ProgressDialogHelper progressDialogHelper;
    private ServiceHelper serviceHelper;
//    private Event event;
    private RatingBar rtbComments;
    private EditText edtComments;
    private Button btnSubmit;
    private String checkString;
    private String reviewId = "";
    private ImageView ivBack;
    TextView skip;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
//        event = (Event) getIntent().getSerializableExtra("eventObj");
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        rtbComments = findViewById(R.id.ratingBar);
        edtComments = findViewById(R.id.edtComments);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        skip = (TextView) findViewById(R.id.skip);
        skip.setOnClickListener(this);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnSubmit) {
            submitNewRecord();
//            if (checkString.equalsIgnoreCase("new")) {
//
//            }
        }
        if (v == skip) {
            Intent intent = new Intent (this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void submitNewRecord() {

        float getrate = rtbComments.getRating();
        int getrate1 = rtbComments.getNumStars();
        checkString = "new";

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, PreferenceStorage.getRateOrderId(this));
            jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserId(getApplicationContext()));
            jsonObject.put(SkilExConstants.KEY_RATINGS, "" + rtbComments.getRating());
            jsonObject.put(SkilExConstants.KEY_COMMENTS, edtComments.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.REVIEW;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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
                String status = response.getString("status");
                if (status.equalsIgnoreCase("Success")){
                    Intent intent = new Intent (this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
//                if (status.equalsIgnoreCase("new")) {
//                    checkString = "new";
//                } else if (status.equalsIgnoreCase("success")) {
//                    Intent intent = new Intent(getApplicationContext(), EventReviewActivity.class);
//                    intent.putExtra("eventObj", event);
//                    startActivity(intent);
//                    finish();
//                } else if (status.equalsIgnoreCase("exist")) {
//                    checkString = "update";
//                    JSONArray getEventReviews = response.getJSONArray("Reviewdetails");
//                    updateEventReviews(getEventReviews);
//                }
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
