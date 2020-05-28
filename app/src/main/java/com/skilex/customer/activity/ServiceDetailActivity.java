package com.skilex.customer.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.skilex.customer.R;
import com.skilex.customer.adapter.ReviewAdapter;
import com.skilex.customer.bean.support.Category;
import com.skilex.customer.bean.support.Review;
import com.skilex.customer.bean.support.ReviewList;
import com.skilex.customer.bean.support.Service;
import com.skilex.customer.bean.support.TrendingServices;
import com.skilex.customer.customview.CircleImageView;
import com.skilex.customer.helper.AlertDialogHelper;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.CommonUtils;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;
import com.skilex.customer.utils.SkilExValidator;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class ServiceDetailActivity extends AppCompatActivity implements IServiceListener, View.OnClickListener, DialogClickListener {
    private static final String TAG = ServiceDetailActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ImageView serviceImage;
    private TextView serviceCost, costText, serviceIncludes, serviceExcludes, serviceProcedure, serviceOthers;
    private ScrollView scrollView;
    Service service;
    TrendingServices trendingServices;
    Button bookNow;
    String res = "";
    String page = "";
    int totalCount = 0;
    protected boolean isLoadingForFirstTime = true;
    ReviewAdapter reviewAdapter;
    ArrayList<Review> reviewArrayList = new ArrayList<>();
//    ListView reviewsListView;
    LinearLayout reviewsListView;
    ReviewList reviewList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        page = getIntent().getStringExtra("page");
        if (page.equalsIgnoreCase("category")) {
            trendingServices = (TrendingServices) getIntent().getSerializableExtra("cat");
        } else {
            service = (Service) getIntent().getSerializableExtra("serviceObj");
        }
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        reviewsListView = findViewById(R.id.listView_reviews);
        reviewsListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        serviceCost = (TextView) findViewById(R.id.cost);
        costText = (TextView) findViewById(R.id.cost_text);
        serviceIncludes = (TextView) findViewById(R.id.include_text);
        serviceExcludes = (TextView) findViewById(R.id.exclude_text);
        serviceProcedure = (TextView) findViewById(R.id.procedure_text);
        serviceOthers = (TextView) findViewById(R.id.others_text);
        scrollView = (ScrollView) findViewById(R.id.extras);
        serviceImage = (ImageView) findViewById(R.id.service_image);
        bookNow = (Button) findViewById(R.id.book_now);
        bookNow.setOnClickListener(this);

        callGetSubCategoryService();
    }

    public void callGetSubCategoryService() {
//        if (classTestArrayList != null)
//            classTestArrayList.clear();

        if (CommonUtils.isNetworkAvailable(this)) {
//            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            getServiceDetail();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private void getServiceDetail() {
        res = "detail";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        if (page.equalsIgnoreCase("category")) {
            id = trendingServices.getservice_id();
        } else {
            id = service.getservice_id();
        }
        try {
            jsonObject.put(SkilExConstants.SERVICE_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.GET_SERVICE_DETAIL;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void bookService() {
//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        res = "cart";
        JSONObject jsonObject = new JSONObject();

        String idService = "";

        String idCat = "";
        String idSub = "";
        String id = "";
        if (page.equalsIgnoreCase("category")) {
            id = PreferenceStorage.getUserId(this);
            idService = trendingServices.getservice_id();
            idCat = trendingServices.getmain_cat_id();
            idSub = trendingServices.getsub_cat_id();
        } else {
            idService = service.getservice_id();
            idCat = PreferenceStorage.getCatClick(this);
            idSub = PreferenceStorage.getSubCatClick(this);
            id = PreferenceStorage.getUserId(this);
        }
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ID, idService);
            jsonObject.put(SkilExConstants.CATEGORY_ID, idCat);
            jsonObject.put(SkilExConstants.SUB_CAT_ID, idSub);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.ADD_TO_CART;
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
                        if (!res.equalsIgnoreCase("reviewList")) {
                            if (PreferenceStorage.getLang(this).equalsIgnoreCase("tamil")) {
                                AlertDialogHelper.showSimpleAlertDialog(this, msg_ta);
                            } else {
                                AlertDialogHelper.showSimpleAlertDialog(this, msg_en);
                            }
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
//        progressDialogHelper.hideProgressDialog();
        if (validateResponse(response)) {
            try {
                if (res.equalsIgnoreCase("detail")) {
                    JSONObject data = response.getJSONObject("service_details");
                    serviceCost.setText("₹" + data.getString("rate_card"));
                    if (PreferenceStorage.getLang(this).equalsIgnoreCase("tamil")) {
                        costText.setText(data.getString("rate_card_details_ta"));
                    } else {
                        costText.setText(data.getString("rate_card_details"));
                    }
                    if (!data.getString("inclusions").isEmpty() ||
                            !data.getString("exclusions").isEmpty() ||
                            !data.getString("service_procedure").isEmpty() ||
                            !data.getString("others").isEmpty()) {
                        if (PreferenceStorage.getLang(this).equalsIgnoreCase("tamil")) {
                            serviceIncludes.setText(data.getString("inclusions_ta"));
                            serviceExcludes.setText(data.getString("exclusions_ta"));
                            serviceProcedure.setText(data.getString("service_procedure_ta"));
                            serviceOthers.setText(data.getString("others_ta"));
                        } else {
                            serviceIncludes.setText(data.getString("inclusions"));
                            serviceExcludes.setText(data.getString("exclusions"));
                            serviceProcedure.setText(data.getString("service_procedure"));
                            serviceOthers.setText(data.getString("others"));
                        }
                        scrollView.setVisibility(View.VISIBLE);
                    }
                    String url = "";
                    url = data.getString("service_pic_url");
                    if (!url.isEmpty()) {
                        Picasso.get().load(url).into(serviceImage);
                    }
                    loadReviewList();
                } else if (res.equalsIgnoreCase("cart")) {

                    JSONObject data = response.getJSONObject("cart_total");

                    String rate = data.getString("total_amt");
                    String count = data.getString("service_count");

                    PreferenceStorage.saveRate(this, rate);
                    PreferenceStorage.saveServiceCount(this, count);
                    PreferenceStorage.savePurchaseStatus(this, true);

                    Intent newIntent = new Intent(this, BookingSummaryAcivity.class);
                    newIntent.putExtra("page", "serviceDetail");
                    startActivity(newIntent);
                } else if (res.equalsIgnoreCase("reviewList")) {
                    Gson gson = new Gson();
                    reviewList = gson.fromJson(response.toString(), ReviewList.class);
                    if (reviewList.getReviews() != null && reviewList.getReviews().size() > 0) {
                        totalCount = reviewList.getCount();
                        isLoadingForFirstTime = false;
//                        updateListAdapter(reviewList.getReviews());
                        loadMembersList(reviewList.getReviews().size());
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    protected void updateListAdapter(ArrayList<Review> reviewArrayList) {
//        this.reviewArrayList.addAll(reviewArrayList);
////        if (bookingPlanAdapter == null) {
//        reviewAdapter = new ReviewAdapter(this, this.reviewArrayList);
//        reviewsListView.setAdapter(reviewAdapter);
////        } else {
//        reviewAdapter.notifyDataSetChanged();
////        }
//    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onClick(View v) {
        if (v == bookNow) {
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

    private void loadReviewList() {
        res = "reviewList";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        if (page.equalsIgnoreCase("category")) {
            trendingServices = (TrendingServices) getIntent().getSerializableExtra("cat");
            id = trendingServices.getservice_id();
        } else {
            service = (Service) getIntent().getSerializableExtra("serviceObj");
            id = service.getservice_id();
        }

        try {

            jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserId(this));
            jsonObject.put(SkilExConstants.SERVICE_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.SERVICE_REVIEW_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void loadMembersList(int memberCount) {

        try {

            for (int c1 = 0; c1 < memberCount; c1++) {

                RelativeLayout cell = new RelativeLayout(this);
                cell.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                cell.setPadding(20, 20, 20, 20);
                cell.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(100, 100);
                paramsImageView.setMargins(20, 20, 0, 0);

                RelativeLayout.LayoutParams paramsTextViewUserName = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsTextViewUserName.setMargins(20, 20, 0, 0);
                paramsTextViewUserName.addRule(RelativeLayout.RIGHT_OF, R.id.user_profile_img);

                RelativeLayout.LayoutParams paramsTextViewReview = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsTextViewReview.setMargins(0, 10, 0, 10);
                paramsTextViewReview.addRule(RelativeLayout.ALIGN_LEFT, R.id.username_disp);
                paramsTextViewReview.addRule(RelativeLayout.BELOW, R.id.username_disp);

                RelativeLayout.LayoutParams paramsRatingBar = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsRatingBar.setMargins(0, 0, 10, 0);
                paramsRatingBar.addRule(RelativeLayout.ALIGN_PARENT_END);
                paramsRatingBar.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.username_disp);

                RelativeLayout.LayoutParams paramsTextViewRatingName = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsTextViewRatingName.setMargins(0, 10, 0, 10);
                paramsTextViewRatingName.addRule(RelativeLayout.ALIGN_LEFT, R.id.ratingBar);
                paramsTextViewRatingName.addRule(RelativeLayout.ALIGN_RIGHT, R.id.ratingBar);
                paramsTextViewRatingName.addRule(RelativeLayout.BELOW, R.id.ratingBar);


                TextView txtUserName = new TextView(this);
                txtUserName.setLayoutParams(paramsTextViewUserName);

                txtUserName.setText(reviewList.getReviews().get(c1).getCustomer_name());


                txtUserName.setId(R.id.username_disp);
                txtUserName.requestFocusFromTouch();
                txtUserName.setTextSize(18.0f);
                txtUserName.setTypeface(txtUserName.getTypeface(), Typeface.BOLD);
                txtUserName.setTextColor(ContextCompat.getColor(this, R.color.black));

                CircleImageView userImage = new CircleImageView(this);
                userImage.setId(R.id.user_profile_img);
                userImage.setLayoutParams(paramsImageView);

                if (SkilExValidator.checkNullString(reviewList.getReviews().get(c1).getProfile_picture())) {
                    Picasso.get().load(reviewList.getReviews().get(c1).getProfile_picture()).into(userImage);
                } else {
                    userImage.setImageResource(R.drawable.ic_user_profile_image);
                }
                userImage.setBackground(ContextCompat.getDrawable(this, R.drawable.button_circle_white));

                TextView reviewDate = new TextView(this);
                reviewDate.setId(R.id.txtComments);
                reviewDate.setLayoutParams(paramsTextViewReview);
                reviewDate.requestFocusFromTouch();
                reviewDate.setTextSize(14.0f);
                reviewDate.setTextColor(ContextCompat.getColor(this, R.color.black));
                reviewDate.setText(reviewList.getReviews().get(c1).getReview_date());

                RatingBar ratingBar = new RatingBar(this, null, android.R.attr.ratingBarStyleSmall);
                ratingBar.setId(R.id.ratingBar);
                ratingBar.setRating(Integer.parseInt(reviewList.getReviews().get(c1).getRating()));
                ratingBar.setLayoutParams(paramsRatingBar);
                ratingBar.setStepSize(1);
                ratingBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.golden)));
                ratingBar.setNumStars(5);


                TextView rateName = new TextView(this);
                rateName.setId(R.id.rating_name);
                rateName.requestFocusFromTouch();
                rateName.setLayoutParams(paramsTextViewRatingName);
                rateName.setTextSize(14.0f);
                rateName.setGravity(Gravity.CENTER);
                rateName.setTextColor(ContextCompat.getColor(this, R.color.black));
                switch (Integer.parseInt(reviewList.getReviews().get(c1).getRating())) {
                    case 1: rateName.setText("Poor");
                        break;
                    case 2: rateName.setText("Average");
                        break;
                    case 3: rateName.setText("Good");
                        break;
                    case 4: rateName.setText("Very Good");
                        break;
                    case 5: rateName.setText("Excellent");
                        break;
                    default: rateName.setText("Not available");
                }

                cell.addView(userImage);
                cell.addView(txtUserName);
                cell.addView(reviewDate);
                cell.addView(ratingBar);
                cell.addView(rateName);
//                cell.addView(border);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 4);
                params.setMargins(100, 0, 100, 0);

                TextView line1 = new TextView(this);
                line1.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
                line1.setLayoutParams(params);
                line1.setBackgroundColor(ContextCompat.getColor(this, R.color.grey));
                reviewsListView.addView(cell);
                reviewsListView.addView(line1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
