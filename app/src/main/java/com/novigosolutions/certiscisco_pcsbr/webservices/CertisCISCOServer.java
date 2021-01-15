package com.novigosolutions.certiscisco_pcsbr.webservices;

import android.content.Context;

import com.novigosolutions.certiscisco_pcsbr.applications.CertisCISCO;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;

import java.util.Random;

public  class CertisCISCOServer {


    static Random rand = new Random();

    //Production
//    static String[] IPS = new String[]{"http://10.8.8.150/", "http://10.8.8.180/"};
//    public static String getIP() {
//        return IPS[rand.nextInt(2)];
//    }
//    public static String getPATH() {
//        return IPS[rand.nextInt(2)] + "api/DeviceApi/";
//    }
//    public static String SIGNALR_IP = "http://10.8.8.70/signalr";


//    public static String getIP() {
//        return "http://172.16.17.185/PCSAPI/";
//    }
//    public static String getPATH() {
//        return "http://172.16.17.185/PCSAPI/api/DeviceApi/";
//    }
//    public static String SIGNALR_IP = "http://10.8.8.70/signalr";

        //UAT
//    static String[] IPS = new String[]{"http://certisuatapi.azurewebsites.net/", "http://certisuatapi.azurewebsites.net/"};
//    public static String getIP() {
//        return IPS[rand.nextInt(2)];
//    }
//    public static String getPATH() {
//        return IPS[rand.nextInt(2)] + "api/DeviceApi/";
//    }
//    public static String SIGNALR_IP = "http://certisin.azurewebsites.net/signalr";


    //UAT2
//    static String[] IPS = new String[]{"https://pcs-bnruatapi.certispcs.com/", "https://pcs-bnruatapi.certispcs.com/"};
//    public static String getIP() {
//        return IPS[rand.nextInt(2)];
//    }
//    public static String getPATH() {
//        return IPS[rand.nextInt(2)] + "api/DeviceApi/";
//    }
    public static String SIGNALR_IP = "http://certisin.azurewebsites.net/signalr";


    //public static String IP = "http://certisuatapi.azurewebsites.net/";
//    public static String SIGNALR_IP = "http://certisin.azurewebsites.net/signalr";

    //    public static String IP = "http://certisciscoatmsystem.novigotest.com/";
//    public static String SIGNALR_IP = "http://certiscisco.novigotest.com/signalr";

//     Development
//    public static String IP = "http://certiswebapi.novigotest.com/";
//    public static String SIGNALR_IP = "http://certiscisco.novigotest.com/signalr";
//
//    public static String PATH = IP + "api/DeviceApi/";
//    public static String getPATH() {
//        return PATH;
//    }
//    public static String getIP() {
//        return IP;
//    }

    //Dyncamic URL
    public static String getIP() {
        return Preferences.getString("API_URL", CertisCISCO.getContext())+"/";
    }

    public static String getPATH() {
        return getIP() + "api/DeviceApi/";
    }
}
