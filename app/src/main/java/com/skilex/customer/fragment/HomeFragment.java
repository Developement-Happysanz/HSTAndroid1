package com.skilex.customer.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skilex.customer.R;
import com.skilex.customer.activity.MainActivity;
import com.skilex.customer.activity.SearchResultActivity;
import com.skilex.customer.activity.ServiceDetailActivity;
import com.skilex.customer.activity.SubCategoryActivity;
import com.skilex.customer.adapter.CategoryListAdapter;
import com.skilex.customer.adapter.PreferenceListAdapter;
import com.skilex.customer.adapter.TrendingServiceListAdapter;
import com.skilex.customer.bean.support.Category;
import com.skilex.customer.bean.support.TrendingServices;
import com.skilex.customer.bean.support.TrendingServicesList;
import com.skilex.customer.helper.AlertDialogHelper;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.CommonUtils;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;
import com.skilex.customer.utils.SkilExValidator;
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
    private ArrayList<TrendingServices> trendingArrayList = new ArrayList<>();
    TrendingServicesList trendingServicesArrayList;
    private TrendingServiceListAdapter trendingServiceListAdapter;
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
    private LinearLayout layout_all;

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
//        mRecyclerView1 = (RecyclerView) rootView.findViewById(R.id.listView_trends);
        layout_all = (LinearLayout) rootView.findViewById(R.id.layout_all);

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

//        mRecyclerView1.setLayoutManager(layoutManager);

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
        getTrendSerives();

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

        try {
            jsonObject.put(SkilExConstants.KEY_USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.KEY_APP_VERSION, "1");

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

                } else if (res.equalsIgnoreCase("trend")) {
//                    JSONArray getData = response.getJSONArray("services");
//                    Gson gson = new Gson();
//                    Type listType = new TypeToken<ArrayList<TrendingServices>>() {
//                    }.getType();
//                    trendingServicesArrayList = (ArrayList<TrendingServices>) gson.fromJson(getData.toString(), listType);
//                    trendingServiceListAdapter = new TrendingServiceListAdapter(getActivity(), trendingServicesArrayList, HomeFragment.this);
//                    mRecyclerView1.setAdapter(trendingServiceListAdapter);

                    Gson gson = new Gson();
                    trendingServicesArrayList = gson.fromJson(response.toString(), TrendingServicesList.class);
                    if (trendingServicesArrayList.getserviceArrayList() != null && trendingServicesArrayList.getserviceArrayList().size() > 0) {
                        int totalCount = trendingServicesArrayList.getCount();
//                    this.serviceHistoryArrayList.addAll(ongoingServiceList.getserviceArrayList());
                        boolean isLoadingForFirstTime = false;
//                        updateListAdapter(serviceHistoryList.getFeedbackArrayList());
                        loadMembersList(trendingServicesArrayList.getserviceArrayList().size());
                    } else {
                        if (trendingArrayList != null) {
                            trendingArrayList.clear();
//                            updateListAdapter(serviceHistoryList.getFeedbackArrayList());
                            loadMembersList(trendingServicesArrayList.getserviceArrayList().size());
                        }
                    }

                    loadMob();

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

//    @Override
//    public void onItemCslick(View view, int position) {
//        d(TAG, "onEvent list item click" + position);
//        TrendingServices category = null;
////        if ((trendingServiceListAdapter != null) && (trendingServiceListAdapter.ismSearching())) {
////            d(TAG, "while searching");
////            int actualindex = trendingServiceListAdapter.getActualEventPos(position);
////            d(TAG, "actual index" + actualindex);
////            category = trendingServicesArrayList.get(actualindex);
////        } else {
////        }
//        category = trendingServicesArrayList.get(position);
//        intent = new Intent(getActivity(), ServiceDetailActivity.class);
//        intent.putExtra("cat", category);
//        intent.putExtra("page", "category");
//        startActivity(intent);
//
//    }

    private void setImageInFlipr(String imgUrl) {
        ImageView image = new ImageView(rootView.getContext());
        Picasso.get().load(imgUrl).into(image);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
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

    private void getTrendSerives() {
        res = "trend";
        JSONObject jsonObject = new JSONObject();
        id = PreferenceStorage.getUserId(getActivity());
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.GET_TREND_SERIVES;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void loadMembersList(int memberCount) {

        try {

            for (int c1 = 0; c1 < memberCount; c1++) {
                final int aa = c1;
                RelativeLayout maincell = new RelativeLayout(getActivity());
//                cell.setLayoutParams(new RelativeLayout.LayoutParams(300, 300));
                maincell.setPadding(10, 10, 10, 10);
                maincell.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_history));

                RelativeLayout cell = new RelativeLayout(getActivity());
//                cell.setLayoutParams(new RelativeLayout.LayoutParams(300, 300));
                cell.setPadding(0, 0, 0, 0);
                cell.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shadow_round));

                RelativeLayout.LayoutParams cellParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
                cellParams.setMargins(10, 10, 10, 10);
                cell.setLayoutParams(cellParams);
                cell.setElevation(10.0f);


                RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(400, 200);
                paramsImageView.setMargins(0, 0, 0, 0);
                paramsImageView.addRule(RelativeLayout.CENTER_HORIZONTAL);

                RelativeLayout.LayoutParams paramsTextView = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                paramsTextView.setMargins(10, 10, 0, 0);
                paramsTextView.addRule(RelativeLayout.BELOW, R.id.trend_img);

//                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(4, ViewGroup.LayoutParams.MATCH_PARENT);
//                params2.setMargins(0, 80, 10, 80);
//                params2.addRule(RelativeLayout.ALIGN_PARENT_END);
//                params2.addRule(RelativeLayout.CENTER_VERTICAL);


                TextView line1 = new TextView(getActivity());
                line1.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                String name = "";
                if (PreferenceStorage.getLang(getActivity()).equalsIgnoreCase("tamil")) {
                    name = trendingServicesArrayList.getserviceArrayList().get(c1).getservice_ta_name();
                } else {
                    name = trendingServicesArrayList.getserviceArrayList().get(c1).getservice_name();
                }

                line1.setText(name);

                line1.setId(R.id.trend_name);
                line1.requestFocusFromTouch();
                line1.setGravity(Gravity.CENTER_VERTICAL);
                line1.setTextSize(14.0f);
                line1.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                line1.setLayoutParams(paramsTextView);

                ImageView line2 = new ImageView(getActivity());
//                line2.setId(R.id.trend_img);
                line2.setLayoutParams(paramsImageView);
                line2.setScaleType(ImageView.ScaleType.FIT_XY);
                if (SkilExValidator.checkNullString(trendingServicesArrayList.getserviceArrayList().get(c1).getservice_pic_url())) {
                    Picasso.get().load(trendingServicesArrayList.getserviceArrayList().get(c1).getservice_pic_url()).into(line2);
                } else {
                    line2.setImageResource(R.drawable.banner_img_sample);
                }
//                line2.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shadow_round));
                line2.setPadding(0, 0, 0, 0);

//                TextView line3 = new TextView(getActivity());
//                line3.setLayoutParams(params2);
//                line3.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey));

                CardView cardView = new CardView(getActivity());
                cardView.setRadius(5.0f);
                cardView.addView(line2);
                cardView.setLayoutParams(paramsImageView);
                cardView.setId(R.id.trend_img);

                cell.addView(line1);
                cell.addView(cardView);
//                cell.addView(line3);
//                cell.addView(border);
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TrendingServices category = null;
                        category = trendingServicesArrayList.getserviceArrayList().get(aa);
                        intent = new Intent(getActivity(), ServiceDetailActivity.class);
                        intent.putExtra("cat", category);
                        intent.putExtra("page", "category");
                        startActivity(intent);
                    }
                });
                maincell.addView(cell);
                layout_all.addView(maincell);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
