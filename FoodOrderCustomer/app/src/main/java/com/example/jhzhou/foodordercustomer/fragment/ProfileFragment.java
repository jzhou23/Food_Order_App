package com.example.jhzhou.foodordercustomer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jhzhou.foodordercustomer.R;
import com.example.jhzhou.foodordercustomer.entity.Customer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jhzhou on 6/12/17.
 */

public class ProfileFragment extends Fragment {

    @BindView(R.id.customer_name_profile) TextView name;
    @BindView(R.id.customer_email_profile) TextView email;
    @BindView(R.id.customer_uuid_profile) TextView uuid;

    private Customer mCustomer;

    public static ProfileFragment getInstance(Customer customer) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("customer", customer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCustomer = getArguments().getParcelable("customer");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name.setText(mCustomer.getUserName());
        email.setText(mCustomer.getEmail());
        uuid.setText(mCustomer.getUUid());
    }
}
