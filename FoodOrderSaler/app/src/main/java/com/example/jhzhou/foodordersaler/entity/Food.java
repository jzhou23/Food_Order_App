package com.example.jhzhou.foodordersaler.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jhzhou on 6/4/17.
 */

public class Food implements Parcelable {

    private String mName;

    private double mPrice;

    private int mCategory;

    private String mImage;

    private int mSpicy;

    private double mRating;

    private String mDescription;

    private boolean mIsSpecial;

    private Map<String, Double> mIngredients;

    public Food() {

    }

    public Food(String name, double price, int category, String image, int spicy, double rating, String description, Map<String, Double> ingredients, boolean isSpecial) {
        mName = name;
        mPrice = price;
        mCategory = category;
        mImage = image;
        mSpicy = spicy;
        mRating = rating;
        mDescription = description;
        mIngredients = ingredients;
        mIsSpecial = isSpecial;
    }

    protected Food(Parcel in) {
        mName = in.readString();
        mPrice = in.readDouble();
        mCategory = in.readInt();
        mImage = in.readString();
        mSpicy = in.readInt();
        mRating = in.readDouble();
        mDescription = in.readString();

        mIngredients = new HashMap<>();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            String key = in.readString();
            double value = in.readDouble();
            mIngredients.put(key, value);
        }

        mIsSpecial = in.readByte() != 0;
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double mPrice) {
        this.mPrice = mPrice;
    }

    public int getCategory() {
        return mCategory;
    }

    public void setCategory(int mCategory) {
        this.mCategory = mCategory;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public int getSpicy() {
        return mSpicy;
    }

    public void setSpicy(int mSpicy) {
        this.mSpicy = mSpicy;
    }

    public double getRating() {
        return mRating;
    }

    public void setRating(double mRating) {
        this.mRating = mRating;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public Map<String, Double> getIngredients() {
        return mIngredients;
    }

    public void setIngredients(Map<String, Double> ingredients) {
        mIngredients = ingredients;
    }

    public boolean getIsSpecial() {
        return mIsSpecial;
    }

    public void setIsSpecial(boolean special) {
        this.mIsSpecial = special;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeDouble(mPrice);
        dest.writeInt(mCategory);
        dest.writeString(mImage);
        dest.writeInt(mSpicy);
        dest.writeDouble(mRating);
        dest.writeString(mDescription);

        if (mIngredients == null || mIngredients.size() == 0) {
            dest.writeInt(0);
        } else {
            dest.writeInt(mIngredients.size());
            for (Map.Entry<String, Double> entry : mIngredients.entrySet()) {
                dest.writeString(entry.getKey());
                dest.writeDouble(entry.getValue());
            }
        }
        dest.writeByte((byte) (mIsSpecial ? 1 : 0));
    }
}