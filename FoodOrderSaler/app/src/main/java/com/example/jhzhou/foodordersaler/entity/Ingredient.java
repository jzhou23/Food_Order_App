package com.example.jhzhou.foodordersaler.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jhzhou on 6/4/17.
 */

public class Ingredient implements Parcelable{
    private String mIngredient;
    private double mSinglePrice;

    public Ingredient() {

    }

    public Ingredient(String ingredient, double singlePrice) {
        mIngredient= ingredient;
        mSinglePrice = singlePrice;
    }

    protected Ingredient(Parcel in) {
        mIngredient = in.readString();
        mSinglePrice = in.readDouble();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public String getIngredient() {
        return mIngredient;
    }

    public void setIngredient(String mIngredient) {
        this.mIngredient = mIngredient;
    }

    public double getSinglePrice() {
        return mSinglePrice;
    }

    public void setSinglePrice(double mSinglePrice) {
        this.mSinglePrice = mSinglePrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mIngredient);
        dest.writeDouble(mSinglePrice);
    }
}