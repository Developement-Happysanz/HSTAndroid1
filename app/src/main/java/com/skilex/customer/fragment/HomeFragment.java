package com.skilex.customer.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skilex.customer.R;
import com.skilex.customer.activity.MainActivity;
import com.skilex.customer.activity.SearchResultActivity;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.util.Log.d;

public class HomeFragment extends Fragment implements IServiceListener, DialogClickListener, PreferenceListAdapter.OnItemClickListener {

    private static final String TAG = HomeFragment.class.getName();
    Context context;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private Handler mHandler = new Handler();
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    private ArrayList<Category> categoryArrayList;
    private CategoryListAdapter categoryListAdapter;
    ListView loadMoreListView;
    Category category;
    private PreferenceListAdapter preferenceAdatper;
    private GridLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private SearchView searchView;
    private Animation slide_in_left, slide_in_right, slide_out_left, slide_out_right;
    private View rootView;
    private ViewFlipper viewFlipper;
    private String res = "";
    private ArrayList<String> imgUrl = new ArrayList<>();
    private String id = "";
    private Intent intent;

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
        if (PreferenceStorage.getLang(rootView.getContext()).equalsIgnoreCase("tamil")) {
            searchView.setQueryHint("சேவை தேடல்");
        } else {
            searchView.setQueryHint("Search for services");
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

//                if (categoryArrayList.contains(query)) {
//                    preferenceAdatper.getFilter().filter(query);
//                } else {
//                    Toast.makeText(getActivity(), "No Match found", Toast.LENGTH_LONG).show();
//                }
                if (query != null) {
                    makeSearch(query);
                }

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
        loadMob();

        return rootView;
    }

    private void makeSearch(String eventname) {
        PreferenceStorage.setSearchFor(getActivity(), eventname);
        startActivity(new Intent(getActivity(), SearchResultActivity.class));
    }

    public void initiateServices() {

        serviceHelper = new ServiceHelper(getActivity());
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(getActivity());

    }

    public void getBannerImg() {
        /*if(eventsListAdapter != null){
            eventsListAdapter.clearSearchFlag();
        }*/

//        if (CommonUtils.isNetworkAvailable(getActivity())) {
        res = "bannerImg";
        JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserId(getActivity()));
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.GET_BANNER_IMAGES;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
//        } else {
//            AlertDialogHelper.showSimpleAlertDialog(getActivity(), getString(R.string.error_no_net));
//        }

    }

    public void callGetClassTestService() {

//        if (CommonUtils.isNetworkAvailable(getActivity())) {
//            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        loadMob();
//        } else {
//            AlertDialogHelper.showSimpleAlertDialog(getActivity(), "No Network connection");
//        }
    }

    private void loadMob() {
        res = "category";
        JSONObject jsonObject = new JSONObject();
        id = PreferenceStorage.getUserId(getActivity());

        try {
            jsonObject.put(SkilExConstants.KEY_USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.KEY_APP_VERSION, "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.GET_MAIN_CAT_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private boolean validateSignInResponse(JSONObject response) {
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

                        if (PreferenceStorage.getLang(rootView.getContext()).equalsIgnoreCase("tamil")) {
                            AlertDialogHelper.showSimpleAlertDialog(rootView.getContext(), msg_ta);
                        } else {
                            AlertDialogHelper.showSimpleAlertDialog(rootView.getContext(), msg_en);
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
        if (validateSignInResponse(response)) {
            try {
                if (res.equalsIgnoreCase("bannerImg")) {
                    JSONArray imgdata = response.getJSONArray("banners");
                    for (int i = 0; i < imgdata.length(); i++) {
                        imgUrl.add(imgdata.getJSONObject(i).getString("banner_img"));
                    }
                    for (int i = 0; i < imgUrl.size(); i++) {
                        // create dynamic image view and add them to ViewFlipper
                        setImageInFlipr(imgUrl.get(i));
                    }

                } else if (res.equalsIgnoreCase("category")) {
                    JSONArray getData = response.getJSONArray("categories");
                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<Category>>() {
                    }.getType();
                    categoryArrayList = (ArrayList<Category>) gson.fromJson(getData.toString(), listType);
                    preferenceAdatper = new PreferenceListAdapter(getActivity(), categoryArrayList, HomeFragment.this);
                    mRecyclerView.setAdapter(preferenceAdatper);
                    clearCart();

                } else if (res.equalsIgnoreCase("clear")) {
                    PreferenceStorage.saveServiceCount(rootView.getContext(), "");
                    PreferenceStorage.saveRate(rootView.getContext(), "");
                    PreferenceStorage.savePurchaseStatus(rootView.getContext(), false);
                    getBannerImg();

                }
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
        intent = new Intent(getActivity(), SubCategoryActivity.class);
        intent.putExtra("cat", category);
        startActivity(intent);

    }

    private void setImageInFlipr(String imgUrl) {
        ImageView image = new ImageView(rootView.getContext());
        Picasso.get().load(imgUrl).into(image);
        viewFlipper.addView(image);
    }

    private void clearCart() {
        res = "clear";
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

}
