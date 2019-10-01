package com.skilex.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.gson.Gson;
import com.skilex.customer.R;
import com.skilex.customer.adapter.GeneralServiceListAdapter;
import com.skilex.customer.bean.support.Service;
import com.skilex.customer.bean.support.ServiceList;
import com.skilex.customer.helper.AlertDialogHelper;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.CommonUtils;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class SearchResultActivity extends AppCompatActivity implements IServiceListener, AdapterView.OnItemClickListener, DialogClickListener {
    private static final String TAG = "AdvaSearchResAct";
    private ListView loadMoreListView;
    View view;
    String className;
    String event = "";
    GeneralServiceListAdapter generalServiceListAdapter;
    private ServiceHelper serviceHelper;
    ArrayList<Service> serviceArrayList;
    int pageNumber = 0, totalCount = 0;
    protected ProgressDialogHelper progressDialogHelper;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    private SearchView mSearchView = null;
    String advSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
//        getSupportActionBar().hide();
        loadMoreListView = findViewById(R.id.listView_services);
        loadMoreListView.setOnItemClickListener(this);
        className = this.getClass().getSimpleName();
        serviceArrayList = new ArrayList<>();
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        event = PreferenceStorage.getSearchFor(this);
        if (!event.isEmpty()) {
            makeSearch(event);
            PreferenceStorage.setSearchFor(this, "");
        }
    }

    public void makeSearch(String event) {
        /*if(eventsListAdapter != null){
            eventsListAdapter.clearSearchFlag();
        }*/
        if (serviceArrayList != null)
            serviceArrayList.clear();

        if (CommonUtils.isNetworkAvailable(this)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(SkilExConstants.SEARCH_TEXT, "" + event);
                jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserId(this));


            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String url = SkilExConstants.BUILD_URL + SkilExConstants.SEARCH_SERVICE_LIST;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }

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
    public void onResponse(final JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateResponse(response)) {
            Gson gson = new Gson();
            ServiceList serviceList = gson.fromJson(response.toString(), ServiceList.class);
            if (serviceList.getserviceArrayList() != null && serviceList.getserviceArrayList().size() > 0) {
                totalCount = serviceList.getCount();
                isLoadingForFirstTime = false;
                updateListAdapter(serviceList.getserviceArrayList());
            }
        }
    }

    @Override
    public void onError(final String error) {
        progressDialogHelper.hideProgressDialog();
//                loadMoreListView.onLoadMoreComplete();
        AlertDialogHelper.showSimpleAlertDialog(SearchResultActivity.this, error);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item clicked" + position);
        Service service = null;
        if ((generalServiceListAdapter != null) && (generalServiceListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = generalServiceListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            service = serviceArrayList.get(actualindex);
        } else {
            service = serviceArrayList.get(position);
        }

        Intent intent = new Intent(this, ServiceDetailActivity.class);
        intent.putExtra("serviceObj", service);
        startActivity(intent);
    }

    protected void updateListAdapter(ArrayList<Service> serviceArrayList) {
        this.serviceArrayList.addAll(serviceArrayList);
        if (generalServiceListAdapter == null) {
            generalServiceListAdapter = new GeneralServiceListAdapter(this, this.serviceArrayList);
            loadMoreListView.setAdapter(generalServiceListAdapter);
        } else {
            generalServiceListAdapter.notifyDataSetChanged();
        }
    }

    public void searchForEvent(String eventname) {
        Log.d(TAG, "searchevent called");
        if (generalServiceListAdapter != null) {
            generalServiceListAdapter.startSearch(eventname);
            generalServiceListAdapter.notifyDataSetChanged();
            //loadMoreListView.invalidateViews();
        }
    }

    public void exitSearch() {
        Log.d(TAG, "exit event called");
        if (generalServiceListAdapter != null) {
            generalServiceListAdapter.exitSearch();
            generalServiceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }
}