package com.example.jhzhou.foodordersaler.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jhzhou on 6/13/17.
 */

public class DateUtil {

    public static String getDate(long time) {
        String date = new SimpleDateFormat("MM/dd/yyyy").format(new Date(time));
        return "Placed time: " + date;
    }
}
