package com.skilex.customer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skilex.customer.R;
import com.skilex.customer.adapter.CartServiceDeleteListAdapter;
import com.skilex.customer.adapter.SwipeToDeleteCallback;
import com.skilex.customer.bean.support.CartService;
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

public class BookingSummaryAcivity extends AppCompatActivity implements IServiceListener, DialogClickListener, CartServiceDeleteListAdapter.OnItemClickListener {

    private static final String TAG = BookingSummaryAcivity.class.getName();
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ArrayList<CartService> serviceArrayList = new ArrayList<>();
    CartServiceDeleteListAdapter serviceListAdapter;
    //    ListView loadMoreListView;
    private RecyclerView mRecyclerView;
    Category category;

    TextView advanceAmount, totalCost;
    String res = "";
    Button confrm;
    private Handler handler = new Handler();

    //To start timer
    public void startTimer() {
        handler.postDelayed(new Runnable() {
            public void run() {
                if (!PreferenceStorage.getPurchaseStatus(getApplicationContext())) {
                    handler.removeCallbacksAndMessages(null);
                    showExit();
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    //To start timer
    public void startcartTimer() {
        handler.postDelayed(new Runnable() {
            public void run() {
                if (PreferenceStorage.getCartStatus(getApplicationContext())) {
                    PreferenceStorage.saveCartStatus(getApplicationContext(),false);
                    loadCart();
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_summary);
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        startTimer();
        startcartTimer();

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(BookingSummaryAcivity.this);
                alertDialogBuilder.setTitle(R.string.cart);
                alertDialogBuilder.setMessage(R.string.cart_clear);
                alertDialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        clearCart();
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();

            }
        });

        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(BookingSummaryAcivity.this);
                alertDialogBuilder.setTitle(R.string.cart);
                alertDialogBuilder.setMessage(R.string.cart_clear);
                alertDialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        clearCart();
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
            }
        });

        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PreferenceStorage.getPurchaseStatus(getApplicationContext())) {
                    handler.removeCallbacksAndMessages(null);
                    Intent i = new Intent(getApplicationContext(), AddressActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    showExit();
                }
            }
        });

//        loadMoreListView = findViewById(R.id.listSumService);
        mRecyclerView = findViewById(R.id.listSumService);
        advanceAmount = (TextView) findViewById(R.id.additional_cost);
        totalCost = (TextView) findViewById(R.id.total_cost);
//        confrm = (Button) findViewById(R.id.confirm);
//        confrm.setOnClickListener(this);
        category = (Category) getIntent().getSerializableExtra("cat");

        callGetSubCategoryService();
    }

    private void showExit() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(BookingSummaryAcivity.this);
        alertDialogBuilder.setTitle(R.string.cart);
        alertDialogBuilder.setMessage("All orders cancelled");
        alertDialogBuilder.setPositiveButton(R.string.alert_button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
                handler.removeCallbacksAndMessages(null);
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.alert_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                handler.removeCallbacksAndMessages(null);
            }
        });
        alertDialogBuilder.show();
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
                if (res.equalsIgnoreCase("clear")) {
                    handler.removeCallbacksAndMessages(null);
                    PreferenceStorage.saveServiceCount(this, "");
                    PreferenceStorage.saveRate(this, "");
                    PreferenceStorage.savePurchaseStatus(this, false);
                    Intent i = new Intent(this, SubCategoryActivity.class);
                    i.putExtra("cat", category);
                    startActivity(i);
                    finish();
                } else {
                    JSONArray getData = response.getJSONArray("cart_list");
//                    loadMembersList(getData.length());
                    Gson gson = new Gson();
//                    CartServiceList serviceList = gson.fromJson(response.toString(), CartServiceList.class);
//                    if (serviceList.getserviceArrayList() != null && serviceList.getserviceArrayList().size() > 0) {
//                        totalCount = serviceList.getCount();
//                        this.categoryArrayList.addAll(subCategoryList.getCategoryArrayList());
//                        isLoadingForFirstTime = false;
//                        updateListAdapter(serviceList.getserviceArrayList());
//                    } else {
//                        if (serviceArrayList != null) {
//                            serviceArrayList.clear();
//                            updateListAdapter(serviceList.getserviceArrayList());
//                        }
//                    }
                    Type listType = new TypeToken<ArrayList<CartService>>() {
                    }.getType();
                    serviceArrayList = (ArrayList<CartService>) gson.fromJson(getData.toString(), listType);
                    serviceListAdapter = new CartServiceDeleteListAdapter(this, serviceArrayList, BookingSummaryAcivity.this);
//                    mRecyclerView.setAdapter(serviceListAdapter);
                    setUpRecyclerView();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

//    protected void updateListAdapter(ArrayList<CartService> serviceArrayList) {
//        this.serviceArrayList.clear();
//        this.serviceArrayList.addAll(serviceArrayList);
//        if (serviceListAdapter == null) {
//            serviceListAdapter = new GeneralServiceListAdapter(this, this.serviceArrayList);
//            loadMoreListView.setAdapter(serviceListAdapter);
//            advanceAmount.setText("" + serviceArrayList.get(0).getAdvance_amount());
//            ArrayList<Integer> a = new ArrayList<>();
//            for (int i = 0; i < serviceArrayList.size(); i++) {
////                a.add(Integer.parseInt(serviceArrayList.get(i).getRate_card()));
//            }
//            int sum = 0;
//            for (Integer d : a) {
//                sum += d;
//            }
//            totalCost.setText("" + sum);
//        } else {
//            serviceListAdapter.notifyDataSetChanged();
//        }
//    }

    @Override
    public void onError(String error) {

    }

    public void callGetSubCategoryService() {
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadCart();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, String.valueOf(R.string.error_no_net));
        }
    }

    private void loadCart() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.CART_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private void clearCart() {
        res = "clear";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.CLEAR_CART;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void setUpRecyclerView() {
        mRecyclerView.setAdapter(serviceListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(serviceListAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        if(serviceArrayList.get(0).getAdvance_amount().isEmpty()){
            advanceAmount.setText("₹ 0");
        } else {
            advanceAmount.setText("₹ " + serviceArrayList.get(0).getAdvance_amount());
        }
        ArrayList<Double> a = new ArrayList<>();
        for (int i = 0; i < serviceArrayList.size(); i++) {
            a.add(Double.parseDouble(serviceArrayList.get(i).getRate_card()));
        }
        int sum = 0;
        for (Double d : a) {
            sum += d;
        }
        totalCost.setText("₹ " + sum);
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
