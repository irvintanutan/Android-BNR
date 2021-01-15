package com.novigosolutions.certiscisco_pcsbr.interfaces;


/**
 * Created by dalveersinghdaiya on 19/10/16.
 */

public interface ApiCallback {

    void onResult(int api_code, int result_code, String result_data);
}
