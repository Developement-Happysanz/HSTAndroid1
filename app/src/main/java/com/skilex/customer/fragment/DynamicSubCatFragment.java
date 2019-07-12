package com.skilex.customer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.skilex.customer.R;
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
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class DynamicSubCatFragment extends Fragment implements IServiceListener, AdapterView.OnItemClickListener {
    Context context;
    View view;
    static ArrayList<SubCategory> subCategoryArrayList;
    ArrayList<Service> serviceArrayList = new ArrayList<>();
    int val;
    MainServiceListAdapter serviceListAdapter;
    private static final String TAG = DynamicSubCatFragment.class.getName();
    String subCatId = "";
    private ServiceHelper serviceHelper;
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    private ProgressDialogHelper progressDialogHelper;
    TextView c;
    ListView loadMoreListView;

    public static DynamicSubCatFragment newInstance(int val, ArrayList<SubCategory> categoryArrayList) {
        DynamicSubCatFragment fragment = new DynamicSubCatFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", val);
        fragment.setArguments(args);
        subCategoryArrayList = categoryArrayList;
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        serviceHelper = new ServiceHelper(getActivity());
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(getActivity());
        view = inflater.inflate(R.layout.fragment_list, container, false);
        val = getArguments().getInt("someInt", 0);
//        categories = subCategoryList.getCategoryArrayList();
//        categories = subCategoryList.getCategoryArrayList();
        subCatId = subCategoryArrayList.get(val).getSub_cat_id();
        PreferenceStorage.saveSubCatClick(getActivity(), subCatId);
//        c = view.findViewById(R.id.c);
//        c.setText("" + subCatId);
        loadMoreListView = view.findViewById(R.id.serviceList);
        loadMoreListView.setOnItemClickListener(this);
        loadCat();
        return view;
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                JSONArray getData = response.getJSONArray("services");
//                loadMembersList(getData.length());
                Gson gson = new Gson();
                ServiceList serviceList = gson.fromJson(response.toString(), ServiceList.class);
                if (serviceList.getserviceArrayList() != null && serviceList.getserviceArrayList().size() > 0) {
                    totalCount = serviceList.getCount();
//                    this.categoryArrayList.addAll(subCategoryList.getCategoryArrayList());
                    isLoadingForFirstTime = false;
                    updateListAdapter(serviceList.getserviceArrayList());
                } else {
                    if (serviceArrayList != null) {
                        serviceArrayList.clear();
                        updateListAdapter(serviceList.getserviceArrayList());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

    private boolean validateSignInResponse(JSONObject response) {
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
                        AlertDialogHelper.showSimpleAlertDialog(getActivity(), msg);

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
        JSONObject jsonObject = new JSONObject();
        String id = "";
//        id = category.getCat_id();
        id = PreferenceStorage.getCatClick(getActivity());
        try {
            jsonObject.put(SkilExConstants.MAIN_CATEGORY_ID, id);
            jsonObject.put(SkilExConstants.SUB_CATEGORY_ID, subCatId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.SERVICE_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    protected void updateListAdapter(ArrayList<Service> serviceArrayList) {
        this.serviceArrayList.addAll(serviceArrayList);
        if (serviceListAdapter == null) {
            serviceListAdapter = new MainServiceListAdapter(getActivity(), this.serviceArrayList);
            loadMoreListView.setAdapter(serviceListAdapter);
        } else {
            serviceListAdapter.notifyDataSetChanged();
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

        Intent intent = new Intent(getActivity(), ServiceDetailActivity.class);
        intent.putExtra("serviceObj", service);
        startActivity(intent);
    }

}