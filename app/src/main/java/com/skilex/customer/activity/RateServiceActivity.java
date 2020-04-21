package com.skilex.customer.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.skilex.customer.R;
import com.skilex.customer.adapter.FeebackListAdapter;
import com.skilex.customer.bean.support.Feedback;
import com.skilex.customer.bean.support.FeedbackList;
import com.skilex.customer.helper.AlertDialogHelper;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class RateServiceActivity extends AppCompatActivity implements DialogClickListener, IServiceListener, View.OnClickListener {

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
    private ArrayList<Feedback> feedbackArrayList = new ArrayList<>();
    private FeebackListAdapter feebackListAdapter;
//    private ListView feedList;
    private LinearLayout layout_all;
    FeedbackList serviceHistoryList;
    private String feebackAns = "";
    private int pooos;

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


//        feedList = findViewById(R.id.listfedd);
        layout_all = findViewById(R.id.listfedd);


        skip = (TextView) findViewById(R.id.skip);
        skip.setOnClickListener(this);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getQues();
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
            Intent intent = new Intent(this, MainActivity.class);
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


    private void getQues() {

        checkString = "QUES";

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserId(getApplicationContext()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.FEEDBACK_QUESTION;
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
//                String msg_en = response.getString(SkilExConstants.PARAM_MESSAGE_ENG);
//                String msg_ta = response.getString(SkilExConstants.PARAM_MESSAGE_TAMIL);
                d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d(TAG, "Show error dialog");

                        if (PreferenceStorage.getLang(this).equalsIgnoreCase("tamil")) {
                            AlertDialogHelper.showSimpleAlertDialog(this, msg);
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
        if (validateSignInResponse(response)) {
            try {
                if (checkString.equalsIgnoreCase("QUES")) {
                    Gson gson = new Gson();
                    serviceHistoryList = gson.fromJson(response.toString(), FeedbackList.class);
                    if (serviceHistoryList.getFeedbackArrayList() != null && serviceHistoryList.getFeedbackArrayList().size() > 0) {
                        int totalCount = serviceHistoryList.getCount();
//                    this.serviceHistoryArrayList.addAll(ongoingServiceList.getserviceArrayList());
                        boolean isLoadingForFirstTime = false;
//                        updateListAdapter(serviceHistoryList.getFeedbackArrayList());
                        loadMembersList(serviceHistoryList.getFeedbackArrayList().size());
                    } else {
                        if (feedbackArrayList != null) {
                            feedbackArrayList.clear();
//                            updateListAdapter(serviceHistoryList.getFeedbackArrayList());
                            loadMembersList(serviceHistoryList.getFeedbackArrayList().size());
                        }
                    }
                } else if (checkString.equalsIgnoreCase("ANS")){

                } else {
                    String status = response.getString("status");
                    if (status.equalsIgnoreCase("Success")) {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
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

//    protected void updateListAdapter(ArrayList<Feedback> serviceHistoryArrayLists) {
//        feedbackArrayList.clear();
//        feedbackArrayList.addAll(serviceHistoryArrayLists);
//        if (feebackListAdapter == null) {
//            feebackListAdapter = new FeebackListAdapter(this, feedbackArrayList);
//            feedList.setAdapter(feebackListAdapter);
//        } else {
//            feebackListAdapter.notifyDataSetChanged();
//        }
//    }

    @Override
    public void onError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    private void loadMembersList(int memberCount) {

        try {

            for (int c1 = 0; c1 < memberCount; c1++) {

                LinearLayout cell = new LinearLayout(this);
                cell.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                cell.setPadding(0, 10, 0, 10);
                cell.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,0,0,0);

                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.setMargins(10,10,10,10);


                TextView line1 = new TextView(this);
                line1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                line1.setText(serviceHistoryList.getFeedbackArrayList().get(c1).getfeedback_question());


                line1.setId(R.id.feedback_questionn);
                line1.setHint("Member Name");
                line1.requestFocusFromTouch();
                line1.setTextSize(16.0f);
                line1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                line1.setTextColor(Color.parseColor("#000000"));
                line1.setGravity(Gravity.CENTER_VERTICAL);
                line1.setPadding(10, 0, 10, 0);
                line1.setLayoutParams(params);

                RadioGroup line2 = new RadioGroup(this);
                line2.setOrientation(LinearLayout.HORIZONTAL);
                line2.setLayoutParams(params);

                RadioButton yess = new RadioButton(this);
                yess.setId(R.id.radio_yes);
                yess.setText(R.string.alert_button_yes);
                yess.setLayoutParams(params1);

                RadioButton noo = new RadioButton(this);
                noo.setId(R.id.radio_no);
                noo.setText(R.string.alert_button_no);
                noo.setLayoutParams(params1);
                final int finalC = c1;

                yess.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v == yess) {
                            pooos = finalC;
                            onRadioButtonClicked(yess);
                        }
                    }
                });
                noo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v == noo) {
                            pooos = finalC;
                            onRadioButtonClicked(noo);
                        }
                    }
                });


                line2.addView(yess);
                line2.addView(noo);


                cell.addView(line1);
                cell.addView(line2);
//                cell.addView(border);

                layout_all.addView(cell);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_yes:
                if (checked)
                    // Pirates are the best
                    feebackAns = "Yes";
                sendFeedbac(pooos);
                break;
            case R.id.radio_no:
                if (checked)
                    // Ninjas rule
                    feebackAns = "No";
                sendFeedbac(pooos);
                break;
        }
    }

    private void sendFeedbac(int position) {
        checkString = "ANS";

        JSONObject jsonObject = new JSONObject();
        String idService = "";
        idService = PreferenceStorage.getRateOrderId(this);
        String idCat = "";
        idCat = serviceHistoryList.getFeedbackArrayList().get(position).getid();
        String idSub = "";
        idSub = PreferenceStorage.getSubCatClick(this);
        String id = "";
        id = PreferenceStorage.getUserId(this);

        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, idService);
            jsonObject.put(SkilExConstants.FEEDBAC_ID, idCat);
            jsonObject.put(SkilExConstants.FEEDBAC_tEXT, feebackAns);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.FEEDBACK_ANSWER;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

}
