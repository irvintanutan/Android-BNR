package com.novigosolutions.certiscisco_pcsbr.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

/**
 * Created by niteshpurohit on 08/01/16.
 */
public class Preferences {

    public static boolean getBoolean(String key, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, false);
    }

    public static void saveBoolean(String key, boolean value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static String getString(String key, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, "");
    }

    public static void saveString(String key, String value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static float getFloat(String key, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getFloat(key, 0);
    }

    public static void saveFloat(String key, Float value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    public static int getInt(String key, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, 0);
    }

    public static void saveInt(String key, int value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public static long getLong(String key, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(key, 0);
    }

    public static void saveLong(String key, long value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public static void clearAll(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String,?> prefs = sharedPreferences.getAll();
        for(Map.Entry<String,?> prefToReset : prefs.entrySet()){
            if(!prefToReset.getKey().equals("DeviceID")&&!prefToReset.getKey().equals("TWOWAY")&&!prefToReset.getKey().equals("API_URL"))
            sharedPreferences.edit().remove(prefToReset.getKey()).commit();
        }
    }

    public static void savePrinterInfo(String deviceName,String deviceAdress,Context context){
        String key1="DeviceName";
        String key2="MacAddress";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(key1, deviceName).apply();
        sharedPreferences.edit().putString(key2, deviceAdress).apply();
    }

    public static String getPrinterDeviceName(Context context) {
        String key="DeviceName";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key,"");
    }

    public static String getPrinterMacAddres(Context context) {
        String key="MacAddress";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key,"");
    }

    public static void savePrintHeader(String value,Context context){
        String key="HeaderText";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static void savePrintFooter(String value,Context context){
        String key="FooterText";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static void savePrintlogo(String value,Context context){
        String key="logo";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getLogo(Context context){
        String key="logo";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key,"");
    }

    public static String getPrintHeader(Context context){
        String key="HeaderText";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key,"");
    }

    public static String getPrintFooter(Context context){
        String key="FooterText";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key,"");
    }
}
