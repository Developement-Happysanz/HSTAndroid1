package com.skilex.customer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.skilex.customer.R;
import com.skilex.customer.bean.database.SQLiteHelper;
//import com.skilex.customer.utils.AppSignatureHelper;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExValidator;
import com.skilex.customer.utils.SmsVerification;

import java.util.ArrayList;


public class SplashScreenActivity extends Activity {

    private static int SPLASH_TIME_OUT = 2000;
    SQLiteHelper database;
//    AppSignatureHelper appSignatureHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        database = new SQLiteHelper(getApplicationContext());
//        appSignatureHelper = new AppSignatureHelper(this);
//        appSignatureHelper.getAppSignatures();
        final int getStatus = database.appInfoCheck();

        ArrayList<String> appCodes = new ArrayList<>();
        SmsVerification hash = new SmsVerification(getBaseContext());
        appCodes= hash.getAppSignatures();
        String yourhash = appCodes.get(0);
        Log.d("Hash Key: ", yourhash);

//        Toast.makeText(SplashScreenActivity.this, "Hash key...  " + yourhash , Toast.LENGTH_SHORT).show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!PreferenceStorage.isFirstTimeLaunch(getApplicationContext()) && SkilExValidator.checkNullString(PreferenceStorage.getUserId(getApplicationContext()))) {
                    Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashScreenActivity.this, new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            String newToken = instanceIdResult.getToken();
                            Log.e("newToken", newToken);
                            PreferenceStorage.saveGCM(getApplicationContext(), newToken);
                        }
                    });
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}

