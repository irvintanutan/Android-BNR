package com.novigosolutions.certiscisco_pcsbr.webservices;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novigosolutions.certiscisco_pcsbr.activites.HomeActivity;
import com.novigosolutions.certiscisco_pcsbr.applications.CertisCISCO;
import com.novigosolutions.certiscisco_pcsbr.models.Bags;
import com.novigosolutions.certiscisco_pcsbr.models.Box;
import com.novigosolutions.certiscisco_pcsbr.models.BoxBag;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.CoinSeries;
import com.novigosolutions.certiscisco_pcsbr.models.Currency;
import com.novigosolutions.certiscisco_pcsbr.models.Delivery;
import com.novigosolutions.certiscisco_pcsbr.models.Envelope;
import com.novigosolutions.certiscisco_pcsbr.models.EnvelopeBag;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.models.Wagon;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dhanrajk on 21-07-17.
 */

public class SyncDatabase {
    Gson gson;

    public SyncDatabase() {
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        gson = builder.create();
    }

    public void addnew(JSONArray data, Context context) {
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject jobsJSONObject = data.getJSONObject(i);
                Branch b = saveBranch(jobsJSONObject.toString());
                JSONArray jobs = jobsJSONObject.getJSONArray("Jobs");
                for (int j = 0; j < jobs.length(); j++) {
                    JSONObject jobObject = jobs.getJSONObject(j);
                    jobObject.put("GroupKey", b.GroupKey);
                    saveJob(jobObject, jobsJSONObject.getInt("PointId"));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sync(JSONArray data, Context context) {
        try {
            Boolean isjobonprogress = false;
            StringBuilder fullMessage = new StringBuilder();
            List<String> newPoints = new ArrayList<>();
            List<String> updatedPoints = new ArrayList<>();
            List<String> deletedPoints = new ArrayList<>();
            for (int i = 0; i < data.length(); i++) {
                JSONObject orderJSONObject = data.getJSONObject(i);
                if (orderJSONObject.getString("Status").equalsIgnoreCase("Updated")) {
                    JSONObject jobObject = orderJSONObject.getJSONObject("OrderInfo");
                    int branchpointid = Job.getSingle(jobObject.getInt("TransportMasterId")).BranchPointId;
                    String groupKey = Job.getSingle(jobObject.getInt("TransportMasterId")).GroupKey;
                    if (jobObject.getString("Status").equals("COMPLETED")) {
                        Job.UpdateStatus(jobObject.getInt("TransportMasterId"));
                        Job.UpdateVersion(jobObject.getInt("TransportMasterId"), jobObject.getString("VersionNo"));
                        //Job.UpdateTime(jobObject.getInt("TransportMasterId"),jobObject.getString("ActualFromTime"),jobObject.getString("ActualToTime"));
                    } else {
                        Job.removeSingleJob(jobObject.getInt("TransportMasterId"));
                        jobObject.put("GroupKey", groupKey);
                        saveJob(jobObject, branchpointid);
                        if (!updatedPoints.contains(Branch.getSingle(groupKey).FunctionalCode))
                            updatedPoints.add(Branch.getSingle(groupKey).FunctionalCode);
                    }
//                    Branch.resetStatus(branchpointid);
//                    Branch.resetBranchValues(branchpointid);
//                    Branch.resetSequence(branchpointid, jobObject.getString("SequenceNo"));
//                    if (branchpointid == Preferences.getInt("PROGRESSPOINTID", context)) {
//                        isjobonprogress = true;
//                    }
//                    Branch.resetStatus(groupKey);
                    Branch.resetBranchValues(groupKey);
//                    Branch.resetSequence(groupKey, jobObject.getString("SequenceNo"));
                    if (groupKey.equals(Preferences.getString("PROGRESSGROUPKEY", context))) {
                        isjobonprogress = true;
                    }
                } else if (orderJSONObject.getString("Status").equalsIgnoreCase("New")) {
                    JSONObject jobsJSONObject = orderJSONObject.getJSONObject("OrderInfo");
                    int PointId = jobsJSONObject.getInt("PointId");
                    String GroupKey = jobsJSONObject.getString("GroupKey");
                    //Branch.removeSingle(jobsJSONObject.getInt("PointId"));
                    if (Branch.getSingle(GroupKey) == null) {
                        saveBranch(jobsJSONObject.toString());
                    }
                    JSONArray jobs = jobsJSONObject.getJSONArray("Jobs");
                    for (int j = 0; j < jobs.length(); j++) {
                        JSONObject jobObject = jobs.getJSONObject(j);
                        jobObject.put("GroupKey", GroupKey);
                        Job.removeSingleJob(jobObject.getInt("TransportMasterId"));
                        saveJob(jobObject, PointId);
                    }
//                    Branch.resetStatus(PointId);
//                    Branch.resetBranchValues(PointId);
//                    if (PointId == Preferences.getInt("PROGRESSPOINTID", context)) {
//                        isjobonprogress = true;
//                    }
//                    Branch.resetStatus(GroupKey);
                    Branch.resetBranchValues(GroupKey);
                    if (GroupKey.equals(Preferences.getString("PROGRESSGROUPKEY", context))) {
                        isjobonprogress = true;
                    }

                    if (!newPoints.contains(jobsJSONObject.getString("FunctionalCode")))
                        newPoints.add(jobsJSONObject.getString("FunctionalCode"));
                } else if (orderJSONObject.getString("Status").equalsIgnoreCase("Deleted")) {
                    Job removedJob = Job.removeSingleJob(orderJSONObject.getInt("TransportId"));
                    if (removedJob != null) {
//                        Branch.resetStatus(removedJob.BranchPointId);
//                        if (removedJob.BranchPointId == Preferences.getInt("PROGRESSPOINTID", context)) {
//                            isjobonprogress = true;
//                        }
//                        if (!deletedPoints.contains(Branch.getSingle(removedJob.BranchPointId).FunctionalCode))
//                            deletedPoints.add(Branch.getSingle(removedJob.BranchPointId).FunctionalCode);
//                        if (!Job.hasCollectionJob(removedJob.BranchPointId) && !Job.hasDeliveryJob(removedJob.BranchPointId)) {
//                            Branch.removeSingle(removedJob.BranchPointId);
//                        }
//                        Branch.resetStatus(removedJob.GroupKey);
                        if (removedJob.GroupKey.equals(Preferences.getString("PROGRESSGROUPKEY", context))) {
                            isjobonprogress = true;
                        }
                        if (!deletedPoints.contains(Branch.getSingle(removedJob.GroupKey).FunctionalCode))
                            deletedPoints.add(Branch.getSingle(removedJob.GroupKey).FunctionalCode);
                        if (!Job.hasCollectionJob(removedJob.GroupKey) && !Job.hasDeliveryJob(removedJob.GroupKey)) {
                            Branch.removeSingle(removedJob.GroupKey);
                        }
                    }
                }
            }

            if (!isAppIsInBackground(context)) {
                if (isjobonprogress) {
                    alert(context);
                } else {
                    Toast.makeText(context, "Job Synced", Toast.LENGTH_SHORT).show();
                }
            }
            if (newPoints.size() > 0) {
                fullMessage.append("New job added for ");
                fullMessage.append(newPoints.get(0));
                for (int i = 1; i < newPoints.size(); i++) {
                    fullMessage.append(", ");
                    fullMessage.append(newPoints.get(i));
                }
            }
            if (updatedPoints.size() > 0) {
                if (fullMessage.length() > 0)
                    fullMessage.append("\n");
                fullMessage.append("Job for ");
                fullMessage.append(updatedPoints.get(0));
                for (int i = 1; i < updatedPoints.size(); i++) {
                    fullMessage.append(", ");
                    fullMessage.append(updatedPoints.get(i));
                }
                fullMessage.append(" is updated");
            }
            if (deletedPoints.size() > 0) {
                if (fullMessage.length() > 0)
                    fullMessage.append("\n");
                fullMessage.append("Job for ");
                fullMessage.append(deletedPoints.get(0));
                for (int i = 1; i < deletedPoints.size(); i++) {
                    fullMessage.append(", ");
                    fullMessage.append(deletedPoints.get(i));
                }
                fullMessage.append(" is canceled");
            }
            if (fullMessage.length() > 0) summary(context, fullMessage.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alert(final Context context) {
        if (context != null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(context, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(i);
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.setTitle("Job Updated");
            alertDialog.setMessage("This job is synced from the server");
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alertDialog.show();
        }
    }

    private void summary(final Context context, String syncMessage) {
        if (context != null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.setTitle("Synced");
            alertDialog.setMessage(syncMessage);
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alertDialog.show();
        }
    }

    private Branch saveBranch(String data) {
        Branch branch = gson.fromJson(data, Branch.class);
//        if (branch.Status == null || branch.Status.equals("")) branch.Status = "PENDING";
        branch.save();
        return branch;
    }

    private void saveJob(JSONObject jobObject, int PointId) {
        try {
            Job job = gson.fromJson(jobObject.toString(), Job.class);
            job.BranchPointId = PointId;
            if (job.Status == null || job.Status.isEmpty()) job.Status = "PENDING";
            job.save();
            String groupKey = job.GroupKey;
            int customerid = Branch.getSingle(groupKey).CustomerId;
            if (jobObject.optJSONArray("ProductCurrency") != null) {
                JSONArray jsonArray = jobObject.getJSONArray("ProductCurrency");
                if (jsonArray.length() > 0) {
                    Currency.removeByCustomerId(customerid);
                }
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject product = jsonArray.getJSONObject(k);
                    Currency currency = new Currency();
                    currency.TransportMasterId = customerid;
                    currency.ProductId = product.getInt("Id");
                    currency.ProductName = product.getString("ProductName");
                    currency.ProductCode = product.getString("ProductCode");
                    currency.IsCoinValue = product.getString("IsCoinValue");
                    currency.save();
                }
            }
            JSONArray jsonArray = jobObject.getJSONArray("DeliveryList");
            for (int k = 0; k < jsonArray.length(); k++) {

                JSONObject deliveryobject = jsonArray.getJSONObject(k);
                Delivery delivery = gson.fromJson(deliveryobject.toString(), Delivery.class);
                delivery.save();

                String ItemType = deliveryobject.getString("ItemType");
                switch (ItemType) {
                    case "BAG":
                        Bags bag = new Bags();
                        bag.TransportMasterId = deliveryobject.getInt("TransportMasterId");
                        bag.firstbarcode = deliveryobject.getString("SealNo");
                        bag.secondbarcode = "";
                        bag.save();
                        break;

                    case "Wagon":
                        Wagon wagon = new Wagon();
                        wagon.TransportMasterId = deliveryobject.getInt("TransportMasterId");
                        wagon.firstbarcode = deliveryobject.getString("SealNo");
                        wagon.secondbarcode = "";
                        wagon.save();
                        break;

                    case "Envelopes":
                        List<EnvelopeBag> envelopeBags = EnvelopeBag.getEnvelopesByTransportMasterId(deliveryobject.getInt("TransportMasterId"));
                        if (envelopeBags.size() > 0) {
                            Envelope envelope = new Envelope();
                            envelope.bagid = envelopeBags.get(0).getId();
                            envelope.barcode = deliveryobject.getString("SealNo");
                            envelope.save();
                        } else {
                            EnvelopeBag envelopeBag = new EnvelopeBag();
                            envelopeBag.TransportMasterId = deliveryobject.getInt("TransportMasterId");
                            envelopeBag.envolpeType = "Envelopes";
                            envelopeBag.bagcode = "none";
                            long id = envelopeBag.save();
                            //add here if envelope array comes
                            Envelope envelope = new Envelope();
                            envelope.bagid = id;
                            envelope.barcode = deliveryobject.getString("SealNo");
                            envelope.save();
                        }
                        break;

                    case "Envelope In Bag":
                        EnvelopeBag envelopeBag2 = new EnvelopeBag();
                        envelopeBag2.TransportMasterId = deliveryobject.getInt("TransportMasterId");
                        envelopeBag2.envolpeType = "EnvelopeBag";
                        envelopeBag2.bagcode = deliveryobject.getString("SealNo");
                        envelopeBag2.save();
                        long id = envelopeBag2.save();

                        String envInBag = deliveryobject.getString("EnvelopeInBag");
                        if (TextUtils.isEmpty(envInBag) || envInBag == null) {

                        } else {
                            List<String> envelopList = new ArrayList<String>(Arrays.asList(envInBag.split(",")));
                            if (envelopList.isEmpty() || envelopList == null) {

                            } else {
                                for (int i = 0; i < envelopList.size(); i++) {
                                    Envelope envelope = new Envelope();
                                    envelope.bagid = id;
                                    envelope.barcode = envelopList.get(i);
                                    envelope.save();
                                }
                            }
                        }
                        break;

                    case "BOX":
                        // Box.updateCount(deliveryobject.getInt("TransportMasterId"),deliveryobject.getInt("ProductID"), deliveryobject.getString("Denomination"),deliveryobject.getInt("Qty"));
                        Box.updateCountNew(deliveryobject.getInt("TransportMasterId"), deliveryobject.getInt("ProductID"), deliveryobject.getString("Denomination"), deliveryobject.getInt("Qty"), deliveryobject.getString("CoinSeries"), deliveryobject.getInt("CoinSeriesId"));
                        break;

                    case "Coin Box":
                        BoxBag boxBag = new BoxBag();
                        boxBag.TransportMasterId = deliveryobject.getInt("TransportMasterId");
                        boxBag.bagcode = deliveryobject.getString("SealNo");
                        boxBag.ProductId = deliveryobject.getInt("ProductID");
                        boxBag.ProductName = deliveryobject.getString("Denomination");
                        boxBag.CoinSeriesId = deliveryobject.getInt("CoinSeriesId");
                        boxBag.CoinSeries = deliveryobject.getString("CoinSeries");
                        boxBag.save();
                        break;

                    case "PALLET":
                        Job.updatePalletCount(deliveryobject.getInt("TransportMasterId"), deliveryobject.getInt("Qty"));
                        break;

                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveCoinSeries(JSONObject jsonObject, Context context) {
        try {
            if (jsonObject.getJSONArray("CoinSeriesListing") != null) {
                JSONArray jsonArray = jsonObject.getJSONArray("CoinSeriesListing");
                if (jsonArray.length() == 0) {
                    //TODO something
                } else {
                    CoinSeries.remove();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        CoinSeries coinSeries = new CoinSeries();
                        JSONObject series = jsonArray.getJSONObject(i);
                        //  List<CoinSeries> coinSeriescount = CoinSeries.getSingleCionSeries(series.getInt("Id"));
                        //  if (coinSeriescount.size() == 0) {
                        coinSeries.CoinSeriesId = series.getInt("Id");
                        coinSeries.DataAbbreviation = series.getString("DataAbbreviation");
                        coinSeries.DataDescription = series.getString("DataDescription");
                        coinSeries.save();
                        //  }
                    }
                    /// Toast.makeText(context, "Coin Series Synced", Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //.getJSONObject("Data");
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            if (!taskInfo.isEmpty()) {
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    isInBackground = false;
                    if (CertisCISCO.getCurrentactvity() != null && CertisCISCO.getCurrentactvity() instanceof HomeActivity) {
                        ((HomeActivity) CertisCISCO.getCurrentactvity()).refresh();
                    }
                }
            }
        }
        return isInBackground;
    }

    public static SyncDatabase instance() {
        return new SyncDatabase();
    }
}
