package com.novigosolutions.certiscisco_pcsbr.activites;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco_pcsbr.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;


public class AdminActivity extends BaseActivity implements NetworkChangekListener {
    ImageView imgnetwork;
    EditText edtDeviceID;
    Button btnSave;
    CoordinatorLayout clv;
    Switch switchMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setuptoolbar();
        initializeviews();
    }

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("ADMIN PANEL");
        imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
    }

    private void initializeviews() {
        edtDeviceID = (EditText) findViewById(R.id.edt_device_id);
        switchMessage = findViewById(R.id.switchMessage);
        btnSave = (Button) findViewById(R.id.btn_save);
        edtDeviceID.setText(Preferences.getString("DeviceID", AdminActivity.this));
        if(Preferences.getBoolean("TWOWAY",AdminActivity.this))switchMessage.setChecked(true);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtDeviceID.getText().toString().length() == 0) {
                    edtDeviceID.setError("Cannot be empty");
                } else {
                    Preferences.saveString("DeviceID", edtDeviceID.getText().toString().trim(), AdminActivity.this);
                    Preferences.saveBoolean("TWOWAY",switchMessage.isChecked(), AdminActivity.this);
                    Toast.makeText(AdminActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    alert();
                }
            }
        });
        clv = (CoordinatorLayout) findViewById(R.id.cl);
        setupUI(clv, AdminActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            alert();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
    }

    private void alert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Do you want to logout?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Intent i = new Intent(AdminActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        alert();
    }


    @Override
    public void onNetworkChanged() {
        if (NetworkUtil.getConnectivityStatusString(this))
            imgnetwork.setImageResource(R.drawable.network);
        else
            imgnetwork.setImageResource(R.drawable.no_network);
    }
}
