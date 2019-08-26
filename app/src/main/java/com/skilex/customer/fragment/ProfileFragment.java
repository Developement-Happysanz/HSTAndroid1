package com.skilex.customer.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skilex.customer.R;
import com.skilex.customer.activity.AboutUsActivity;
import com.skilex.customer.activity.MainActivity;
import com.skilex.customer.activity.ProfileActivity;
import com.skilex.customer.activity.SplashScreenActivity;
import com.skilex.customer.customview.CircleImageView;
import com.skilex.customer.helper.AlertDialogHelper;
import com.skilex.customer.helper.ProgressDialogHelper;
import com.skilex.customer.interfaces.DialogClickListener;
import com.skilex.customer.servicehelpers.ServiceHelper;
import com.skilex.customer.serviceinterfaces.IServiceListener;
import com.skilex.customer.utils.LocaleHelper;
import com.skilex.customer.utils.PreferenceStorage;
import com.skilex.customer.utils.SkilExConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class ProfileFragment extends Fragment implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = ProfileFragment.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private View rootView;
    private CircleImageView profileImage;
    private LinearLayout profile, about, share, logout;
    TextView userNmae,number, mail;
    ImageView lan;

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

        serviceHelper = new ServiceHelper(rootView.getContext());
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(rootView.getContext());

        profileImage = rootView.findViewById(R.id.user_profile_img);

        profile = rootView.findViewById(R.id.layout_profile);
        profile.setOnClickListener(this);
        about = rootView.findViewById(R.id.layout_about);
        about.setOnClickListener(this);
        share = rootView.findViewById(R.id.layout_share);
        share.setOnClickListener(this);
        logout = rootView.findViewById(R.id.layout_logout);
        logout.setOnClickListener(this);
        userNmae = rootView.findViewById(R.id.user_name);
        number = rootView.findViewById(R.id.user_phone_number);
        mail = rootView.findViewById(R.id.user_mail);
        lan = rootView.findViewById(R.id.langues);
        lan.setOnClickListener(this);

        getUserInfo();

        return rootView;
    }

    private void getUserInfo() {
        String id ="";
        if (!PreferenceStorage.getUserId(rootView.getContext()).isEmpty()) {
            id = PreferenceStorage.getUserId(rootView.getContext());
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(SkilExConstants.KEY_USER_MASTER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = SkilExConstants.BUILD_URL + SkilExConstants.PROFILE_INFO;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onClick(View v) {
        if (v == profile) {
            if (checkLogin()) {
                Intent homeIntent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(homeIntent);
            }
        }
        if (v == about) {
                Intent homeIntent = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(homeIntent);
        }
        if (v == share) {
//            Intent i = new Intent(android.content.Intent.ACTION_SEND);
//            i.setType("text/plain");
//            i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share");
//            i.putExtra(android.content.Intent.EXTRA_TEXT, "Hey! Get Heyla app and win some exciting rewards. https://goo.gl/JTmdEX");
//            startActivity(Intent.createChooser(i, "Share via"));

        }
        if (v == logout) {
            doLogout();
        }
        if (v == lan) {
            showLangsAlert();
        }
    }

    private void showLangsAlert() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(rootView.getContext());
        alertDialogBuilder.setTitle(R.string.language);
        alertDialogBuilder.setMessage(R.string.choose_language);
        alertDialogBuilder.setPositiveButton("English", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                PreferenceStorage.saveLang(rootView.getContext(), "eng");
                Toast.makeText(rootView.getContext(), "App language is set to English", Toast.LENGTH_SHORT).show();
                LocaleHelper.setLocale(rootView.getContext(), "en");
                Intent i = new Intent(rootView.getContext(), MainActivity.class);
                startActivity(i);
            }
        });
        alertDialogBuilder.setNegativeButton("தமிழ்", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceStorage.saveLang(rootView.getContext(), "tamil");
                Toast.makeText(rootView.getContext(), "மொழி தமிழுக்கு அமைக்கப்பட்டுள்ளது", Toast.LENGTH_SHORT).show();
                LocaleHelper.setLocale(rootView.getContext(), "ta");
                Intent i = new Intent(rootView.getContext(), MainActivity.class);
                startActivity(i);
//                rootView.finish();
            }
        });
        alertDialogBuilder.show();
    }

    private boolean checkLogin() {
        String id = PreferenceStorage.getUserId(rootView.getContext());
        boolean a = false;
        if (!id.isEmpty()) {
            a = true;
        } else {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(rootView.getContext());
            alertDialogBuilder.setTitle(R.string.login);
            alertDialogBuilder.setMessage(R.string.login_to_continue);
            alertDialogBuilder.setPositiveButton(R.string.alert_button_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    doLogout();
                }
            });
            alertDialogBuilder.setNegativeButton(R.string.alert_button_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.show();
        }
        return a;
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
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateResponse(response)) {
            try {
                JSONObject data = response.getJSONObject("user_details");
                String url = data.getString("profile_pic");
                if (!url.isEmpty()) {
                    Picasso.get().load(url).into(profileImage);
                }
                userNmae.setText(data.getString("full_name"));
                number.setText(data.getString("phone_no"));
                mail.setText(data.getString("email"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {

    }
}