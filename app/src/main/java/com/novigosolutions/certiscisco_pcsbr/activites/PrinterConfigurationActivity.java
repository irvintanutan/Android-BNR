package com.novigosolutions.certiscisco_pcsbr.activites;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.content.WebViewContent;
import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.adapters.BluetoothPairedListAdapter;
import com.novigosolutions.certiscisco_pcsbr.adapters.BluetoothScannedListAdapter;
import com.novigosolutions.certiscisco_pcsbr.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco_pcsbr.interfaces.PrintCallBack;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListner3;
import com.novigosolutions.certiscisco_pcsbr.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.zebra.Print;
import com.novigosolutions.certiscisco_pcsbr.zebra.Printer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.novigosolutions.certiscisco_pcsbr.applications.CertisCISCO.getContext;

public class PrinterConfigurationActivity extends BaseActivity implements View.OnClickListener, RecyclerViewClickListner3, NetworkChangekListener, PrintCallBack {
    ImageView imgnetwork, refresh;
    Button btnScanDevice, btnTestPrint;
    RecyclerView recyclerView,recyclerView1;
    TextView txt_device_name,txt_mac_address;
    public  String deviceName="";
    public  String deviceAdress="";
    public String template="";
    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names

    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private List<BluetoothDevice> mpairedlist = new ArrayList<BluetoothDevice>();
    private List<BluetoothDevice> mscannedlist = new ArrayList<BluetoothDevice>();
    private  Set<BluetoothDevice> mScannedList = new HashSet<>();
    BluetoothPairedListAdapter bluetoothPairedListAdapter;
    BluetoothScannedListAdapter bluetoothScannedListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_configuration);
        initialiser();
        setupToolBar();
        pariedDeviceView();
        displayDeviceInfo();
    }

    private void initialiser() {
        refresh = findViewById(R.id.sync_bluetooth);
        btnScanDevice = findViewById(R.id.bt_scan_device);
        btnTestPrint = findViewById(R.id.bt_test_print);
        recyclerView = findViewById(R.id.recyclerview);
        txt_device_name=findViewById(R.id.txt_device_name);
        txt_mac_address=findViewById(R.id.txt_mac_address);
        recyclerView1=findViewById(R.id.recyclerview_scan);
        btnScanDevice.setOnClickListener(this);
        btnTestPrint.setOnClickListener(this);

        Drawable img = ContextCompat.getDrawable( PrinterConfigurationActivity.this,R.drawable.ic_printer);
        btnScanDevice.setCompoundDrawablesWithIntrinsicBounds( null, null, img, null);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        bluetoothScannedListAdapter = new BluetoothScannedListAdapter(PrinterConfigurationActivity.this ,this);
        recyclerView1.setAdapter(bluetoothScannedListAdapter);

    }


    private void setupToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("PRINTER CONFIGURATION");
        TextView UserName = (TextView) toolbar.findViewById(R.id.UserName);
        UserName.setText(Preferences.getString("UserName", this));
        imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
    }

    public void displayDeviceInfo()
    {
        if(TextUtils.isEmpty(Preferences.getPrinterDeviceName(PrinterConfigurationActivity.this)) || !mBTAdapter.isEnabled()) {
            txt_device_name.setText("Printer Name : not set");
            txt_mac_address.setText("Mac Address : not set");
        } else{
            txt_device_name.setText("Printer Name : "+Preferences.getPrinterDeviceName(PrinterConfigurationActivity.this));
            txt_mac_address.setText("Mac Address : "+Preferences.getPrinterMacAddres(PrinterConfigurationActivity.this));
        }
    }

    private void clearDeviceInfo(){
        Preferences.savePrinterInfo("","",PrinterConfigurationActivity.this);
    }

    private void runSyncAnimation() {
        if (refresh != null) {
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
            rotation.setRepeatCount(Animation.INFINITE);
            refresh.startAnimation(rotation);
        }
    }

    private void stopSyncAnimation() {
        if (refresh != null) {
            refresh.clearAnimation();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_scan_device:
                bluetoothOn();
                break;
            case R.id.bt_test_print:
                testPrint();
                break;
        }
    }

    private void pariedDeviceView() {
        listPairedDevices();
        if(mpairedlist.size()==0)
            clearDeviceInfo();
        if (Preferences.getPrinterMacAddres(PrinterConfigurationActivity.this).equals(deviceAdress))
            clearDeviceInfo();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        bluetoothPairedListAdapter = new BluetoothPairedListAdapter(PrinterConfigurationActivity.this, mpairedlist,this);
        recyclerView.setAdapter(bluetoothPairedListAdapter);
        displayDeviceInfo();

    }

    private void bluetoothOn() {
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Toast.makeText(getApplicationContext(), "Bluetooth turned on", Toast.LENGTH_SHORT).show();
        } else {
            runSyncAnimation();
            discover();

        }
    }

    private void discover() {
        if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(), "Scanning stopped", Toast.LENGTH_SHORT).show();
            stopSyncAnimation();
        } else {
            if (mBTAdapter.isEnabled()) {
                mscannedlist.clear(); // clear items
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Scanning started", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth not turned on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void listPairedDevices() {
        mpairedlist.clear();
        if(mBTAdapter!=null) {
            mPairedDevices = mBTAdapter.getBondedDevices();
            if (mBTAdapter.isEnabled()) {
                mpairedlist.addAll(mPairedDevices);
            } else
                Toast.makeText(getApplicationContext(), "Bluetooth is not turned on,Cannot see the paired devices", Toast.LENGTH_LONG).show();
        }
    }


    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
            IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mscannedlist.clear();
            mScannedList.clear();
            registerReceiver(blReceiver, intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
            IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(blReceiver, intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testPrint(){
        Printer p= new Printer(PrinterConfigurationActivity.this,this);
        if(p.checkBluetoothConection()){
            if(p.checkDeviceAvailability()){
                try {
                    CommonMethods.showProgressDialog(PrinterConfigurationActivity.this,"Preparing the test print...");
                    p.generatestTemplate();
                  //  p.testprint(Printer.TEST_PRINT);
                }catch (Exception e){
                    CommonMethods.dismissProgressDialog();
                    e.printStackTrace();
                }
            }else
                Toast.makeText(PrinterConfigurationActivity.this, "Please select the printer", Toast.LENGTH_LONG).show();
        }else
            Toast.makeText(PrinterConfigurationActivity.this, "Bluetooth not on", Toast.LENGTH_LONG).show();
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            mscannedlist.clear();
//            mScannedList.clear();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(mScannedList.add(device)) {
                    mscannedlist.add(device);
                    bluetoothScannedListAdapter.addDevice(device);
                }

            }
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    Toast.makeText(getApplicationContext(), "paired", Toast.LENGTH_SHORT).show();
                    Preferences.savePrinterInfo(deviceName,deviceAdress,PrinterConfigurationActivity.this);
                    pariedDeviceView();
                    displayDeviceInfo();
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                    Toast.makeText(getApplicationContext(), "unpaired", Toast.LENGTH_SHORT).show();// showToast("Unpaired");
                    pariedDeviceView();
                }
                // else
                // Toast.makeText(getApplicationContext(), "cannot process the request..", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getApplicationContext(), "something went wrong..", Toast.LENGTH_SHORT).show();
            }
            stopSyncAnimation();
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent Data){
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                pariedDeviceView();
                displayDeviceInfo();
            }
            else
                Toast.makeText(PrinterConfigurationActivity.this,"Failed to enable the bluetooth",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(BluetoothDevice device, int position,String status) {
        Toast.makeText(getApplicationContext(), "Processing... "+device.getAddress(), Toast.LENGTH_SHORT).show();
        if(status.equals("pair")) {
            deviceName= device.getName();
            deviceAdress=device.getAddress();
            pairDevice(device);
        }
        else if (status.equals("connect"))
        {
            deviceName= device.getName();
            deviceAdress=device.getAddress();
            Preferences.savePrinterInfo(deviceName,deviceAdress,PrinterConfigurationActivity.this);
            displayDeviceInfo();
        }
        else if (status.equals("unpair")) {
            deviceAdress=device.getAddress();
            deviceName=device.getName();
            unpairDevice(device);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    protected void onStop() {
        super.onStop();
//        if(blReceiver!=null){
//            unregisterReceiver(blReceiver);
//        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        Printer p= new Printer(PrinterConfigurationActivity.this,this);
        CommonMethods.dismissProgressDialog();
        this.template = template;
        p.setSetZplTemplate(template);
        //p.print(template);
        p.testprint(Printer.TEST_PRINT,"");
    }

    @Override
    public void onPrint(int flag, String message) {

    }
}