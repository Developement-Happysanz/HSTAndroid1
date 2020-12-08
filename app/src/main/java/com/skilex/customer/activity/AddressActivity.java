package com.skilex.customer.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.skilex.customer.R;
import com.skilex.customer.bean.support.StoreTimeSlot;
import com.skilex.customer.ccavenue.activity.InitialScreenActivity;
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

public class AddressActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback, IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = SubCategoryActivity.class.getName();

    LatLng position, myPosition;
    MapView mapView;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    Geocoder geocoder;
    private List<Address> addresses;
    EditText customerAddress, customerAreaInfo, customerName, customerNumber, serviceTimeSlot, serviceDate, customerNotes;
    Button bookNow;
    final Calendar myCalendar = Calendar.getInstance();
    private String res = "";

    ArrayAdapter<StoreTimeSlot> timeSlotAdapter = null;
    ArrayList<StoreTimeSlot> timeList;
    String timeSlotId = "";

    private Location location;
    private TextView locationTv;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;

    private DatePickerDialog fromDatePickerDialog;

    private RelativeLayout addressPopup;
    private View clickbait;
    private RadioButton addressOne, addressTwo;
    private Button submitAddress;
    private String addressStringOne, nameOne, contactOne, latlanOne, locationOne;
    private String addressStringTwo, nameTwo, contactTwo, latlanTwo, locationTwo;
    private String selectedLatLan;
    private Boolean radioAddress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_address);

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

        addressAlert();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(new View(this).getWindowToken(), 0);
        return true;
    }

    private void initializeThings() {

        customerAddress = (EditText) findViewById(R.id.customer_address);
        customerAddress.setEnabled(false);
        customerAreaInfo = (EditText) findViewById(R.id.customer_address1);
        customerAreaInfo.setEnabled(false);
        customerName = (EditText) findViewById(R.id.customer_name);
        customerNumber = (EditText) findViewById(R.id.customer_phone);
        serviceDate = (EditText) findViewById(R.id.date);
        serviceDate.setOnClickListener(this);

        addressPopup = (RelativeLayout) findViewById(R.id.address_popup);
        addressPopup.setOnClickListener(this);
        clickbait = (View) findViewById(R.id.clickbait_layout);
        clickbait.setOnClickListener(this);
        addressOne = (RadioButton) findViewById(R.id.address_one);
        addressTwo = (RadioButton) findViewById(R.id.address_two);
        submitAddress = (Button) findViewById(R.id.btn_submit_address);
        submitAddress.setOnClickListener(this);
//        serviceDate.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                new DatePickerDialog(AddressActivity.this, date, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//            }
//        });

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                Calendar newDate = Calendar.getInstance();
//                newDate.set(year, monthOfYear, dayOfMonth);
//                String myFormat = "dd-MM-yyyy"; //In which you need put here
//                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
//
//                serviceDate.setText(sdf.format(myCalendar.getTime()));
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
                serviceTimeSlot.setText("");
                serviceTimeSlot.setHint(R.string.time);
                callTimeSlotService();

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        DatePicker dP = fromDatePickerDialog.getDatePicker();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Date result = cal.getTime();
        dP.setMinDate(System.currentTimeMillis() - 1000);
        dP.setMaxDate(result.getTime());

        serviceTimeSlot = (EditText) findViewById(R.id.time_slot);
        serviceTimeSlot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimeSlotList();
            }
        });
        customerNotes = (EditText) findViewById(R.id.customer_notes);
        bookNow = (Button) findViewById(R.id.book_now);
        bookNow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setVals();
            }
        });

//        LocationManager locationManager = (LocationManager)
//                getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            showAlert();
//            return;
//        }
//        Location location = locationManager.getLastKnownLocation(locationManager
//                .getBestProvider(criteria, false));
//        double latitude = location.getLatitude();
//        double longitude = location.getLongitude();
//        myPosition = new LatLng(latitude, longitude);

        // we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
    }

    private void addressAlert() {
        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        res = "address_list";
        String id = "";
        id = PreferenceStorage.getUserId(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.KEY_CUST_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = SkilExConstants.BUILD_URL + SkilExConstants.ADDRESS_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

    }


    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

//    private void setDateTimeField() {
//        serviceDate.setOnClickListener(this);
//
//        Calendar newCalendar = Calendar.getInstance();
//        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                Calendar newDate = Calendar.getInstance();
//                newDate.set(year, monthOfYear, dayOfMonth);
//                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
//            }
//
//        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//
//    }


    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        serviceDate.setText(sdf.format(myCalendar.getTime()));
        callTimeSlotService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPlayServices()) {
            locationTv.setText("You need to install Google Play Services to use the App properly");
        }
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop location updates
        if (googleApiClient != null && googleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
        mapView.onPause();
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
        mMap = googleMap;
        int height = 48;
        int width = 48;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_current_location);
        Bitmap b = bitmapdraw.getBitmap();
        final Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        LatLng latLng = new LatLng(11.0168, 76.9558);
        LatLng latLng1 = new LatLng(11.010273, 76.987062);
        if (myPosition == null) {
            myPosition = latLng1;
        } else {
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 15));
//        googleMap.addMarker(new MarkerOptions().position(myPosition).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
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
                        addresses = geocoder.getFromLocation(position.latitude, position.longitude, 4); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String address = addresses.get(2).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    customerAddress.setEnabled(true);
                    customerAddress.setText(address);
                    String area = "";
                    if (addresses.get(2).getSubLocality() != null) {
                        area = addresses.get(2).getSubLocality();
                    } else {
                        area = addresses.get(2).getLocality();
                    }
                    customerAreaInfo.setText(area);
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
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private void showAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_location_permission)
                .setMessage(R.string.text_location_permission)
                .setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
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
                if (!status.equalsIgnoreCase("success") && !(res.equalsIgnoreCase("add_address"))) {
//                    String msg = response.getString(SkilExConstants.PARAM_MESSAGE);
                    String msg_en = response.getString(SkilExConstants.PARAM_MESSAGE_ENG);
                    String msg_ta = response.getString(SkilExConstants.PARAM_MESSAGE_TAMIL);
                    d(TAG, "status val" + status + "msg" + msg_en);
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d(TAG, "Show error dialog");
                        if (res.equalsIgnoreCase("address_list")) {
                            showDialog();
                        } else {
                            if (PreferenceStorage.getLang(this).equalsIgnoreCase("tamil")) {
                                AlertDialogHelper.showSimpleAlertDialog(this, msg_ta);
                            } else {
                                AlertDialogHelper.showSimpleAlertDialog(this, msg_en);
                            }
                        }
                    }
                } else {
                    signInSuccess = true;
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


                } else if (res.equalsIgnoreCase("send")) {
                    JSONObject getData = response.getJSONObject("service_details");
                    PreferenceStorage.saveOrderId(this, getData.getString("order_id"));
                    if (getData.getString("advance_payment_status").equalsIgnoreCase("NA")) {
//                        Toast.makeText(this, "Order Placed", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(this, AdvancePaymentActivity.class);
                        startActivity(i);
                        finish();
                    } else if (getData.getString("advance_payment_status").equalsIgnoreCase("N")) {
                        PreferenceStorage.saveAdvanceAmt(this, getData.getString("advance_amount"));
                        Intent i = new Intent(this, InitialScreenActivity.class);
                        i.putExtra("advpay", getData.getString("advance_amount"));
                        i.putExtra("page", "advance_pay");
                        startActivity(i);
                        finish();
                    }
                    addAddress();
                } else if (res.equalsIgnoreCase("address_list")) {
                    JSONArray list = response.getJSONArray("address_list");
                    addressPopup.setVisibility(View.VISIBLE);
                    findViewById(R.id.scroll).setVisibility(View.GONE);
                    switch (list.length()) {
                        case 1:
                            addressOne.setText(list.getJSONObject(0).getString("serv_address") +
                                    "\n" + list.getJSONObject(0).getString("contact_name") +
                                    "\nPhone: " + list.getJSONObject(0).getString("contact_no"));
                            addressOne.setChecked(true);
                            addressStringOne = list.getJSONObject(0).getString("serv_address");
                            nameOne = list.getJSONObject(0).getString("contact_name");
                            contactOne = list.getJSONObject(0).getString("contact_no");
                            latlanOne = list.getJSONObject(0).getString("serv_lat_lon");
                            locationOne = list.getJSONObject(0).getString("serv_loc");
                            addressTwo.setVisibility(View.GONE);
                            break;
                        case 2:
                            addressOne.setText(list.getJSONObject(0).getString("serv_address") +
                                    "\n" + list.getJSONObject(0).getString("contact_name") +
                                    "\nPhone: " + list.getJSONObject(0).getString("contact_no"));
                            addressOne.setChecked(true);
                            addressStringOne = list.getJSONObject(0).getString("serv_address");
                            nameOne = list.getJSONObject(0).getString("contact_name");
                            contactOne = list.getJSONObject(0).getString("contact_no");
                            latlanOne = list.getJSONObject(0).getString("serv_lat_lon");
                            locationOne = list.getJSONObject(0).getString("serv_loc");

                            addressTwo.setText(list.getJSONObject(1).getString("serv_address") +
                                    "\n" + list.getJSONObject(1).getString("contact_name") +
                                    "\nPhone: " + list.getJSONObject(1).getString("contact_no"));

                            addressStringTwo = list.getJSONObject(0).getString("serv_address");
                            nameTwo = list.getJSONObject(0).getString("contact_name");
                            contactTwo = list.getJSONObject(0).getString("contact_no");
                            latlanTwo = list.getJSONObject(0).getString("serv_lat_lon");
                            locationTwo = list.getJSONObject(0).getString("serv_loc");
                            break;
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
        if (validateFields()) {

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
            if (position != null) {
                latlng = position.latitude + "," + position.longitude;
            } else {
                latlng = "";
            }
            if (latlng.isEmpty() || latlng.equalsIgnoreCase(",")) {
                if (customerAddress.getText().toString().isEmpty()) {
                    if (PreferenceStorage.getLang(this).equalsIgnoreCase("tamil")) {
                        AlertDialogHelper.showSimpleAlertDialog(this, "வரைபடத்தில் உங்கள் இருப்பிடத்தைத் தேர்வுசெய்யவும் அல்லது முகவரியை உள்ளிடவும்.");
                    } else {
                        AlertDialogHelper.showSimpleAlertDialog(this, "Please pick your location in map or enter address.");
                    }
                } else if (radioAddress) {
                    position = getLocationFromAddress(customerAddress.getText().toString());
                    latlng = selectedLatLan;

                    if (distance(position.latitude, position.longitude, 11.021238, 76.966356) < 20.000) {
                        sendVals(id, latlng, newDate);
                    } else {
                        AlertDialogHelper.showSimpleAlertDialog(this, "We don't provide this service in your area currently");
                    }
                } else {
                    position = getLocationFromAddress(customerAddress.getText().toString());
                    if (position != null) {
                        latlng = position.latitude + "," + position.longitude;
                    } else {
                        latlng = "";
                    }
                    if (distance(position.latitude, position.longitude, 11.021238, 76.966356) < 20.000) {
                        sendVals(id, latlng, newDate);
                    } else {
                        AlertDialogHelper.showSimpleAlertDialog(this, "We don't provide this service in your area currently");
                    }
                }
            } else {
                if (distance(position.latitude, position.longitude, 11.021238, 76.966356) < 20.000) {
                    sendVals(id, latlng, newDate);
                } else {
                    AlertDialogHelper.showSimpleAlertDialog(this, "We don't provide this service in your area currently");
                }
            }
        }

    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }

    private void sendVals(String id, String latLng, String newDate) {
        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        res = "send";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.CONTACT_PERSON, customerName.getText().toString());
            jsonObject.put(SkilExConstants.CONTACT_PERSON_NUMBER, customerNumber.getText().toString());
            jsonObject.put(SkilExConstants.SERVICE_LATLNG, latLng);
//            if (addresses.isEmpty()) {
//                jsonObject.put(SkilExConstants.SERVICE_LOCATION, "");
//            } else {
            jsonObject.put(SkilExConstants.SERVICE_LOCATION, customerAreaInfo.getText().toString());
//            }
            jsonObject.put(SkilExConstants.SERVICE_ADDRESS, customerAddress.getText().toString());
            jsonObject.put(SkilExConstants.ORDER_DATE, newDate);
            jsonObject.put(SkilExConstants.ORDER_TIMESLOT, timeSlotId);
            jsonObject.put(SkilExConstants.ORDER_NOTES, customerNotes.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = SkilExConstants.BUILD_URL + SkilExConstants.PROCEED_TO_BOOK;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

    }

    private void addAddress() {
        res = "add_address";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.KEY_CUST_ID, PreferenceStorage.getUserId(this));
            jsonObject.put(SkilExConstants.CONTACT_NAME, customerName.getText().toString());
            jsonObject.put(SkilExConstants.CONTACT_NUMBER, customerNumber.getText().toString());
            jsonObject.put(SkilExConstants.SERV_LATLNG, selectedLatLan);
            jsonObject.put(SkilExConstants.SERV_LOCATION, customerAreaInfo.getText().toString());
            jsonObject.put(SkilExConstants.SERV_ADDRESS, customerAddress.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = SkilExConstants.BUILD_URL + SkilExConstants.ADD_ADDRESS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

    }

    private void showDialog() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Location");
        alertDialogBuilder.setMessage(R.string.empty_address);
        alertDialogBuilder.setPositiveButton(R.string.alert_button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
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
//            customerAddress.setError(getString(R.string.empty_address));
//            requestFocus(customerAddress);
            showDialog();
            return false;
        }
        if (!SkilExValidator.checkNullString(this.customerAreaInfo.getText().toString().trim())) {
            customerAreaInfo.setError(getString(R.string.empty_locality));
            requestFocus(customerAreaInfo);
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
        if (PreferenceStorage.getLang(this).equalsIgnoreCase("tamil")) {
            header.setText("நேரத்தைத் தேர்ந்தெடுக்கவும்");
        } else {
            header.setText("Select Time Slot");
        }
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

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            // Logic to handle location object

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            myPosition = new LatLng(latitude, longitude);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startLocationUpdates();

        // Permissions ok, we get last location
//        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//        if (location != null) {
//            locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
//        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            myPosition = new LatLng(latitude, longitude);

                            int height = 48;
                            int width = 48;
                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_current_location);
                            Bitmap b = bitmapdraw.getBitmap();
                            final Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 20));
                            mMap.addMarker(new MarkerOptions().position(myPosition).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

                        }
                    }
                });


//        startLocationUpdates();
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            } else {
                Address location = address.get(0);
                addresses = address;
                location.getLatitude();
                location.getLongitude();
                p1 = new LatLng((location.getLatitude()), (location.getLongitude()));
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return p1;
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

//        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            myPosition = new LatLng(latitude, longitude);

                            int height = 48;
                            int width = 48;
                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_current_location);
                            Bitmap b = bitmapdraw.getBitmap();
                            final Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 20));
                            mMap.addMarker(new MarkerOptions().position(myPosition).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(AddressActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }
    }


    @Override
    public void onClick(View v) {
        if (v == serviceDate) {
            fromDatePickerDialog.show();
        }
        if (v == submitAddress) {
            radioAddress = true;
            findViewById(R.id.scroll).setVisibility(View.VISIBLE);
            addressPopup.setVisibility(View.GONE);
            if (addressOne.isSelected()) {
                customerAddress.setText(addressStringOne);
                customerName.setText(nameOne);
                customerNumber.setText(contactOne);
                customerAddress.setText(addressStringOne);
                customerAreaInfo.setText(locationOne);
                selectedLatLan = (latlanOne);
            } else {
                customerAddress.setText(addressStringTwo);
                customerName.setText(nameTwo);
                customerNumber.setText(contactTwo);
                customerAddress.setText(addressStringTwo);
                customerAreaInfo.setText(locationTwo);
                selectedLatLan = (latlanTwo);
            }
        }
        if (v == clickbait) {
            radioAddress = false;
            addressPopup.setVisibility(View.GONE);
            findViewById(R.id.scroll).setVisibility(View.VISIBLE);
        }
    }
}