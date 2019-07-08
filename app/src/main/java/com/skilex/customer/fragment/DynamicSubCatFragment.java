package com.skilex.customer.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skilex.customer.R;

public class DynamicSubCatFragment extends Fragment {
    View view;
    public static DynamicSubCatFragment newInstance(int val) {
        DynamicSubCatFragment fragment = new DynamicSubCatFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", val);
        fragment.setArguments(args);
        return fragment;
    }
    int val;
    TextView c;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        val = getArguments().getInt("someInt", 0);
        c = view.findViewById(R.id.c);
        c.setText("" + val);
        return view;
    }
}