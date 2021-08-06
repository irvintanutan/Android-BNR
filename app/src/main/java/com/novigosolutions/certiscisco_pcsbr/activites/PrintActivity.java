package com.novigosolutions.certiscisco_pcsbr.activites;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import com.novigosolutions.certiscisco_pcsbr.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco_pcsbr.interfaces.PrintCallBack;
import com.novigosolutions.certiscisco_pcsbr.models.Bags;
import com.novigosolutions.certiscisco_pcsbr.models.Box;
import com.novigosolutions.certiscisco_pcsbr.models.BoxBag;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Break;
import com.novigosolutions.certiscisco_pcsbr.models.EnvelopeBag;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.zebra.BulkImageGenerator;
import com.novigosolutions.certiscisco_pcsbr.zebra.BulkPrintData;
import com.novigosolutions.certiscisco_pcsbr.zebra.BulkSelectedPrintData;
import com.novigosolutions.certiscisco_pcsbr.zebra.Content;
import com.novigosolutions.certiscisco_pcsbr.zebra.GenerateImage;
import com.novigosolutions.certiscisco_pcsbr.zebra.Print;
import com.novigosolutions.certiscisco_pcsbr.zebra.Printer;
import com.novigosolutions.certiscisco_pcsbr.zebra.ZPLUtil;
import com.zebra.sdk.util.internal.StringUtilities;


import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.novigosolutions.certiscisco_pcsbr.applications.CertisCISCO.getContext;

public class PrintActivity extends BaseActivity implements View.OnClickListener, PrintJobListAdapter.CheckBoxListnerCallBack, NetworkChangekListener, PrintCallBack {
    Button btnBack,btnPrint,btnPrintAll,btnSelectedPrint,btnCancel;
    TextView txtPrintCount;
    ImageView imageView;
    int transporterMasterId;
    String status;
    ImageView imgnetwork;
    CardView progressView;
    // Toolbar toolbar;
    public static List<String> list;
    protected MenuItem refreshItem = null;
    RelativeLayout layoutPrintProcess;
    LinearLayout layoutSelectJobs,layoutToolBar;
    RecyclerView recyclerView;
    List<Branch> jobList;
    List<Print> printDataArray ;
    List<String> printTemplateList;
    List<String> bulktemplateList=new ArrayList<>();
    private PrintJobListAdapter listAdapter;
    Runnable runnable;
    Handler handler;
    Printer printer;
    GenerateImage generateImage;
    public static List<Job> finalList;
    BulkImageGenerator bulkImageGenerator;
    Thread t;
    int printCount=0;
    Boolean alertFlag=true;
    private ProgressDialog progressDialog;
    String groupKey;
    int isDelivered = 0, isCollection = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        initialiser();
        setupToolBar();
    }

    private void initialiser() {
        btnPrint=findViewById(R.id.bt_print);
        btnPrintAll=findViewById(R.id.bt_printAll);
        btnBack=findViewById(R.id.bt_back);
        btnCancel=findViewById(R.id.bt_cancel_print);
        btnSelectedPrint=findViewById(R.id.bt_print_selected);
        txtPrintCount=findViewById(R.id.lb_print_count);
        imageView=findViewById(R.id.img_print);
        progressView=findViewById(R.id.cv_progress);
        layoutSelectJobs=(LinearLayout)findViewById(R.id.lc_recycle);
        layoutPrintProcess=findViewById(R.id.lc_progress);
        btnBack.setOnClickListener(this);
        layoutToolBar=findViewById(R.id.lc_tool_bar);
        btnPrintAll.setOnClickListener(this);
        btnPrint.setOnClickListener(this);
        btnSelectedPrint.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        recyclerView=findViewById(R.id.recyclerview);
        progressView.setVisibility(View.GONE);
        printer=new Printer(PrintActivity.this,this);
        list=new ArrayList<String>();
        printDataArray=new ArrayList<>();
        printTemplateList=new ArrayList<>();
    }

    private void setupToolBar() {
        Bundle extras = getIntent().getExtras();
        status=extras.getString("status");
        groupKey = extras.getString("groupKey");
        isDelivered = extras.getInt("isDelivery");
        transporterMasterId = extras.getInt("transporterMasterId");
        isCollection = extras.getInt("isCollection");
        if(status.equals("COMPLETED")){
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
        }else{
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            singlePrintViewInit();
        }
    }

    private void singlePrintViewInit(){
        layoutToolBar.setVisibility(View.GONE);
        setPrintImage();
        list.clear();
        list.add(groupKey);
        if(status.equals("SINGLE")){
            btnBack.setText("HOME");
        } else if(status.equals("PRINT")){
            btnPrint.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            checkBluetoothConnection();
        }
    }

    private void bulkPrintViewInit(){
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
        listAdapter = new PrintJobListAdapter(jobList,status, this,this);
        recyclerView.setAdapter(listAdapter);
    }

    private List<Branch> getJobList() {
        return Branch.getCompletedBranches();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_print:
                checkBluetoothConnection();
                break;
            case R.id.bt_printAll:
                printAll();
                break;
            case R.id.bt_back:
                if (status.equals("SINGLE")) {
                    Intent intent=new Intent(PrintActivity.this,HomeActivity.class);
                    startActivity(intent);
                }else {
                    onBackPressed();
                }
                break;
            case R.id.bt_print_selected:
                checkBluetoothConnection();
                break;
            case R.id.bt_cancel_print:
                alertView();
                break;
        }
    }


    //click on printAll button
    private void printAll() {
        list.clear();
        for(int i=0;i<jobList.size();i++){
            list.add(jobList.get(i).GroupKey);
        }
        checkBluetoothConnection();
    }

    // disabling all buttons and enabling print progress ui
    private void printUIChange(){
        if (status.equals("COMPLETED")) {
            layoutPrintProcess.setVisibility(View.VISIBLE);
            btnPrintAll.setVisibility(View.GONE);
            btnSelectedPrint.setVisibility(View.GONE);
            layoutSelectJobs.setVisibility(View.GONE);
            btnCancel.setVisibility(View.VISIBLE);
        }else{
            btnPrint.setVisibility(View.GONE);
        }
        txtPrintCount.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.GONE);
        txtPrintCount.setText("Printed 0 / "+printDataArray.size());
        Glide.with(this)
                .asGif()
                .load(R.raw.printerr)
                .into(imageView);
        //calling actually print data passing method
        receiptPrint();

    }

    private void checkBluetoothConnection(){
        if (list.size()!=0) {
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
                }else {
                    Toast.makeText(PrintActivity.this, "Please select the printer", Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(PrintActivity.this, "Bluetooth not on", Toast.LENGTH_LONG).show();
            }
        }else {
            raiseSnakbar("No jobs are selected");
        }
    }

    private void sendPrintDataValidation(){
        try{
            if(printDataArray.size()==0)
                raiseSnakbar("No Data to print");
            else
                printUIChange();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<Job> getAllJobList() {
        return Job.getSpecificJobListByType(isDelivered, isCollection, groupKey);
    }

    private void processPrintData() {

        printDataArray.clear();
        BulkSelectedPrintData bulkPrintData = new BulkSelectedPrintData(PrintActivity.this, status, getAllJobList(), isDelivered);
        bulkPrintData.execute();

//        printDataArray.clear();
//        BulkPrintData bulkPrintData = new BulkPrintData(PrintActivity.this,status,list);
//        bulkPrintData.execute();
//
    }

    public void prepareSinglePrintData(){
        printDataArray.clear();
        ArrayList<String> collectionModeArray=new ArrayList<>();
        String collectionMode="";
        Print print=new Print();

        Job job=Job.getSingle(transporterMasterId);
        Branch branch=Branch.getSingle(job.GroupKey);
        list.add(job.GroupKey);

        print.setLogo( printer.getLogo());
        print.setCertisAddress(Preferences.getPrintHeader(PrintActivity.this));

        //Service details
        print.setDate(CommonMethods.getDateForPrint(branch.StartTime));

        if(TextUtils.isEmpty(job.ActualFromTime)){
            print.setServiceStartTime("");
        }else {
            print.setServiceStartTime(CommonMethods.getTimeIn12Hour(job.ActualFromTime));
        }

        if(TextUtils.isEmpty(job.ActualToTime)){
            print.setServiceEndTime("");
        }else {
            print.setServiceEndTime(CommonMethods.getTimeIn12Hour(job.ActualToTime));
        }

        //Transaction Details
        print.setTransactionId(job.OrderNo);
        print.setFunctionalLocation(job.PDFunctionalCode);
        print.setDeliveryPoint(job.BranchCode);

        if(job.IsFloatDeliveryOrder && !job.IsCollectionOrder){
            print.setCollection(false);
        }
        if(!job.IsFloatDeliveryOrder && job.IsCollectionOrder){
            print.setCollection(true);
        }
        if (job.CanCollectedBag) {
//            List<Bags> bags = Bags.getByTransportMasterId(job.TransportMasterId);
//            if(!bags.isEmpty())
                collectionModeArray.add("Sealed Duffel Bag");
        }
        if(job.CanCollectCoinBox) {
//            List<BoxBag> boxBags = BoxBag.getByTransportMasterId(job.TransportMasterId);
//            if (!boxBags.isEmpty()) {
                collectionModeArray.add("Coin Bag");
          //  }
        }
        if (job.CanCollectedBox) {
//            List<Box> boxes = Box.getBoxByTransportMasterId(job.TransportMasterId);
//            if(!boxes.isEmpty()) {
                collectionModeArray.add("Box");
          //  }
        }
        if (job.CanCollectedEnvelop) {
           // List<EnvelopeBag> envelopeBags = EnvelopeBag.getEnvelopesByTransportMasterId(job.TransportMasterId);
//            if (!envelopeBags.isEmpty()) {
                collectionModeArray.add("Envelopes");
          //  }
        }
        if (job.CanCollectedEnvelopInBag) {
            //List<EnvelopeBag> envelopeBags = EnvelopeBag.getEnvelopesInBagByTransportMasterId(job.TransportMasterId);
//            if (!envelopeBags.isEmpty()) {
                collectionModeArray.add("Envelopes In Bag");
           // }
        }
        if (job.CanCollectPallet) {
           // int palletCount = Job.getSingle(job.TransportMasterId).palletCount;
//            if (palletCount > 0) {
                collectionModeArray.add("Pallet");
           // }
        }

        if(collectionModeArray.size()==0 || collectionModeArray==null){
            if(job.IsFloatDeliveryOrder && !job.IsCollectionOrder){
                collectionMode="No Collection";
            }
            if(!job.IsFloatDeliveryOrder && job.IsCollectionOrder){
                collectionMode="No Collection";
            }
        }else {
            collectionMode = TextUtils.join(", ",collectionModeArray);
        }

        print.setCollectionMode(collectionMode);
       // print.setBank(job.CreditTo);
        if(TextUtils.isEmpty(job.CreditTo)) {
            print.setBank("");
        }else {
            print.setBank(job.CreditTo);
        }
        //Customer Details
        print.setCustomerName(job.CustomerName);
        print.setBranchName(branch.BranchName);
        print.setCustomerLocation(job.BranchStreetName+" "+job.BranchTower+" "+job.BranchTown+" "+job.BranchPinCode);
        print.setContentList(Job.getPrintContent(transporterMasterId));
//        if (status.equals("SINGLE")) {
//            print.setCustomerAcknowledgment(printer.getSign());
//        }
//        else {
            if (TextUtils.isEmpty(job.CustomerSign)) {
                print.setCustomerAcknowledgment("");
            }else {
                print.setCustomerAcknowledgment(printer.getSign(job.CustomerSign));
            }
       // }



        if(TextUtils.isEmpty(job.CustomerSignature)){
            print.setCustomerSignature("");
        }else {
           print.setCustomerSignature(printer.getSign(job.CustomerSignature));
        }

        if(TextUtils.isEmpty(job.CName)){
            print.setCName("");
        }else {
            print.setCName(job.CName);
        }

        if (TextUtils.isEmpty(job.StaffID)){
            print.setStaffId("");
        }else {
            print.setStaffId(job.StaffID);
        }

        //Handed/received
        print.setCertisTransactionOfficer(Preferences.getString("TrasactionOfficerName",PrintActivity.this));
        print.setTransactionOfficerId(Preferences.getString("UserCode",PrintActivity.this));
        print.setFooter(Preferences.getPrintFooter(PrintActivity.this));
        printDataArray.add(print);
    }

    public void receiptPrint(){
            if(printer.doConnectionTestForBulk()){
                bulkImageGenerator=new BulkImageGenerator(PrintActivity.this,printDataArray);
                bulkImageGenerator.execute();
            }else {
                Toast.makeText(PrintActivity.this,"Failed to connect the printer",Toast.LENGTH_LONG).show();
            }
    }


//    public void startPrinttemplate(){
//        printer.printBulk(Printer.PRINT,status);
//        for(int i=0;i<bulktemplateList.size();i++){
//
//        }
//    }

//   Runnable multiPrintRunnable = new Runnable() {
//        public void run() {
//
//            if(Thread.currentThread().isInterrupted()) {
//                printer.disconnect();
//                return;
//            }
//            Looper.prepare();
//            try{
//                for(int i=0;i<printDataArray.size();i++){
//                    generateImage=new GenerateImage(PrintActivity.this,"print",printDataArray.get(i));
//                    generateImage.execute();
//                }
//                printer.disconnect();
//
//            }catch (Exception e){
//                printer.disconnect();
//                e.printStackTrace();
//            }
//            Looper.loop();
//            Looper.myLooper().quit();
//        }
//    };


    //click on cancel button
    public void cancelProcess(){
        alertFlag=true;
        printCount=0;
        printDataArray.clear();
        if (status.equals("COMPLETED")) {
            list.clear();
            layoutSelectJobs.setVisibility(View.VISIBLE);
            btnPrintAll.setVisibility(View.VISIBLE);
            btnSelectedPrint.setVisibility(View.GONE);
            layoutPrintProcess.setVisibility(View.GONE);
            printRecyclerViewInit();
        }else {
            setPrintImage();
            btnPrint.setVisibility(View.VISIBLE);
        }
        btnCancel.setVisibility(View.GONE);
        btnBack.setVisibility(View.VISIBLE);

    }

    private void setPrintImage(){
        Glide.with(this)
                .load(R.drawable.printerrimg)
                .into(imageView);
    }



    @Override
    protected void onResume() {
        super.onResume();
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
//        if (status.equals("COMPLETED")) {
//            handler = new Handler();
//            runnable = new Runnable() {
//                public void run() {
//                    list.clear();
//                    printRecyclerViewInit();
//                    handler.postDelayed(this, 60000);
//                }
//            };
//            handler.postDelayed(runnable, 0);
//            if(list.size()==0 || list==null ){
//                btnSelectedPrint.setVisibility(View.GONE);
//            }
//        }
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
            Intent intent=new Intent(PrintActivity.this,HomeActivity.class);
            startActivity(intent);
        } else{
            onBackPressed();
        }
        finish();
        return true;
    }

    @Override
    public void onChange() {
        if(list.size()==0)
            btnSelectedPrint.setVisibility(View.GONE);
        else
            btnSelectedPrint.setVisibility(View.VISIBLE);
    }

    public void alertView(){
//        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PrintActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
//        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                printer.cancelBulkPrint();    // cancel the print job
//                cancelProcess();
//            }
//        });
//        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int i) {
//                if(status.equals("PRINT")){
//                    onBackPressed();
//                }
//                dialog.dismiss();
//            }
//        });
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.setCancelable(false);
//        alertDialog.setTitle("Warning");
//        alertDialog.setMessage("Are you sure you want to cancel the printing process");
//        alertDialog.show();
        printer.cancelBulkPrint();    // cancel the print job
        cancelProcess();
    }

    public void setTemplate(String template) {
        Printer p= new Printer(PrintActivity.this,this);
        //  CommonMethods.dismissProgressDialog();
        p.setSetZplTemplate(template);
        if (status.equals("COMPLETED")){
            if(alertFlag) {
                p.printBulk(Printer.PRINT, status);
                alertFlag=false;
            }
            else {
                // printer.printBulkReceipt();
            }
        }else {
            p.testprint(Printer.TEST_PRINT, status);
        }
    }

    public List<String> getbulktemplateList(){
        return bulktemplateList;
    }

    public void setprintDataArray(List<Print> printDataArray){
        this.printDataArray=printDataArray;
        sendPrintDataValidation();
    }

    public void setbulktemplateList(List<String> bulktemplateList){
        this.bulktemplateList=bulktemplateList;
//        startPrinttemplate();
        printer.printBulk(Printer.PRINT, status);
    }

    @Override
    public void onPrint(int flag, String message) {
        if (flag == 1) {
            Toast.makeText(PrintActivity.this,"Printing Completed",Toast.LENGTH_LONG).show();
            if (status.equals("SINGLE")){
                onBackPressed();
                finish();
            }else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(PrintActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);
            }
        } else if (flag == 2) {
            printCount++;
            txtPrintCount.setText("Printed "+ printCount+" / " + printDataArray.size());
        } else if (flag == 3) {

        }
    }
}