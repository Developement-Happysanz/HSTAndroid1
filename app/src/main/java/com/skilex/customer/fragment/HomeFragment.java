package com.skilex.customer.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skilex.customer.R;
import com.skilex.customer.activity.MainActivity;
import com.skilex.customer.activity.SubCategoryActivity;
import com.skilex.customer.adapter.CategoryListAdapter;
import com.skilex.customer.adapter.PreferenceListAdapter;
import com.skilex.customer.bean.support.Category;
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

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.util.Log.d;

public class HomeFragment extends Fragment implements IServiceListener, DialogClickListener, PreferenceListAdapter.OnItemClickListener {

    private static final String TAG = HomeFragment.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    Handler mHandler = new Handler();
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    ArrayList<Category> categoryArrayList;
    CategoryListAdapter categoryListAdapter;
    ListView loadMoreListView;
    Category category;
    private PreferenceListAdapter preferenceAdatper;
    private GridLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private SearchView searchView;
    private Animation slide_in_left, slide_in_right, slide_out_left, slide_out_right;
    private View rootView;
    private ViewFlipper viewFlipper;
    public static HomeFragment newInstance(int position) {
        HomeFragment frag = new HomeFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_main_category, container, false);
        initiateServices();

        categoryArrayList = new ArrayList<>();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listView_categories);

//      create animations
        slide_in_left = AnimationUtils.loadAnimation(getActivity(), R.anim.in_from_left);
        slide_in_right = AnimationUtils.loadAnimation(getActivity(), R.anim.in_from_right);
        slide_out_left = AnimationUtils.loadAnimation(getActivity(), R.anim.out_to_left);
        slide_out_right = AnimationUtils.loadAnimation(getActivity(), R.anim.out_to_right);

        viewFlipper = rootView.findViewById(R.id.view_flipper);
        viewFlipper.setInAnimation(slide_in_right);
        //set the animation for the view leaving th screen
        viewFlipper.setOutAnimation(slide_out_left);
//        loadMoreListView = (ListView) rootView.findViewById(R.id.list_main_category);
//        loadMoreListView.setOnItemClickListener(this);

        callGetClassTestService();

        mLayoutManager = new GridLayoutManager(getActivity(), 6);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (preferenceAdatper.getItemViewType(position) > 0) {
                    return preferenceAdatper.getItemViewType(position);
                } else {
                    return 4;
                }
                //return 2;
            }
        });
        mRecyclerView.setLayoutManager(mLayoutManager);

        searchView = rootView.findViewById(R.id.search_cat_list);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

//                if (categoryArrayList.contains(query)) {
                    preferenceAdatper.getFilter().filter(query);
//                } else {
//                    Toast.makeText(getActivity(), "No Match found", Toast.LENGTH_LONG).show();
//                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //    adapter.getFilter().filter(newText);
                return false;
            }
        });
        PreferenceStorage.saveServiceCount(getActivity(), "");
        PreferenceStorage.saveRate(getActivity(), "");

        return rootView;
    }

    public void initiateServices() {

        serviceHelper = new ServiceHelper(getActivity());
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(getActivity());

    }

    public void callGetClassTestService() {

        if (CommonUtils.isNetworkAvailable(getActivity())) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadMob();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(getActivity(), "No Network connection");
        }
    }

    private void loadMob() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(getActivity());
        try {
            jsonObject.put(SkilExConstants.KEY_USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.GET_MAIN_CAT_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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

//    protected void updateListAdapter(ArrayList<Category> categoryArrayList) {
//        this.categoryArrayList.addAll(categoryArrayList);
//        if (categoryListAdapter == null) {
////            categoryListAdapter = new CategoryListAdapter(getActivity(), this.categoryArrayList);
//            loadMoreListView.setAdapter(categoryListAdapter);
//        } else {
//            categoryListAdapter.notifyDataSetChanged();
//        }
//    }

    @Override
    public void onResponse(final JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                JSONArray getData = response.getJSONArray("categories");
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Category>>() {
                }.getType();
                categoryArrayList = (ArrayList<Category>) gson.fromJson(getData.toString(), listType);
                preferenceAdatper = new PreferenceListAdapter(getActivity(), categoryArrayList, HomeFragment.this);
                mRecyclerView.setAdapter(preferenceAdatper);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("ajazFilterresponse : ", response.toString());

            mHandler.post(new Runnable() {
                @Override
                public void run() {


//                    Gson gson = new Gson();
//                    CategoryList categoryList = gson.fromJson(response.toString(), CategoryList.class);
//                    if (categoryList.getCategoryArrayList() != null && categoryList.getCategoryArrayList().size() > 0) {
//                        totalCount = categoryList.getCount();
//                        isLoadingForFirstTime = false;
//                        updateListAdapter(categoryList.getCategoryArrayList());
//                    }
//                    else {
//                        if (categoryArrayList != null) {
//                            categoryArrayList.clear();
//                            updateListAdapter(categoryList.getCategoryArrayList());
//                        }
//                    }
                }
            });
        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onItemClick(View view, int position) {
        d(TAG, "onEvent list item click" + position);
        Category category = null;
        if ((categoryListAdapter != null) && (categoryListAdapter.ismSearching())) {
            d(TAG, "while searching");
            int actualindex = categoryListAdapter.getActualEventPos(position);
            d(TAG, "actual index" + actualindex);
            category = categoryArrayList.get(actualindex);
        } else {
            category = categoryArrayList.get(position);
        }
        Intent intent = new Intent(getActivity(), SubCategoryActivity.class);
        intent.putExtra("cat", category);
        startActivity(intent);
    }
}
