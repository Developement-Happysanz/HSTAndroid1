package com.skilex.customer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.skilex.customer.R;
import com.skilex.customer.activity.OngoingServiceActivity;
import com.skilex.customer.activity.RequestedServicesActivity;
import com.skilex.customer.activity.ServiceHistoryActivity;

public class ServicesFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private LinearLayout request, ongoing, history;

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

        rootView = inflater.inflate(R.layout.fragment_services, container, false);

        request = rootView.findViewById(R.id.req_service_layout);
        request.setOnClickListener(this);
        ongoing = rootView.findViewById(R.id.ong_service_layout);
        ongoing.setOnClickListener(this);
        history = rootView.findViewById(R.id.service_history_layout);
        history.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View v) {
        if (v == request) {
            Intent intent = new Intent(getActivity(), RequestedServicesActivity.class);
//            intent.putExtra("cat", category);
            startActivity(intent);
        }
        if (v == ongoing) {
            Intent intent = new Intent(getActivity(), OngoingServiceActivity.class);
//            intent.putExtra("cat", category);
            startActivity(intent);
        }
        if (v == history) {
            Intent intent = new Intent(getActivity(), ServiceHistoryActivity.class);
//            intent.putExtra("cat", category);
            startActivity(intent);
        }

    }
}
