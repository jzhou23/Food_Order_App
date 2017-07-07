package com.example.jhzhou.foodordersaler.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by jhzhou on 6/5/17.
 */

public class Order implements Parcelable {
    private Food mFood;
    private int mNums;
    private List<String> mAdditionList;

    public Order() {

    }

    public Order(Food food, int nums, List<String> additionList) {
        mFood = food;
        mNums = nums;
        mAdditionList = additionList;
    }

    public Food getFood() {
        return mFood;
    }

    public void setFood(Food food) {
        mFood = food;
    }

    public int getNums() {
        return mNums;
    }

    public void setNums(int nums) {
        mNums = nums;
    }

    public List<String> getAdditionList() {
        return mAdditionList;
    }

    public void setAdditionList(List<String> additionList) {
        mAdditionList = additionList;
    }

    protected Order(Parcel in) {
        mFood = in.readParcelable(Food.class.getClassLoader());
        mNums = in.readInt();
        mAdditionList = in.createStringArrayList();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mFood, flags);
        dest.writeInt(mNums);
        dest.writeStringList(mAdditionList);
    }
}