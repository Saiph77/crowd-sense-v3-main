package com.fzu.crowdsense.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConversionUtil {
    public static String convertDateToString(Date date) {
        SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            String dateString = sdfInput.format(date);
            Date parsedDate = sdfInput.parse(dateString);
            return sdfOutput.format(parsedDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
