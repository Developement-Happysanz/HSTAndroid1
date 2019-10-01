package com.skilex.customer.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.ortiz.touchview.TouchImageView;
import com.skilex.customer.R;
import com.skilex.customer.utils.SkilExValidator;
import com.squareup.picasso.Picasso;

public class ViewBillImageZoom extends AppCompatActivity {

    private TouchImageView billImg;
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bill_zoom);
        billImg = findViewById(R.id.bill);
        url = (String) getIntent().getSerializableExtra("eventObj");
        if (SkilExValidator.checkNullString(url)) {
            Picasso.get().load(url).into(billImg);
        } else {
//            holder.mImageView.setImageResource(R.drawable.ic_user_profile_image);
        }
    }


}
