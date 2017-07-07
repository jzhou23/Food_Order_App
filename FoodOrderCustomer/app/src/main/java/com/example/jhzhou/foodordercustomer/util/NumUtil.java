package com.example.jhzhou.foodordercustomer.util;

/**
 * Created by jhzhou on 6/13/17.
 */

public class NumUtil {

    public static String addDollarSign(double price) {
        return "$" + String.valueOf(price);
    }

    public static String addPlusDollarSign(double price) {
        return "+ $" + String.valueOf(price);
    }
}
