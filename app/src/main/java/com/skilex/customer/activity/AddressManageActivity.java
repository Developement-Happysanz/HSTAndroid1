package com.skilex.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

public class AddressManageActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = ProfileActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private String res = "";

    private RelativeLayout addressTwoLayout;
    private View clickbait;
    private TextView addressOne, addressTwo;
    private ImageView addressOneEdit, addressTwoEdit;
    private Button submitAddress;
    private String addressIDOne, addressStringOne, nameOne, contactOne, latlanOne, locationOne;
    private String addressIDTwo, addressStringTwo, nameTwo, contactTwo, latlanTwo, locationTwo;
    private String selectedLatLan;
    private Boolean radioAddress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_address);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        addressTwoLayout = findViewById(R.id.address_one_layout);
        addressOne = findViewById(R.id.address_one);
        addressTwo = findViewById(R.id.address_two);
        addressOneEdit = findViewById(R.id.edit_address_one);
        addressTwoEdit = findViewById(R.id.edit_address_two);
        addressOneEdit.setOnClickListener(this);
        addressTwoEdit.setOnClickListener(this);

        addressAlert();
    }

    private void addressAlert() {
        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        res = "address_list";
        String id = "";
        id = PreferenceStorage.getUserId(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.KEY_CUST_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = SkilExConstants.BUILD_URL + SkilExConstants.ADDRESS_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

    }

    @Override
    public void onClick(View v) {
        if (v == addressOneEdit) {
            Intent homeIntent = new Intent(this, AddressEditActivity.class);
            homeIntent.putExtra("addressID", addressIDOne);
            homeIntent.putExtra("addressStringOne", addressStringOne);
            homeIntent.putExtra("nameOne", nameOne);
            homeIntent.putExtra("contactOne", contactOne);
            homeIntent.putExtra("latlanOne", latlanOne);
            homeIntent.putExtra("locationOne", locationOne);
            startActivity(homeIntent);
            finish();
        }
        if (v == addressTwoEdit) {
            Intent homeIntent = new Intent(this, AddressEditActivity.class);
            homeIntent.putExtra("addressID", addressIDTwo);
            homeIntent.putExtra("addressStringOne", addressStringTwo);
            homeIntent.putExtra("nameOne", nameTwo);
            homeIntent.putExtra("contactOne", contactTwo);
            homeIntent.putExtra("latlanOne", latlanTwo);
            homeIntent.putExtra("locationOne", locationTwo);
            startActivity(homeIntent);
            finish();
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
                if (!status.equalsIgnoreCase("success") && !(res.equalsIgnoreCase("add_address"))) {
//                    String msg = response.getString(SkilExConstants.PARAM_MESSAGE);
                    String msg_en = response.getString(SkilExConstants.PARAM_MESSAGE_ENG);
                    String msg_ta = response.getString(SkilExConstants.PARAM_MESSAGE_TAMIL);
                    d(TAG, "status val" + status + "msg" + msg_en);
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d(TAG, "Show error dialog");
                        if (PreferenceStorage.getLang(this).equalsIgnoreCase("tamil")) {
                            AlertDialogHelper.showSimpleAlertDialog(this, msg_ta);
                        } else {
                            AlertDialogHelper.showSimpleAlertDialog(this, msg_en);
                        }
                        if (res.equalsIgnoreCase("address_list")) {
                            addressOneEdit.setClickable(false);
                            addressTwoEdit.setClickable(false);
                        }
                    }
                } else {
                    signInSuccess = true;
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
                if (res.equalsIgnoreCase("address_list")) {
                    JSONArray list = response.getJSONArray("address_list");
                    switch (list.length()) {
                        case 1:
                            addressOne.setText(list.getJSONObject(0).getString("serv_address") +
                                    "\n" + list.getJSONObject(0).getString("contact_name") +
                                    "\nPhone: " + list.getJSONObject(0).getString("contact_no"));
                            addressIDOne = list.getJSONObject(0).getString("id");
                            addressStringOne = list.getJSONObject(0).getString("serv_address");
                            nameOne = list.getJSONObject(0).getString("contact_name");
                            contactOne = list.getJSONObject(0).getString("contact_no");
                            latlanOne = list.getJSONObject(0).getString("serv_lat_lon");
                            locationOne = list.getJSONObject(0).getString("serv_loc");
                            addressTwoLayout.setVisibility(View.GONE);
                            break;
                        case 2:
                            addressOne.setText(list.getJSONObject(0).getString("serv_address") +
                                    "\n" + list.getJSONObject(0).getString("contact_name") +
                                    "\nPhone: " + list.getJSONObject(0).getString("contact_no"));
                            addressIDOne = list.getJSONObject(0).getString("id");
                            addressStringOne = list.getJSONObject(0).getString("serv_address");
                            nameOne = list.getJSONObject(0).getString("contact_name");
                            contactOne = list.getJSONObject(0).getString("contact_no");
                            latlanOne = list.getJSONObject(0).getString("serv_lat_lon");
                            locationOne = list.getJSONObject(0).getString("serv_loc");

                            addressTwo.setText(list.getJSONObject(1).getString("serv_address") +
                                    "\n" + list.getJSONObject(1).getString("contact_name") +
                                    "\nPhone: " + list.getJSONObject(1).getString("contact_no"));

                            addressIDTwo = list.getJSONObject(1).getString("id");
                            addressStringTwo = list.getJSONObject(1).getString("serv_address");
                            nameTwo = list.getJSONObject(1).getString("contact_name");
                            contactTwo = list.getJSONObject(1).getString("contact_no");
                            latlanTwo = list.getJSONObject(1).getString("serv_lat_lon");
                            locationTwo = list.getJSONObject(1).getString("serv_loc");
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {

    }
}
