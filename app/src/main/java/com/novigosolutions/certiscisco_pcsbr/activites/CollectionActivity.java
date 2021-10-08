package com.novigosolutions.certiscisco_pcsbr.activites;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.activites.dialogs.BagDialog;
import com.novigosolutions.certiscisco_pcsbr.activites.dialogs.BarCodeScanActivity;
import com.novigosolutions.certiscisco_pcsbr.activites.dialogs.BoxDialog;
import com.novigosolutions.certiscisco_pcsbr.activites.dialogs.CoinBoxDialog;
import com.novigosolutions.certiscisco_pcsbr.activites.dialogs.EnvelopeDialog;
import com.novigosolutions.certiscisco_pcsbr.activites.dialogs.PalletDialog;
import com.novigosolutions.certiscisco_pcsbr.activites.dialogs.WagonDialog;
import com.novigosolutions.certiscisco_pcsbr.adapters.CollectionSummaryAdapter;
import com.novigosolutions.certiscisco_pcsbr.interfaces.DialogResult;
import com.novigosolutions.certiscisco_pcsbr.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.objects.Summary;
import com.novigosolutions.certiscisco_pcsbr.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco_pcsbr.utils.Constants;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CollectionActivity extends BarCodeScanActivity implements DialogResult, NetworkChangekListener {
    Spinner spinner;
    int trasportMasterId;
    TextView from_customer_name, from_functional_code, from_street_tower, from_town_pin, to_customer_name, to_functional_code, to_street_tower, to_town_pin;
    Button btn_go, btn_next,btnCancel;
    RecyclerView recyclerView;
    private CollectionSummaryAdapter mAdapter;
    List<Summary> collectionSummaries;
    ImageView imgnetwork;
    int customerID;
    String GroupKey;
    //private EMDKWrapper0 emdkWrapper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        setuptoolbar();

        spinner = findViewById(R.id.spn_collection_type);

        from_customer_name = findViewById(R.id.from_customer_name);
        from_functional_code = findViewById(R.id.from_functional_code);
        from_street_tower = findViewById(R.id.from_street_tower);
        from_town_pin = findViewById(R.id.from_town_pin);

        to_customer_name = findViewById(R.id.to_customer_name);
        to_functional_code = findViewById(R.id.to_functional_code);
        to_street_tower = findViewById(R.id.to_street_tower);
        to_town_pin = findViewById(R.id.to_town_pin);

        btn_go = findViewById(R.id.btn_go);
        btn_next = findViewById(R.id.btn_next);
        recyclerView = findViewById(R.id.recyclerview);
        btnCancel = findViewById(R.id.btn_cancel);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            trasportMasterId = extras.getInt("TransportMasterId");
            GroupKey = Job.getSingle(trasportMasterId).GroupKey;
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Job.getCollectionTypes(trasportMasterId));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        collectionSummaries = Job.getCollectionSummary(trasportMasterId);
        mAdapter = new CollectionSummaryAdapter(collectionSummaries, false, this);
        recyclerView.setAdapter(mAdapter);

        Job job = Job.getSingle(trasportMasterId);
        Branch branch = Branch.getSingle(job.GroupKey);
        customerID=branch.CustomerId;

        from_customer_name.setText(branch.CustomerName);
        from_functional_code.setText(branch.FunctionalCode);
        String fstreet_tower = job.BranchStreetName;
        if (!job.BranchTower.isEmpty()) {
            if (fstreet_tower.isEmpty()) fstreet_tower = job.BranchTower;
            else fstreet_tower = fstreet_tower + ", " + job.BranchTower;
        }
        from_street_tower.setText(fstreet_tower);
        String ftown_pin = job.BranchTown;
        if (!job.BranchPinCode.isEmpty()) {
            if (ftown_pin.isEmpty()) ftown_pin = job.BranchPinCode;
            else ftown_pin = ftown_pin + ", " + job.BranchPinCode;
        }
        from_town_pin.setText(ftown_pin);

        to_customer_name.setText(job.CustomerName);
        to_functional_code.setText(job.FunctionalCode);
        String tstreet_tower = job.StreetName;
        if (job.Tower != null && !job.Tower.isEmpty()) {
            if (tstreet_tower.isEmpty()) tstreet_tower = job.Tower;
            else tstreet_tower = tstreet_tower + ", " + job.Tower;
        }
        to_street_tower.setText(tstreet_tower);
        String ttown_pin = job.Town;
        if (!job.PinCode.isEmpty()) {
            if (ttown_pin.isEmpty()) ttown_pin = job.PinCode;
            else ttown_pin = ttown_pin + ", " + job.PinCode;
        }
        to_town_pin.setText(ttown_pin);

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialoge();
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert();
            }
        });
    }

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("COLLECTION");
        TextView UserName = (TextView) toolbar.findViewById(R.id.UserName);
        UserName.setText(Preferences.getString("UserName", this));
        imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        super.onPause();
    }


    private void openDialoge() {
        String collection_type = spinner.getSelectedItem().toString();
        if (collection_type.equals("Sealed Bag")) {
            BagDialog bagDialog = new BagDialog(CollectionActivity.this, trasportMasterId, this);
            bagDialog.setCancelable(false);
            bagDialog.show();
        } else if (collection_type.equals("Envelope(s)")) {
            EnvelopeDialog envelopeDialog = new EnvelopeDialog(CollectionActivity.this, trasportMasterId, this, "Envelopes");
            envelopeDialog.setCancelable(false);
            envelopeDialog.show();
        } else if (collection_type.equals("Envelope(s) In Bag")) {
            EnvelopeDialog envelopeDialog = new EnvelopeDialog(CollectionActivity.this, trasportMasterId, this, "EnvelopeBag");
            envelopeDialog.setCancelable(false);
            envelopeDialog.show();
        } else if (collection_type.equals("Box")) {
            BoxDialog boxDialog = new BoxDialog(CollectionActivity.this, trasportMasterId, this,customerID);
            boxDialog.setCancelable(false);
            boxDialog.show();
        } else if (collection_type.equals("Pallet")) {
            PalletDialog palletDialog = new PalletDialog(CollectionActivity.this, trasportMasterId, this);
            palletDialog.setCancelable(false);
            palletDialog.show();
        } else if (collection_type.equals("Coin Bag")) {
            Log.e("openeing","coin bag");
            CoinBoxDialog coinBoxDialog = new CoinBoxDialog(CollectionActivity.this, trasportMasterId, this,customerID);
            coinBoxDialog.setCancelable(false);
            coinBoxDialog.show();
            Window window=coinBoxDialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else if (collection_type.equals("Wagon")) {
            WagonDialog wagonDialog = new WagonDialog(CollectionActivity.this, trasportMasterId, this);
            wagonDialog.setCancelable(false);
            wagonDialog.show();
        }
    }

    private void refresh() {
        List<Summary> tempList = Job.getCollectionSummary(trasportMasterId);
        collectionSummaries.clear();
        collectionSummaries.addAll(tempList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResult() {
        refresh();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onNetworkChanged() {
        if (NetworkUtil.getConnectivityStatusString(this))
            imgnetwork.setImageResource(R.drawable.network);
        else
            imgnetwork.setImageResource(R.drawable.no_network);
    }

    private void alert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Are you sure you want to cancel the transaction?\nCurrent progress will be lost.");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Job.clearBranchCollecion(GroupKey);
                setResult(Constants.FINISHONRESULT);
                onBackPressed();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }
}
