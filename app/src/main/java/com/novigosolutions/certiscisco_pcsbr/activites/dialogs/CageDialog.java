package com.novigosolutions.certiscisco_pcsbr.activites.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.activites.CollectionActivity;
import com.novigosolutions.certiscisco_pcsbr.adapters.StringDeleteAdapter;
import com.novigosolutions.certiscisco_pcsbr.interfaces.ApiCallback;
import com.novigosolutions.certiscisco_pcsbr.interfaces.DialogResult;
import com.novigosolutions.certiscisco_pcsbr.interfaces.IOnScannerData;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco_pcsbr.models.Bags;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Cage;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
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


public class CageDialog extends Dialog implements View.OnClickListener, IOnScannerData, ApiCallback {

    Context context;
    EditText txt_cage_no, txt_cage_seal;
    int TransportMasterId;
    DialogResult mDialogResult;
    ImageView img_manual_entry;
    List<String> bar_code_list = new ArrayList<>();
    StringDeleteAdapter listAdapter;
    ImageView imgCageNo, imgCageSeal;
    Button btnScanCageNo, btnScanCageSeal, cancel, confirm;
    String scanType;
    String noBarcode = "0000000000000";

    public CageDialog(Context context, int TransportMasterId, DialogResult mDialogResult) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);
        this.context = context;
        this.TransportMasterId = TransportMasterId;
        this.mDialogResult = mDialogResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cage_dialog);
        initialize();
        initializeBarcode();
    }


    private void initialize() {
        img_manual_entry = findViewById(R.id.img_manual_entry);
        txt_cage_no = findViewById(R.id.barcodeTextCageNo);
        txt_cage_no.clearFocus();
        txt_cage_seal = findViewById(R.id.barcodeTextCageSeal);
        btnScanCageNo = findViewById(R.id.btnScanCageNo);
        btnScanCageSeal = findViewById(R.id.btnScanCageSeal);
        cancel = findViewById(R.id.cancel);
        confirm = findViewById(R.id.confirm);

        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        btnScanCageSeal.setOnClickListener(this);
        btnScanCageNo.setOnClickListener(this);
        img_manual_entry.setOnClickListener(this);

        ((BarCodeScanActivity) context).registerScannerEvent(this);
    }

    private void openManualEntry() {
        txt_cage_no.setEnabled(true);
        txt_cage_seal.setEnabled(true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnScanCageNo:
                try {
                    scanType = "CageNo";
                    ((BarCodeScanActivity) context).scansoft();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Barcode Not Recognized", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnScanCageSeal:
                try {
                    scanType = "CageSeal";
                    ((BarCodeScanActivity) context).scansoft();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.cancel:
                dismiss();
                break;

            case R.id.confirm:
                saveToCage();
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
        }
    }

    private void saveToCage() {
        if (txt_cage_seal.getText().toString().equals(noBarcode) || txt_cage_no.getText().toString().equals(noBarcode)) {
            Toast.makeText(context, "No Barcode Scanned", Toast.LENGTH_LONG).show();
        } else {

            String cageNo = txt_cage_no.getText().toString();
            String cageSeal = txt_cage_seal.getText().toString();

            Cage cage = new Cage();
            cage.TransportMasterId = TransportMasterId;
            cage.CageSeal = cageSeal;
            cage.CageNo = cageNo;
            cage.save();

            Job.saveItemsToCage(TransportMasterId, cageNo, cageSeal);

            if (mDialogResult != null) {
                mDialogResult.onResult();
            }

            dismiss();
        }
    }


    private void initializeBarcode() {
//        String noBarcode = "0000000000000";
//        txt_cage_no.setText(noBarcode);
//        txt_cage_seal.setText(noBarcode);
    }


    @Override
    public void onDataScanned(String data) {
        if (data.isEmpty()) {
            ((CollectionActivity) context).invalidbarcodealert("Empty");
        } else if (isThereInList(data, scanType) || Branch.isExist(data))
            ((CollectionActivity) context).invalidbarcodealert("Duplicate");
        else {
            if (scanType.equals("CageNo")) {
                txt_cage_no.setText(data);
            } else {
                txt_cage_seal.setText(data);
            }
        }
    }

    private boolean isThereInList(String data, String scanType) {
        if (scanType.equals("CageNo") && data.equals(txt_cage_seal.getText().toString()))
            return true;
        else if (scanType.equals("CageSeal") && data.equals(txt_cage_no.getText().toString()))
            return true;

        return false;
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

}