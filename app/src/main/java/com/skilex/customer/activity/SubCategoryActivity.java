package com.skilex.customer.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.skilex.customer.R;
import com.skilex.customer.adapter.MainServiceListAdapter;
import com.skilex.customer.adapter.SubCategoryTabAdapter;
import com.skilex.customer.bean.support.Category;
import com.skilex.customer.bean.support.SubCategory;
import com.skilex.customer.bean.support.SubCategoryList;
import com.skilex.customer.helper.AlertDialogHelper;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.CommonUtils;
import com.skilex.customer.utils.SkilExConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class SubCategoryActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = SubCategoryActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    Handler mHandler = new Handler();
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    ArrayList<SubCategory> categoryArrayList;
    MainServiceListAdapter categoryListAdapter;
    ListView loadMoreListView;
    Category category;
    TabLayout tab;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        categoryArrayList = new ArrayList<>();

        category = (Category) getIntent().getSerializableExtra("cat");
//        loadMoreListView = (LinearLayout) findViewById(R.id.layout_member_list);
        loadMoreListView = (ListView) findViewById(R.id.listView_sub_categories);
        loadMoreListView.setOnItemClickListener(this);
        callGetSubCategoryService();

        tab = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onResponse(final JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                JSONArray getData = response.getJSONArray("sub_categories");
//                loadMembersList(getData.length());
                Gson gson = new Gson();
                SubCategoryList subCategoryList = gson.fromJson(response.toString(), SubCategoryList.class);
                if (subCategoryList.getCategoryArrayList() != null && subCategoryList.getCategoryArrayList().size() > 0) {
                    totalCount = subCategoryList.getCount();
                    isLoadingForFirstTime = false;
                    updateListAdapter(subCategoryList.getCategoryArrayList());
                } else {
                    if (categoryArrayList != null) {
                        categoryArrayList.clear();
                        updateListAdapter(subCategoryList.getCategoryArrayList());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

    @Override
    public void onError(String error) {

    }

    public void callGetSubCategoryService() {
//        if (classTestArrayList != null)
//            classTestArrayList.clear();

        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadCat();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private void loadCat() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = category.getCat_id();
        try {
            jsonObject.put(SkilExConstants.MAIN_CATEGORY_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.GET_SUB_CAT_LIST;
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

    protected void updateListAdapter(ArrayList<SubCategory> categoryArrayList) {
        this.categoryArrayList.addAll(categoryArrayList);
        if (categoryListAdapter == null) {
            categoryListAdapter = new MainServiceListAdapter(this, this.categoryArrayList);
            loadMoreListView.setAdapter(categoryListAdapter);
        } else {
            categoryListAdapter.notifyDataSetChanged();
        }
    }

    private void loadMembersList(int memberCount) {

        try {

            for (int c1 = 0; c1 < memberCount; c1++) {

                RelativeLayout.LayoutParams paramsMain = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 120);
                paramsMain.setMargins(40, 20, 40, 20);

                FrameLayout.LayoutParams paramsMain1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 120);
                paramsMain1.setMargins(40, 20, 40, 20);

                FrameLayout maincell = new FrameLayout(this);
                maincell.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                maincell.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                maincell.setElevation(12.0f);
                maincell.setLayoutParams(paramsMain1);

                RelativeLayout cell = new RelativeLayout(this);
                cell.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 120));
                cell.setPadding(0, 0, 0, 0);
                cell.setLayoutParams(paramsMain);


                RelativeLayout.LayoutParams paramsCategoryName = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsCategoryName.setMargins(320, 0, 0, 0);
                paramsCategoryName.addRule(RelativeLayout.CENTER_IN_PARENT);
                paramsCategoryName.addRule(RelativeLayout.LEFT_OF, R.id.add_to_list);


                RelativeLayout.LayoutParams paramsAddToList = new RelativeLayout.LayoutParams(80, ViewGroup.LayoutParams.MATCH_PARENT);
                paramsAddToList.setMargins(0, 0, 0, 0);
                paramsAddToList.addRule(RelativeLayout.ALIGN_PARENT_END);

                FrameLayout.LayoutParams paramsCategoryImage = new FrameLayout.LayoutParams(180, 100);
                paramsCategoryImage.setMargins(40, 0, 0, 0);
                paramsCategoryImage.gravity = Gravity.CENTER_VERTICAL;

                TextView categoryName = new TextView(this);
                categoryName.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                categoryName.setText("samplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesamplesample");


                categoryName.setId(R.id.sub_category_name);
                categoryName.requestFocusFromTouch();
                categoryName.setTextSize(16.0f);
                categoryName.setBackgroundColor(Color.parseColor("#FFFFFF"));
                categoryName.setSingleLine(true);
                categoryName.setTextColor(Color.parseColor("#000000"));
                categoryName.setLayoutParams(paramsCategoryName);

                ImageView categoryImage = new ImageView(this);
                categoryImage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                categoryImage.setImageDrawable(getResources().getDrawable(R.drawable.sample_test, getApplicationContext().getTheme()));

                categoryImage.setId(R.id.sub_category_image);
                categoryImage.setBackgroundColor(Color.parseColor("#FFFFFF"));
                categoryImage.setLayoutParams(paramsCategoryImage);

                final ImageView addToList = new ImageView(this);
                addToList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));


                addToList.setId(R.id.add_to_list);
                addToList.setBackgroundColor(Color.parseColor("#3F6CB4"));
                addToList.setImageDrawable(getResources().getDrawable(R.drawable.ic_control_point_black_24dp, getApplicationContext().getTheme()));

                addToList.requestFocusFromTouch();
                addToList.setPressed(true);
//                if (gnStaffList.getGroups().get(c1).getStatus().equalsIgnoreCase("1")) {
//                    addToList.setImageResource(R.drawable.ic_select);
//                } else {
//                    addToList.setImageResource(R.drawable.ic_de_select);
//                }
                addToList.setPadding(10, 10, 10, 10);
                addToList.setLayoutParams(paramsAddToList);
                final int finalC = c1;
                addToList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (addToList.getDrawable().equals(R.drawable.ic_control_point_black_24dp)) {
                            addToList.setImageDrawable(getResources().getDrawable(R.drawable.ic_completed, getApplicationContext().getTheme()));
                            addToList.setBackgroundColor(Color.parseColor("#39B54A"));
                        } else {
                            addToList.setBackgroundColor(Color.parseColor("#3F6CB4"));
                            addToList.setImageDrawable(getResources().getDrawable(R.drawable.ic_control_point_black_24dp, getApplicationContext().getTheme()));
                        }
                    }
                });

//                TextView border = new TextView(this);
//                border.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
//                border.setHeight(1);
//                border.setBackgroundColor(Color.BLACK);

                cell.addView(categoryName);
                cell.addView(addToList);
//                cell.addView(border);

                maincell.addView(cell);
                maincell.addView(categoryImage);
                loadMoreListView.addView(maincell);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialiseTabs() {
        for (int k = 0; k < 10; k++) {
            tab.addTab(tab.newTab().setText("" + k));
        }
        SubCategoryTabAdapter adapter = new SubCategoryTabAdapter
                (getSupportFragmentManager(), tab.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });
//        tab.removeOnTabSelectedListener(TabLayout.OnTabSelectedListener);
//Bonus Code : If your tab layout has more than 2 tabs then tab will scroll other wise they will take whole width of the screen
        if (tab.getTabCount() == 2) {
            tab.setTabMode(TabLayout.
                    MODE_FIXED);
        } else {
            tab.setTabMode(TabLayout.
                    MODE_SCROLLABLE);
        }
    }

}