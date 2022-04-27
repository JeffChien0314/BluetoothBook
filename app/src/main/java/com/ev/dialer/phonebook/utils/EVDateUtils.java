package com.ev.dialer.phonebook.utils;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.text.format.DateUtils.WEEK_IN_MILLIS;
import static android.text.format.DateUtils.isToday;

public class EVDateUtils {
    private static Context mContext;
    private static EVDateUtils mInstance;

    public static EVDateUtils getInstance(Context context) {
        if (null == mInstance) {
            mContext = context;
            mInstance = new EVDateUtils();
        }
        return mInstance;
    }

    private String getTimeDescription(long millis) {
        if (isToday(millis)) {
            return dateCompareTo(new Date(millis));
        } else if (isThisWeek(millis)) {

        }
        return "";
    }

    private boolean isThisWeek(long millis) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(new Date(millis));

        int year = cd.get(Calendar.YEAR); //获取年份
        int month = cd.get(Calendar.MONTH); //获取月份
        int day = cd.get(Calendar.DAY_OF_MONTH); //获取日期
        int week = cd.get(Calendar.DAY_OF_WEEK); //获取星期
        if ((System.currentTimeMillis() - millis < WEEK_IN_MILLIS))
            return true;
        return false;
        /*return (thenYear == time.year)
                && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay);
        */
    }

    /**
     * @param time    1541569323155
     * @param pattern yyyy-MM-dd HH:mm:ss
     * @return 2018-11-07 13:42:03
     */
    public static String getDate2String(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(date);
    }

    public static String getWeek(long time) {

        Calendar cd = Calendar.getInstance();
        cd.setTime(new Date(time));

        int year = cd.get(Calendar.YEAR); //获取年份
        int month = cd.get(Calendar.MONTH); //获取月份
        int day = cd.get(Calendar.DAY_OF_MONTH); //获取日期
        int week = cd.get(Calendar.DAY_OF_WEEK); //获取星期

        String weekString;
        switch (week) {
            case Calendar.SUNDAY:
                weekString = "Sunday";
                break;
            case Calendar.MONDAY:
                weekString = "Monday";
                break;
            case Calendar.TUESDAY:
                weekString = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                weekString = "Wednesday";
                break;
            case Calendar.THURSDAY:
                weekString = "Thursday";
                break;
            case Calendar.FRIDAY:
                weekString = "Friday";
                break;
            default:
                weekString = "Saturday";
                break;
        }

        return weekString;
    }

    /**
     * @param dateString 2018-11-07 13:42:03,
     * @param pattern    yyyy-MM-dd HH:mm:ss
     * @return 1541569323000
     */

    public static long getString2Date(String dateString, String pattern) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    private String getRelativeTime(long millis) {
        boolean validTimestamp = millis > 0;

        return validTimestamp ? DateUtils.getRelativeTimeSpanString(
                millis, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE).toString() : "";
    }

    public Date parserDateFormat(String string) {
        if (string == null) return null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public String dateCompareTo(long when) {
        return dateCompareTo(new Date(when));
    }


    public String dateCompareTo(Date date) {
        if (date == null) return null;
        String dateInfo = null;

        Date todayDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(todayDate);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTime(date);
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);

        if (date != null) {
            int day = day1 - day2;
            switch (day) {
                case 0:
                    if (getTimeFormat()) {
                        dateInfo = getCurrentTime(date);
                    } else {
                        if (Calendar.AM == getTimeStyle(date)) {
                            dateInfo = getCurrentTime(date) + " AM";
                        } else {
                            dateInfo = getCurrentTime(date) + " PM";
                        }
                    }
                    break;
                case 1:
                    dateInfo = "yesterday";
                    break;
                default:
                    if (day < 7) {
                        dateInfo = dateToWeek(date);
                    } else {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        dateInfo = simpleDateFormat.format(date);
                    }

            }
        }
        return dateInfo;
    }

    private String getCurrentTime(Date date) {
        String time;
        String TIME_FORMAT = "hh:mm";//hh 12h ,HH 24h
        if (getTimeFormat()) {
            TIME_FORMAT = "HH:mm";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);
        time = simpleDateFormat.format(date);
        return time;
    }

    private boolean getTimeFormat() {
        return DateFormat.is24HourFormat(mContext);
    }

    private int getTimeStyle(Date date) {
        int mStyle;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        mStyle = calendar.get(Calendar.AM_PM);

        return mStyle;
    }

    public String dateToWeek(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //一周的第几天
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }


}
