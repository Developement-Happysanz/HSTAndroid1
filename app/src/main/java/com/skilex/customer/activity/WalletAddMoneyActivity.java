package com.skilex.customer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.skilex.customer.R;
import com.skilex.customer.bean.support.Service;
import com.skilex.customer.ccavenue.activity.InitialScreenActivity;
import com.skilex.customer.helper.AlertDialogHelper;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.CommonUtils;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;
import com.skilex.customer.utils.SkilExValidator;

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class WalletAddMoneyActivity extends AppCompatActivity implements View.OnClickListener, DialogClickListener {
    private static final String TAG = WalletAddMoneyActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ImageView serviceImage;
    private TextView addMoney;
    private EditText addAmt;
    Service service;
    String res = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_add_money);

        service = (Service) getIntent().getSerializableExtra("serviceObj");
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addMoney = (TextView) findViewById(R.id.btn_add);
        addMoney.setOnClickListener(this);
        addAmt = (EditText) findViewById(R.id.add_amount);


    }

    @Override
    public void onClick(View v) {
        if (v == addMoney) {
            if (validateFields()) {
                Intent i = new Intent(this, InitialScreenActivity.class);
                i.putExtra("advpay", addAmt.getText().toString());
                i.putExtra("page", "wallet");
                startActivity(i);
                finish();
            }

        }
    }
    private boolean validateFields() {
        if (!SkilExValidator.checkNullString(this.addAmt.getText().toString().trim())) {
            addAmt.setError(getString(R.string.empty_entry));
            requestFocus(addAmt);
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
}