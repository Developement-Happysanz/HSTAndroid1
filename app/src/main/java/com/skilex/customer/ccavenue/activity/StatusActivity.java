package com.skilex.customer.ccavenue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skilex.customer.R;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;

import org.json.JSONException;
import org.json.JSONObject;


public class StatusActivity extends AppCompatActivity implements IServiceListener, DialogClickListener {

	LinearLayout advLayout, payLayout;
	ImageView paymentIcon, bookingIcon;
	TextView paymentStatus, paymentComment, bookingStatus, bookingComment;
	Button booking, rate;
	String page = "";
	String status = "";

	private ServiceHelper serviceHelper;
	private ProgressDialogHelper progressDialogHelper;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_status);

		initVals();

		Intent mainIntent = getIntent();

		TextView tv4 = (TextView) findViewById(R.id.textView1);
		tv4.setText(mainIntent.getStringExtra("transStatus"));
		status = mainIntent.getStringExtra("transStatus");

		page = (String) mainIntent.getStringExtra("page");

		setPageVal();
		sendAdvanceStatus();

	}

	private void initVals() {

		serviceHelper = new ServiceHelper(this);
		serviceHelper.setServiceListener(this);
		progressDialogHelper = new ProgressDialogHelper(this);

		advLayout = findViewById(R.id.advance_layout);
		bookingIcon = findViewById(R.id.status_img);
		bookingStatus = findViewById(R.id.status_text);
		bookingComment = findViewById(R.id.status_comment_text);
		booking = findViewById(R.id.home_booking);

		payLayout = findViewById(R.id.payment_layout);
		paymentIcon = findViewById(R.id.payment_status_icon);
		paymentStatus = findViewById(R.id.payment_status_text);
		paymentComment = findViewById(R.id.payment_status_comment_text);
		rate = findViewById(R.id.rate_service);
	}

	private void setPageVal() {

		if(page.equalsIgnoreCase("advance_pay")) {
			payLayout.setVisibility(View.GONE);

			findViewById(R.id.toolbar).setVisibility(View.VISIBLE);

			advLayout.setVisibility(View.VISIBLE);
			if(status.equalsIgnoreCase("Transaction Declined!")||status.equalsIgnoreCase("Transaction Cancelled!")) {
				bookingIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_servicebook_failed));
				bookingStatus.setText(R.string.booking_failed);
				bookingComment.setText(R.string.booking_failed_comment);
				booking.setText(R.string.try_again);
			} else {
				bookingIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_servicebook_success));
				bookingStatus.setText(R.string.booking_success);
				bookingComment.setText(R.string.booking_success_comment);
				booking.setText(R.string.go_home);
			}
		} else {
			advLayout.setVisibility(View.GONE);
			findViewById(R.id.toolbar).setVisibility(View.GONE);
			payLayout.setVisibility(View.VISIBLE);
			if(status.equalsIgnoreCase("Transaction Declined!")||status.equalsIgnoreCase("Transaction Cancelled!")) {

				payLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.payment_failed_bg));
				paymentIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_payment_failed));
				paymentStatus.setText(R.string.payment_failed);
				paymentComment.setText(R.string.payment_failed_comment);
				rate.setText(R.string.try_again);
				rate.setBackground(ContextCompat.getDrawable(this, R.drawable.button_try_again));

			} else {

				payLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.payment_success_bg));
				paymentIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_payment_success));
				paymentStatus.setText(R.string.payment_success);
				paymentComment.setText(R.string.payment_success_comment);
				rate.setText(R.string.rating_text);
				rate.setBackground(ContextCompat.getDrawable(this, R.drawable.button_rate_service));

			}
		}
	}

	public void showToast(String msg) {
		Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
	}

	private void sendAdvanceStatus() {
		JSONObject jsonObject = new JSONObject();

		String id = "";
		id = PreferenceStorage.getAdvanceAmt(this);

		String orderId = "";
		orderId = PreferenceStorage.getOrderId(this);
		try {
			jsonObject.put(SkilExConstants.ADVANCE_AMOUNT, id);
			jsonObject.put(SkilExConstants.ORDER_ID, orderId);
			jsonObject.put(SkilExConstants.ADVANCE_STATUS, "N");

		} catch (JSONException e) {
			e.printStackTrace();
		}

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
		String url = SkilExConstants.BUILD_URL + SkilExConstants.ADVANCE_PAYMENT;
		serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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