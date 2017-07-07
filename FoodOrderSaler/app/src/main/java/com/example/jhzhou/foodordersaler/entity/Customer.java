package com.example.jhzhou.foodordersaler.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhzhou on 6/4/17.
 */

public class Customer implements Parcelable{

    private String mUUid;
    private String mUserName;
    private String mEmail;
    private String mImage;
    private List<Bag> mHistory;

    public Customer() {

    }

    public Customer(String uuid, String userName, String email) {
        mUUid = uuid;
        mUserName = userName;
        mEmail = email;
        mImage = "";
        mHistory = new ArrayList<Bag>();
    }

    public Customer(String uuid, String userName, String email, String image, List<Bag> history) {
        mUUid = uuid;
        mUserName = userName;
        mEmail = email;
        mImage = image;
        mHistory = history;
    }

    public Customer(String userName) {
        mUserName = userName;
    }

    protected Customer(Parcel in) {
        mUUid = in.readString();
        mUserName = in.readString();
        mEmail = in.readString();
        mImage = in.readString();
        mHistory = in.createTypedArrayList(Bag.CREATOR);
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

    public String getUUid() {
        return mUUid;
    }

    public void setUUid(String uuid) {
        mUUid = uuid;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public List<Bag> getHistory() {
        return mHistory;
    }

    public void setHistory(List<Bag> history) {
        mHistory = history;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUUid);
        dest.writeString(mUserName);
        dest.writeString(mEmail);
        dest.writeString(mImage);
        dest.writeTypedList(mHistory);
    }
}


