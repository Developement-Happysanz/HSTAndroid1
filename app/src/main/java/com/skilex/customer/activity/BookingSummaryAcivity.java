package com.skilex.customer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.skilex.customer.R;
import com.skilex.customer.serviceinterfaces.IServiceListener;

import org.json.JSONObject;

public class BookingSummaryAcivity extends AppCompatActivity implements IServiceListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_summary);
    }

    @Override
    public void onResponse(JSONObject response) {

    }

    @Override
    public void onError(String error) {

    }
}
