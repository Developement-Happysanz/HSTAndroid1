package com.skilex.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.skilex.customer.R;
import com.skilex.customer.adapter.RequestedServiceListAdapter;
import com.skilex.customer.adapter.ServiceHistoryListAdapter;
import com.skilex.customer.bean.support.OngoingService;
import com.skilex.customer.bean.support.OngoingServiceList;
import com.skilex.customer.bean.support.ServiceHistory;
import com.skilex.customer.bean.support.ServiceHistoryList;
import com.skilex.customer.helper.AlertDialogHelper;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.CommonUtils;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class ServiceHistoryActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = ServiceHistoryActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ListView historyListView;
    private ArrayList<ServiceHistory> serviceHistoryArrayList = new ArrayList<>();
    private ServiceHistoryListAdapter serviceHistoryListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_history);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        historyListView = findViewById(R.id.his_service_list);
        historyListView.setOnItemClickListener(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        callReqService();
    }

    public void callReqService() {
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadReqService();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, String.valueOf(R.string.error_no_net));
        }
    }

    private void loadReqService() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.HISTORY_SERVICES;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item clicked" + position);
        ServiceHistory service = null;
        if ((serviceHistoryListAdapter != null) && (serviceHistoryListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = serviceHistoryListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            service = serviceHistoryArrayList.get(actualindex);
        } else {
            service = serviceHistoryArrayList.get(position);
        }

        Intent intent = new Intent(this, ServiceSummaryActivity.class);
        intent.putExtra("serviceObj", service);
        startActivity(intent);
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
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateResponse(response)) {
            try {
                JSONArray getData = response.getJSONArray("service_list");
//                    loadMembersList(getData.length());
                Gson gson = new Gson();
                ServiceHistoryList serviceHistoryList = gson.fromJson(response.toString(), ServiceHistoryList.class);
                if (serviceHistoryList.getserviceArrayList() != null && serviceHistoryList.getserviceArrayList().size() > 0) {
                    int totalCount = serviceHistoryList.getCount();
//                    this.serviceHistoryArrayList.addAll(ongoingServiceList.getserviceArrayList());
                    boolean isLoadingForFirstTime = false;
                    updateListAdapter(serviceHistoryList.getserviceArrayList());
                } else {
                    if (serviceHistoryArrayList != null) {
                        serviceHistoryArrayList.clear();
                        updateListAdapter(serviceHistoryList.getserviceArrayList());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void updateListAdapter(ArrayList<ServiceHistory> serviceHistoryArrayLists) {
        serviceHistoryArrayList.clear();
        serviceHistoryArrayList.addAll(serviceHistoryArrayLists);
        if (serviceHistoryListAdapter == null) {
            serviceHistoryListAdapter = new ServiceHistoryListAdapter(this, serviceHistoryArrayList);
            historyListView.setAdapter(serviceHistoryListAdapter);
        } else {
            serviceHistoryListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(String error) {

    }
}