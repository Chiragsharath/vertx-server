package com.ka09solutions.common;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by Chirag on 27-02-2017.
 */
public class DateUtil {

    public static Date convertDate(String dateString)
    {
       return Date.valueOf((dateString).substring(0, 10));
    }

    public static String convertDateString(String date)
    {
        java.util.Date startDate = null;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        return sdf.format(startDate);
    }
}
