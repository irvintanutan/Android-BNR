package com.novigosolutions.certiscisco_pcsbr.activites;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.adapters.PrintJobListAdapter;
import com.novigosolutions.certiscisco_pcsbr.adapters.PrintSelectedJobListAdapter;
import com.novigosolutions.certiscisco_pcsbr.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco_pcsbr.interfaces.PrintCallBack;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.objects.Summary;
import com.novigosolutions.certiscisco_pcsbr.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;
import com.novigosolutions.certiscisco_pcsbr.utils.Constants;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.zebra.BulkImageGenerator;
import com.novigosolutions.certiscisco_pcsbr.zebra.BulkPrintData;
import com.novigosolutions.certiscisco_pcsbr.zebra.BulkSelectedPrintData;
import com.novigosolutions.certiscisco_pcsbr.zebra.GenerateImage;
import com.novigosolutions.certiscisco_pcsbr.zebra.Print;
import com.novigosolutions.certiscisco_pcsbr.zebra.PrinterSelected;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PrintSelectedJobActivity extends BaseActivity implements View.OnClickListener, PrintSelectedJobListAdapter.CheckBoxListnerCallBack, NetworkChangekListener, PrintCallBack {
    Button btnBack, btnPrint, btnPrintAll, btnSelectedPrint, btnCancel;
    TextView txtPrintCount;
    ImageView imageView;
    ConstraintLayout mainContainer;
    int transporterMasterId;
    String status;
    ImageView imgnetwork;
    CardView progressView;
    // Toolbar toolbar;
    public static List<Job> list;
    protected MenuItem refreshItem = null;
    RelativeLayout layoutPrintProcess;
    LinearLayout layoutSelectJobs, layoutToolBar;
    RecyclerView recyclerView;
    List<Job> jobList;
    List<Print> printDataArray;
    List<String> printTemplateList;
    List<String> bulktemplateList = new ArrayList<>();
    private PrintSelectedJobListAdapter listAdapter;
    Runnable runnable;
    Handler handler;
    PrinterSelected printer;
    GenerateImage generateImage;
    BulkImageGenerator bulkImageGenerator;
    Thread t;
    int printCount = 0;
    Boolean alertFlag = true;
    private ProgressDialog progressDialog;
    String groupKey;
    int isCollection, isDelivered;
    boolean isOffline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        initialiser();
        setupToolBar();
    }

    private void initialiser() {
        mainContainer = findViewById(R.id.mainContainer);
        btnPrint = findViewById(R.id.bt_print);
        btnPrintAll = findViewById(R.id.bt_printAll);
        btnBack = findViewById(R.id.bt_back);
        btnCancel = findViewById(R.id.bt_cancel_print);
        btnSelectedPrint = findViewById(R.id.bt_print_selected);
        txtPrintCount = findViewById(R.id.lb_print_count);
        imageView = findViewById(R.id.img_print);
        progressView = findViewById(R.id.cv_progress);
        layoutSelectJobs = (LinearLayout) findViewById(R.id.lc_recycle);
        layoutPrintProcess = findViewById(R.id.lc_progress);
        btnBack.setOnClickListener(this);
        layoutToolBar = findViewById(R.id.lc_tool_bar);
        btnPrintAll.setOnClickListener(this);
        btnPrint.setOnClickListener(this);
        btnSelectedPrint.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        recyclerView = findViewById(R.id.recyclerview);
        progressView.setVisibility(View.GONE);
        printer = new PrinterSelected(PrintSelectedJobActivity.this, this);
        list = new ArrayList<>();
        list.clear();
        printDataArray = new ArrayList<>();
        printTemplateList = new ArrayList<>();
    }

    private void setupToolBar() {
        Bundle extras = getIntent().getExtras();
        status = extras.getString("status");
        groupKey = extras.getString("groupKey");
        isDelivered = extras.getInt("isDelivered");
        isCollection = extras.getInt("isCollection");
        transporterMasterId = extras.getInt("transporterMasterId");
        isOffline = extras.getBoolean("isOffline");
        Log.e("TRANSPORT ID ", Integer.toString(transporterMasterId));
        if (status.equals("COMPLETED")) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            mTitle.setText("COMPLETED JOBS");
            TextView UserName = (TextView) toolbar.findViewById(R.id.UserName);
            UserName.setText(Preferences.getString("UserName", this));
            imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
            bulkPrintViewInit();
            clearSelectedJob();
            if (transporterMasterId != 123) {
                if (isOffline) {
                    list = Job.getSpecificJobListByType(transporterMasterId);
                } else {
                    Job job = Job.getSingle(transporterMasterId);
                    list = Job.getJobListByType(isDelivered, isCollection, job.GroupKey , job.BranchCode , job.PFunctionalCode, job.ActualFromTime, job.ActualToTime);
                }
                mainContainer.setVisibility(View.GONE);
                checkBluetoothConnection();
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            list = Job.getSpecificJobListByType(isDelivered, isCollection, transporterMasterId);
            singlePrintViewInit();
        }
    }

    private void singlePrintViewInit() {
        layoutToolBar.setVisibility(View.GONE);
        setPrintImage();
        if (status.equals("SINGLE")) {
            btnBack.setText("HOME");
        } else if (status.equals("PRINT")) {
            btnPrint.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            checkBluetoothConnection();
        }
    }


    private void bulkPrintViewInit() {
        layoutSelectJobs.setVisibility(View.VISIBLE);
        btnPrintAll.setVisibility(View.VISIBLE);
        btnPrint.setVisibility(View.GONE);
        layoutPrintProcess.setVisibility(View.GONE);
        printRecyclerViewInit();
    }

    private void printRecyclerViewInit() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        jobList = getJobList();
        listAdapter = new PrintSelectedJobListAdapter(jobList, status, this, this, isDelivered);
        recyclerView.setAdapter(listAdapter);
    }

    private List<Job> getJobList() {
        return Job.getJobListByType(isDelivered, isCollection);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_print:
            case R.id.bt_print_selected:
                checkBluetoothConnection();
                break;
            case R.id.bt_printAll:
                printAll();
                break;
            case R.id.bt_back:
                if (status.equals("SINGLE")) {
                    clearSelectedJob();
                    Intent intent = new Intent(PrintSelectedJobActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    clearSelectedJob();
                    onBackPressed();
                }
                break;
            case R.id.bt_cancel_print:
                alertView();
                break;
        }
    }

    //click on printAll button
    private void printAll() {
        list.clear();
        list = jobList;
        checkBluetoothConnection();
    }

    // disabling all buttons and enabling print progress ui
    private void printUIChange() {
        if (status.equals("COMPLETED")) {
            layoutPrintProcess.setVisibility(View.VISIBLE);
            btnPrintAll.setVisibility(View.GONE);
            btnSelectedPrint.setVisibility(View.GONE);
            layoutSelectJobs.setVisibility(View.GONE);
            btnCancel.setVisibility(View.VISIBLE);
        } else {
            btnPrint.setVisibility(View.GONE);
        }
        txtPrintCount.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.GONE);
        txtPrintCount.setText("Printed 0 / " + printDataArray.size());
        Glide.with(this)
                .asGif()
                .load(R.raw.printerr)
                .into(imageView);
        //calling actually print data passing method
        receiptPrint();

    }

    private void checkBluetoothConnection() {
        if (list.size() != 0) {
            if (printer.checkBluetoothConection()) {
                if (printer.checkDeviceAvailability()) {
                    try {
                        processPrintData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        hideProgressDialog();
                        raiseSnakbar("Print data error");
                        cancelProcess();
                    }
                } else {
                    Toast.makeText(PrintSelectedJobActivity.this, "Please select the printer", Toast.LENGTH_LONG).show();
                    if (transporterMasterId != 123) {
                        Intent intent = new Intent(PrintSelectedJobActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            } else {
                Toast.makeText(PrintSelectedJobActivity.this, "Bluetooth not on", Toast.LENGTH_LONG).show();
            }
        } else {
            raiseSnakbar("No jobs are selected");
        }
    }

    private void sendPrintDataValidation() {
        try {
            if (printDataArray.size() == 0)
                raiseSnakbar("No Data to print");
            else
                printUIChange();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processPrintData() {
        printDataArray.clear();
        BulkSelectedPrintData bulkPrintData = new BulkSelectedPrintData(PrintSelectedJobActivity.this, status, list, isDelivered);
        bulkPrintData.execute();
    }

    public void receiptPrint() {
        hideProgressDialog();
        if (printer.doConnectionTestForBulk()) {
            bulkImageGenerator = new BulkImageGenerator(PrintSelectedJobActivity.this, printDataArray);
            bulkImageGenerator.execute();
        } else {
            Toast.makeText(PrintSelectedJobActivity.this, "Failed to connect the printer", Toast.LENGTH_LONG).show();
        }
    }

    //click on cancel button
    public void cancelProcess() {
        alertFlag = true;
        printCount = 0;
        printDataArray.clear();

        if (transporterMasterId != 123) {
            Intent intent = new Intent(PrintSelectedJobActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        if (status.equals("COMPLETED")) {
            list.clear();
            layoutSelectJobs.setVisibility(View.VISIBLE);
            btnPrintAll.setVisibility(View.VISIBLE);
            btnSelectedPrint.setVisibility(View.GONE);
            layoutPrintProcess.setVisibility(View.GONE);
            printRecyclerViewInit();
            clearSelectedJob();
        } else {
            setPrintImage();
            btnPrint.setVisibility(View.VISIBLE);
        }
        btnCancel.setVisibility(View.GONE);
        btnBack.setVisibility(View.VISIBLE);


    }

    private void setPrintImage() {
        Glide.with(this)
                .load(R.drawable.printerrimg)
                .into(imageView);
    }


    @Override
    protected void onResume() {
        super.onResume();
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
    }

    @Override
    public void onNetworkChanged() {
        if (status.equals("COMPLETED")) {
            if (NetworkUtil.getConnectivityStatusString(this))
                imgnetwork.setImageResource(R.drawable.network);
            else
                imgnetwork.setImageResource(R.drawable.no_network);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (status.equals("SINGLE")) {
            clearSelectedJob();
            Intent intent = new Intent(PrintSelectedJobActivity.this, HomeActivity.class);
            startActivity(intent);
        } else {
            onBackPressed();
            clearSelectedJob();
        }
        finish();
        return true;
    }

    @Override
    public void onChange() {
        if (list.size() == 0) {
            btnSelectedPrint.setVisibility(View.GONE);
            btnPrintAll.setVisibility(View.VISIBLE);
        } else {
            btnSelectedPrint.setVisibility(View.VISIBLE);
            btnPrintAll.setVisibility(View.GONE);
        }
    }

    public void alertView() {
        printer.cancelBulkPrint();    // cancel the print job
        cancelProcess();
    }

    public List<String> getbulktemplateList() {
        return bulktemplateList;
    }

    public void setprintDataArray(List<Print> printDataArray) {
        this.printDataArray = printDataArray;
        sendPrintDataValidation();
        mainContainer.setVisibility(View.VISIBLE);
    }

    public void setbulktemplateList(List<String> bulktemplateList) {
        this.bulktemplateList = bulktemplateList;
        printer.printBulk(PrinterSelected.PRINT, status);
    }

    @Override
    public void onPrint(int flag, String message) {
        if (flag == 1) {
            Toast.makeText(PrintSelectedJobActivity.this, "Printing Completed", Toast.LENGTH_LONG).show();
            if (status.equals("SINGLE")) {
                Intent intent = new Intent(PrintSelectedJobActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(PrintSelectedJobActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);
            }
        } else if (flag == 2) {
            printCount++;
            txtPrintCount.setText("Printed " + printCount + " / " + printDataArray.size());
        } else if (flag == 3) {

        }
    }

    void clearSelectedJob() {
        if (status.equals("COMPLETED"))
            for (Job job : jobList) {
                job.setSelected(false);
            }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PrintSelectedJobActivity.this, SelectedJobListActivity.class);
        intent.putExtra("isCollection", Constants.isCollection ? 1:0);
        intent.putExtra("isDelivered", Constants.isCollection ? 0:1);
        startActivity(intent);
        finish();
    }
}