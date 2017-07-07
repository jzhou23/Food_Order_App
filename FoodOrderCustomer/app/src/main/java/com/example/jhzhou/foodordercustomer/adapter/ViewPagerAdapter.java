package com.example.jhzhou.foodordercustomer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.jhzhou.foodordercustomer.entity.Bag;
import com.example.jhzhou.foodordercustomer.entity.Customer;
import com.example.jhzhou.foodordercustomer.fragment.HistoryFragment;
import com.example.jhzhou.foodordercustomer.fragment.ProfileFragment;
import com.example.jhzhou.foodordercustomer.fragment.PromotionFragment;

import java.util.ArrayList;

/**
 * Created by jhzhou on 6/12/17.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 3;
    private Customer mCustomer;

    public ViewPagerAdapter(FragmentManager fm, Customer customer) {
        super(fm);
        mCustomer = customer;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ProfileFragment.getInstance(mCustomer);
            case 1:
                return PromotionFragment.getInstance();
            case 2:
                return HistoryFragment.getInstance((ArrayList<Bag>) mCustomer.getHistory());
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Profile";
            case 1:
                return "Promotion";
            case 2:
                return "History";
            default:
                return "";
        }
    }
}
