package com.novigosolutions.certiscisco_pcsbr.activites.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.adapters.CoinAdapter;
import com.novigosolutions.certiscisco_pcsbr.constant.UserLog;
import com.novigosolutions.certiscisco_pcsbr.interfaces.DialogResult;
import com.novigosolutions.certiscisco_pcsbr.models.Box;
import com.novigosolutions.certiscisco_pcsbr.models.CoinSeries;
import com.novigosolutions.certiscisco_pcsbr.models.Currency;
import com.novigosolutions.certiscisco_pcsbr.service.UserLogService;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class BoxDialog extends Dialog implements View.OnClickListener {

    Context context;
    Button btn_add, btn_done, btn_cancel;
    EditText edt_count;
    TextView txt_count;
    List<Currency> currencies;
    int customerID;

    List<String> currancyNames = new ArrayList<>();
    List<Integer> counts = new ArrayList<>();
    List<Integer> ids = new ArrayList<>();

    List<String> denominationlist = new ArrayList<>();
    DialogResult mDialogResult;
    RecyclerView recyclerView;
    private CoinAdapter mAdapter;
    int TransportMasterId;
    int pos = 0;

    Spinner spinner, spinnerCoinSeries;
    LinearLayout ltCoinSpinner;
    List<CoinSeries> coinSeries;
    List<String> coinserieslist = new ArrayList<>();
    List<String> coinseriesDescription = new ArrayList<>();
    List<Integer> coinseriesIds = new ArrayList<Integer>();

    public BoxDialog(Context context, int TransportMasterId, DialogResult mDialogResult, int customerID) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);
        this.context = context;
        this.TransportMasterId = TransportMasterId;
        this.mDialogResult = mDialogResult;
        this.customerID = customerID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.box_dialog);
        btn_add = findViewById(R.id.btn_add);
        btn_done = findViewById(R.id.btn_done);
        btn_cancel = findViewById(R.id.btn_cancel);
        spinner = findViewById(R.id.spn_denomination);
        spinnerCoinSeries = findViewById(R.id.spn_coinseries);
        ltCoinSpinner = findViewById(R.id.lt_coinseries);
        recyclerView = findViewById(R.id.recyclerview);
        edt_count = findViewById(R.id.edt_count);
        txt_count = findViewById(R.id.txt_count);
        btn_add.setOnClickListener(this);
        btn_done.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        List<Box> boxes = Box.getBoxByTransportMasterIdWithoutCage(TransportMasterId);
        if (boxes != null) {
            for (int i = 0; i < boxes.size(); i++) {
                Box box = boxes.get(i);
                currancyNames.add(box.ProductName);
                counts.add(box.count);
                ids.add(box.ProductId);
                coinseriesDescription.add(box.CoinSeries);
                coinseriesIds.add(box.CoinSeriesId);
            }
        }
        //  currencies = Currency.getByCustomerId(Job.getSingle(TransportMasterId).CustomerId);
        //  currencies=Currency.getByCustomerId(Job.getSingle(TransportMasterId).TransportMasterId);
        currencies = Currency.getByCustomerId(customerID);
        for (int i = 0; i < currencies.size(); i++) {
            denominationlist.add(currencies.get(i).ProductName);
        }
        coinSeries = CoinSeries.getAllCoinSeries();
        if (coinSeries.size() != 0) {
            for (int i = 0; i < coinSeries.size(); i++) {
                coinserieslist.add(coinSeries.get(i).DataDescription);
            }
        }
        if (denominationlist.size() < 1) btn_add.setVisibility(View.GONE);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, denominationlist);
        spinner.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, coinserieslist);
        spinnerCoinSeries.setAdapter(dataAdapter1);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        //mAdapter = new CoinAdapter(currancyNames, counts);
        mAdapter = new CoinAdapter(currancyNames, counts, coinseriesDescription);
        recyclerView.setAdapter(mAdapter);
        txt_count.setText("Count : " + itemCount());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (currencies.get(i).IsCoinValue.equals("Yes")) {
                    ltCoinSpinner.setVisibility(View.VISIBLE);
                } else {
                    ltCoinSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_add:
                if (edt_count.length() == 0) {
                    Toast.makeText(context, "Enter quantity", Toast.LENGTH_SHORT).show();
                } else if (edt_count.length() > 0) {
                    int count = 0;
                    if (!ids.isEmpty() && ids != null && ids.size() > 0) {
                        count = ids.size();
                    }
                    pos = hasDuplicateEntry(count);
                    if (pos != -1) {
                        int newCount = counts.get(pos) + Integer.parseInt(edt_count.getText().toString());
                        counts.set(pos, newCount);
                        mAdapter.notifyItemChanged(pos);
                    } else {
                        currancyNames.add(spinner.getSelectedItem().toString());
                        counts.add(Integer.parseInt(edt_count.getText().toString()));
                        ids.add(currencies.get(spinner.getSelectedItemPosition()).ProductId);
                        if (currencies.get(spinner.getSelectedItemPosition()).IsCoinValue.equals("Yes")) {
                            coinseriesDescription.add(spinnerCoinSeries.getSelectedItem().toString());
                            coinseriesIds.add(coinSeries.get(spinnerCoinSeries.getSelectedItemPosition()).CoinSeriesId);
                        } else {
                            coinseriesDescription.add("null");
                            coinseriesIds.add(0);
                        }
                        mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
                    }
                    UserLogService.save(UserLog.COLLECTION.toString(), "Currency : "
                            + spinner.getSelectedItem().toString() + " , Count : " + Integer.parseInt(edt_count.getText().toString()) + " , Series: "
                            + spinnerCoinSeries.getSelectedItem().toString(), "BOX SERIES", context);

                    txt_count.setText("Count : " + itemCount());
                    edt_count.setText("");
                } else {
                    Toast.makeText(context, "Invalid Count", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_done:
                if (ids.size() > 0) {
                    for (int i = 0; i < ids.size(); i++) {
                        //  Box.updateCount(TransportMasterId, ids.get(i), currancyNames.get(i), counts.get(i));
                        Box.updateCountNew(TransportMasterId, ids.get(i), currancyNames.get(i), counts.get(i), coinseriesDescription.get(i), coinseriesIds.get(i), null, null);
                    }

                    if (mDialogResult != null) {
                        mDialogResult.onResult();
                    }
                    dismiss();
                } else {
                    if (edt_count.length() == 0)
                        Toast.makeText(context, "Enter quantity", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(context, "Add Box", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    public int itemCount() {
        int count = 0;
        for (Integer e : counts) {
            count += e;
        }
        return count;
    }

    private int hasDuplicateEntry(int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                if (ids.get(i).equals(currencies.get(spinner.getSelectedItemPosition()).ProductId) && currancyNames.get(i).equals(spinner.getSelectedItem().toString())) {
                    if (currencies.get(spinner.getSelectedItemPosition()).IsCoinValue.equals("Yes")) {
                        if (coinseriesIds.get(i).equals(coinSeries.get(spinnerCoinSeries.getSelectedItemPosition()).CoinSeriesId)) {
                            return i;
                        }
                    } else {
                        return i;
                    }

                }
            }
        }
        return -1;
    }
}