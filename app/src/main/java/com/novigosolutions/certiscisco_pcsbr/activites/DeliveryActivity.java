package com.novigosolutions.certiscisco_pcsbr.activites;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
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
import com.novigosolutions.certiscisco_pcsbr.adapters.UnsealedListAdapter;
import com.novigosolutions.certiscisco_pcsbr.constant.ClickListener;
import com.novigosolutions.certiscisco_pcsbr.constant.RecyclerTouchListener;
import com.novigosolutions.certiscisco_pcsbr.expandable.CageAdapter;
import com.novigosolutions.certiscisco_pcsbr.expandable.CageListAdapter;
import com.novigosolutions.certiscisco_pcsbr.expandable.Items;
import com.novigosolutions.certiscisco_pcsbr.interfaces.ApiCallback;
import com.novigosolutions.certiscisco_pcsbr.interfaces.DialogResult;
import com.novigosolutions.certiscisco_pcsbr.interfaces.IOnScannerData;
import com.novigosolutions.certiscisco_pcsbr.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Cage;
import com.novigosolutions.certiscisco_pcsbr.models.Delivery;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.objects.Summary;
import com.novigosolutions.certiscisco_pcsbr.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco_pcsbr.utils.Constants;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;
import com.novigosolutions.certiscisco_pcsbr.zebra.Print;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DeliveryActivity extends BarCodeScanActivity implements IOnScannerData, View.OnClickListener, ApiCallback, DialogResult, NetworkChangekListener, UnsealedListAdapter.UnsealedClickCallback {
    TextView txt_customer_name, txt_functional_code, txt_branch_name;// txt_street_tower, txt_town_pin;
    TextView txt_branch_address, txt_order_remarks, txt_sealed_item_count, txt_unsealed_item_count;
    //    int PointId;
    int TransportMasterId;
    String GroupKey;
    private RecyclerView recyclerViewbag, recyclerViewbox, recyclerViewCage;
    private SealedListAdapter sealedListAdapter;
    private UnsealedListAdapter unsealedListAdapter;
    List<Delivery> bagList;
    List<Delivery> boxList;
    String BranchCode, PFunctionalCode, actualFromTime, actualToTime;
    ImageView imgnetwork, img_manual_entry;
    Button btn_scan, btn_submit, btn_ok, btn_postpone;
    EditText editText;
    View l_manual_entry;
    Job j;
    CageListAdapter cageListAdapter;

    // private EMDKWrapper emdkWrapper = null;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        setuptoolbar();
//        if (android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.contains("Motorola Solutions")) {
//            emdkWrapper = new EMDKWrapper(this);
//        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
//            PointId = extras.getInt("PointId");
            TransportMasterId = extras.getInt("TransportMasterId");
            GroupKey = extras.getString("GroupKey");
        }

        j = Job.getSingle(TransportMasterId);

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
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

        btn_postpone = findViewById(R.id.btn_postpone);
        btn_postpone.setOnClickListener(this);
        txt_sealed_item_count = (TextView) findViewById(R.id.sealedItemCount);
        txt_unsealed_item_count = (TextView) findViewById(R.id.unsealedItemCount);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        if(Delivery.hasPendingDeliveryItems(j.TransportMasterId)) {
//            bagList = Delivery.getPendingSealedByTransportId(TransportMasterId);
//            boxList = Delivery.getPendingUnSealedByTransportId(TransportMasterId);
//        }else if(!TextUtils.isEmpty(j.DependentOrderId)){
//            bagList = Delivery.getPendingSealedByTransportId(Job.getSingleByOrderNo(j.DependentOrderId).TransportMasterId);
//            boxList = Delivery.getPendingUnSealedByTransportId(Job.getSingleByOrderNo(j.DependentOrderId).TransportMasterId);
//        }
        List<Delivery> list = Delivery.getPendingCageSealedByPointId(GroupKey, BranchCode, PFunctionalCode, actualFromTime, actualToTime);
        bagList = list.stream().filter(distinctByKey(p -> p.SealNo))
                .collect(Collectors.toList());

        setSealedScannedCount();
        boxList = Delivery.getPendingCageUnSealedByPointId(GroupKey, BranchCode, PFunctionalCode, actualFromTime, actualToTime);
        recyclerViewbag.setLayoutManager(mLayoutManager);
        recyclerViewbag.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewbag.getContext(), DividerItemDecoration.VERTICAL);
        recyclerViewbag.addItemDecoration(dividerItemDecoration);
        sealedListAdapter = new SealedListAdapter(bagList);
        recyclerViewbag.setAdapter(sealedListAdapter);


        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        setUnsealedScannedCount();
        recyclerViewbox.setLayoutManager(mLayoutManager2);
        recyclerViewbox.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(recyclerViewbox.getContext(), DividerItemDecoration.VERTICAL);
        recyclerViewbox.addItemDecoration(dividerItemDecoration2);
        unsealedListAdapter = new UnsealedListAdapter(boxList);
        unsealedListAdapter.setUnsealedClickCallback(DeliveryActivity.this);
        recyclerViewbox.setAdapter(unsealedListAdapter);
        txt_customer_name = (TextView) findViewById(R.id.txt_customer_name);
        txt_functional_code = (TextView) findViewById(R.id.txt_functional_code);
//        txt_street_tower = (TextView) findViewById(R.id.txt_street_tower);
//        txt_town_pin = (TextView) findViewById(R.id.txt_town_pin);
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
//                if(!TextUtils.isEmpty(jj.OrderRemarks)){
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
//                }
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
//        String street_tower = branch.StreetName;
//        if (!branch.Tower.isEmpty()) {
//            if (street_tower.isEmpty()) street_tower = branch.Tower;
//            else street_tower = street_tower + ", " + branch.Tower;
//        }
//        txt_street_tower.setText(street_tower);
//        String town_pin = branch.Town;
//        if (!branch.PinCode.isEmpty()) {
//            if (town_pin.isEmpty()) town_pin = branch.PinCode;
//            else town_pin = town_pin + ", " + branch.PinCode;
//        }
//        txt_town_pin.setText(town_pin);


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
        List<com.novigosolutions.certiscisco_pcsbr.models.Cage> cageList = com.novigosolutions.certiscisco_pcsbr.models.Cage.getByTransportMasterId(
                Job.getAllOrderNosId(GroupKey, BranchCode, PFunctionalCode, "PENDING", PFunctionalCode, actualFromTime, actualToTime)
        );
        cageListAdapter = new CageListAdapter(cageList);
        recyclerViewCage.setAdapter(cageListAdapter);
        recyclerViewCage.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewCage, new ClickListener() {

            @Override
            public void onClick(View view, int position) {
                Cage cage = cageList.get(position);
                Intent intent = new Intent(DeliveryActivity.this , CageDeliveryActivity.class);
                intent.putExtra("CageNo" , cage.CageNo);
                intent.putExtra("CageSeal" , cage.CageSeal);
                intent.putExtra("TransportMasterId" , cage.TransportMasterId);
                intent.putExtra("GroupKey", GroupKey);
                startActivity(intent);
                finish();
            }

            @Override
            public void onLongClick(View view, final int position) {
            }
        }));
    }

    @SuppressLint("NewApi")
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private void setSealedScannedCount() {
        int scanned = 0;
        for (Delivery d : bagList) {
            if (d.IsScanned) {
                scanned++;
            }
        }
        txt_sealed_item_count.setText(getString(R.string.item_count, scanned, bagList.size()));
    }

    private void setUnsealedScannedCount() {
        int scanned = 0;
        int total_box_count = 0;
        for (Delivery d : boxList) {
            total_box_count += d.Qty;
            if (d.IsScanned) {
                scanned += d.Qty;
            }
        }
        txt_unsealed_item_count.setVisibility(View.VISIBLE);
        txt_unsealed_item_count.setText(getString(R.string.item_count, scanned, total_box_count));
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
        mTitle.setText("DELIVERY");
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
            case R.id.btn_postpone:
                alert2();
                break;
            case R.id.btn_submit:
                if (Job.isAllDeliveryScanned(GroupKey, BranchCode, PFunctionalCode, actualFromTime, actualToTime)) {
                    Intent intent = new Intent(this, SummaryActivity.class);
//                    intent.putExtra("PointId", PointId);
                    intent.putExtra("TransportMasterId", TransportMasterId);
                    intent.putExtra("GroupKey", GroupKey);
                    intent.putExtra("summaryType", Constants.DELIVERY);
                    intent.putExtra("isSummary", false);
                    startActivityForResult(intent, 000);
                } else {
                    Toast.makeText(this, "Scan all", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
        registerScannerEvent(this);
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        super.onPause();
        unregisterScannerEvent();
    }


    @SuppressLint("NewApi")
    @Override
    public void onDataScanned(String data) {
        if (data.isEmpty()) {
            invalidbarcodealert("Empty");
        } else {
            if (Delivery.setScanned(GroupKey, data, BranchCode, PFunctionalCode, actualFromTime, actualToTime)) {
                List<Delivery> templist = Delivery.getPendingSealedByPointId(GroupKey, BranchCode, PFunctionalCode, actualFromTime, actualToTime);
                templist = templist.stream().filter(distinctByKey(p -> p.SealNo))
                        .collect(Collectors.toList());
                bagList.clear();
                bagList.addAll(templist);
                sealedListAdapter.notifyDataSetChanged();
                setSealedScannedCount();
            } else {
                invalidbarcodealert("Invalid");
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
                Delivery.clearBranchDelivery(GroupKey, BranchCode, PFunctionalCode, actualFromTime, actualToTime);
                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    private void openDialogue() {
        PostponeDialog postponeDialog = new PostponeDialog(this, GroupKey, this, j);
        postponeDialog.setCancelable(false);
        postponeDialog.show();
    }

    private void alert2() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Do you want to cancel this delivery?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                openDialogue();
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
    public void onResult() {
        Branch branch = Branch.getSingle(GroupKey);
        if (branch.isRescheduled) {
//            Job.setDelivered(TransportMasterId);
            Job.setDelivered(GroupKey, BranchCode, PFunctionalCode, Constants.startTime, Constants.endTime);
//            if (Job.getIncompleteCollectionJobsOfPoint(GroupKey).size()>0) {
//                Intent intent = new Intent(this, CollectionDetailActivity.class);
////                intent.putExtra("PointId", PointId);
//                intent.putExtra("GroupKey", GroupKey);
//                startActivity(intent);
//            }
            finish();
        }
    }

    @Override
    public void onSelect() {
        //updat the counter
        setUnsealedScannedCount();
    }
}
