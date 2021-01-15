package com.novigosolutions.certiscisco_pcsbr.activites.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.activites.CollectionActivity;
import com.novigosolutions.certiscisco_pcsbr.adapters.StringAdapter;
import com.novigosolutions.certiscisco_pcsbr.adapters.StringDeleteAdapter;
import com.novigosolutions.certiscisco_pcsbr.interfaces.ApiCallback;
import com.novigosolutions.certiscisco_pcsbr.interfaces.DialogResult;
import com.novigosolutions.certiscisco_pcsbr.interfaces.IOnScannerData;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Envelope;
import com.novigosolutions.certiscisco_pcsbr.models.EnvelopeBag;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.utils.Constants;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;
import com.symbol.emdk.barcode.ScannerException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class EnvelopeDialog extends Dialog implements View.OnClickListener, IOnScannerData, ApiCallback,RecyclerViewClickListener {

    Context context;
    Button btn_scan, btn_done, btn_cancel;
    RecyclerView listView;
    TextView txt_head, txt_bag_code,txt_count,txt_count_bag;
    LinearLayout ll_bag, ll_listView;
    List<String> bar_code_list = new ArrayList<>();
    DialogResult mDialogResult;
    StringDeleteAdapter listAdapter;
    String envelopeType;
    int TransportMasterId;
    Boolean isBagCodeScan = false;
    ImageView img_manual_entry;
    View l_manual_entry;
    Button btn_ok;
    EditText editText;

    public EnvelopeDialog(Context context, int TransportMasterId, DialogResult mDialogResult, String envelopeType) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);
        this.context = context;
        this.TransportMasterId = TransportMasterId;
        this.envelopeType = envelopeType;
        this.mDialogResult = mDialogResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.envelope_dialog);

        btn_scan = findViewById(R.id.btn_scan);
        btn_done = findViewById(R.id.btn_done);
        btn_cancel = findViewById(R.id.btn_cancel);
        listView = findViewById(R.id.listview);
        txt_head = findViewById(R.id.txt_head);
        txt_bag_code = findViewById(R.id.bag_code);
        txt_count = findViewById(R.id.txt_count);
        txt_count_bag=findViewById(R.id.txt_count_bag);//added line
        txt_count_bag.setVisibility(View.GONE);
        ll_bag = findViewById(R.id.ll_bag);
        ll_listView = findViewById(R.id.ll_listview);
        if (envelopeType.equals("Envelopes")) {
            txt_head.setText("Envelope(s)");
            btn_done.setText("Done");
        } else {
            txt_head.setText("Envelope(s) In Bag");
            btn_done.setText("Add To Bag");
        }
        img_manual_entry = findViewById(R.id.img_manual_entry);
        l_manual_entry = findViewById(R.id.l_manual_entry);
        img_manual_entry.setOnClickListener(this);
        btn_scan.setOnClickListener(this);
        btn_done.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_ok = findViewById(R.id.btn_ok);
        editText = findViewById(R.id.edittext);
        btn_ok.setOnClickListener(this);
        listAdapter = new StringDeleteAdapter(bar_code_list,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(listAdapter);
        if (Preferences.getBoolean("EnableManualEntry", context) || Job.getSingle(TransportMasterId).EnableManualEntry) {
            enableManualEntry();
        }
        ((BarCodeScanActivity)context).registerScannerEvent(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                try {
                    ((BarCodeScanActivity)context).scansoft();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_done:
                if (btn_done.getText().equals("Add To Bag")) {
                    if (bar_code_list.size() > 0) {
                        isBagCodeScan = true;
                        ll_bag.setVisibility(View.VISIBLE);
                        txt_bag_code.requestFocus();
                        btn_done.setText("Done");
                        l_manual_entry.setVisibility(View.GONE);
                        StringAdapter stringAdapter = new StringAdapter(bar_code_list);
                        listView.setAdapter(stringAdapter);
                    } else {
                        Toast.makeText(context, "Envelope(s) can't be empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //to include special characters in the barcode entry ,removed regx condition
                    //  String bagcode = txt_bag_code.getText().toString().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
                    String bagcode = txt_bag_code.getText().toString().trim();
                    if (envelopeType.equals("EnvelopeBag") && bagcode.length() == 0) {
                        Toast.makeText(context, "Bag bar" +
                                "code can't be empty ", Toast.LENGTH_SHORT).show();
                    } else if (envelopeType.equals("EnvelopeBag") && (bar_code_list.contains(bagcode) || Branch.isExist(bagcode))) {
                        ((CollectionActivity) context).invalidbarcodealert("Duplicate");
                    } else if (bar_code_list.size() == 0) {
                        Toast.makeText(context, "Envelope(s) can't be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        EnvelopeBag envelopeBag = new EnvelopeBag();
                        envelopeBag.TransportMasterId = TransportMasterId;
                        envelopeBag.envolpeType = envelopeType;
                        if (envelopeType.equals("Envelopes"))
                            envelopeBag.bagcode = "none";
                        else
                            envelopeBag.bagcode = txt_bag_code.getText().toString().trim();
                        long id = envelopeBag.save();

                        for (int i = 0; i < bar_code_list.size(); i++) {
                            Envelope envelope = new Envelope();
                            envelope.bagid = id;
                            envelope.barcode = bar_code_list.get(i);
                            envelope.save();
                        }
                        if (mDialogResult != null) {
                            mDialogResult.onResult();
                        }
                        dismiss();
                    }
                }
                break;
            case R.id.img_manual_entry:
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
                break;
            case R.id.btn_ok:
                //String barcode = editText.getText().toString().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
                String barcode = editText.getText().toString().trim();
                if (barcode.length() > 0) {
                    if (bar_code_list.contains(barcode) || Branch.isExist(barcode)) {
                        ((CollectionActivity) context).invalidbarcodealert("Duplicate");
                    } else {
                        add(barcode);
                        editText.setText("");
                    }
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }


    @Override
    public void onDataScanned(String data) {
        if (data.isEmpty()) {
            ((CollectionActivity) context).invalidbarcodealert("Empty");
        } else if (bar_code_list.contains(data) || Branch.isExist(data))
            ((CollectionActivity) context).invalidbarcodealert("Duplicate");
        else {
            if (txt_bag_code.isFocused()) {
                txt_bag_code.setText(data);
              //  txt_count_bag.setText("Count : "+"1");

            } else if (editText.isFocused()) {
                add(data);
            } else if (isBagCodeScan) {
                txt_bag_code.setText(data);
            //    txt_count_bag.setText("Count : "+"1");
            } else {
                add(data);
            }
        }
    }

    private void add(String data)
    {
        bar_code_list.add(data);
        listAdapter.notifyDataSetChanged();
        txt_count.setText("Count : "+bar_code_list.size());
    }

    private void remove(int pos)
    {
        bar_code_list.remove(pos);
        listAdapter.notifyDataSetChanged();
        txt_count.setText("Count : "+bar_code_list.size());
    }

    private void enableManualEntry() {
        txt_bag_code.setFocusableInTouchMode(true);
        txt_bag_code.setCursorVisible(true);
        l_manual_entry.setVisibility(View.VISIBLE);
        img_manual_entry.setVisibility(View.GONE);
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
                        enableManualEntry();
                    } else if (result_data.replaceAll("\\W", "").equalsIgnoreCase("REJECTED")) {
                        Job.UpdateRequestId(TransportMasterId,"");
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
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                remove(pos);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();

    }


}