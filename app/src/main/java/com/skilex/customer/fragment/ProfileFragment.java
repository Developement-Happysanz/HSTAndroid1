package com.skilex.customer.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.skilex.customer.R;
import com.skilex.customer.activity.ProfileActivity;
import com.skilex.customer.activity.SplashScreenActivity;
import com.skilex.customer.customview.CircleImageView;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.utils.PreferenceStorage;

public class ProfileFragment extends Fragment implements View.OnClickListener, DialogClickListener {

    private static final String TAG = ProfileFragment.class.getName();

    private View rootView;
    private CircleImageView profileImage;
    private LinearLayout profile, about, share, logout;

    public static ProfileFragment newInstance(int position) {
        ProfileFragment frag = new ProfileFragment();
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

        rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        profileImage = rootView.findViewById(R.id.user_profile_img);

        profile = rootView.findViewById(R.id.layout_profile);
        profile.setOnClickListener(this);
        about = rootView.findViewById(R.id.layout_about);
        about.setOnClickListener(this);
        share = rootView.findViewById(R.id.layout_share);
        share.setOnClickListener(this);
        logout = rootView.findViewById(R.id.layout_logout);
        logout.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {
        if (v == profile) {
            Intent homeIntent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(homeIntent);
        }
        if (v == about) {
            Intent homeIntent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(homeIntent);
        }
        if (v == share) {
            Intent homeIntent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(homeIntent);
        }
        if (v == logout) {
            doLogout();
        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    public void doLogout() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.edit().clear().apply();
//        TwitterUtil.getInstance().resetTwitterRequestToken();
        Intent homeIntent = new Intent(getActivity(), SplashScreenActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        getActivity().finish();
    }

}