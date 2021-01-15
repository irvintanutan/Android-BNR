package com.novigosolutions.certiscisco_pcsbr.utils;

/**
 * Created by dhanrajk on 05-12-17.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.SystemClock;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CommonMethods {
    private static ProgressDialog progressDialog;
    private static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
    private static DateFormat timeFormat = new SimpleDateFormat("K:mma");
    private static DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat dateFormat2 = new SimpleDateFormat("dd-MMM-yyyy");
    private static DateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private static DateFormat serverTimeFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
    private static DateFormat serverTimeFormat2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm");

    private static DateFormat timeFormat2 = new SimpleDateFormat("HH:mm");

    public static Date getTimeNow() {
        return Calendar.getInstance().getTime();
    }

    public static Date getLoginDate(String date) {
        try {
            return dateFormat2.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCurrentDateTime(Context context) {
        try {
            return dateTimeFormat.format(new Date(getServerTimeInms(context)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCurrentTime() {
        return timeFormat.format(getTimeNow());
    }

    public static String getCurrentDate() {

        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    public static String getPostPoneStringDate(Date date) {
        return dateFormat1.format(date);
    }

    public static Date getPostPoneDate(String string) {
        try {
            return dateFormat1.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toDay() {
        return dateFormat1.format(getTimeNow());
    }

    public static String getHourMinitue(String string) {
        try {
            return timeFormat2.format(dateFormat3.parse(string));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getInFormate(Date date) {
        return serverTimeFormat2.format(date);
    }

    public static String getServerTimeInFormate(Context context) {
        return serverTimeFormat2.format(new Date(getServerTimeInms(context)));
    }

    public static long getServerTimeInms(Context context) {
        try {
            long sinceloggedIn = SystemClock.elapsedRealtime() - Preferences.getLong("sinceLoggedIn", context);
            return serverTimeFormat.parse(Preferences.getString("LoggedOn", context)).getTime() + sinceloggedIn;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static Date getBreakTime(String string) {
        try {
            return dateFormat3.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getStartTime(String string)
    {
        try {
            return timeFormat2.format(dateFormat3.parse(string));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getTimeIn12Hour(String dateAndTime){
        String date=dateAndTime;
        String time="";
        // This is the format date we want
        DateFormat mSDF = new SimpleDateFormat("hh:mm a");
        // This format date is actually present
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        try {
            time= mSDF.format(formatter.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getTimeIn12Hour1(String dateAndTime){
        String date=dateAndTime;
        String time="";
        // This is the format date we want
        DateFormat mSDF = new SimpleDateFormat("hh:mm a");
        // This format date is actually present
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm");
        try {
            time= mSDF.format(formatter.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getDateForPrint(String dateAndTime){
//        String date=dateAndTime;
//        String formattedDate="";
//        // This is the format date we want
//        DateFormat mSDF = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
//        // This format date is actually present
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm",Locale.getDefault());
//        try {
//            formattedDate= mSDF.format(formatter.parse(date));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return  formattedDate;

        String s="";
        Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date;
        try {
            date = (Date)((DateFormat) formatter).parse(dateAndTime);
            formatter = new SimpleDateFormat("dd-MMM-yyyy");
            s = formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s;
    }


    public static void showProgressDialog(Context context, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            //progressDialog.setOnCancelListener(cancelListener);

        }

        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public static void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }
}
