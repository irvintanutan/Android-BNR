package com.novigosolutions.certiscisco_pcsbr.activites;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.novigosolutions.certiscisco_pcsbr.BuildConfig;
import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.applications.CertisCISCO;
import com.novigosolutions.certiscisco_pcsbr.interfaces.ApiCallback;
import com.novigosolutions.certiscisco_pcsbr.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.recivers.IntervalChangedReceiver;
import com.novigosolutions.certiscisco_pcsbr.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;
import com.novigosolutions.certiscisco_pcsbr.webservices.CertisCISCOServices;
import com.novigosolutions.certiscisco_pcsbr.webservices.SyncDatabase;
import com.novigosolutions.certiscisco_pcsbr.webservices.UnsafeOkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * LoginActivity.java - class that loads first.
 *
 * @author dhanrajk
 * @version 1.0
 * @compmany novigosolutions
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, ApiCallback, NetworkChangekListener {
    EditText edtteamid, edtpassword;
    Button btn_login, btn_clear;
    TextView deviceid;
    TextInputLayout mtxtinUserid, mtxtinPassword;
    ImageView imgnetwork;
    Spinner mspindate;
    TextView version;
    CoordinatorLayout clv;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    AlertDialog dialog;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setuptoolbar();
        initializeviews();
        setactions();
    }

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("LOGIN");
        imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
    }

    private void initializeviews() {
        version = findViewById(R.id.version);
        version.setText(BuildConfig.VERSION_NAME);
        edtteamid = (EditText) findViewById(R.id.edtTeamid);
        edtpassword = (EditText) findViewById(R.id.edtPassword);
        mtxtinUserid = (TextInputLayout) findViewById(R.id.txtinputuserid);
        mtxtinPassword = (TextInputLayout) findViewById(R.id.txtinputpassword);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        deviceid = (TextView) findViewById(R.id.deviceid);
        mspindate = (Spinner) findViewById(R.id.spndate);
        clv = (CoordinatorLayout) findViewById(R.id.cl);
    }

    private void setactions() {
        btn_login.setOnClickListener(this);
        btn_clear.setOnClickListener(this);

        edtteamid.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                if (edtteamid.getText().length() > 0) {
                    mtxtinUserid.setError(null);
                    mtxtinUserid.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }
        });
        edtpassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                if (edtpassword.getText().length() > 0) {
                    mtxtinPassword.setError(null);
                    mtxtinPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }
        });

        Calendar calendar = Calendar.getInstance();

        List<String> dates = new ArrayList<String>();
        calendar.add(Calendar.DATE, -1);
        dates.add(sdf.format(calendar.getTime()));
        calendar.add(Calendar.DATE, +1);
        dates.add(sdf.format(calendar.getTime()));
        calendar.add(Calendar.DATE, +1);
        dates.add(sdf.format(calendar.getTime()));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dates);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspindate.setAdapter(dataAdapter);
        mspindate.setSelection(1);
        setupUI(clv, LoginActivity.this);
    }

    private void setDeviceid() {
        String device = Preferences.getString("DeviceID", LoginActivity.this);
        if (device.length() > 0) {
            deviceid.setText("Device ID : " + device);
        }
    }

    @Override
    public void onClick(View v) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        int id = v.getId();
        switch (id) {
            case R.id.btn_login:
                if (!TextUtils.isEmpty(Preferences.getString("API_URL", CertisCISCO.getContext()))) {
                    validate();
//                dumpdummydata();
                } else {
                    Toast.makeText(this, "Please set API URL", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_clear:
                edtteamid.setText("");
                edtpassword.setText("");
                mtxtinUserid.setError(null);
                mtxtinUserid.setErrorEnabled(false);
                mtxtinPassword.setError(null);
                mtxtinPassword.setErrorEnabled(false);
                break;
        }

    }

    private void validate() {
        Boolean failflag = false;
        String teamid, password;
        teamid = edtteamid.getText().toString();
        password = edtpassword.getText().toString();
        if (teamid.isEmpty()) {
            mtxtinUserid.setError("User Id is required");
            failflag = true;
        }
        if (password.isEmpty()) {
            mtxtinPassword.setError("Password is required");
            failflag = true;
        }
        if (!failflag) {
            if (NetworkUtil.getConnectivityStatusString(LoginActivity.this)) {
                JsonObject json = new JsonObject();
                try {
                    json.addProperty("DeviceId", Preferences.getString("DeviceID", LoginActivity.this));
                    json.addProperty("UserCode", false ? "TEST" : teamid);
                    json.addProperty("Password", false ? "TEST" : password);
                    json.addProperty("LoginDate", false ? "2023-05-19": sdf2.format(sdf.parse(mspindate.getSelectedItem().toString())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.e("json", json.toString());
                login(json);
            } else {
                raiseInternetSnakbar();
            }
        }
    }

    private void login(JsonObject jsonObject) {
        showProgressDialog("Loading...");
        APICaller.instance().login(this, jsonObject);
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.pressback), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void savejob(JSONArray jobs) {
        try {

            Branch.clearAllTables();
            SyncDatabase.instance().addnew(jobs, LoginActivity.this);
            Intent intent = new Intent(this, IntervalChangedReceiver.class);
            sendBroadcast(intent);
            Preferences.saveBoolean("LoggedIn", true, LoginActivity.this);
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveCoinSeries(JSONObject jsonObject) {
        SyncDatabase.instance().saveCoinSeries(jsonObject, LoginActivity.this);
    }

    private void SavePrintSetting(JSONObject jsonObject) {
        try {
            if (jsonObject.getJSONArray("PrintReceiptSetting") != null) {
                JSONArray jsonArray = jsonObject.getJSONArray("PrintReceiptSetting");
                JSONObject jsonArray1 = jsonArray.getJSONObject(0);
                String logo = jsonArray1.getString("logo");
                String HeaderText = jsonArray1.getString("HeaderText");
                String FooterText = jsonArray1.getString("FooterText");
                FooterText = FooterText.replaceAll("\n", "<br>");
                Preferences.savePrintlogo(logo, LoginActivity.this);
                Preferences.savePrintHeader(HeaderText, LoginActivity.this);
                Preferences.savePrintFooter(FooterText, LoginActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDeviceid();
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
    }

    @Override
    public void onNetworkChanged() {
        if (NetworkUtil.getConnectivityStatusString(this))
            imgnetwork.setImageResource(R.drawable.network);
        else
            imgnetwork.setImageResource(R.drawable.no_network);
    }

    private void dumpdummydata() {
        try {
//            JSONArray jsonArray =new JSONArray("[[{\"Token\":\"AB61EC2D-B8FF-492B-A14B-542D974E3F36\",\"LoggedInUser\":383,\"UserName\":\"R12\",\"UserCode\":\"62909\",\"TeamId\":596,\"Role\":\"Supervisor\",\"LoginTime\":\"24-Dec-2019 01:02:52\",\"EnableManualEntry\":false}],[{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":443,\"BranchCode\":\"CIMB BANK @  SINGAPORE LAND TOWER\\n\",\"FunctionalCode\":\"HANSHAN-CIMB\",\"StreetName\":\"RAFFLES PLACE\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"48623\",\"CustomerCode\":\"16031311\",\"CustomerName\":\"HANSHAN MONEY EXPRESS PTE. LTD.\",\"CustomerId\":66,\"SequenceNo\":\"2\",\"Jobs\":[{\"TransportMasterId\":44315,\"FloatDeliveryOrderId\":7610,\"CollectionOrderId\":0,\"OrderNo\":\"TJ1912173123\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":true,\"IsCollectionOrder\":false,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"16031311\",\"CustomerName\":\"HANSHAN MONEY EXPRESS PTE. LTD.\",\"CustomerId\":66,\"PointId\":443,\"BranchCode\":\"CIMB BANK @  SINGAPORE LAND TOWER\\n\",\"FunctionalCode\":\"HANSHAN-CIMB\",\"StreetName\":\"RAFFLES PLACE\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"48623\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"122391946\",\"ProductCurrency\":[],\"DeliveryList\":[{\"TransportMasterId\":44315,\"FloatDeliveryOrderId\":7610,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA2968674\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1},{\"TransportMasterId\":44315,\"FloatDeliveryOrderId\":7610,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA5914659\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1},{\"TransportMasterId\":44315,\"FloatDeliveryOrderId\":7610,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA5974291\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1},{\"TransportMasterId\":44315,\"FloatDeliveryOrderId\":7610,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA5974292\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1},{\"TransportMasterId\":44315,\"FloatDeliveryOrderId\":7610,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA5974293\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1}],\"ReffId\":36937,\"SequenceNo\":\"2\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 18:00\",\"JobEndTime\":\"2019-12-23 20:00\",\"BranchETA\":\"2019-12-23T18:00:00\",\"BranchETD\":\"2019-12-23T20:00:00\",\"ClientBreak\":\"      -      \",\"OrderRemarks\": \"Delivery for Collected Items to Target\"}],\"StartTime\":\"2019-12-23 18:00\",\"EndTime\":\"2019-12-23 20:00\",\"ClientBreak\":\"      -      \",\"OrderRemarks\": \"Delivery for Collected Items to Target\",},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":990,\"BranchCode\":\"MARINA SQUARE\",\"FunctionalCode\":\"ROBINSO-MARINASQ\",\"StreetName\":\"6 RAFFLES BLVD\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"39594\",\"CustomerCode\":\"102228\",\"CustomerName\":\"ROBINSON & COMPANY (SINGAPORE) PRIV\",\"CustomerId\":160,\"SequenceNo\":\"11\",\"Jobs\":[{\"TransportMasterId\":44449,\"FloatDeliveryOrderId\":7620,\"CollectionOrderId\":0,\"OrderNo\":\"TJ1912173257\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":true,\"IsCollectionOrder\":false,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"102228\",\"CustomerName\":\"ROBINSON & COMPANY (SINGAPORE) PRIV\",\"CustomerId\":160,\"PointId\":990,\"BranchCode\":\"MARINA SQUARE\",\"FunctionalCode\":\"ROBINSO-MARINASQ\",\"StreetName\":\"6 RAFFLES BLVD\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"39594\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"122391946\",\"ProductCurrency\":[],\"DeliveryList\":[{\"TransportMasterId\":44449,\"FloatDeliveryOrderId\":7620,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA5786697\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1}],\"ReffId\":37038,\"SequenceNo\":\"11\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 10:30\",\"JobEndTime\":\"2019-12-23 15:00\",\"BranchETA\":\"2019-12-23T10:30:00\",\"BranchETD\":\"2019-12-23T15:00:00\",\"ClientBreak\":\"      -      \",\"OrderRemarks\": \"Delivery for Collected Items to Target\"},{\"TransportMasterId\":44450,\"FloatDeliveryOrderId\":7621,\"CollectionOrderId\":0,\"OrderNo\":\"TJ1912173258\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":true,\"IsCollectionOrder\":false,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"102228\",\"CustomerName\":\"ROBINSON & COMPANY (SINGAPORE) PRIV\",\"CustomerId\":160,\"PointId\":990,\"BranchCode\":\"MARINA SQUARE\",\"FunctionalCode\":\"ROBINSO-MARINASQ\",\"StreetName\":\"6 RAFFLES BLVD\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"39594\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"122391946\",\"ProductCurrency\":[],\"DeliveryList\":[{\"TransportMasterId\":44450,\"FloatDeliveryOrderId\":7621,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA6039505\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1}],\"ReffId\":37050,\"SequenceNo\":\"11\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 10:30\",\"JobEndTime\":\"2019-12-23 15:00\",\"BranchETA\":\"2019-12-23T10:30:00\",\"BranchETD\":\"2019-12-23T15:00:00\",\"ClientBreak\":\"      -      \",\"OrderRemarks\": \"Delivery for Collected Items to Target\"},{\"TransportMasterId\":44451,\"FloatDeliveryOrderId\":7622,\"CollectionOrderId\":0,\"OrderNo\":\"TJ1912173259\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":true,\"IsCollectionOrder\":false,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"102228\",\"CustomerName\":\"ROBINSON & COMPANY (SINGAPORE) PRIV\",\"CustomerId\":160,\"PointId\":990,\"BranchCode\":\"MARINA SQUARE\",\"FunctionalCode\":\"ROBINSO-MARINASQ\",\"StreetName\":\"6 RAFFLES BLVD\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"39594\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"122391946\",\"ProductCurrency\":[],\"DeliveryList\":[{\"TransportMasterId\":44451,\"FloatDeliveryOrderId\":7622,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA5774779\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1}],\"ReffId\":37051,\"SequenceNo\":\"11\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 10:30\",\"JobEndTime\":\"2019-12-23 15:00\",\"BranchETA\":\"2019-12-23T10:30:00\",\"BranchETD\":\"2019-12-23T15:00:00\",\"ClientBreak\":\"      -      \",\"OrderRemarks\": \"Delivery for Collected Items to Target\"},{\"TransportMasterId\":44452,\"FloatDeliveryOrderId\":7623,\"CollectionOrderId\":0,\"OrderNo\":\"TJ1912173260\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":true,\"IsCollectionOrder\":false,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"102228\",\"CustomerName\":\"ROBINSON & COMPANY (SINGAPORE) PRIV\",\"CustomerId\":160,\"PointId\":990,\"BranchCode\":\"MARINA SQUARE\",\"FunctionalCode\":\"ROBINSO-MARINASQ\",\"StreetName\":\"6 RAFFLES BLVD\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"39594\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"122391946\",\"ProductCurrency\":[],\"DeliveryList\":[{\"TransportMasterId\":44452,\"FloatDeliveryOrderId\":7623,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA5842503\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1}],\"ReffId\":37055,\"SequenceNo\":\"11\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 10:30\",\"JobEndTime\":\"2019-12-23 15:00\",\"BranchETA\":\"2019-12-23T10:30:00\",\"BranchETD\":\"2019-12-23T15:00:00\",\"ClientBreak\":\"      -      \",\"OrderRemarks\": \"Delivery for Collected Items to Target\"},{\"TransportMasterId\":44453,\"FloatDeliveryOrderId\":7624,\"CollectionOrderId\":0,\"OrderNo\":\"TJ1912173261\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":true,\"IsCollectionOrder\":false,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"102228\",\"CustomerName\":\"ROBINSON & COMPANY (SINGAPORE) PRIV\",\"CustomerId\":160,\"PointId\":990,\"BranchCode\":\"MARINA SQUARE\",\"FunctionalCode\":\"ROBINSO-MARINASQ\",\"StreetName\":\"6 RAFFLES BLVD\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"39594\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"122391946\",\"ProductCurrency\":[],\"DeliveryList\":[{\"TransportMasterId\":44453,\"FloatDeliveryOrderId\":7624,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA5980089\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1}],\"ReffId\":37045,\"SequenceNo\":\"11\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 10:30\",\"JobEndTime\":\"2019-12-23 15:30\",\"BranchETA\":\"2019-12-23T10:30:00\",\"BranchETD\":\"2019-12-23T15:30:00\",\"ClientBreak\":\"      -      \"},{\"TransportMasterId\":44454,\"FloatDeliveryOrderId\":7625,\"CollectionOrderId\":0,\"OrderNo\":\"TJ1912173262\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":true,\"IsCollectionOrder\":false,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"102228\",\"CustomerName\":\"ROBINSON & COMPANY (SINGAPORE) PRIV\",\"CustomerId\":160,\"PointId\":990,\"BranchCode\":\"MARINA SQUARE\",\"FunctionalCode\":\"ROBINSO-MARINASQ\",\"StreetName\":\"6 RAFFLES BLVD\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"39594\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"122391946\",\"ProductCurrency\":[],\"DeliveryList\":[{\"TransportMasterId\":44454,\"FloatDeliveryOrderId\":7625,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA5918665\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1}],\"ReffId\":37043,\"SequenceNo\":\"11\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 13:00\",\"JobEndTime\":\"2019-12-23 17:00\",\"BranchETA\":\"2019-12-23T13:00:00\",\"BranchETD\":\"2019-12-23T17:00:00\",\"ClientBreak\":\"      -      \"},{\"TransportMasterId\":44455,\"FloatDeliveryOrderId\":7626,\"CollectionOrderId\":0,\"OrderNo\":\"TJ1912173263\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":true,\"IsCollectionOrder\":false,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"102228\",\"CustomerName\":\"ROBINSON & COMPANY (SINGAPORE) PRIV\",\"CustomerId\":160,\"PointId\":990,\"BranchCode\":\"MARINA SQUARE\",\"FunctionalCode\":\"ROBINSO-MARINASQ\",\"StreetName\":\"6 RAFFLES BLVD\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"39594\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"122391946\",\"ProductCurrency\":[],\"DeliveryList\":[{\"TransportMasterId\":44455,\"FloatDeliveryOrderId\":7626,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA5842499\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1}],\"ReffId\":37056,\"SequenceNo\":\"11\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 13:00\",\"JobEndTime\":\"2019-12-23 17:00\",\"BranchETA\":\"2019-12-23T13:00:00\",\"BranchETD\":\"2019-12-23T17:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 10:30\",\"EndTime\":\"2019-12-23 17:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":1437,\"BranchCode\":\"CIMB BANK @  SINGAPORE LAND TOWER\\n\",\"FunctionalCode\":\"ZGRPL00-CIMB\",\"StreetName\":\"RAFFLES PLACE\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"48623\",\"CustomerCode\":\"16039414\",\"CustomerName\":\"ZHONGGUO REMITTANCE PTE. LTD.\",\"CustomerId\":219,\"SequenceNo\":\"2\",\"Jobs\":[{\"TransportMasterId\":44708,\"FloatDeliveryOrderId\":7673,\"CollectionOrderId\":0,\"OrderNo\":\"TJ1912173516\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":true,\"IsCollectionOrder\":false,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"16039414\",\"CustomerName\":\"ZHONGGUO REMITTANCE PTE. LTD.\",\"CustomerId\":219,\"PointId\":1437,\"BranchCode\":\"CIMB BANK @  SINGAPORE LAND TOWER\\n\",\"FunctionalCode\":\"ZGRPL00-CIMB\",\"StreetName\":\"RAFFLES PLACE\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"48623\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204355\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":37266,\"SequenceNo\":\"2\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 18:00\",\"JobEndTime\":\"2019-12-23 20:00\",\"BranchETA\":\"2019-12-23T18:00:00\",\"BranchETD\":\"2019-12-23T20:00:00\",\"ClientBreak\":\"      -      \"},{\"TransportMasterId\":44709,\"FloatDeliveryOrderId\":7674,\"CollectionOrderId\":0,\"OrderNo\":\"TJ1912173517\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":true,\"IsCollectionOrder\":false,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"16039414\",\"CustomerName\":\"ZHONGGUO REMITTANCE PTE. LTD.\",\"CustomerId\":219,\"PointId\":1437,\"BranchCode\":\"CIMB BANK @  SINGAPORE LAND TOWER\\n\",\"FunctionalCode\":\"ZGRPL00-CIMB\",\"StreetName\":\"RAFFLES PLACE\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"48623\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"122391946\",\"ProductCurrency\":[],\"DeliveryList\":[{\"TransportMasterId\":44709,\"FloatDeliveryOrderId\":7674,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA5966231\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1},{\"TransportMasterId\":44709,\"FloatDeliveryOrderId\":7674,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA5966232\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1},{\"TransportMasterId\":44709,\"FloatDeliveryOrderId\":7674,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA5966233\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1},{\"TransportMasterId\":44709,\"FloatDeliveryOrderId\":7674,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA5966234\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1},{\"TransportMasterId\":44709,\"FloatDeliveryOrderId\":7674,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"BA5966235\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1}],\"ReffId\":37265,\"SequenceNo\":\"2\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 18:00\",\"JobEndTime\":\"2019-12-23 20:00\",\"BranchETA\":\"2019-12-23T18:00:00\",\"BranchETD\":\"2019-12-23T20:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 18:00\",\"EndTime\":\"2019-12-23 20:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"PENDING\",\"PointId\":1438,\"BranchCode\":\"RHB BANK BUILDING\",\"FunctionalCode\":\"ZGRPL00-RHB\",\"StreetName\":\"90 CECIL STREET\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"69531\",\"CustomerCode\":\"16039414\",\"CustomerName\":\"ZHONGGUO REMITTANCE PTE. LTD.\",\"CustomerId\":219,\"SequenceNo\":\"1\",\"Jobs\":[{\"TransportMasterId\":44710,\"FloatDeliveryOrderId\":7675,\"CollectionOrderId\":0,\"OrderNo\":\"TJ1912173518\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":true,\"IsCollectionOrder\":false,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"16039414\",\"CustomerName\":\"ZHONGGUO REMITTANCE PTE. LTD.\",\"CustomerId\":219,\"PointId\":1438,\"BranchCode\":\"RHB BANK BUILDING\",\"FunctionalCode\":\"ZGRPL00-RHB\",\"StreetName\":\"90 CECIL STREET\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"69531\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204355\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":37263,\"SequenceNo\":\"1\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"PENDING\",\"JobStartTime\":\"2019-12-23 18:00\",\"JobEndTime\":\"2019-12-23 20:00\",\"BranchETA\":\"2019-12-23T18:00:00\",\"BranchETD\":\"2019-12-23T20:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 18:00\",\"EndTime\":\"2019-12-23 20:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":64,\"BranchCode\":\"MARINA BAY SANDS\",\"FunctionalCode\":\"BOTTEGA-MBSB2\",\"StreetName\":\"2 BAYFRONT AVE #B2-72A/73/74 THE SHOPPES MARINA BAY SANDS\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"18972\",\"CustomerCode\":\"16015005\",\"CustomerName\":\"BOTTEGA VENETA SINGAPORE PRIVATE LI\",\"CustomerId\":21,\"SequenceNo\":\"19\",\"Jobs\":[{\"TransportMasterId\":44756,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37298,\"OrderNo\":\"TJ1912173556\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1820,\"BranchCode\":\"VCS - PL - VAULT\",\"FunctionalCode\":\"VCS - PL - VAULT\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204713\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"19\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 10:30\",\"JobEndTime\":\"2019-12-23 20:00\",\"BranchETA\":\"2019-12-23T10:30:00\",\"BranchETD\":\"2019-12-23T20:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 10:30\",\"EndTime\":\"2019-12-23 20:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":68,\"BranchCode\":\"DING TAI FENG @ MARINA BAY SAND\",\"FunctionalCode\":\"BREADTK-MBSDTF\",\"StreetName\":\"2 BAYFRONT AVENUE #B1-01 MARINA BAY SANDS DING TAI FENG\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"18972\",\"CustomerCode\":\"16081900\",\"CustomerName\":\"BREADTALK GROUP LIMITED\",\"CustomerId\":22,\"SequenceNo\":\"15\",\"Jobs\":[{\"TransportMasterId\":44760,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37308,\"OrderNo\":\"TJ1912173560\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204713\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"15\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 12:00\",\"JobEndTime\":\"2019-12-23 22:00\",\"BranchETA\":\"2019-12-23T12:00:00\",\"BranchETD\":\"2019-12-23T22:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 12:00\",\"EndTime\":\"2019-12-23 22:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":73,\"BranchCode\":\"DING TAI FENG @ CHINATOWN POINT\",\"FunctionalCode\":\"BREADTK-CHIDTF\",\"StreetName\":\"133 NEW BRIDGE ROAD, #02-01/02\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"59413\",\"CustomerCode\":\"16081900\",\"CustomerName\":\"BREADTALK GROUP LIMITED\",\"CustomerId\":22,\"SequenceNo\":\"23\",\"Jobs\":[{\"TransportMasterId\":44765,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37306,\"OrderNo\":\"TJ1912173565\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204713\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"23\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 12:00\",\"JobEndTime\":\"2019-12-23 22:00\",\"BranchETA\":\"2019-12-23T12:00:00\",\"BranchETD\":\"2019-12-23T22:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 12:00\",\"EndTime\":\"2019-12-23 22:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":161,\"BranchCode\":\"MARINA BAY SAND #B1-138/139/140\",\"FunctionalCode\":\"DOLCE00-MBS\",\"StreetName\":\"2 BAYFRONT AVENUE #B1-138/139/140 THE SHOPPES AT MARINA BAY SANDS\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"18972\",\"CustomerCode\":\"16095320\",\"CustomerName\":\"DOLCE & GABBANA SINGAPORE PTE. LTD.\",\"CustomerId\":43,\"SequenceNo\":\"13\",\"Jobs\":[{\"TransportMasterId\":44837,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37367,\"OrderNo\":\"TJ1912173637\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204719\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"13\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 11:00\",\"JobEndTime\":\"2019-12-23 19:00\",\"BranchETA\":\"2019-12-23T11:00:00\",\"BranchETD\":\"2019-12-23T19:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 11:00\",\"EndTime\":\"2019-12-23 19:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":162,\"BranchCode\":\"MARINA BAY SAND #B1-55A\",\"FunctionalCode\":\"DOLCE00-MBS55A\",\"StreetName\":\"2 BAYFRONT AVENUE #B1-55A THE SHOPPES AT MARINA BAY SANDS\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"18972\",\"CustomerCode\":\"16095320\",\"CustomerName\":\"DOLCE & GABBANA SINGAPORE PTE. LTD.\",\"CustomerId\":43,\"SequenceNo\":\"20\",\"Jobs\":[{\"TransportMasterId\":44838,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37366,\"OrderNo\":\"TJ1912173638\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204719\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"20\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 11:00\",\"JobEndTime\":\"2019-12-23 19:00\",\"BranchETA\":\"2019-12-23T11:00:00\",\"BranchETD\":\"2019-12-23T19:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 11:00\",\"EndTime\":\"2019-12-23 19:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":165,\"BranchCode\":\"PEARL'S HILL\",\"FunctionalCode\":\"EMSVCPL-PEARL\",\"StreetName\":\"201 PEARL'S HILL TERRACE\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"168977\",\"CustomerCode\":\"16138704\",\"CustomerName\":\"E M REAL ESTATE PTE LTD\",\"CustomerId\":44,\"SequenceNo\":\"25\",\"Jobs\":[{\"TransportMasterId\":44841,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37370,\"OrderNo\":\"TJ1912173641\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204719\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"25\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 10:30\",\"JobEndTime\":\"2019-12-23 17:30\",\"BranchETA\":\"2019-12-23T10:30:00\",\"BranchETD\":\"2019-12-23T17:30:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 10:30\",\"EndTime\":\"2019-12-23 17:30\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":196,\"BranchCode\":\"FEO - FAR EAST SQUARE (DEAN & DELUCA)\",\"FunctionalCode\":\"FAREAST-FES1\",\"StreetName\":\"47 PEKIN STREET #01-01 S048777 (AVOID LUNCH 1200 - 1400 HRS)\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"48777\",\"CustomerCode\":\"500163\",\"CustomerName\":\"FAR EAST MANAGEMENT PTE LTD\",\"CustomerId\":48,\"SequenceNo\":\"5\",\"Jobs\":[{\"TransportMasterId\":44852,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37378,\"OrderNo\":\"TJ1912173652\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204720\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"5\",\"CanCollectedBag\":false,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":true,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 10:00\",\"JobEndTime\":\"2019-12-23 17:00\",\"BranchETA\":\"2019-12-23T10:00:00\",\"BranchETD\":\"2019-12-23T17:00:00\",\"ClientBreak\":\"12:00 - 14:00\"}],\"StartTime\":\"2019-12-23 10:00\",\"EndTime\":\"2019-12-23 17:00\",\"ClientBreak\":\"12:00 - 14:00\"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":2147,\"BranchCode\":\"FEO - AMOY\",\"FunctionalCode\":\"FAREAST-FEC5\",\"StreetName\":\"76 TELOK AYER STREET\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"48464\",\"CustomerCode\":\"500163\",\"CustomerName\":\"FAR EAST MANAGEMENT PTE LTD\",\"CustomerId\":48,\"SequenceNo\":\"6\",\"Jobs\":[{\"TransportMasterId\":44878,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37389,\"OrderNo\":\"TJ1912173678\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204720\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"6\",\"CanCollectedBag\":false,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":true,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 10:00\",\"JobEndTime\":\"2019-12-23 17:00\",\"BranchETA\":\"2019-12-23T10:00:00\",\"BranchETD\":\"2019-12-23T17:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 10:00\",\"EndTime\":\"2019-12-23 17:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":279,\"BranchCode\":\"COS @ MARINA BAY SAND\",\"FunctionalCode\":\"H&M0000-MBSCOS\",\"StreetName\":\"10 BAYFRONT AVENUE MARINA BAY SANDS B2-98/99/100\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"18972\",\"CustomerCode\":\"16060498\",\"CustomerName\":\"H & M HENNES & MAURITZ PTE. LTD.\",\"CustomerId\":62,\"SequenceNo\":\"18\",\"Jobs\":[{\"TransportMasterId\":44902,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37431,\"OrderNo\":\"TJ1912173702\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204723\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"18\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 13:00\",\"JobEndTime\":\"2019-12-23 18:00\",\"BranchETA\":\"2019-12-23T13:00:00\",\"BranchETD\":\"2019-12-23T18:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 13:00\",\"EndTime\":\"2019-12-23 18:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":307,\"BranchCode\":\"SPRINGLEAF TOWER\",\"FunctionalCode\":\"MACD000-150\",\"StreetName\":\"23 ANSON ROAD #01-03 SPRINGLEAF TOWER\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"59413\",\"CustomerCode\":\"104478\",\"CustomerName\":\"HANBAOBAO PTE. LTD.\",\"CustomerId\":64,\"SequenceNo\":\"21\",\"Jobs\":[{\"TransportMasterId\":44915,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37446,\"OrderNo\":\"TJ1912173715\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1820,\"BranchCode\":\"VCS - PL - VAULT\",\"FunctionalCode\":\"VCS - PL - VAULT\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204725\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"21\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 10:00\",\"JobEndTime\":\"2019-12-23 22:00\",\"BranchETA\":\"2019-12-23T10:00:00\",\"BranchETD\":\"2019-12-23T22:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 10:00\",\"EndTime\":\"2019-12-23 22:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":369,\"BranchCode\":\"RAFFLES CITY SHOPPING CENTRE\",\"FunctionalCode\":\"MACD000-RC153\",\"StreetName\":\"252 NORTH BRIDGE ROAD #01-49/50/51\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"179103\",\"CustomerCode\":\"104478\",\"CustomerName\":\"HANBAOBAO PTE. LTD.\",\"CustomerId\":64,\"SequenceNo\":\"4\",\"Jobs\":[{\"TransportMasterId\":44933,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37444,\"OrderNo\":\"TJ1912173733\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1820,\"BranchCode\":\"VCS - PL - VAULT\",\"FunctionalCode\":\"VCS - PL - VAULT\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204725\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"4\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 10:00\",\"JobEndTime\":\"2019-12-23 22:00\",\"BranchETA\":\"2019-12-23T10:00:00\",\"BranchETD\":\"2019-12-23T22:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 10:00\",\"EndTime\":\"2019-12-23 22:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":552,\"BranchCode\":\"CLUB MONACO\",\"FunctionalCode\":\"CRAWFOR-MONACO\",\"StreetName\":\"2 BAYFRONT AVENUE #B2-101 CANAL LEVEL THE SHOPPES AT MARINA BAY SANDS\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"18972\",\"CustomerCode\":\"104437\",\"CustomerName\":\"LANE CRAWFORD (SINGAPORE) PTE LTD\",\"CustomerId\":96,\"SequenceNo\":\"17\",\"Jobs\":[{\"TransportMasterId\":45037,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37533,\"OrderNo\":\"TJ1912173837\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204733\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"17\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 12:00\",\"JobEndTime\":\"2019-12-23 21:00\",\"BranchETA\":\"2019-12-23T12:00:00\",\"BranchETD\":\"2019-12-23T21:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 12:00\",\"EndTime\":\"2019-12-23 21:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":622,\"BranchCode\":\"RIVERSIDE POINT\",\"FunctionalCode\":\"NIKE000-RVSDPT\",\"StreetName\":\"30 MERCHANT ROAD #2-18/23\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"58282\",\"CustomerCode\":\"101902\",\"CustomerName\":\"NIKE SINGAPORE PTE LTD\",\"CustomerId\":126,\"SequenceNo\":\"27\",\"Jobs\":[{\"TransportMasterId\":45067,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37554,\"OrderNo\":\"TJ1912173867\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204737\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"27\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 11:00\",\"JobEndTime\":\"2019-12-23 17:00\",\"BranchETA\":\"2019-12-23T11:00:00\",\"BranchETD\":\"2019-12-23T17:00:00\",\"ClientBreak\":\"      -      \"},{\"TransportMasterId\":45068,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37555,\"OrderNo\":\"TJ1912173868\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1820,\"BranchCode\":\"VCS - PL - VAULT\",\"FunctionalCode\":\"VCS - PL - VAULT\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204737\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"27\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 11:00\",\"JobEndTime\":\"2019-12-23 17:00\",\"BranchETA\":\"2019-12-23T11:00:00\",\"BranchETD\":\"2019-12-23T17:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 11:00\",\"EndTime\":\"2019-12-23 17:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":773,\"BranchCode\":\"361-FAIRPRICE TG PAGAR PLAZA (TGP)\",\"FunctionalCode\":\"NTUC-TGP\",\"StreetName\":\"BLK 5 TANJONG PAGAR PLAZA, #01-01 #01-01\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"81005\",\"CustomerCode\":\"104749\",\"CustomerName\":\"NTUC FAIRPRICE CO-OPERATIVE LTD\",\"CustomerId\":128,\"SequenceNo\":\"7\",\"Jobs\":[{\"TransportMasterId\":45141,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37675,\"OrderNo\":\"TJ1912173941\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204739\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"7\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 10:00\",\"JobEndTime\":\"2019-12-23 21:00\",\"BranchETA\":\"2019-12-23T10:00:00\",\"BranchETD\":\"2019-12-23T21:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 10:00\",\"EndTime\":\"2019-12-23 21:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":835,\"BranchCode\":\"463-FAIRPRICE CHINATOWN POINT (CHINATWN)\",\"FunctionalCode\":\"FP00000-463\",\"StreetName\":\"CHINATOWN POINT (CHINATWN - 463)\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"59413\",\"CustomerCode\":\"104749\",\"CustomerName\":\"NTUC FAIRPRICE CO-OPERATIVE LTD\",\"CustomerId\":128,\"SequenceNo\":\"22\",\"Jobs\":[{\"TransportMasterId\":45181,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37619,\"OrderNo\":\"TJ1912173981\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204739\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"22\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 10:00\",\"JobEndTime\":\"2019-12-23 21:00\",\"BranchETA\":\"2019-12-23T10:00:00\",\"BranchETD\":\"2019-12-23T21:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 10:00\",\"EndTime\":\"2019-12-23 21:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":904,\"BranchCode\":\"PSB ACADEMY - MARINA SQ @ 6 RAFFLES BLVD\",\"FunctionalCode\":\"PSBACAD-MARINA\",\"StreetName\":\"6 RAFFLES BOULEVARD #03-200 MARINA SQUARE\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"39594\",\"CustomerCode\":\"16029674\",\"CustomerName\":\"PSB ACADEMY PTE. LTD.\",\"CustomerId\":149,\"SequenceNo\":\"10\",\"Jobs\":[{\"TransportMasterId\":45208,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37692,\"OrderNo\":\"TJ1912174008\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204747\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"10\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 09:00\",\"JobEndTime\":\"2019-12-23 17:00\",\"BranchETA\":\"2019-12-23T09:00:00\",\"BranchETD\":\"2019-12-23T17:00:00\",\"ClientBreak\":\"      -      \"},{\"TransportMasterId\":45209,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37693,\"OrderNo\":\"TJ1912174009\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1820,\"BranchCode\":\"VCS - PL - VAULT\",\"FunctionalCode\":\"VCS - PL - VAULT\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204747\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"10\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 09:00\",\"JobEndTime\":\"2019-12-23 17:00\",\"BranchETA\":\"2019-12-23T09:00:00\",\"BranchETD\":\"2019-12-23T17:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 09:00\",\"EndTime\":\"2019-12-23 17:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":975,\"BranchCode\":\"MARK & SPENCER @ MARINA SQUARE\",\"FunctionalCode\":\"ROBINSO-M&SMS\",\"StreetName\":\"#02-109/111TO 117 AND #03-100 TO 107 MARK & SPENCER MARINA SQUARE\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"39594\",\"CustomerCode\":\"102228\",\"CustomerName\":\"ROBINSON & COMPANY (SINGAPORE) PRIV\",\"CustomerId\":160,\"SequenceNo\":\"12\",\"Jobs\":[{\"TransportMasterId\":45237,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37733,\"OrderNo\":\"TJ1912174037\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1820,\"BranchCode\":\"VCS - PL - VAULT\",\"FunctionalCode\":\"VCS - PL - VAULT\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204749\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"12\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 10:30\",\"JobEndTime\":\"2019-12-23 15:00\",\"BranchETA\":\"2019-12-23T10:30:00\",\"BranchETD\":\"2019-12-23T15:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 10:30\",\"EndTime\":\"2019-12-23 15:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":1391,\"BranchCode\":\"CHINATOWN NEL TO\",\"FunctionalCode\":\"TRANSIT-NE4\",\"StreetName\":\"151 NEW BRIDGE ROAD (CHINATOWN NEL TO)\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"59443\",\"CustomerCode\":\"105096\",\"CustomerName\":\"TRANSIT LINK PTE LTD\",\"CustomerId\":210,\"SequenceNo\":\"24\",\"Jobs\":[{\"TransportMasterId\":45510,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37919,\"OrderNo\":\"TJ1912174310\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"121720484\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"24\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":true,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 12:30\",\"JobEndTime\":\"2019-12-23 19:00\",\"BranchETA\":\"2019-12-23T12:30:00\",\"BranchETD\":\"2019-12-23T19:00:00\",\"ClientBreak\":\"16:00 - 17:00\"},{\"TransportMasterId\":45511,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":37979,\"OrderNo\":\"TJ1912174311\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1820,\"BranchCode\":\"VCS - PL - VAULT\",\"FunctionalCode\":\"VCS - PL - VAULT\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"121720484\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"24\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":true,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 12:30\",\"JobEndTime\":\"2019-12-23 19:00\",\"BranchETA\":\"2019-12-23T12:30:00\",\"BranchETD\":\"2019-12-23T19:00:00\",\"ClientBreak\":\"16:00 - 17:00\"}],\"StartTime\":\"2019-12-23 12:30\",\"EndTime\":\"2019-12-23 19:00\",\"ClientBreak\":\"16:00 - 17:00\"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":1853,\"BranchCode\":\"KNIGHTSBRIDGE\",\"FunctionalCode\":\"CIMBANK-ORCHAR\",\"StreetName\":\"270 Orchard Rd, #03-02 Knightsbridge CIMB Bank Orchard Branch\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"238857\",\"CustomerCode\":\"104999\",\"CustomerName\":\"CIMB BANK BERHAD\",\"CustomerId\":226,\"SequenceNo\":\"3\",\"Jobs\":[{\"TransportMasterId\":45658,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":38039,\"OrderNo\":\"TJ1912174458\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"104999\",\"CustomerName\":\"CIMB BANK BERHAD\",\"CustomerId\":226,\"PointId\":1852,\"BranchCode\":\"CIMB BANK @  SINGAPORE LAND TOWER\\n\",\"FunctionalCode\":\"CIMBANK-CIMB\",\"StreetName\":\"50, #01-02 Raffles Place\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"48623\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204815\",\"ProductCurrency\":[{\"Id\":34,\"ProductName\":\"$ 0.05\",\"ProductCode\":\"$\"},{\"Id\":33,\"ProductName\":\"$ 0.10\",\"ProductCode\":\"$\"},{\"Id\":32,\"ProductName\":\"$ 0.20\",\"ProductCode\":\"$\"},{\"Id\":31,\"ProductName\":\"$ 0.50\",\"ProductCode\":\"$\"},{\"Id\":30,\"ProductName\":\"$ 1.00\",\"ProductCode\":\"$\"},{\"Id\":39,\"ProductName\":\"$ 2.00\",\"ProductCode\":\"$\"},{\"Id\":38,\"ProductName\":\"$ 5.00\",\"ProductCode\":\"$\"},{\"Id\":37,\"ProductName\":\"$ 10.00\",\"ProductCode\":\"$\"},{\"Id\":36,\"ProductName\":\"$ 20.00\",\"ProductCode\":\"$\"},{\"Id\":35,\"ProductName\":\"$ 50.00\",\"ProductCode\":\"$\"},{\"Id\":40,\"ProductName\":\"$ 100.00\",\"ProductCode\":\"$\"}],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"3\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":true,\"CanCollectPallet\":false,\"CanCollectCoinBox\":true,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 10:00\",\"JobEndTime\":\"2019-12-23 15:00\",\"BranchETA\":\"2019-12-23T10:00:00\",\"BranchETD\":\"2019-12-23T15:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 10:00\",\"EndTime\":\"2019-12-23 15:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":1852,\"BranchCode\":\"CIMB BANK @  SINGAPORE LAND TOWER\\n\",\"FunctionalCode\":\"CIMBANK-CIMB\",\"StreetName\":\"50, #01-02 Raffles Place\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"48623\",\"CustomerCode\":\"104999\",\"CustomerName\":\"CIMB BANK BERHAD\",\"CustomerId\":226,\"SequenceNo\":\"3\",\"Jobs\":[{\"TransportMasterId\":45659,\"FloatDeliveryOrderId\":7851,\"CollectionOrderId\":0,\"OrderNo\":\"TJ1912174459\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":true,\"IsCollectionOrder\":false,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"104999\",\"CustomerName\":\"CIMB BANK BERHAD\",\"CustomerId\":226,\"PointId\":1852,\"BranchCode\":\"CIMB BANK @  SINGAPORE LAND TOWER\\n\",\"FunctionalCode\":\"CIMBANK-CIMB\",\"StreetName\":\"50, #01-02 Raffles Place\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"48623\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1223101936\",\"ProductCurrency\":[],\"DeliveryList\":[{\"TransportMasterId\":45659,\"FloatDeliveryOrderId\":7851,\"TotalAmount\":null,\"ItemType\":\"BAG\",\"IsBox\":false,\"SealNo\":\"GA0279751\",\"Denomination\":null,\"ItemAmount\":0.00,\"Qty\":1}],\"ReffId\":38039,\"SequenceNo\":\"3\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":true,\"CanCollectPallet\":false,\"CanCollectCoinBox\":true,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 08:00\",\"JobEndTime\":\"2019-12-23 18:00\",\"BranchETA\":\"2019-12-23T08:00:00\",\"BranchETD\":\"2019-12-23T18:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 08:00\",\"EndTime\":\"2019-12-23 18:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":1894,\"BranchCode\":\"ICBC - CHINATOWN \",\"FunctionalCode\":\"ICBC000-CTNPT\",\"StreetName\":\"133 New Bridge Road, #01-10, Chinatown Point\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"59413\",\"CustomerCode\":\"130301\",\"CustomerName\":\"INDUSTRIAL AND COMMERCIAL BANK OF C\",\"CustomerId\":233,\"SequenceNo\":\"26\",\"Jobs\":[{\"TransportMasterId\":45661,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":38042,\"OrderNo\":\"TJ1912174461\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204815\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"26\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 11:00\",\"JobEndTime\":\"2019-12-23 16:00\",\"BranchETA\":\"2019-12-23T11:00:00\",\"BranchETD\":\"2019-12-23T16:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 11:00\",\"EndTime\":\"2019-12-23 16:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":1901,\"BranchCode\":\"ICBC - RAFFLES QUAY\",\"FunctionalCode\":\"ICBC-RAFFQ\",\"StreetName\":\"6 Raffles Quay, #01-01, Raffles Place\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"48580\",\"CustomerCode\":\"130301\",\"CustomerName\":\"INDUSTRIAL AND COMMERCIAL BANK OF C\",\"CustomerId\":233,\"SequenceNo\":\"8\",\"Jobs\":[{\"TransportMasterId\":45662,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":38041,\"OrderNo\":\"TJ1912174462\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204815\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"8\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 11:00\",\"JobEndTime\":\"2019-12-23 16:00\",\"BranchETA\":\"2019-12-23T11:00:00\",\"BranchETD\":\"2019-12-23T16:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 11:00\",\"EndTime\":\"2019-12-23 16:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":1911,\"BranchCode\":\"GARDENS BY THE BAY\",\"FunctionalCode\":\"OCBCGDN-CHULIA\",\"StreetName\":\"65 Chulia St #01-00 OCBC Centre OCBC Bank @ OCBC Centre\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"\",\"CustomerCode\":\"103488\",\"CustomerName\":\"OCBC BANK\",\"CustomerId\":236,\"SequenceNo\":\"28\",\"Jobs\":[{\"TransportMasterId\":45665,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":38071,\"OrderNo\":\"TJ1912174465\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204816\",\"ProductCurrency\":[{\"Id\":34,\"ProductName\":\"$ 0.05\",\"ProductCode\":\"$\"},{\"Id\":33,\"ProductName\":\"$ 0.10\",\"ProductCode\":\"$\"},{\"Id\":32,\"ProductName\":\"$ 0.20\",\"ProductCode\":\"$\"},{\"Id\":31,\"ProductName\":\"$ 0.50\",\"ProductCode\":\"$\"},{\"Id\":30,\"ProductName\":\"$ 1.00\",\"ProductCode\":\"$\"},{\"Id\":39,\"ProductName\":\"$ 2.00\",\"ProductCode\":\"$\"},{\"Id\":38,\"ProductName\":\"$ 5.00\",\"ProductCode\":\"$\"},{\"Id\":37,\"ProductName\":\"$ 10.00\",\"ProductCode\":\"$\"},{\"Id\":35,\"ProductName\":\"$ 50.00\",\"ProductCode\":\"$\"},{\"Id\":40,\"ProductName\":\"$ 100.00\",\"ProductCode\":\"$\"}],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"28\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":true,\"CanCollectedBox\":true,\"CanCollectPallet\":false,\"CanCollectCoinBox\":true,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 15:00\",\"JobEndTime\":\"2019-12-23 18:00\",\"BranchETA\":\"2019-12-23T15:00:00\",\"BranchETD\":\"2019-12-23T18:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 15:00\",\"EndTime\":\"2019-12-23 18:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":1984,\"BranchCode\":\"BATTERY RD\",\"FunctionalCode\":\"SCB0000-BR\",\"StreetName\":\"Battery Road\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"\",\"CustomerCode\":\"103687\",\"CustomerName\":\"STANDARD CHARTERED BANK (SINGAPORE)\",\"CustomerId\":238,\"SequenceNo\":\"29\",\"Jobs\":[{\"TransportMasterId\":45728,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":38099,\"OrderNo\":\"TJ1912174528\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1779,\"BranchCode\":\"VCS - PL - ATM\",\"FunctionalCode\":\"VCS - PL - ATM\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204818\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"29\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":true,\"CanCollectPallet\":false,\"CanCollectCoinBox\":true,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 17:00\",\"JobEndTime\":\"2019-12-23 17:00\",\"BranchETA\":\"2019-12-23T17:00:00\",\"BranchETD\":\"2019-12-23T17:00:00\",\"ClientBreak\":\"      -      \"},{\"TransportMasterId\":45729,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":38097,\"OrderNo\":\"TJ1912174529\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1820,\"BranchCode\":\"VCS - PL - VAULT\",\"FunctionalCode\":\"VCS - PL - VAULT\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204818\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"29\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":true,\"CanCollectPallet\":false,\"CanCollectCoinBox\":true,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 17:00\",\"JobEndTime\":\"2019-12-23 17:00\",\"BranchETA\":\"2019-12-23T17:00:00\",\"BranchETD\":\"2019-12-23T17:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 17:00\",\"EndTime\":\"2019-12-23 17:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":2433,\"BranchCode\":\"MARINA BAY SANDS\",\"FunctionalCode\":\"BNPPRBS-MBS\",\"StreetName\":\"2 BAYFRONT AVENUE #B2-30/31 THE SHOPPES AT MBS\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"18972\",\"CustomerCode\":\"16025630.00\",\"CustomerName\":\"BNP PARIBAS - HERMES\",\"CustomerId\":306,\"SequenceNo\":\"14\",\"Jobs\":[{\"TransportMasterId\":45883,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":38241,\"OrderNo\":\"TJ1912174683\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1820,\"BranchCode\":\"VCS - PL - VAULT\",\"FunctionalCode\":\"VCS - PL - VAULT\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1217204827\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"14\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":false,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 12:00\",\"JobEndTime\":\"2019-12-23 18:00\",\"BranchETA\":\"2019-12-23T12:00:00\",\"BranchETD\":\"2019-12-23T18:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 12:00\",\"EndTime\":\"2019-12-23 18:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":577,\"BranchCode\":\"MARINA SQUARE\",\"FunctionalCode\":\"MUJI000-MARINA\",\"StreetName\":\"6 RAFFLES BOULEVARD #02-326-330 MARINA SQUARE\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"39594\",\"CustomerCode\":\"144681\",\"CustomerName\":\"MUJI (SINGAPORE) PTE LTD\",\"CustomerId\":113,\"SequenceNo\":\"9\",\"Jobs\":[{\"TransportMasterId\":52201,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":43420,\"OrderNo\":\"TJ1912210903\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1820,\"BranchCode\":\"VCS - PL - VAULT\",\"FunctionalCode\":\"VCS - PL - VAULT\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"122111542\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"9\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":true,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 10:30\",\"JobEndTime\":\"2019-12-23 18:00\",\"BranchETA\":\"2019-12-23T10:30:00\",\"BranchETD\":\"2019-12-23T18:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 10:30\",\"EndTime\":\"2019-12-23 18:00\",\"ClientBreak\":\"      -      \"},{\"EnableManualEntry\":null,\"Status\":\"SCHEDULED\",\"PointId\":507,\"BranchCode\":\"(CASHPOINT 13919) MARINA BAY SAND\",\"FunctionalCode\":\"ICONIC0-CPMBS\",\"StreetName\":\"1 BAYFRONT AVENUE MARINA BAY SANDS TOWER 3 LEVEL 56\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"18971\",\"CustomerCode\":\"16094244\",\"CustomerName\":\"ICONIC LOCATIONS SINGAPORE PTE. LTD\",\"CustomerId\":75,\"SequenceNo\":\"16\",\"Jobs\":[{\"TransportMasterId\":52430,\"FloatDeliveryOrderId\":0,\"CollectionOrderId\":43463,\"OrderNo\":\"TJ1912211132\",\"OrderDt\":\"2019-12-23T00:00:00\",\"Version\":null,\"IsFloatDeliveryOrder\":false,\"IsCollectionOrder\":true,\"TeamId\":596,\"TeamName\":\"R12\",\"DeliveryDt\":\"2019-12-23T00:00:00\",\"DCustomerCode\":\"CERTIS\",\"CustomerName\":\"CERTIS\",\"CustomerId\":244,\"PointId\":1780,\"BranchCode\":\"VCS - PL - B&R LEVEL 1\",\"FunctionalCode\":\"VCS - PL - B&R LEVEL 1\",\"StreetName\":\"20 JALAN AFIFI\",\"Tower\":\"\",\"Town\":\"\",\"PinCode\":\"409179\",\"ContactName\":\"\",\"ContactNo\":\"\",\"VersionNo\":\"1221134529\",\"ProductCurrency\":[],\"DeliveryList\":[],\"ReffId\":0,\"SequenceNo\":\"16\",\"CanCollectedBag\":true,\"CanCollectedEnvelop\":false,\"CanCollectedEnvelopInBag\":true,\"CanCollectedBox\":false,\"CanCollectPallet\":false,\"CanCollectCoinBox\":false,\"ETA\":\"dd\",\"CancelRequested\":false,\"Status\":\"SCHEDULED\",\"JobStartTime\":\"2019-12-23 09:00\",\"JobEndTime\":\"2019-12-23 17:00\",\"BranchETA\":\"2019-12-23T09:00:00\",\"BranchETD\":\"2019-12-23T17:00:00\",\"ClientBreak\":\"      -      \"}],\"StartTime\":\"2019-12-23 09:00\",\"EndTime\":\"2019-12-23 17:00\",\"ClientBreak\":\"      -      \",\"OrderRemarks\": \"Delivery for Collected Items to Target\"}]]");
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset());
            JSONArray loginJsonArray = jsonArray.getJSONArray(0);
            JSONObject jp = loginJsonArray.getJSONObject(0);
            Preferences.clearAll(LoginActivity.this);
            JSONArray dataArray = jsonArray.getJSONArray(1);
            Log.e("TOKEN" , jp.getString("Token") + " " + Integer.parseInt(jp.getString("LoggedInUser")));
            Preferences.saveString("AuthToken", jp.getString("Token"), LoginActivity.this);
            Preferences.saveString("LoggedOn", jp.getString("LoginTime"), LoginActivity.this);
            Preferences.saveString("LoggedInDate", sdf2.format(sdf.parse(mspindate.getSelectedItem().toString())), LoginActivity.this);
            Preferences.saveInt("UserId", Integer.parseInt(jp.getString("LoggedInUser")), LoginActivity.this);
            Preferences.saveInt("TeamId", Integer.parseInt(jp.getString("TeamId")), LoginActivity.this);
            Preferences.saveString("UserName", jp.getString("UserName"), LoginActivity.this);
            Preferences.saveString("UserCode", jp.getString("UserCode"), LoginActivity.this);
            Preferences.saveBoolean("EnableManualEntry", jp.getBoolean("EnableManualEntry"), LoginActivity.this);
            Preferences.saveLong("sinceLoggedIn", SystemClock.elapsedRealtime(), LoginActivity.this);
            Preferences.saveString("LoginDate", mspindate.getSelectedItem().toString(), LoginActivity.this);
            savejob(dataArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = LoginActivity.this.getAssets().open("dummy.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onResult(int api_code, int result_code, String result_data) {
        try {
            Log.e("login response code", ":" + result_code);
            Log.e("login response code", ":" + result_data);
            if (result_code == 200) {
                JSONObject obj = new JSONObject(result_data);
                String result = obj.getString("Result");
                Log.e("login response", result_data);
                String messege = obj.getString("Message");
                if (result.equals("Success")) {
                    if (messege.equals("LoggedIn")) {
                        if (Preferences.getString("DeviceID", LoginActivity.this).equals("")) {
                            raiseSnakbar("Device ID not set");
                        } else {
                            raiseSnakbar("Same user logged in from another device");
                        }
                    } else {
                        JSONArray jsonArray = obj.getJSONArray("Data");
                        JSONArray loginJsonArray = jsonArray.getJSONArray(0);
                        JSONObject jp = loginJsonArray.getJSONObject(0);
                        if (jp.getString("Role").equalsIgnoreCase("Admin")) {
                            raiseSnakbar(messege);
                            startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                        } else if (jp.getString("Role").equals("Supervisor")) {
                            if (Preferences.getString("DeviceID", LoginActivity.this).equals("")) {
                                raiseSnakbar("Device ID not set");
                            } else {
                                Preferences.clearAll(LoginActivity.this);
                                JSONArray dataArray = jsonArray.getJSONArray(1);

                                Log.e("TOKEN" , jp.getString("Token") + " " + Integer.parseInt(jp.getString("LoggedInUser")));
                                Preferences.saveString("AuthToken", jp.getString("Token"), LoginActivity.this);
                                Preferences.saveString("LoggedOn", jp.getString("LoginTime"), LoginActivity.this);
                                Preferences.saveString("LoggedInDate", sdf2.format(sdf.parse(mspindate.getSelectedItem().toString())), LoginActivity.this);
                                Preferences.saveInt("UserId", Integer.parseInt(jp.getString("LoggedInUser")), LoginActivity.this);
                                Preferences.saveInt("TeamId", Integer.parseInt(jp.getString("TeamId")), LoginActivity.this);
                                Preferences.saveString("UserName", jp.getString("UserName"), LoginActivity.this);
                                Preferences.saveString("UserCode", jp.getString("UserCode"), LoginActivity.this);
                                Preferences.saveString("TrasactionOfficerName", jp.getString("TrasactionOfficerName"), LoginActivity.this);
                                Preferences.saveBoolean("EnableManualEntry", jp.getBoolean("EnableManualEntry"), LoginActivity.this);
                                Preferences.saveLong("sinceLoggedIn", SystemClock.elapsedRealtime(), LoginActivity.this);
                                Preferences.saveString("LoginDate", mspindate.getSelectedItem().toString(), LoginActivity.this);
                                savejob(dataArray);
                                saveCoinSeries(obj);
                                SavePrintSetting(obj);
                                raiseSnakbar(messege);
                            }
                        } else {
                            raiseSnakbar("Invalid User Role");
                        }
                    }
                } else {
                    raiseSnakbar(messege);
                }

            } else {
                raiseSnakbar(getResources().getString(R.string.networkerror));
            }
        } catch (Exception e) {
            raiseSnakbar(getResources().getString(R.string.datakerror));
            e.printStackTrace();
        }
        hideProgressDialog();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            View v = LayoutInflater.from(LoginActivity.this).inflate(R.layout.settings_view, null);
            final EditText et = v.findViewById(R.id.etUrl);
            if (!TextUtils.isEmpty(Preferences.getString("API_URL", CertisCISCO.getContext()))) {
                et.setText(Preferences.getString("API_URL", CertisCISCO.getContext()));
            }
            dialog = new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Set API URL")
                    .setView(v)
                    .setPositiveButton("Confirm", null)
                    .setNeutralButton("Clear", null)
                    .setNegativeButton("Cancel", null)
                    .show();

            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAndSetIP(et.getText().toString());
                }
            });
            Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            neutralButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et.setText("");
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkAndSetIP(final String ip) {
        showProgressDialog("Loading...");
        try {
            //        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            //        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.connectTimeout(30, TimeUnit.SECONDS);
            httpClient.readTimeout(30, TimeUnit.SECONDS);
            httpClient.writeTimeout(30, TimeUnit.SECONDS);
            //        httpClient.addInterceptor(logging);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ip.trim() + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                    .build();
            CertisCISCOServices service = retrofit.create(CertisCISCOServices.class);
            Call<ResponseBody> call = service.ping();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    hideProgressDialog();
                    int result_code = response.code();
                    if (result_code == 200) {
                        Preferences.saveString("API_URL", ip.trim(), CertisCISCO.getContext());
                        Toast.makeText(LoginActivity.this, "IP set", Toast.LENGTH_SHORT).show();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Could not connect. Please verify the URL", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    hideProgressDialog();
                    Toast.makeText(LoginActivity.this, "Error: Could not set IP", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            hideProgressDialog();
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
