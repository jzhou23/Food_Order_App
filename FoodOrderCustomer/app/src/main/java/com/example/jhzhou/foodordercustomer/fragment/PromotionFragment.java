package com.example.jhzhou.foodordercustomer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jhzhou.foodordercustomer.R;

import butterknife.ButterKnife;

/**
 * Created by jhzhou on 6/12/17.
 */

public class PromotionFragment extends Fragment {

    public static PromotionFragment getInstance() {
        return new PromotionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promotion, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
