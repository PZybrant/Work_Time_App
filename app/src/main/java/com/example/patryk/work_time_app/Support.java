package com.example.patryk.work_time_app;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public abstract class Support {

    public static String convertTimeToString(long timeInMilis) {
        long seconds = timeInMilis / 1000;

        long minutes = seconds / 60;
        seconds = seconds % 60;

        long hours = minutes / 60;
        minutes = minutes % 60;

        String s = (seconds < 10) ? ("0" + seconds) : String.valueOf(seconds);
        String m = (minutes < 10) ? ("0" + minutes) : String.valueOf(minutes);
        String h = (hours < 10) ? ("0" + hours) : String.valueOf(hours);

        return String.format("%s:%s:%s", h, m, s);
    }

    public static String convertDateToString(Date date) {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static Calendar convertFromString(String s) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            df.setLenient(false);
            Date tempDate = df.parse(s);
            calendar.setTime(tempDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static String makeDateText(Calendar calendar) {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return dateFormat.format(calendar.getTime());

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    public static String makeTimeText(Calendar calendar) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            return dateFormat.format(calendar.getTime());

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static long calculateDifference(long time1, long time2) {
        return (time1 - time2) * (-1);
    }
}
