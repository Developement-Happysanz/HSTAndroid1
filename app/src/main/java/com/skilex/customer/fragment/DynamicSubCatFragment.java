package com.skilex.customer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.skilex.customer.R;
import com.skilex.customer.activity.BookingSummaryAcivity;
import com.skilex.customer.activity.ServiceDetailActivity;
import com.skilex.customer.activity.SubCategoryActivity;
import com.skilex.customer.adapter.MainServiceListAdapter;
import com.skilex.customer.bean.support.Category;
import com.skilex.customer.bean.support.Service;
import com.skilex.customer.bean.support.ServiceList;
import com.skilex.customer.bean.support.SubCategory;
import com.skilex.customer.bean.support.SubCategoryList;
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

import java.util.ArrayList;
import java.util.Timer;

import java.util.TimerTask;

import static android.util.Log.d;

public class DynamicSubCatFragment extends Fragment implements IServiceListener, AdapterView.OnItemClickListener, DialogClickListener {
    Context context;
    private View view;
    private static ArrayList<SubCategory> subCategoryArrayList;
    private ArrayList<Service> serviceArrayList;
    private int val;
    private MainServiceListAdapter serviceListAdapter;
    private static final String TAG = DynamicSubCatFragment.class.getName();
    private String subCatId = "";
    private ServiceHelper serviceHelper;
    private int totalCount = 0, checkrun = 0;
    private  boolean isLoadingForFirstTime = true;
    private ProgressDialogHelper progressDialogHelper;
    private ListView loadMoreListView;
    private Boolean msgErr = false;
    private Boolean noService = false;
    private String res = "";
    private String id = "";

    private static boolean _hasLoadedOnce = false; // your boolean field

    public static DynamicSubCatFragment newInstance(int val, ArrayList<SubCategory> categoryArrayList) {
        DynamicSubCatFragment fragment = new DynamicSubCatFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", val);
        fragment.setArguments(args);
        subCategoryArrayList = categoryArrayList;
        if (String.valueOf(val).equalsIgnoreCase("1")) {
            _hasLoadedOnce = true;
        } else {
            _hasLoadedOnce = false;
        }
        return fragment;
    }


    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(true);

        if (this.isVisible()) {
            // we check that the fragment is becoming visible
//            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            if (isFragmentVisible_ && !_hasLoadedOnce) {
                loadCat();
                _hasLoadedOnce = true;
                if(noService) {
                    AlertDialogHelper.showSimpleAlertDialog(view.getContext(), "No service found");
                    noService = false;
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        serviceHelper = new ServiceHelper(view.getContext());
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(view.getContext());
        val = getArguments().getInt("someInt", 0);
//        categories = subCategoryList.getCategoryArrayList();
//        categories = subCategoryList.getCategoryArrayList();
        subCatId = subCategoryArrayList.get(val).getSub_cat_id();
//        PreferenceStorage.saveSubCatClick(view.getContext(), subCatId);
//        rateCount = (TextView) view.findViewById(R.id.service_count);
//        summary = (TextView) view.findViewById(R.id.view_summary);
//        summary.setOnClickListener(this);
//        c = view.findViewById(R.id.c);
//        c.setText("" + subCatId);
        loadMoreListView = view.findViewById(R.id.serviceList);
        loadMoreListView.setOnItemClickListener(this);
        clearCart();
        return view;
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                if (res.equalsIgnoreCase("services")) {
                    JSONArray getData = response.getJSONArray("services");
//                loadMembersList(getData.length());
                    Gson gson = new Gson();
                    ServiceList serviceList = gson.fromJson(response.toString(), ServiceList.class);
                    serviceArrayList = new ArrayList<>();
                    if (serviceList.getserviceArrayList() != null && serviceList.getserviceArrayList().size() > 0) {
                        totalCount = serviceList.getCount();
//                    this.categoryArrayList.addAll(subCategoryList.getCategoryArrayList());
                        isLoadingForFirstTime = false;
                        updateListAdapter(serviceList.getserviceArrayList());
                    } else {
                        if (serviceArrayList != null) {
                            serviceArrayList.clear();
                            updateListAdapter(serviceList.getserviceArrayList());
//                            serviceListAdapter = new MainServiceListAdapter(view.getContext(), this.serviceArrayList);
//                            loadMoreListView.setAdapter(serviceListAdapter);
                        }
                    }
                } else if (res.equalsIgnoreCase("clear")) {
                    PreferenceStorage.saveServiceCount(view.getContext(), "");
                    PreferenceStorage.saveRate(view.getContext(), "");
                    PreferenceStorage.savePurchaseStatus(view.getContext(), false);
                    loadCat();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

    private void clearCart() {
        res = "clear";
        id = PreferenceStorage.getUserId(view.getContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.CLEAR_CART;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
//                String status = response.getString("status");
//                String msg = response.getString(SkilExConstants.PARAM_MESSAGE);
//                d(TAG, "status val" + status + "msg" + msg);
//
//                if ((status != null)) {
//                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
//                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
//                        signInSuccess = false;
//                        d(TAG, "Show error dialog");
////                        if (msg.equalsIgnoreCase("Services not found")) {
////                            msgErr = true;
////                        }
////                        AlertDialogHelper.showSimpleAlertDialog(view.getContext(), msg);
//                        if (msg.equalsIgnoreCase("Service not found")){
//                            noService = true;
//                        }
//
//                    } else {
//                        signInSuccess = true;
//                    }
//                }
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
                        if (msg.equalsIgnoreCase("Services not found")){
                            msgErr = true;
                            noService = true;
                        }
//                        if (PreferenceStorage.getLang(view.getContext()).equalsIgnoreCase("tamil")) {
//                            AlertDialogHelper.showSimpleAlertDialog(view.getContext(), msg_ta);
//                        } else {
//                            AlertDialogHelper.showSimpleAlertDialog(view.getContext(), msg_en);
//                        }

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
    public void onError(String error) {

    }

    private void loadCat() {
        res = "services";
        JSONObject jsonObject = new JSONObject();
        String catId = "";
//        id = category.getCat_id();
        catId = PreferenceStorage.getCatClick(view.getContext());
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.MAIN_CATEGORY_ID, catId);
            jsonObject.put(SkilExConstants.SUB_CATEGORY_ID, subCatId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.SERVICE_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    protected void updateListAdapter(ArrayList<Service> serviceArrayList) {
        if (msgErr) {
//            AlertDialogHelper.showSimpleAlertDialog(view.getContext(), "No Services found");
        } else {
            msgErr = false;
            this.serviceArrayList.clear();
            this.serviceArrayList.addAll(serviceArrayList);
            if (serviceListAdapter == null) {
                serviceListAdapter = new MainServiceListAdapter(view.getContext(), this.serviceArrayList);
                loadMoreListView.setAdapter(serviceListAdapter);
            } else {
                serviceListAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item clicked" + position);
        Service service = null;
        if ((serviceListAdapter != null) && (serviceListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = serviceListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            service = serviceArrayList.get(actualindex);
        } else {
            service = serviceArrayList.get(position);
        }

        Intent intent = new Intent(view.getContext(), ServiceDetailActivity.class);
        intent.putExtra("serviceObj", service);
        intent.putExtra("page", "service");
        startActivity(intent);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }
}