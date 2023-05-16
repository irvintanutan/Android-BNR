package com.novigosolutions.certiscisco_pcsbr.webservices;

import com.novigosolutions.certiscisco_pcsbr.applications.CertisCISCO;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;

import java.util.Random;

public  class CertisCISCOServer {
    static Random rand = new Random();
    public static String SIGNALR_IP = "http://certisin.azurewebsites.net/signalr";

    //Dyncamic URL
    public static String getIP() {
        return Preferences.getString("API_URL", CertisCISCO.getContext())+"/";
    }

    public static String getPATH() {
        return getIP() + "api/DeviceApi/";
    }
}
