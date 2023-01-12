package com.novigosolutions.certiscisco_pcsbr.activites.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.activites.CollectionActivity;
import com.novigosolutions.certiscisco_pcsbr.adapters.CoinBagAdapter;
import com.novigosolutions.certiscisco_pcsbr.adapters.StringDeleteAdapter;
import com.novigosolutions.certiscisco_pcsbr.interfaces.ApiCallback;
import com.novigosolutions.certiscisco_pcsbr.interfaces.DialogResult;
import com.novigosolutions.certiscisco_pcsbr.interfaces.IOnScannerData;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco_pcsbr.models.Bags;
import com.novigosolutions.certiscisco_pcsbr.models.BoxBag;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.CoinSeries;
import com.novigosolutions.certiscisco_pcsbr.models.Currency;
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

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class CoinBoxDialog extends Dialog implements View.OnClickListener, IOnScannerData, ApiCallback, RecyclerViewClickListener {

    Context context;
    Button btn_scan, btn_done, btn_cancel, btn_add;
    LinearLayout ll_manual_entry;
    TextView txt_bag_code,txt_count;
    List<Currency> currencies;
    List<String> denominationlist = new ArrayList<>();
    DialogResult mDialogResult;
    int TransportMasterId;
    Spinner spinner;
    ImageView img_manual_entry;

    RecyclerView recyclerView;
    List<String> bar_code_list = new ArrayList<>();
    StringDeleteAdapter listAdapter;
    CoinBagAdapter coinBagAdapter;

    int customerID;
    List<CoinSeries> coinSeries;
    List<String> coinserieslist = new ArrayList<>();
    Spinner spinnerCoinSeries;
    LinearLayout ltCoinSpinner;

    List<String> mDenominationList=new ArrayList<>();
    List<Integer> mProductIdList = new ArrayList<>();
    List<String> mBarCodeList = new ArrayList<>();
    List<String> mCoinSeriesList=new ArrayList<>();
    List<Integer> mCoinSeriesIdList = new ArrayList<>();

    public CoinBoxDialog(Context context, int TransportMasterId, DialogResult mDialogResult,int customerID) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);
        this.context = context;
        this.TransportMasterId = TransportMasterId;
        this.mDialogResult = mDialogResult;
        this.customerID=customerID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.coin_box_dialog);
        btn_done = findViewById(R.id.btn_done);
        btn_add = findViewById(R.id.btn_add);
        btn_cancel = findViewById(R.id.btn_cancel);
        spinner = findViewById(R.id.spn_denomination);
        ll_manual_entry=findViewById(R.id.ll_manual_entry);
        btn_done.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        txt_bag_code = findViewById(R.id.txt_bag_code);
        txt_count=findViewById(R.id.txt_count);
        recyclerView = findViewById(R.id.recyclerview);
        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);
        img_manual_entry = findViewById(R.id.img_manual_entry);
        img_manual_entry.setOnClickListener(this);
      //  currencies = Currency.getByCustomerId(Job.getSingle(TransportMasterId).CustomerId);
        spinnerCoinSeries = findViewById(R.id.spn_coinseries);
        ltCoinSpinner=findViewById(R.id.lt_coinseries);

        //currencies=Currency.getByCustomerId(Job.getSingle(TransportMasterId).TransportMasterId);
        currencies=Currency.getByCustomerId(customerID);
        Log.e("boxcount", ":" + currencies.size());
        for (int i = 0; i < currencies.size(); i++) {
           // denominationlist.add(currencies.get(i).ProductName);
            if(currencies.get(i).IsCoinValue.equals("Yes")){
                denominationlist.add(currencies.get(i).ProductName);
            }
        }
        coinSeries=CoinSeries.getAllCoinSeries();
        if (coinSeries.size()!= 0){
            for (int i=0;i<coinSeries.size();i++){
                coinserieslist.add(coinSeries.get(i).DataDescription);
            }
        }
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,coinserieslist);
        spinnerCoinSeries.setAdapter(dataAdapter1);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, denominationlist);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               // bar_code_list.clear();
               // listAdapter.notifyDataSetChanged();
                txt_count.setText("Count : "+mBarCodeList.size());
                if(currencies.get(i).IsCoinValue.equals("Yes")){
                    ltCoinSpinner.setVisibility(View.VISIBLE);
                }else {
                    ltCoinSpinner.setVisibility(View.GONE);
                }
                }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (Preferences.getBoolean("EnableManualEntry", context) || Job.getSingle(TransportMasterId).EnableManualEntry) {
            enableManualEntry();
        }

        coinBagAdapter = new CoinBagAdapter(mBarCodeList,mDenominationList,mCoinSeriesList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(coinBagAdapter);
        txt_count.setText("Count : "+mBarCodeList.size());

        ((BarCodeScanActivity) context).registerScannerEvent(this);
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
                if (mBarCodeList.size() > 0) {
                    Boolean failed=false;
                    for (int i = 0; i < mBarCodeList.size(); i++) {
                      //  String bagcode = mBarCodeList.get(i).replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
                        String bagcode = mBarCodeList.get(i);
                        if (bagcode.length() > 0) {
                            if (Branch.isExist(bagcode)) {
                                ((CollectionActivity) context).invalidbarcodealert("Duplicate");
                                failed=true;
                            }
                            else {
                                BoxBag boxBag = new BoxBag();
                                boxBag.TransportMasterId = TransportMasterId;
                                boxBag.bagcode = bagcode;
                                boxBag.ProductId = mProductIdList.get(i);
                                boxBag.ProductName = mDenominationList.get(i);
                                boxBag.CoinSeries=mCoinSeriesList.get(i);
                                boxBag.CoinSeriesId=mCoinSeriesIdList.get(i);
//                                boxBag.TransportMasterId = TransportMasterId;
//                                boxBag.bagcode = bagcode;
//                                boxBag.ProductId = currencies.get(spinner.getSelectedItemPosition()).ProductId;
//                                boxBag.ProductName = spinner.getSelectedItem().toString();
//                                boxBag.CoinSeries=spinnerCoinSeries.getSelectedItem().toString();
//                                boxBag.CoinSeriesId=coinSeries.get(spinnerCoinSeries.getSelectedItemPosition()).CoinSeriesId;
                               boxBag.save();
                            }
                        }
                        else
                        {
                            failed=true;
                        }
                    }
                    if(!failed)
                    {
                        if (mDialogResult != null) {
                            mDialogResult.onResult();
                        }
                        dismiss();
                    }
                } else {
                    Toast.makeText(context, "Barcode can't be empty", Toast.LENGTH_SHORT).show();
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

            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_add:
                if (txt_bag_code.getText().toString().length() > 0) {
                    if (Branch.isExist(txt_bag_code.getText().toString()))
                        ((CollectionActivity) context).invalidbarcodealert("Duplicate");
                    else {
                        add(txt_bag_code.getText().toString());
                        txt_bag_code.setText("");
                    }
                } else {
                    Toast.makeText(context, "Enter Bag Code", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void add(String data) {
        if (mBarCodeList.contains(data) || Branch.isExist(data)) {
            ((CollectionActivity) context).invalidbarcodealert("Duplicate");
        } else {
            mBarCodeList.add(data);
            mDenominationList.add(spinner.getSelectedItem().toString());
            mProductIdList.add(currencies.get(spinner.getSelectedItemPosition()).ProductId);
            mCoinSeriesList.add(spinnerCoinSeries.getSelectedItem().toString());
            mCoinSeriesIdList.add(coinSeries.get(spinnerCoinSeries.getSelectedItemPosition()).CoinSeriesId);
            coinBagAdapter.notifyDataSetChanged();
            txt_count.setText("Count : "+mBarCodeList.size());
            recyclerView.smoothScrollToPosition(coinBagAdapter.getItemCount()-1);

//            bar_code_list.add(data);
//            listAdapter.notifyDataSetChanged();
//            txt_count.setText("Count : "+bar_code_list.size());
//            recyclerView.smoothScrollToPosition(listAdapter.getItemCount() -1);
        }
    }

    private void remove(int pos) {

        mBarCodeList.remove(pos);
        mDenominationList.remove(pos);
        mProductIdList.remove(pos);
        mCoinSeriesList.remove(pos);
        mCoinSeriesIdList.remove(pos);

       /// bar_code_list.remove(pos);
        //  listAdapter.notifyDataSetChanged();
        txt_count.setText("Count : "+mBarCodeList.size());
        coinBagAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDataScanned(String data) {
        if (Branch.isExist(data))
            ((CollectionActivity) context).invalidbarcodealert("Duplicate");
        else {
            add(data);
        }
    }

    private void enableManualEntry() {
        ll_manual_entry.setVisibility(View.VISIBLE);
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