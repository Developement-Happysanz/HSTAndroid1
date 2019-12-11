package com.skilex.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.skilex.customer.R;
import com.skilex.customer.adapter.OngoingServiceListAdapter;
import com.skilex.customer.bean.support.Category;
import com.skilex.customer.bean.support.OngoingService;
import com.skilex.customer.bean.support.OngoingServiceList;
import com.skilex.customer.bean.support.Service;
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

public class OngoingServiceActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = OngoingServiceActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ArrayList<OngoingService> ongoingServiceArrayList = new ArrayList<>();
    private ListView loadMoreListView;
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    OngoingServiceListAdapter ongoingServiceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_services);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadMoreListView = findViewById(R.id.ong_service_list);
        loadMoreListView.setOnItemClickListener(this);

        callGetSubCategoryService();

    }

    public void callGetSubCategoryService() {
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadOnGoService();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private void loadOnGoService() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.ONGOING_SERVICES;
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
        if (service.getorder_status().equalsIgnoreCase("Initiated") || service.getorder_status().equalsIgnoreCase("Hold")) {
            Intent intent = new Intent(this, OngoingServiceDetailActivity.class);
            intent.putExtra("serviceObj", service);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ServiceSummaryActivity.class);
            intent.putExtra("serviceObj", service);
            intent.putExtra("service_page", "ongoing");
            startActivity(intent);
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
            ongoingServiceListAdapter = new OngoingServiceListAdapter(this, ongoingServiceArrayList);
            loadMoreListView.setAdapter(ongoingServiceListAdapter);
        } else {
            ongoingServiceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(String error) {

    }
}
