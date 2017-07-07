package com.example.jhzhou.foodordersaler.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jhzhou on 6/5/17.
 */

public class Bag implements Parcelable {
    private List<Order> mOrderList;
    private boolean mIsStay;
    private String mNotes;
    //    private Customer mCustomer;
    private String mCustomerUUid;
    private double mAmount;
    private long mTime;

    public Bag() {
        mOrderList = new ArrayList<Order>();
        mIsStay = false;
    }

    public Bag(List<Order> orderList, boolean isStay, String notes, String uuid, double amount) {
        mOrderList = orderList;
        mIsStay = isStay;
        mNotes = notes;
        mCustomerUUid = uuid;
        mAmount = amount;
        setCurrentTime();
    }

    public double getAmount() {
        return mAmount;
    }

    public void setAmount(double amount) {
        mAmount = amount;
    }

//    public Customer getCustomer() {
//        return mCustomer;
//    }
//
//    public void setCustomer(Customer customer) {
//        mCustomer = customer;
//    }


    public String getCustomerUUid() {
        return mCustomerUUid;
    }

    public void setCustomerUUid(String uuid) {
        mCustomerUUid = uuid;
    }

    protected Bag(Parcel in) {
        mOrderList = in.createTypedArrayList(Order.CREATOR);
        mIsStay = in.readByte() != 0;
        mNotes = in.readString();
//        mCustomer = in.readParcelable(Customer.class.getClassLoader());
        mCustomerUUid = in.readString();
        mAmount = in.readDouble();
        mTime = in.readLong();
    }

    public static final Creator<Bag> CREATOR = new Creator<Bag>() {
        @Override
        public Bag createFromParcel(Parcel in) {
            return new Bag(in);
        }

        @Override
        public Bag[] newArray(int size) {
            return new Bag[size];
        }
    };

    public List<Order> getOrderList() {
        return mOrderList;
    }

    public void setOrderList(List<Order> orderList) {
        mOrderList = orderList;
    }

    public boolean getIsStay() {
        return mIsStay;
    }

    public void setIsStay(boolean isStay) {
        mIsStay = isStay;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public void addOrder(Order order) {
        if (order == null) {
            return;
        }

        int index = checkSameOrder(order);
        if (index == -1) {
            mOrderList.add(order);
        } else {
            int origialNum = mOrderList.get(index).getNums();
            int totalNum = origialNum + order.getNums();
            mOrderList.get(index).setNums(totalNum);
        }
    }

    private int checkSameOrder(Order order) {

        for (int i = 0; i < mOrderList.size(); i++) {
            Order o  = mOrderList.get(i);
            if (order.getFood().getName().equals(o.getFood().getName())) {
                List<String> list = o.getAdditionList();
                List<String> list2 = order.getAdditionList();
                if (list == null && list2 == null) {
                    return i;
                }
                if (list == null || list2 == null) {
                    continue;
                }

                if (list.size() != list2.size()) {
                    continue;
                }

                for (int j = 0; j < list.size(); j++) {
                    String s1 = list.get(j);
                    String s2 = list2.get(j);
                    if (!s1.equals(s2)) {
                        break;
                    }

                    if (j == list.size() - 1) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mOrderList);
        dest.writeByte((byte) (mIsStay ? 1 : 0));
        dest.writeString(mNotes);
//        dest.writeParcelable(mCustomer, flags);
        dest.writeString(mCustomerUUid);
        dest.writeDouble(mAmount);
        dest.writeLong(mTime);
    }

    public static double getOrderPrice(Order order) {
        int nums = order.getNums();
        double itemPrice = order.getFood().getPrice();
        Map<String, Double> map = order.getFood().getIngredients();

        List<String> addtionList = order.getAdditionList();
        double addtionPrice = 0;
        if (addtionList != null && addtionList.size() != 0) {
            for (String addition: addtionList) {
                addtionPrice += map.get(addition);
            }
        }

        double totalPrice = (itemPrice + addtionPrice) * nums;
        return totalPrice;
    }

    public void addOrderPrice(Order order) {
        mAmount += Bag.getOrderPrice(order);
    }

    public void setCurrentTime() {
        mTime = new Date().getTime();
    }
}




