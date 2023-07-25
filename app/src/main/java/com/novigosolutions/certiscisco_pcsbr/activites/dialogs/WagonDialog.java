package com.novigosolutions.certiscisco_pcsbr.activites.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.activites.CollectionActivity;
import com.novigosolutions.certiscisco_pcsbr.adapters.StringDeleteAdapter;
import com.novigosolutions.certiscisco_pcsbr.constant.UserLog;
import com.novigosolutions.certiscisco_pcsbr.interfaces.ApiCallback;
import com.novigosolutions.certiscisco_pcsbr.interfaces.DialogResult;
import com.novigosolutions.certiscisco_pcsbr.interfaces.IOnScannerData;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco_pcsbr.models.Bags;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.models.Wagon;
import com.novigosolutions.certiscisco_pcsbr.service.UserLogService;
import com.novigosolutions.certiscisco_pcsbr.utils.Constants;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class WagonDialog extends Dialog implements View.OnClickListener, IOnScannerData, ApiCallback, RecyclerViewClickListener {

    Context context;
    Button btn_scan, btn_done, btn_cancel;
    RadioGroup rg;
    RadioButton rb1, rb2;
    EditText txtbag1, txtbag2;
    TextView txt_count, txt_seal;
    int TransportMasterId;
    DialogResult mDialogResult;
    ImageView img_manual_entry;
    RecyclerView recyclerView;
    List<String> bar_code_list = new ArrayList<>();
    StringDeleteAdapter listAdapter;
    LinearLayout ll_manual_entry, ll_listview, ll_seal2;

    public WagonDialog(Context context, int TransportMasterId, DialogResult mDialogResult) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);
        this.context = context;
        this.TransportMasterId = TransportMasterId;
        this.mDialogResult = mDialogResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wagon_dialog);
        findviewbyid();
        initialize();
    }

    private void findviewbyid() {
        btn_scan = findViewById(R.id.btn_scan);
        btn_done = findViewById(R.id.btn_done);
        btn_cancel = findViewById(R.id.btn_cancel);
        txtbag1 = findViewById(R.id.txtbag1);
        txtbag2 = findViewById(R.id.txtbag2);
        rg = findViewById(R.id.rg);
        rb1 = findViewById(R.id.no1);
        rb2 = findViewById(R.id.no2);
        recyclerView = findViewById(R.id.recyclerview);
        txt_count = findViewById(R.id.txt_count);
        txt_seal = findViewById(R.id.txt_seal);
        img_manual_entry = findViewById(R.id.img_manual_entry);
        ll_manual_entry = findViewById(R.id.ll_manual_entry);
        ll_listview = findViewById(R.id.ll_listview);
        ll_seal2 = findViewById(R.id.ll_seal2);
    }

    private void initialize() {
        img_manual_entry.setOnClickListener(this);
        btn_scan.setOnClickListener(this);
        btn_done.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                txt_seal.setText("");
                if (checkedId == R.id.no1) {
                    ll_seal2.setVisibility(View.GONE);
                } else if (checkedId == R.id.no2) {
                    ll_seal2.setVisibility(View.VISIBLE);
                }
            }
        });
        listAdapter = new StringDeleteAdapter(bar_code_list, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(listAdapter);
        ((BarCodeScanActivity) context).registerScannerEvent(this);
    }

    private void openManualEntry() {
        ll_listview.setVisibility(View.GONE);
        ll_manual_entry.setVisibility(View.VISIBLE);
        btn_scan.setVisibility(View.GONE);
        img_manual_entry.setVisibility(View.GONE);
    }

    private void closeManualEntry() {
        txtbag1.setText("");
        txtbag2.setText("");
        ll_listview.setVisibility(View.VISIBLE);
        ll_manual_entry.setVisibility(View.GONE);
        btn_scan.setVisibility(View.VISIBLE);
        img_manual_entry.setVisibility(View.VISIBLE);
    }

    private void add(String data) {
        bar_code_list.add(data);
        listAdapter.notifyDataSetChanged();
        txt_count.setText("Count : " + bar_code_list.size());
    }

    private void remove(int pos) {
        bar_code_list.remove(pos);
        listAdapter.notifyDataSetChanged();
        txt_count.setText("Count : " + bar_code_list.size());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                try {
                    ((BarCodeScanActivity) context).scansoft();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_done:
                if (ll_manual_entry.getVisibility() == View.VISIBLE) {
                    if (rb1.isChecked()) {
                        // String barcode1 = txtbag1.getText().toString().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
                        String barcode1 = txtbag1.getText().toString().trim();
                        if (barcode1.length() > 0) {
                            if (isThereInList(barcode1) || Branch.isExist(barcode1))
                                ((CollectionActivity) context).invalidbarcodealert("Duplicate");
                            else {
                                add(barcode1);
                                closeManualEntry();
                            }
                        } else {
                            Toast.makeText(context, "Barcode can't be empty", Toast.LENGTH_SHORT).show();
                        }
                    } else {
//                        String barcode1 = txtbag1.getText().toString().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
//                        String barcode2 = txtbag2.getText().toString().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
                        String barcode1 = txtbag1.getText().toString().trim();
                        String barcode2 = txtbag2.getText().toString().trim();
                        if (barcode1.length() > 0 && barcode2.length() > 0) {
                            if (barcode2.equals(barcode1) || isThereInList(barcode1) || isThereInList(barcode2) || Branch.isExist(barcode1) || Branch.isExist(barcode2))
                                ((CollectionActivity) context).invalidbarcodealert("Duplicate");
                            else {
                                add(barcode1 + "," + barcode2);
                                closeManualEntry();
                            }
                        } else {
                            Toast.makeText(context, "Barcode can't be empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (bar_code_list.size() > 0) {
                        for (int i = 0; i < bar_code_list.size(); i++) {
                            String bagcode = bar_code_list.get(i);
                            String[] bagcodes = bagcode.split(",");
                            Wagon wagon = new Wagon();
                            wagon.TransportMasterId = TransportMasterId;
                            wagon.firstbarcode = bagcodes.length > 0 ? bagcodes[0] : "";
                            wagon.secondbarcode = bagcodes.length > 1 ? bagcodes[1] : "";
                            wagon.save();
                        }
                        if (mDialogResult != null) {
                            mDialogResult.onResult();
                        }
                        dismiss();
                    } else {
                        Toast.makeText(context, "Add Wagon", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.img_manual_entry:
                if (Preferences.getBoolean("EnableManualEntry", context) || Job.getSingle(TransportMasterId).EnableManualEntry) {
                    openManualEntry();
                } else {
                    if (NetworkUtil.getConnectivityStatusString(context)) {
                        ((CollectionActivity) context).showProgressDialog("Loading...");
                        String requestId = Job.getSingle(TransportMasterId).requestId;
                        if (requestId != null && requestId.length() > 0) {
                            APICaller.instance().getrequestStatus(this, context, requestId);
                        } else {

                            APICaller.instance().requestForEdit(this, context, "COLLECTION", Job.getSingle(TransportMasterId).GroupKey);
                        }
                    } else {
                        ((CollectionActivity) context).raiseInternetSnakbar();
                    }
                }
                break;
            case R.id.btn_cancel:
                if (ll_manual_entry.getVisibility() == View.VISIBLE) {
                    closeManualEntry();
                } else {
                    dismiss();
                }
                break;
            default:
                break;
        }

    }

    private boolean isThereInList(String data) {
        for (int i = 0; i < bar_code_list.size(); i++) {
            String bagcode = bar_code_list.get(i);
            String[] bagcodes = bagcode.split(",");
            for (int j = 0; j < bagcodes.length; j++) {
                if (data.equals(bagcodes[j])) return true;
            }
        }
        return false;
    }

    @Override
    public void onDataScanned(String data) {
        if (data.isEmpty()) {
            ((CollectionActivity) context).invalidbarcodealert("Empty");
            UserLogService.save(UserLog.COLLECTION.toString(), "SCANNED INVALID (" + data + ")", "SCANNED WAGON", context);
        } else if (data.equals(txt_seal.getText().toString()) || isThereInList(data) || Branch.isExist(data)) {
            ((CollectionActivity) context).invalidbarcodealert("Duplicate");
            UserLogService.save(UserLog.COLLECTION.toString(), "SCANNED INVALID (" + data + ")", "SCANNED WAGON", context);
        } else {
            UserLogService.save(UserLog.COLLECTION.toString(), "SCANNED (" + data + ")", "SCANNED WAGON", context);

            if (rb1.isChecked()) {
                add(data);
            } else if (txt_seal.length() > 0) {
                add(txt_seal.getText().toString() + "," + data);
                txt_seal.setText("");
            } else {
                txt_seal.setText(data);
            }
        }
    }

    @Override
    public void onResult(int api_code, int result_code, String result_data) {
        try {
            Log.e("result_data", result_data);
            ((CollectionActivity) context).hideProgressDialog();
            if (result_code == 200) {
                if (api_code == Constants.GETREQUESTSTATUS) {
                    if (result_data.replaceAll("\\W", "").equalsIgnoreCase("CONFIRMED")) {
                        Job.EnableManualEntry(TransportMasterId);
                        openManualEntry();
                    } else if (result_data.replaceAll("\\W", "").equalsIgnoreCase("REJECTED")) {
                        Job.UpdateRequestId(TransportMasterId, "");
                        Toast.makeText(context, "Request is rejected", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Request is pending", Toast.LENGTH_SHORT).show();
                    }
                } else if (api_code == Constants.REQUESTFOREDIT) {
                    JSONObject obj = new JSONObject(result_data);
                    if (obj.getString("Result").equals("Success")) {
                        Job.UpdateRequestId(TransportMasterId, obj.getString("Data"));
                        Toast.makeText(context, "Requested", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recyclerViewListClicked(final int pos) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Are you sure you want to delete?");
        alertDialog.setPositiveButton("Yes", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                remove(pos);
            }
        });
        alertDialog.setNegativeButton("No", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();

    }
}