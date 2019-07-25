package com.skilex.customer.activity;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.serviceinterfaces.IServiceListener;

import org.json.JSONObject;

public class RequestedServicesDetailActivity  extends AppCompatActivity implements IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {
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
