package com.novigosolutions.certiscisco_pcsbr.activites;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.adapters.CollectionSummaryAdapter;
import com.novigosolutions.certiscisco_pcsbr.adapters.StringAdapter;
import com.novigosolutions.certiscisco_pcsbr.interfaces.ApiCallback;
import com.novigosolutions.certiscisco_pcsbr.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco_pcsbr.models.Box;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Delivery;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.models.Reschedule;
import com.novigosolutions.certiscisco_pcsbr.objects.Summary;
import com.novigosolutions.certiscisco_pcsbr.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco_pcsbr.ui.SignatureView;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;
import com.novigosolutions.certiscisco_pcsbr.utils.Constants;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomerSummaryScreen extends BaseActivity implements View.OnClickListener, ApiCallback, NetworkChangekListener {

    TextView txt_customer_name, txt_del_head, txt_del_count, txt_branch_name, txt_functional_code;//, txt_town_pin ,, txt_street_tower
    //    int PointId;
    TextView txt_sealed_count, txt_unsealed_count;  //nidheesh
    String GroupKey;
    int summaryType = 0;
    ImageView img_erase;
    SignatureView signatureView;
    ImageView imgnetwork;
    Button button_submit;
    View bll;
    LinearLayout ll_lists, ll_delivery;
    Button btn_ok, btn_print;
    Button btnCancel;
    int TransportMasterId;
    static int total_item_counter;
    EditText txt_staff_name, txt_staff_id;
    LinearLayout linearLayout_staff_details;
    int isDelivery = 0, isCollection = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_summary_screen);

        setuptoolbar();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
//            PointId = extras.getInt("PointId");
            GroupKey = extras.getString("GroupKey");
            summaryType = extras.getInt("summaryType");
            TransportMasterId = extras.getInt("TransportMasterId");
        }
        ll_lists = findViewById(R.id.ll_lists);
        ll_delivery = findViewById(R.id.ll_delivery);
        txt_customer_name = (TextView) findViewById(R.id.txt_customer_name);
        txt_functional_code = (TextView) findViewById(R.id.txt_functional_code);
        //txt_street_tower = (TextView) findViewById(R.id.txt_street_tower);
        txt_del_head = (TextView) findViewById(R.id.txt_del_head);
        txt_del_count = (TextView) findViewById(R.id.txt_del_count);
        //txt_town_pin = (TextView) findViewById(R.id.txt_town_pin);
        txt_branch_name = (TextView) findViewById(R.id.txt_txt_branch_name);

        //nidheesh ** start
        txt_sealed_count = (TextView) findViewById(R.id.txt_seal_count);
        txt_unsealed_count = (TextView) findViewById(R.id.txt_unseal_count);
        txt_sealed_count.setVisibility(View.GONE);
        txt_unsealed_count.setVisibility(View.GONE);
        total_item_counter = 0;
        txt_staff_name = findViewById(R.id.txt_staff_name);
        txt_staff_id = findViewById(R.id.txt_staff_id);
        linearLayout_staff_details = findViewById(R.id.lyt_staff_details);

        //nidheesh ** end


        Branch branch = Branch.getSingle(GroupKey);
        Job job = Job.getSingle(TransportMasterId);
        if (isSummary(branch, job)) {
            bll = findViewById(R.id.bll);
            bll.setVisibility(View.GONE);
//            btn_ok = findViewById(R.id.btn_ok);
//            btn_ok.setVisibility(View.VISIBLE);
//            btn_ok.setOnClickListener(this);
//            btn_print=findViewById(R.id.btn_print);
//            btn_print.setVisibility(View.VISIBLE);
//            btn_print.setOnClickListener(this);
        } else {
            LinearLayout linearLayout = findViewById(R.id.ll);
            signatureView = new SignatureView(this);
            linearLayout.addView(signatureView);
            img_erase = findViewById(R.id.img_erase);
            img_erase.setOnClickListener(this);
            button_submit = findViewById(R.id.btn_submit);
            button_submit.setOnClickListener(this);
            btnCancel = findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(this);
        }
        txt_customer_name.setText(branch.CustomerName);
        if (job.IsFloatDeliveryOrder) {
            isDelivery = 1;
            isCollection = 0;
            txt_functional_code.setText(Job.getDeliveryOrderNos(branch.GroupKey));
        } else {
            txt_functional_code.setText(job.OrderNo);
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
        txt_branch_name.setText(branch.BranchCode);

        if (summaryType == Constants.COLLECTION || summaryType == Constants.COLLECTIONDELIVERY) {
            if (summaryType == Constants.COLLECTION) {
                ll_delivery.setVisibility(View.GONE);
            }
            ll_lists = findViewById(R.id.ll_lists);
            List<Job> jobs = new ArrayList<>();
            if (isSummary(branch, job))
//                jobs = Job.getCollectionJobsOfPoint(GroupKey);
                jobs.add(Job.getSingle(TransportMasterId));
            else
//                jobs = Job.getIncompleteCollectionJobsOfPoint(GroupKey);
                jobs.add(Job.getSingle(TransportMasterId));
            if (jobs.size() > 0) {
                for (int i = 0; i < jobs.size(); i++) {
                    List<Summary> collectionSummaries = Job.getCollectionSummary(jobs.get(i).TransportMasterId);
                    View titleList = this.getLayoutInflater().inflate(R.layout.title_list, null);
                    TextView txt_branch_code = titleList.findViewById(R.id.txt_branch_code);

                    //nidheesh ** start
                    TextView txt_col_box_count = titleList.findViewById(R.id.txt_col_bag_count);  // nidheesh
                    TextView txt_envs_count = titleList.findViewById(R.id.txt_col_env_count);
                    //nidheesh ** end

                    txt_branch_code.setText("" + jobs.get(i).BranchCode + " - " + jobs.get(i).OrderNo);
                    TextView txt_pin = titleList.findViewById(R.id.txt_pin);
                    txt_pin.setText("" + jobs.get(i).PinCode);
                    TextView txt_message = titleList.findViewById(R.id.txt_message);
                    RecyclerView recyclerView = titleList.findViewById(R.id.recyclerview);
                    if (jobs.get(i).isNoCollection) {
                        txt_message.setVisibility(View.VISIBLE);
                        txt_message.setText("No Collection");
                        recyclerView.setVisibility(View.GONE);
                    } else if (collectionSummaries.size() < 1) {
                        txt_message.setVisibility(View.VISIBLE);
//                        txt_message.setText("No Offline Data");
                        txt_message.setText("No Collection");
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
                        recyclerView.addItemDecoration(dividerItemDecoration);
                        CollectionSummaryAdapter mAdapter = new CollectionSummaryAdapter(collectionSummaries, true, this);
                        recyclerView.setAdapter(mAdapter);
                    }
                    ll_lists.addView(titleList);
                    itemCounter(collectionSummaries);
                    boxCounter(jobs.get(i).TransportMasterId);
                    //nidheesh ** start
                    txt_col_box_count.setVisibility(View.VISIBLE);
                    txt_col_box_count.setText("Items Count : " + total_item_counter);
                    int envCountValue = counter(collectionSummaries);
                    if (envCountValue == 0) {
                        txt_envs_count.setVisibility(View.GONE);
                    } else {
                        txt_envs_count.setVisibility(View.VISIBLE);
                        txt_envs_count.setText("Env-In-Bag : " + envCountValue);
                    }
                    //nidheesh ** end

                }
            } else {
                ll_lists.setVisibility(View.GONE);
            }


        }
        if (summaryType == Constants.DELIVERY || summaryType == Constants.COLLECTIONDELIVERY) {
//            txt_del_head.setText("Delivery" + " - " + Job.getDeliveryOrderNos(GroupKey));
            txt_del_head.setText("Delivery" + " - " + Job.getSingle(TransportMasterId).OrderNo);
            if (summaryType == Constants.DELIVERY) {
                ll_lists.setVisibility(View.GONE);
            }
            if (Branch.getSingle(GroupKey).isRescheduled) {
                View head = findViewById(R.id.rescheduled);
                head.setVisibility(View.VISIBLE);
                View dlist = findViewById(R.id.lldeliveries);
                dlist.setVisibility(View.GONE);
            } else {
                List<Delivery> bagList = null;
                if (isSummary(branch, job)) bagList = Delivery.getSealedByPointId(GroupKey);
                else bagList = Delivery.getPendingSealedByPointId(GroupKey);
                if (bagList.size() > 0) {
                    List<String> baglist = new ArrayList<>();
                    RecyclerView baglistView = findViewById(R.id.baglistview);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    baglistView.setLayoutManager(mLayoutManager);
                    baglistView.setItemAnimator(new DefaultItemAnimator());
                    for (int i = 0; i < bagList.size(); i++) {
                        if (bagList.get(i).ItemType.equals("Coin Box") || bagList.get(i).ItemType.equals("BOX")) {
                            if (bagList.get(i).CoinSeriesId == 0) {
                                baglist.add(bagList.get(i).ItemType + "(" + bagList.get(i).SealNo + ")");
                            } else {
                                baglist.add(bagList.get(i).ItemType + "(" + bagList.get(i).SealNo + ")(" + bagList.get(i).CoinSeries + ")");
                            }
                        } else
                            baglist.add(bagList.get(i).ItemType + "(" + bagList.get(i).SealNo + ")");
                    }
                    StringAdapter bagAdapter = new StringAdapter(baglist);
                    baglistView.setAdapter(bagAdapter);
                } else {
                    findViewById(R.id.no_bag_list).setVisibility(View.VISIBLE);
                }

                //nidheesh ** start
                txt_sealed_count.setVisibility(View.VISIBLE);
                txt_sealed_count.setText("Item count : " + bagList.size());
                //nidheesh ** end

                List<Delivery> boxList = null;
                if (isSummary(branch, job)) boxList = Delivery.getUnSealedByPointId(GroupKey);
                else boxList = Delivery.getPendingUnSealedByPointId(GroupKey);
                if (boxList.size() > 0) {
                    RecyclerView boxlistView = findViewById(R.id.boxlistview);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    boxlistView.setLayoutManager(mLayoutManager);
                    boxlistView.setItemAnimator(new DefaultItemAnimator());
                    List<String> boxlist = new ArrayList<>();
                    //nidheesh ** start
                    int unsealed = 0;
                    //nidheesh ** end

                    for (int i = 0; i < boxList.size(); i++) {
                        //nidheesh ** start
                        unsealed += boxList.get(i).Qty;
                        //nidheesh ** end
                        if (boxList.get(i).ItemType.equals("BOX") || boxList.get(i).ItemType.equals("Coin Box")) {
                            if (boxList.get(i).CoinSeriesId == 0)
                                boxlist.add(boxList.get(i).ItemType + "(" + boxList.get(i).Denomination + " * " + boxList.get(i).Qty + ")");
                            else
                                boxlist.add(boxList.get(i).ItemType + "(" + boxList.get(i).Denomination + " * " + boxList.get(i).Qty + ")(" + boxList.get(i).CoinSeries + ")");
                        } else {
                            boxlist.add(boxList.get(i).ItemType + "(" + boxList.get(i).Qty + ")");
                        }
//                        if (boxList.get(i).ItemType.equals("BOX"))
//                            boxlist.add(boxList.get(i).ItemType + "(" + boxList.get(i).Denomination + " * " + boxList.get(i).Qty + ")");
//                        else
//                            boxlist.add(boxList.get(i).ItemType + "(" + boxList.get(i).Qty + ")");
                    }
                    StringAdapter boxAdapter = new StringAdapter(boxlist);
                    boxlistView.setAdapter(boxAdapter);
                    //nidheesh ** start
                    txt_unsealed_count.setVisibility(View.VISIBLE);
                    txt_unsealed_count.setText("Item count : " + unsealed);
                    //nidheesh ** end
                } else {
                    findViewById(R.id.no_box_list).setVisibility(View.VISIBLE);
                }
                //    txt_del_count.setText((bagList.size()+boxList.size())+" items");
            }
        }


    }

    private boolean isSummary(Branch branch, Job job) {
        if (job.Status.equals("COMPLETED")
                || (summaryType == Constants.COLLECTION && job.isOfflineSaved)
                || (summaryType == Constants.DELIVERY &&
                (job.isOfflineSaved || Reschedule.isOfflineRescheduled(branch.PointId)))
                || (summaryType == Constants.COLLECTIONDELIVERY && job.isOfflineSaved &&
                Reschedule.isOfflineRescheduled(branch.PointId))) {
            return true;
        } else {
            return false;
        }
    }

    //nidheesh ** start
    public int counter(List<Summary> collectionSummaries) {
        int envolops_count = 0;
        List<Summary> summary = collectionSummaries;
        for (int i = 0; i < summary.size(); i++) {
            if (summary.get(i).Collection_type.equals("EnvelopeBag")) {
                String msg = summary.get(i).Message;
                String[] elements = msg.split(",");
                List<String> fixedLenghtList = Arrays.asList(elements);
                ArrayList<String> listOfString = new ArrayList<String>(fixedLenghtList);
                envolops_count += listOfString.size();
            }
        }
        return envolops_count;
    }

    public void itemCounter(List<Summary> collectionSummaries) {
        int item_count = 0;
        List<Summary> summary = collectionSummaries;
        for (int i = 0; i < summary.size(); i++) {
            if (summary.get(i).Collection_type.equals("Box")) {
//                int count=0;
//                count=boxCount(summary.get(i).Message);
//                if(count>0){
//                    item_count+=count;
//                }
            } else if (summary.get(i).Collection_type.equals("Pallet")) {
                item_count += Integer.parseInt(summary.get(i).Message);
            } else {
                item_count++;
            }

        }
        total_item_counter += item_count;
        // return item_count;
    }

    public int boxCount(String message) {
        String str = message;
        int count = Integer.parseInt(str.substring(str.indexOf('(') + 1, str.indexOf(')')));
        return count;
    }

    private void boxCounter(int transportMasterId) {
        int count = 0;
        List<Box> box = Box.getBoxByTransportMasterId(transportMasterId);
        if (box.size() > 0) {
            for (int i = 0; i < box.size(); i++) {
                count += box.get(i).count;
            }
        }
        total_item_counter += count;
    }
    //nidheesh ** end

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("CUSTOMER SUMMARY");
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
    protected void onResume() {
        super.onResume();
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_erase) {
            signatureView.clearSignature();
        } else if (id == R.id.btn_submit) {
            if (TextUtils.isEmpty(txt_staff_name.getText().toString().trim()) && TextUtils.isEmpty(txt_staff_id.getText().toString().trim())) {
                txt_staff_id.requestFocus();
                Toast.makeText(this, "Please enter Customer Name/Staff ID", Toast.LENGTH_LONG).show();
                txt_staff_id.clearFocus();
            } else {
                button_submit.setEnabled(false);
                showProgressDialog("Loading...");
                String sign = signatureView.getByteArray();
                if (sign == null) {
                    button_submit.setEnabled(true);
                    hideProgressDialog();
                    Toast.makeText(this, "Signature is empty", Toast.LENGTH_SHORT).show();
                } else {
                    String name = (TextUtils.isEmpty(txt_staff_name.toString().trim())) ? "" : txt_staff_name.getText().toString().trim();
                    String staffID = (TextUtils.isEmpty(txt_staff_id.toString().trim())) ? "" : txt_staff_id.getText().toString().trim();

                    Job job = Job.getSingle(TransportMasterId);
                    Branch.updateJobEndTime(GroupKey, CommonMethods.getCurrentDateTime(this));
                    if (job.IsFloatDeliveryOrder) {
                        Job.updateDeliveryJobsEndTime(GroupKey, CommonMethods.getCurrentDateTime(this));
                        Job.setDeliveryJobsFinished(GroupKey);
                    } else {
                        Job.updateJobEndTime(TransportMasterId, CommonMethods.getCurrentDateTime(this));
                        Job.setFinished(TransportMasterId);
                    }

                    if (summaryType == Constants.COLLECTION) {

                        Branch.updateColCustomerSignature(GroupKey, sign);
                        Job.UpdateCustomerSignature(TransportMasterId, sign);
                        Branch.UpdateNameandStaffIdD(GroupKey, name, staffID);
                        Job.UpdateNameAndStaffID(TransportMasterId, name, staffID);

                        if (NetworkUtil.getConnectivityStatusString(this)) {
//                        showProgressDialog("Loading...");
                            APICaller.instance().SubmitBulkCollection(this, this, GroupKey);
                        } else {
//                        Branch.setColOfflineStatus(GroupKey, 1);
                            Job.setOfflineSaved(TransportMasterId, 1);
                            setResult(Constants.FINISHONRESULT);
                            button_submit.setEnabled(true);
                            hideProgressDialog();
                            finish();
                            Toast.makeText(this, "Saved offline", Toast.LENGTH_SHORT).show();
                        }
                    } else if (summaryType == Constants.DELIVERY) {

                        Branch.updateDelCustomerSignature(GroupKey, sign);
                        Job.UpdateCustomerSignature(TransportMasterId, sign);
                        Branch.UpdateNameandStaffIdD(GroupKey, name, staffID);
                        Job.UpdateNameAndStaffID(TransportMasterId, name, staffID);

                        if (NetworkUtil.getConnectivityStatusString(this)) {
//                        showProgressDialog("Loading...");
                            APICaller.instance().SubmitDeliveryList(this, this, GroupKey);
                        } else {
//                        Branch.setDelOfflineStatus(GroupKey, 1);
                            Job.setOfflineSaved(TransportMasterId, 1);
                            setResult(Constants.FINISHONRESULT);
                            button_submit.setEnabled(true);
                            hideProgressDialog();
//                        if (Job.getIncompleteCollectionJobsOfPoint(GroupKey).size() > 0) {
//                            Intent intent = new Intent(this, CollectionDetailActivity.class);
////                            intent.putExtra("PointId", PointId);
//                            intent.putExtra("GroupKey", GroupKey);
//                            startActivity(intent);
//                        }
                            finish();
                            Toast.makeText(this, "Saved offline", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        } else if (id == R.id.btn_cancel) {
            alert();
        }
//        else if (id == R.id.btn_ok) {
//            onBackPressed();
//        }
    }

    @Override
    public void onNetworkChanged() {
        if (NetworkUtil.getConnectivityStatusString(this))
            imgnetwork.setImageResource(R.drawable.network);
        else
            imgnetwork.setImageResource(R.drawable.no_network);
    }

    @Override
    public void onResult(int api_code, int result_code, String result_data) {
        try {
            button_submit.setEnabled(true);
            if (result_code == 200) {
                JSONObject obj = new JSONObject(result_data);
                if (obj.getString("Result").equals("Success")) {
                    if (summaryType == Constants.COLLECTION) {
                        Job.setCollected(TransportMasterId);
                    } else if (summaryType == Constants.DELIVERY) {
                        Job.setDelivered(GroupKey);
//                        if (Job.getIncompleteCollectionJobsOfPoint(GroupKey).size() > 0) {
//                            Intent intent = new Intent(this, CollectionDetailActivity.class);
////                            intent.putExtra("PointId", PointId);
//                            intent.putExtra("GroupKey", GroupKey);
//                            startActivity(intent);
//                        }
                    }
                    APICaller.instance().sync(null, getApplicationContext());
                    setResult(Constants.FINISHONRESULT);
                    //   finish();
                }
                raiseSnakbar("Success");

                int retries = 0;
                boolean hasReceipt = false;
                while (retries <= 40 && hasReceipt == false) {
                    try {
                        hasReceipt = hasReceiptNo(TransportMasterId);
                        retries++;
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                hideProgressDialog();
                Intent intent = new Intent(CustomerSummaryScreen.this, PrintSelectedJobActivity.class);
                intent.putExtra("isCollection", isCollection);
                intent.putExtra("isDelivered", isDelivery);
                intent.putExtra("status", "SINGLE");
                intent.putExtra("transporterMasterId", TransportMasterId);
                startActivity(intent);

            } else {
                hideProgressDialog();
                raiseSnakbar(":" + result_code);
                Log.e("ERROR ON SYNCING", result_data);
                Toast.makeText(getApplicationContext(), result_data, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            hideProgressDialog();
            raiseSnakbar("Error");
            e.printStackTrace();
        }
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
                finish();
                onBackPressed();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }


    private boolean hasReceiptNo(int transportMasterId) {
      boolean result = false;
      Job j = Job.getSingle(transportMasterId);
      Log.e("JOB" , j.toString());
      if (!j.ReceiptNo.equals("null") && j.ReceiptNo != null)
      {
          result = true;
          Log.e("RETRIES" , result + " " + transportMasterId + " " + j.ReceiptNo);
      }
      Log.e("RETRIES" , result + " " + transportMasterId);
      return  result;
    }
}