package com.example.jhzhou.foodordersaler.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by jhzhou on 6/12/17.
 */

public class Staff extends Customer implements Parcelable{

    private double mTotalProfit;

    public Staff() {

    }

    public Staff(double totalProfit, String uuid, String userName, String email, String image, List<Bag> history) {
        super(uuid, userName, email, image, history);
        mTotalProfit = totalProfit;
    }

    protected Staff(Parcel in) {
        super(in);
        mTotalProfit = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(mTotalProfit);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Staff> CREATOR = new Creator<Staff>() {
        @Override
        public Staff createFromParcel(Parcel in) {
            return new Staff(in);
        }

        @Override
        public Staff[] newArray(int size) {
            return new Staff[size];
        }
    };

    public double getTotalProfit() {
        return mTotalProfit;
    }

    public void setTotalProfit(double totalProfit) {
        mTotalProfit = totalProfit;
    }

}
