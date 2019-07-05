package com.skilex.customer.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.skilex.customer.R;
import com.skilex.customer.fragment.HomeFragment;
import com.skilex.customer.fragment.ProfileFragment;
import com.skilex.customer.fragment.ServicesFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    int checkPointSearch = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
//        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.action_home:
                                changeFragment(0);
//                                fabView.setVisibility(View.VISIBLE);
                                break;

                            case R.id.action_services:
                                changeFragment(1);
//                                fabView.setVisibility(View.VISIBLE);
                                break;
                            case R.id.action_profile:
                                changeFragment(2);
//                                fabView.setVisibility(View.VISIBLE);
                                break;
                        }
                        return true;
                    }
                });

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);

            }
        });

        changeFragment(0);
        bottomNavigationView.setSelectedItemId(R.id.action_home);

    }

    private void changeFragment(int position) {

        Fragment newFragment = null;

        if (position == 1) {
            checkPointSearch = 1;
            newFragment = new ServicesFragment();
        } else if (position == 0) {
            checkPointSearch = 0;
            newFragment = new HomeFragment();
        } else if (position == 2) {
            checkPointSearch = 2;
            newFragment = new ProfileFragment();
        }

        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragmentContainer, newFragment)
                .commit();
    }

}
