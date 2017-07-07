package com.example.jhzhou.foodordersaler;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jhzhou.foodordersaler.adapter.DetailsAdapter;
import com.example.jhzhou.foodordersaler.entity.Bag;
import com.example.jhzhou.foodordersaler.entity.Customer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jhzhou on 6/3/17.
 */

public class DetailsActivity extends AppCompatActivity {

    public static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    public static final String BAG = "bag";
    public static final String CUSTOMER = "customer";
    public static final String POSITION = "position";

    @BindView(R.id.head_portrait_details) ImageView circleImage;
    @BindView(R.id.customer_name_details) TextView name;
    @BindView(R.id.is_stay_details) TextView isStay;
    @BindView(R.id.comments_details) TextView comment;
    @BindView(R.id.recycler_view_details) RecyclerView recyclerView;
    @BindView(R.id.done_button_details) Button done;
    @BindView(R.id.total_price_details) TextView totalPrice;

    private DetailsAdapter mAdapter;
    private Bag mBag;
    private Customer mCustomer;
    private int mPoistion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            mBag = intent.getParcelableExtra(BAG);
            mCustomer = intent.getParcelableExtra(CUSTOMER);
            mPoistion = intent.getIntExtra(POSITION, 0);
        }

        if (mBag.getIsStay()) {
            isStay.setText("Stay Here");
        } else {
            isStay.setText("Take Away");
        }

        String image = mCustomer.getImage();
        if (image != null && !image.equals("")) {
            Glide.with(this).load(Uri.parse(image)).into(circleImage);
        }
        name.setText(mCustomer.getUserName());

        String comments = mBag.getNotes();
        if (comments != null && !comments.equals("")) {
            comment.setVisibility(View.VISIBLE);
            comment.setText(comments);
        }

        mAdapter = new DetailsAdapter(this, mBag.getOrderList());
        recyclerView.setAdapter(mAdapter);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAdapter.checkBoxList()) {
                    Log.v(LOG_TAG, "not all checkBox is checked");
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra(POSITION, mPoistion);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

}
