package com.skilex.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skilex.customer.R;
import com.skilex.customer.adapter.ViewBillAdapter;
import com.skilex.customer.bean.support.Bill;
import com.skilex.customer.bean.support.ServiceHistory;
import com.skilex.customer.fragment.HomeFragment;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.util.Log.d;

public class ViewBillActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, ViewBillAdapter.OnItemClickListener {

    private static final String TAG = ServiceSummaryActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ViewBillAdapter preferenceAdatper;
    private GridLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    ArrayList<Bill> categoryArrayList = new ArrayList<>();
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 12;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private String url = "";

    ServiceHistory serviceHistory;
    String res = "";
    String ser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bills);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ser = getIntent().getStringExtra("serv");
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.listView_bills);
        mLayoutManager = new GridLayoutManager(this, 6);
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

        callGetServiceSummary();
    }


    public void callGetServiceSummary() {
        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            getServiceSummary();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private void getServiceSummary() {
        res = "summary";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);
        String orderid = "";
        orderid = ser;
        try {
            jsonObject.put(SkilExConstants.USER_MASTER_ID, id);
            jsonObject.put(SkilExConstants.SERVICE_ORDER_ID, orderid);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.VIEW_BILLS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }


    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onEvent list item click" + position);
        Bill taskData = null;
        if ((preferenceAdatper != null)) {
            Log.d(TAG, "while searching");
//            int actualindex = preferenceAdatper.getItem(position);
//            Log.d(TAG, "actual index" + actualindex);
            taskData = preferenceAdatper.getItem(position);
        } else {
            taskData = preferenceAdatper.getItem(position);
        }
        url = taskData.getFile_bill();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                == PackageManager.PERMISSION_DENIED) {
//
//            Log.d("permission", "permission denied to SEND_SMS - requesting it");
//            String[] perm = {Manifest.permission.READ_EXTERNAL_STORAGE};
//
//            ActivityCompat.requestPermissions(ViewBillActivity.this, perm, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//
//        }
////        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
////                == PackageManager.PERMISSION_DENIED) {
////
////            Log.d("permission", "permission denied to SEND_SMS - requesting it");
////            String[] perm = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
////
////            ActivityCompat.requestPermissions(ViewBillActivity.this, perm, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
////
////        }
//        else {
            Intent intent = new Intent(getApplicationContext(), ViewBillImageZoom.class);
            intent.putExtra("eventObj", url);
            startActivity(intent);
//            openImageIntent();
//            new DownloadFile().execute(url);
//        }
    }

//    private class DownloadFile extends AsyncTask<String, String, String> {
//
//        private ProgressDialog progressDialog;
//        private String fileName;
//        private String folder;
//        private boolean isDownloaded;
//
//        /**
//         * Before starting background thread
//         * Show Progress Bar Dialog
//         */
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            this.progressDialog = new ProgressDialog(ViewBillActivity.this);
//            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            this.progressDialog.setCancelable(false);
//            this.progressDialog.show();
//        }
//
//        /**
//         * Downloading file in background thread
//         */
//        @Override
//        protected String doInBackground(String... f_url) {
//            int count;
//            try {
//                URL url = new URL(f_url[0]);
//                URLConnection connection = url.openConnection();
//                connection.connect();
//                // getting file length
//                int lengthOfFile = connection.getContentLength();
//
//
//                // input stream to read file - with 8k buffer
//                InputStream input = new BufferedInputStream(url.openStream(), 8192);
//
//                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
//
//                //Extract file name from URL
//                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());
//
//                //Append timestamp to file name
//                fileName = timestamp + "_" + fileName;
//
//                //External directory path to save file
//                folder = Environment.getExternalStorageDirectory() + File.separator + "Download/";
//
//                //Create androiddeft folder if it does not exist
//                File directory = new File(folder);
//
//                if (!directory.exists()) {
//                    directory.mkdirs();
//                }
//
//                // Output stream to write file
//                OutputStream output = new FileOutputStream(folder + fileName);
//
//                byte data[] = new byte[1024];
//
//                long total = 0;
//
//                while ((count = input.read(data)) != -1) {
//                    total += count;
//                    // publishing the progress....
//                    // After this onProgressUpdate will be called
//                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
//                    Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));
//
//                    // writing data to file
//                    output.write(data, 0, count);
//                }
//
//                // flushing output
//                output.flush();
//
//                // closing streams
//                output.close();
//                input.close();
//                return "Downloaded at: " + folder + fileName;
//
//            } catch (Exception e) {
//                Log.e("Error: ", e.getMessage());
//            }
//
//            return "Something went wrong";
//        }
//
//        /**
//         * Updating progress bar
//         */
//        protected void onProgressUpdate(String... progress) {
//            // setting progress percentage
//            progressDialog.setProgress(Integer.parseInt(progress[0]));
//        }
//
//
//        @Override
//        protected void onPostExecute(String message) {
//            // dismiss the dialog after the file was downloaded
//            this.progressDialog.dismiss();
//
//            // Display File path after downloading
//            Toast.makeText(getApplicationContext(),
//                    message, Toast.LENGTH_LONG).show();
//        }
//    }


    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.cancelProgressDialog();
        try {
            if (response.getString("status").equalsIgnoreCase("success")){
                JSONArray getData = response.getJSONArray("service_bill");
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Bill>>() {
                }.getType();
                categoryArrayList = (ArrayList<Bill>) gson.fromJson(getData.toString(), listType);
                preferenceAdatper = new ViewBillAdapter(this, categoryArrayList, this);
                mRecyclerView.setAdapter(preferenceAdatper);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String error) {

    }

//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
////                    new DownloadFile().execute(url);
//
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//
//        }
//    }
}
