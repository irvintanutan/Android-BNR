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
    TextView txt_count, txt_cage_no, txt_cage_seal;
    int TransportMasterId;
    DialogResult mDialogResult;
    ImageView img_manual_entry;
    List<String> bar_code_list = new ArrayList<>();
    StringDeleteAdapter listAdapter;
    ImageView imgCageNo, imgCageSeal;
    Button btnScanCageNo, btnScanCageSeal;
    String scanType;

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
    }


    private void initialize() {
        img_manual_entry = findViewById(R.id.img_manual_entry);
        imgCageNo = findViewById(R.id.imgCageNo);
        imgCageSeal = findViewById(R.id.imgCageSeal);
        txt_cage_no = findViewById(R.id.barcodeTextCageNo);
        txt_cage_seal = findViewById(R.id.barcodeTextCageSeal);
        btnScanCageNo = findViewById(R.id.btnScanCageNo);
        btnScanCageSeal = findViewById(R.id.btnScanCageSeal);
        btnScanCageSeal.setOnClickListener(this);
        btnScanCageNo.setOnClickListener(this);

        ((BarCodeScanActivity) context).registerScannerEvent(this);
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
        }
    }

    @Override
    public void onDataScanned(String data) {
        if (scanType.equals("CageNo")) {
            txt_cage_no.setText(data);
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(data, BarcodeFormat.CODABAR, 150, 50);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                imgCageNo.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        } else {
            txt_cage_seal.setText(data);
            MultiFormatWriter multiFormatWriter2 = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter2.encode(data, BarcodeFormat.CODABAR, 150, 50);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                imgCageSeal.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResult(int api_code, int result_code, String result_data) {
    }

}