package com.example.patryk.work_time_app.data;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public final class Converters {

    @TypeConverter
    public Calendar fromTimestamp(String val) {
        if (val == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            df.setLenient(false);
            Date tempDate = df.parse(val);
            calendar.setTime(tempDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    @TypeConverter
    public String dateToTimestamp(Calendar date) {
        if (date == null) {
            return null;
        } else {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                return dateFormat.format(date.getTime());

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
