package com.skilex.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.skilex.customer.R;
import com.skilex.customer.bean.support.Wallet;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WalletListAdapter  extends BaseAdapter {

    private static final String TAG = WalletListAdapter.class.getName();
    private final Transformation transformation;
    private Context context;
    private ArrayList<Wallet> walletArrayList;
    String className;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();

    public WalletListAdapter(Context context, ArrayList<Wallet> walletArrayList) {
        this.context = context;
        this.walletArrayList = walletArrayList;
        this.className = className;

        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(0)
                .oval(false)

                .build();
        mSearching = false;
    }

    @Override
    public int getCount() {
        if (mSearching) {

            if (!mAnimateSearch) {
                mAnimateSearch = true;
            }
            return mValidSearchIndices.size();

        } else {

            return walletArrayList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return walletArrayList.get(mValidSearchIndices.get(position));
        } else {
            return walletArrayList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.wallet_list_item, parent, false);

            holder = new ViewHolder();
            holder.txtNote = (TextView) convertView.findViewById(R.id.note);
            holder.txtDate = (TextView) convertView.findViewById(R.id.txt_event_date);
            holder.txtAmt = convertView.findViewById(R.id.amt);
            holder.txtTime = convertView.findViewById(R.id.time);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mSearching) {

            position = mValidSearchIndices.get(position);

        } else {
            Log.d("Event List Adapter", "getview pos called" + position);
        }

        holder.txtNote.setText(walletArrayList.get(position).getnotes());


        String start = walletArrayList.get(position).getcreated_date();

        try {
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
            Date date = (Date) formatter.parse(start);
            SimpleDateFormat event_date = new SimpleDateFormat("dd MMM yyyy");
            String date_name = event_date.format(date.getTime());
            if ((start != null) ) {
                holder.txtDate.setText(date_name);
            } else {
                holder.txtDate.setText("N/A");
            }
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        holder.txtAmt.setText(walletArrayList.get(position).gettransaction_amt());
        holder.txtTime.setText(walletArrayList.get(position).getcreated_time());
        holder.status = walletArrayList.get(position).getstatus();
        if (holder.status.equalsIgnoreCase("Credited")) {
            holder.txtAmt.setText("+ ₹"+walletArrayList.get(position).gettransaction_amt());
            holder.txtAmt.setTextColor(ContextCompat.getColor(context, R.color.add_money_green));
        } else {
            holder.txtAmt.setText("- ₹"+walletArrayList.get(position).gettransaction_amt());
            holder.txtAmt.setTextColor(ContextCompat.getColor(context, R.color.add_money_red));
        }

        return convertView;
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < walletArrayList.size(); i++) {
            String eventname = walletArrayList.get(i).getid();
            if ((eventname != null) && !(eventname.isEmpty())) {
                if (eventname.toLowerCase().contains(eventName.toLowerCase())) {
                    mValidSearchIndices.add(i);
                }
            }
        }
        Log.d("Event List Adapter", "notify" + mValidSearchIndices.size());
    }

    public void exitSearch() {
        mSearching = false;
        mValidSearchIndices.clear();
        mAnimateSearch = false;
        // notifyDataSetChanged();
    }

    public void clearSearchFlag() {
        mSearching = false;
    }

    public class ViewHolder {
        public TextView txtNote, txtDate, txtAmt, txtTime;
        public String status;
    }

    public boolean ismSearching() {
        return mSearching;
    }

    public int getActualEventPos(int selectedSearchpos) {
        if (selectedSearchpos < mValidSearchIndices.size()) {
            return mValidSearchIndices.get(selectedSearchpos);
        } else {
            return 0;
        }
    }
}
