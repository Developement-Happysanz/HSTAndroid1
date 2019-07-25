package com.skilex.customer.activity;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.skilex.customer.R;
import com.skilex.customer.bean.support.StoreTimeSlot;
import com.skilex.customer.bean.support.SubCategoryList;
import com.skilex.customer.helper.AlertDialogHelper;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.CommonUtils;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;
import com.skilex.customer.utils.SkilExValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.util.Log.d;

public class AddressActivity extends FragmentActivity implements OnMapReadyCallback, IServiceListener, DialogClickListener {
    private static final String TAG = SubCategoryActivity.class.getName();

    LatLng position, myPosition;
    MapView mapView;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    Geocoder geocoder;
    private List<Address> addresses;
    EditText customerAddress, customerName, customerNumber, serviceTimeSlot, serviceDate;
    Button bookNow;
    final Calendar myCalendar = Calendar.getInstance();
    private String res = "";

    ArrayAdapter<StoreTimeSlot> timeSlotAdapter = null;
    ArrayList<StoreTimeSlot> timeList;
    String timeSlotId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initializeThings();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.map);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            showAlert();
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        myPosition = new LatLng(latitude, longitude);
    }

    private void initializeThings() {
        customerAddress = (EditText) findViewById(R.id.customer_address);
        customerName = (EditText) findViewById(R.id.customer_name);
        customerNumber = (EditText) findViewById(R.id.customer_phone);
        serviceDate = (EditText) findViewById(R.id.date);
        serviceDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddressActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        serviceTimeSlot = (EditText) findViewById(R.id.time_slot);
        serviceTimeSlot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimeSlotList();
            }
        });
        bookNow = (Button) findViewById(R.id.book_now);
        bookNow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setVals();
            }
        });
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1, date2;
            try {
                date1 = sdf.parse("" + year + "-" + monthOfYear + "-" + dayOfMonth);
                date2 = sdf.parse("" + Calendar.getInstance().get(Calendar.YEAR) + "-" + Calendar.getInstance().get(Calendar.MONTH) +
                        "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                if (date1.before(date2)) {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(AddressActivity.this);
                    alertDialogBuilder.setTitle("Date");
                    alertDialogBuilder.setMessage("The minimum date is today.");
                    alertDialogBuilder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialogBuilder.show();
                } else {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    };


    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        serviceDate.setText(sdf.format(myCalendar.getTime()));
        callTimeSlotService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        int height = 48;
        int width = 48;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_current_location);
        Bitmap b = bitmapdraw.getBitmap();
        final Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//        LatLng latLng = new LatLng(11.0168, 76.9558);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 12));
        googleMap.addMarker(new MarkerOptions().position(myPosition).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
//                allPoints.add(point);
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(myPosition).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                googleMap.addMarker(new MarkerOptions().position(point));
                position = point;

                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                if (position != null) {
                    try {
                        addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    customerAddress.setText(address);
                }

//                String city = addresses.get(0).getLocality();
//                String state = addresses.get(0).getAdminArea();
//                String country = addresses.get(0).getCountryName();
//                String postalCode = addresses.get(0).getPostalCode();
//                String knownName = addresses.get(0).getFeatureName();
            }
        });
    }

    public void callTimeSlotService() {

        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            loadSlot();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, "No Network connection");
        }
    }

    private void showAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_location_permission)
                .setMessage(R.string.text_location_permission)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(AddressActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                99);
                    }
                })
                .create()
                .show();
    }

    private void loadSlot() {
        res = "time";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        String date = "";
        PreferenceStorage.getUserId(this);
        date = serviceDate.getText().toString();
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_DATE, date);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = SkilExConstants.BUILD_URL + SkilExConstants.GET_TIME_SLOT;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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
                if (res.equalsIgnoreCase("time")) {
                    JSONArray getData = response.getJSONArray("service_time_slot");
                    int getLength = getData.length();
                    String timeId = "";
                    String timeName = "";
                    timeList = new ArrayList<>();

                    for (int i = 0; i < getLength; i++) {

                        timeId = getData.getJSONObject(i).getString("timeslot_id");
                        timeName = getData.getJSONObject(i).getString("time_range");
                        timeList.add(new StoreTimeSlot(timeId, timeName));
                    }

                    timeSlotAdapter = new ArrayAdapter<StoreTimeSlot>(getApplicationContext(), R.layout.time_slot_layout, R.id.time_slot_range, timeList) { // The third parameter works around ugly Android legacy. http://stackoverflow.com/a/18529511/145173
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            Log.d(TAG, "getview called" + position);
                            View view = getLayoutInflater().inflate(R.layout.time_slot_layout, parent, false);
                            TextView gendername = (TextView) view.findViewById(R.id.time_slot_range);
                            gendername.setText(timeList.get(position).getTimeName());

                            // ... Fill in other views ...
                            return view;
                        }
                    };


                } else if (res.equalsIgnoreCase("send")){
                    JSONObject getData = response.getJSONObject("service_details");
                    PreferenceStorage.saveOrderId(this, getData.getString("order_id"));
                    if(getData.getString("advance_payment_status").equalsIgnoreCase("NA")) {
                        Toast.makeText(this, "Order Placed", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(this, AssignProviderActivity.class);
                        startActivity(i);
                        finish();
                    } else if (getData.getString("advance_payment_status").equalsIgnoreCase("N")) {
//                        getData.getString("advance_amount");
//                        i.putExtra("advpay")
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

    private void setVals() {

        String id = "";
        id = PreferenceStorage.getUserId(this);

        String oldDate = "";
        Date date = null;
        String newDate = "";
        oldDate = serviceDate.getText().toString();
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        String format = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            date = formatter.parse(oldDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        newDate = sdf.format(date);

        String latlng = "";
        latlng = position.latitude + "," +position.longitude;
        sendVals(id,latlng,newDate);

    }

    private void sendVals(String id, String latLng, String newDate) {
        if (validateFields()) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            res = "send";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
                jsonObject.put(SkilExConstants.CONTACT_PERSON, customerName.getText().toString());
                jsonObject.put(SkilExConstants.CONTACT_PERSON_NUMBER, customerNumber.getText().toString());
                jsonObject.put(SkilExConstants.SERVICE_LATLNG, latLng);
                jsonObject.put(SkilExConstants.SERVICE_LOCATION, addresses.get(0).getSubLocality());
                jsonObject.put(SkilExConstants.SERVICE_ADDRESS, customerAddress.getText().toString());
                jsonObject.put(SkilExConstants.ORDER_DATE, newDate);
                jsonObject.put(SkilExConstants.ORDER_TIMESLOT, timeSlotId);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = SkilExConstants.BUILD_URL + SkilExConstants.PROCEED_TO_BOOK;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
        }
    }

    private boolean validateFields() {
        if (!SkilExValidator.checkMobileNumLength(this.customerNumber.getText().toString().trim())) {
            customerNumber.setError(getString(R.string.error_number));
            requestFocus(customerNumber);
            return false;
        }
        if (!SkilExValidator.checkNullString(this.customerNumber.getText().toString().trim())) {
            customerNumber.setError(getString(R.string.empty_entry));
            requestFocus(customerNumber);
            return false;
        }
        if (!SkilExValidator.checkNullString(this.customerName.getText().toString().trim())) {
            customerName.setError(getString(R.string.empty_entry));
            requestFocus(customerName);
            return false;
        }
        if (!SkilExValidator.checkNullString(this.customerAddress.getText().toString().trim())) {
            customerAddress.setError(getString(R.string.empty_entry));
            requestFocus(customerAddress);
            return false;
        }
        if (!SkilExValidator.checkNullString(this.serviceTimeSlot.getText().toString().trim())) {
            serviceTimeSlot.setError(getString(R.string.empty_entry));
            requestFocus(serviceTimeSlot);
            return false;
        }
        if (!SkilExValidator.checkNullString(this.serviceDate.getText().toString().trim())) {
            customerAddress.setError(getString(R.string.empty_entry));
            requestFocus(customerAddress);
            return false;
        } else {
            return true;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void showTimeSlotList() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.header_layout, null);
        TextView header = (TextView) view.findViewById(R.id.header);
        header.setText("Select City");
        builderSingle.setCustomTitle(view);

        builderSingle.setAdapter(timeSlotAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StoreTimeSlot cty = timeList.get(which);
                        serviceTimeSlot.setText(cty.getTimeName());
                        timeSlotId = cty.getTimeId();
                    }
                });
        builderSingle.show();
    }

}