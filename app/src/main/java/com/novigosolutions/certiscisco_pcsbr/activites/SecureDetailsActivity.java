package com.novigosolutions.certiscisco_pcsbr.activites;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.activites.dialogs.BarCodeScanActivity;
import com.novigosolutions.certiscisco_pcsbr.activites.dialogs.PostponeDialog;
import com.novigosolutions.certiscisco_pcsbr.adapters.SealedListAdapter;
import com.novigosolutions.certiscisco_pcsbr.adapters.SecuredSealedListAdapter;
import com.novigosolutions.certiscisco_pcsbr.adapters.SecuredUnsealedListAdapter;
import com.novigosolutions.certiscisco_pcsbr.adapters.UnsealedListAdapter;
import com.novigosolutions.certiscisco_pcsbr.constant.ClickListener;
import com.novigosolutions.certiscisco_pcsbr.constant.RecyclerTouchListener;
import com.novigosolutions.certiscisco_pcsbr.expandable.CageListAdapter;
import com.novigosolutions.certiscisco_pcsbr.expandable.SecureCageListAdapter;
import com.novigosolutions.certiscisco_pcsbr.interfaces.ApiCallback;
import com.novigosolutions.certiscisco_pcsbr.interfaces.DialogResult;
import com.novigosolutions.certiscisco_pcsbr.interfaces.IOnScannerData;
import com.novigosolutions.certiscisco_pcsbr.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Cage;
import com.novigosolutions.certiscisco_pcsbr.models.Consignment;
import com.novigosolutions.certiscisco_pcsbr.models.Delivery;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.objects.SecureObject;
import com.novigosolutions.certiscisco_pcsbr.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco_pcsbr.utils.Constants;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SecureDetailsActivity extends BarCodeScanActivity implements IOnScannerData, View.OnClickListener, ApiCallback, NetworkChangekListener, UnsealedListAdapter.UnsealedClickCallback {
    TextView txt_customer_name, txt_functional_code, txt_branch_name;
    TextView txt_branch_address, txt_order_remarks, txt_sealed_item_count, txt_unsealed_item_count;
    //    int PointId;
    int TransportMasterId;
    String GroupKey;
    private RecyclerView recyclerViewbag, recyclerViewbox, recyclerViewCage;
    private SecuredSealedListAdapter sealedListAdapter;
    private SecuredUnsealedListAdapter unsealedListAdapter;
    List<SecureObject> bagList;
    List<SecureObject> boxList;
    String BranchCode, PFunctionalCode, actualFromTime, actualToTime;
    ImageView imgnetwork, img_manual_entry;
    Button btn_scan, btn_secure, btn_ok, btn_cancel;
    EditText editText;
    View l_manual_entry;
    Job j;
    List<Cage> cageList = new ArrayList<>();
    SecureCageListAdapter cageListAdapter;
    boolean isBagListComplete = false, isBoxListComplete = false, isCageListComplete = false;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_detail);
        setuptoolbar();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            TransportMasterId = extras.getInt("TransportMasterId");
            GroupKey = extras.getString("GroupKey");
        }

        j = Job.getSingle(TransportMasterId);
        Constants.TRANSPORT_MASTER_ID = TransportMasterId;

        BranchCode = j.BranchCode;
        PFunctionalCode = j.PFunctionalCode;
        actualFromTime = j.ActualFromTime;
        actualToTime = j.ActualToTime;

        recyclerViewbag = findViewById(R.id.recyclerviewbag);
        recyclerViewbox = findViewById(R.id.recyclerviewbox);
        recyclerViewCage = findViewById(R.id.recyclerviewCage);
        editText = findViewById(R.id.edittext);
        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);
        img_manual_entry = findViewById(R.id.img_manual_entry);
        l_manual_entry = findViewById(R.id.l_manual_entry);
        img_manual_entry.setOnClickListener(this);
        btn_secure = findViewById(R.id.btn_secure);
        btn_secure.setOnClickListener(this);
        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        txt_sealed_item_count = (TextView) findViewById(R.id.sealedItemCount);
        txt_unsealed_item_count = (TextView) findViewById(R.id.unsealedItemCount);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        List<SecureObject> list = Job.getSecureObjects(TransportMasterId);
        bagList = list.stream().filter(l -> l.IsSealed)
                .collect(Collectors.toList());

        boxList = list.stream().filter(l -> l.IsSealed == false)
                .collect(Collectors.toList());

        setSealedScannedCount();
        recyclerViewbag.setLayoutManager(mLayoutManager);
        recyclerViewbag.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewbag.getContext(), DividerItemDecoration.VERTICAL);
        recyclerViewbag.addItemDecoration(dividerItemDecoration);
        sealedListAdapter = new SecuredSealedListAdapter(bagList);
        recyclerViewbag.setAdapter(sealedListAdapter);


        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        setUnsealedScannedCount();
        recyclerViewbox.setLayoutManager(mLayoutManager2);
        recyclerViewbox.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(recyclerViewbox.getContext(), DividerItemDecoration.VERTICAL);
        recyclerViewbox.addItemDecoration(dividerItemDecoration2);
        unsealedListAdapter = new SecuredUnsealedListAdapter(boxList, this);
        unsealedListAdapter.setUnsealedClickCallback(SecureDetailsActivity.this);
        recyclerViewbox.setAdapter(unsealedListAdapter);
        txt_customer_name = (TextView) findViewById(R.id.txt_customer_name);
        txt_functional_code = (TextView) findViewById(R.id.txt_functional_code);
        txt_branch_name = (TextView) findViewById(R.id.txt_txt_branch_name);
        txt_branch_address = (TextView) findViewById(R.id.txt_txt_branch_address);
        Branch branch = Branch.getSingle(GroupKey);
        txt_customer_name.setText(branch.CustomerName);
        txt_functional_code.setText(Job.getAllOrderNos(j.GroupKey, j.BranchCode, j.PFunctionalCode, "PENDING", j.PDFunctionalCode, j.ActualFromTime, j.ActualToTime));
        txt_branch_name.setText(branch.BranchCode);
        String address = "Address: ";
        if (!TextUtils.isEmpty(j.StreetName)) {
            address = address + j.StreetName;
        }
        if (!TextUtils.isEmpty(j.Tower)) {
            if (TextUtils.isEmpty(j.StreetName)) {
                address = address + j.Tower;
            } else {
                address = address + "," + j.Tower;
            }
        }
        if (!TextUtils.isEmpty(j.Town)) {
            if (TextUtils.isEmpty(j.Tower)) {
                address = address + j.Town;
            } else {
                address = address + "," + j.Town;
            }
        }
        if (!TextUtils.isEmpty(j.PinCode)) {
            address = address + "-" + j.PinCode;
        }
        txt_branch_address.setText(address);
        txt_order_remarks = (TextView) findViewById(R.id.txt_order_remarks);
        List<Job> jjl = Job.getDeliveryJobsOfPoint(GroupKey, BranchCode, PFunctionalCode, actualFromTime, actualToTime);
        if (jjl.size() > 1) {
            String rem = null;
            for (Job jj : jjl) {
                if (rem == null) {
                    if (!TextUtils.isEmpty(jj.OrderRemarks))
                        rem = jj.OrderRemarks;
                } else {
                    if (!TextUtils.isEmpty(jj.OrderRemarks)) {
                        if (rem.equals(jj.OrderRemarks)) {
                            rem = jj.OrderRemarks;
                        } else {
                            rem = "";
                        }
                    }
                }
            }
            if (!TextUtils.isEmpty(rem)) {
                txt_order_remarks.setVisibility(View.VISIBLE);
                txt_order_remarks.setText("Remarks: " + rem);
            }
        } else if (!TextUtils.isEmpty(j.OrderRemarks)) {
            txt_order_remarks.setVisibility(View.VISIBLE);
            txt_order_remarks.setText("Remarks: " + j.OrderRemarks);
        } else {
            txt_order_remarks.setVisibility(View.GONE);
        }

        if (Preferences.getBoolean("EnableManualEntry", this) || Branch.getSingle(GroupKey).EnableManualEntry) {
            enableManualEntry();
        }

        setCageListView();
    }

    public void setCageListView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewCage.setLayoutManager(mLayoutManager);
        recyclerViewCage.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewCage.getContext(), DividerItemDecoration.VERTICAL);
        recyclerViewCage.addItemDecoration(dividerItemDecoration);
        cageList = Cage.getByTransportMasterId(TransportMasterId);
        cageListAdapter = new SecureCageListAdapter(cageList);
        recyclerViewCage.setAdapter(cageListAdapter);
    }

    private void setSealedScannedCount() {
        int scanned = 0;
        for (SecureObject d : bagList) {
            if (d.IsScanned && d.SecondBarcode == null) {
                scanned++;
            } else if (d.IsScanned && d.IsScannedSecond && d.SecondBarcode != null) {
                scanned++;
            }
        }
        txt_sealed_item_count.setText(getString(R.string.item_count, scanned, bagList.size()));
        if (scanned == bagList.size()) {
            isBagListComplete = true;
        } else isBagListComplete = false;
    }

    private void setUnsealedScannedCount() {
        int scanned = 0;
        int total_box_count = 0;
        for (SecureObject d : boxList) {
            total_box_count += d.Quantity;
            if (d.IsScanned) {
                scanned += d.Quantity;
            }
        }
        txt_unsealed_item_count.setVisibility(View.VISIBLE);
        txt_unsealed_item_count.setText(getString(R.string.item_count, scanned, total_box_count));

        if (scanned == total_box_count) {
            isBoxListComplete = true;
        } else isBoxListComplete = false;
    }

    private void enableManualEntry() {
        l_manual_entry.setVisibility(View.VISIBLE);
        img_manual_entry.setVisibility(View.GONE);
    }

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Scan to Secure");
        TextView UserName = (TextView) toolbar.findViewById(R.id.UserName);
        UserName.setText(Preferences.getString("UserName", this));
        imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        alert();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_scan:
                try {
                    scansoft();
//                    if (emdkWrapper != null)
//                        emdkWrapper.scansoft(this);
//                    else Toast.makeText(this, "Scanner not supported", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_cancel:
                alert2();
                break;
            case R.id.btn_secure:
                if (validateItems()) {
                    secureVehicle();
                } else {
                    alertVehicle();
                }

                break;
            case R.id.img_manual_entry:
                if (NetworkUtil.getConnectivityStatusString(this)) {
                    showProgressDialog("Loading...");
                    String requestId = Branch.getSingle(GroupKey).requestId;
                    if (requestId != null && requestId.length() > 0) {
                        APICaller.instance().getrequestStatus(this, this, requestId);
                    } else {
                        APICaller.instance().requestForEdit(this, this, "DELIVERY", GroupKey);
                    }
                } else {
                    raiseInternetSnakbar();
                }
                break;
            case R.id.btn_ok:
                if (editText.length() > 0) {
                    onDataScanned(editText.getText().toString());
                    editText.setText("");
                }
                break;
        }
    }

    private boolean validateItems() {

        if (boxList.isEmpty()) isBoxListComplete = true;
        if (bagList.isEmpty()) isBagListComplete = true;
        if (cageList.isEmpty()) isCageListComplete = true;

        return isBoxListComplete && isBagListComplete && isCageListComplete;
    }

    private void secureVehicle() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Are you sure you want to proceeed ? ");
        alertDialog.setPositiveButton("Yes", (dialog, which) -> {
            showProgressDialog("Securing Vehicle . . . ");
            APICaller.instance().secureVehicle(SecureDetailsActivity.this, SecureDetailsActivity.this, j.TransportMasterId);
        });
        alertDialog.setNegativeButton("No", (dialog, which) -> {
        });
        alertDialog.show();
    }

    private void alertVehicle() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("All Items must be scanned");
        alertDialog.setPositiveButton("Ok", (dialog, which) -> {
        });
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
        registerScannerEvent(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterScannerEvent();
    }


    @SuppressLint("NewApi")
    @Override
    public void onDataScanned(String data) {
        if (data.isEmpty()) {
            invalidbarcodealert("Empty");
        } else {
            checkSealedBags(data);
            checkCageList(data);
        }

    }

    private void checkCageList(String scan) {
        for (int a = 0; a < cageList.size(); a++) {
            Cage cage = cageList.get(a);
            if (cage.CageNo.equals(scan)) {
                cage.IsCageNoScanned = true;
            } else if (cage.CageSeal.equals(scan)) {
                cage.IsCageSealScanned = true;
            }

            cageList.set(a, cage);
            cageListAdapter = new SecureCageListAdapter(cageList);
            recyclerViewCage.setAdapter(cageListAdapter);
        }

        for (int a = 0; a < cageList.size(); a++) {
            Cage cage = cageList.get(a);
            if (cage.IsCageSealScanned && cage.IsCageNoScanned) {
                isCageListComplete = true;
            } else {
                isCageListComplete = false;
                break;
            }
        }
    }

    private void checkSealedBags(String scan) {
        for (int a = 0; a < bagList.size(); a++) {
            SecureObject secureObject = bagList.get(a);
            if (scan.equals(secureObject.Barcode)) {
                secureObject.IsScanned = true;
                bagList.set(a, secureObject);
                sealedListAdapter = new SecuredSealedListAdapter(bagList);
                recyclerViewbag.setAdapter(sealedListAdapter);
                setSealedScannedCount();
            } else if (scan.equals(secureObject.SecondBarcode)) {
                secureObject.IsScannedSecond = true;
                bagList.set(a, secureObject);
                sealedListAdapter = new SecuredSealedListAdapter(bagList);
                recyclerViewbag.setAdapter(sealedListAdapter);
                setSealedScannedCount();
            }
        }
    }

    private void alert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Are you sure you want to go back?\nCurrent progress will be lost.");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(SecureDetailsActivity.this, SecureJobActivity.class);
                Constants.BackDestination = "ALL";
                Constants.isAll = true;
                startActivity(intent);
                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    private void alert2() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Do you want to cancel this delivery?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(SecureDetailsActivity.this, SecureJobActivity.class);
                Constants.BackDestination = "ALL";
                Constants.isAll = true;
                startActivity(intent);
                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.e("resultCode", ":" + resultCode);
        if (resultCode == Constants.FINISHONRESULT) //matches the result code passed from B
        {
            finish();
        }
    }

    @Override
    public void onResult(int api_code, int result_code, String result_data) {
        try {
            Log.e("result_data", result_data);
            hideProgressDialog();
            if (result_code == 200) {
                if (api_code == Constants.GETREQUESTSTATUS) {
                    if (result_data.replaceAll("\\W", "").equalsIgnoreCase("CONFIRMED")) {
                        Branch.EnableManualEntry(GroupKey);
                        enableManualEntry();
                    } else if (result_data.replaceAll("\\W", "").equalsIgnoreCase("REJECTED")) {
                        Branch.UpdateRequestId(GroupKey, "");
                        Toast.makeText(this, "Request is rejected", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Request is pending", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    JSONObject obj = new JSONObject(result_data);
                    if (obj.getString("Result").equals("Success")) {
                        if (api_code == Constants.REQUESTFOREDIT) {
                            Branch.UpdateRequestId(GroupKey, obj.getString("Data"));
                            Toast.makeText(this, "Requested", Toast.LENGTH_SHORT).show();
                        } else if (api_code == Constants.SECUREVEHICLE) {
                            hideProgressDialog();
                            Job.UpdateSecureVehicle(TransportMasterId);
                            Intent intent = new Intent(SecureDetailsActivity.this, SecureJobActivity.class);
                            Constants.BackDestination = "PENDING";
                            Constants.isAll = false;
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            } else {
                raiseSnakbar(":" + result_code);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkChanged() {
        if (NetworkUtil.getConnectivityStatusString(this))
            imgnetwork.setImageResource(R.drawable.network);
        else
            imgnetwork.setImageResource(R.drawable.no_network);
    }

    @Override
    public void onSelect() {
        setUnsealedScannedCount();
    }
}
