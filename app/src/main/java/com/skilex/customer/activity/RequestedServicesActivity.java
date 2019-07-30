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
import com.skilex.customer.adapter.OngoingServiceListAdapter;
import com.skilex.customer.adapter.RequestedServiceListAdapter;
import com.skilex.customer.bean.support.OngoingService;
import com.skilex.customer.bean.support.OngoingServiceList;
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

public class RequestedServicesActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = RequestedServicesActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ArrayList<OngoingService> ongoingServiceArrayList = new ArrayList<>();
    private ListView loadMoreListView;
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    RequestedServiceListAdapter ongoingServiceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_services);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadMoreListView = findViewById(R.id.req_service_list);
        loadMoreListView.setOnItemClickListener(this);

        callReqService();

    }

    public void callReqService() {
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadReqService();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
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
        String url = SkilExConstants.BUILD_URL + SkilExConstants.REQUESTED_SERVICES;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item clicked" + position);
        OngoingService service = null;
        if ((ongoingServiceListAdapter != null) && (ongoingServiceListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = ongoingServiceListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            service = ongoingServiceArrayList.get(actualindex);
        } else {
            service = ongoingServiceArrayList.get(position);
        }

        Intent intent = new Intent(this, RequestedServicesDetailActivity.class);
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
                d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(this, msg);

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
                OngoingServiceList ongoingServiceList = gson.fromJson(response.toString(), OngoingServiceList.class);
                if (ongoingServiceList.getserviceArrayList() != null && ongoingServiceList.getserviceArrayList().size() > 0) {
                    totalCount = ongoingServiceList.getCount();
//                    this.ongoingServiceArrayList.addAll(ongoingServiceList.getserviceArrayList());
                    isLoadingForFirstTime = false;
                    updateListAdapter(ongoingServiceList.getserviceArrayList());
                } else {
                    if (ongoingServiceArrayList != null) {
                        ongoingServiceArrayList.clear();
                        updateListAdapter(ongoingServiceList.getserviceArrayList());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void updateListAdapter(ArrayList<OngoingService> ongoingServiceArrayLists) {
        ongoingServiceArrayList.clear();
        ongoingServiceArrayList.addAll(ongoingServiceArrayLists);
        if (ongoingServiceListAdapter == null) {
            ongoingServiceListAdapter = new RequestedServiceListAdapter(this, ongoingServiceArrayList);
            loadMoreListView.setAdapter(ongoingServiceListAdapter);
        } else {
            ongoingServiceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(String error) {

    }
}
