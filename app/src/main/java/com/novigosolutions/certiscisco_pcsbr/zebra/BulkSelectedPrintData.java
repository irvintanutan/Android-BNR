package com.novigosolutions.certiscisco_pcsbr.zebra;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.novigosolutions.certiscisco_pcsbr.activites.PrintActivity;
import com.novigosolutions.certiscisco_pcsbr.activites.PrintSelectedJobActivity;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class BulkSelectedPrintData extends AsyncTask<Void, Void, List<Print>> {
    private Context context;
    private List<Job> jobs;
    private ProgressDialog dialog;
    private String status;
    private int isDelivery;
    PrinterSelected printer;

    public BulkSelectedPrintData(Context context, String status, List<Job> list, int isDelivery) {
        this.context = context;
        this.jobs = list;
        this.status = status;
        this.isDelivery = isDelivery;
        dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("The data is being prepared. Kindly wait...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected List<Print> doInBackground(Void... voids) {
        printer = new PrinterSelected(context);
        List<Print> printList = new ArrayList<>();
        try {
            for (int j = 0; j < jobs.size(); j++) {
                Job job = jobs.get(j);
                Branch branch = Branch.getSingle(String.valueOf(job.GroupKey));
                ArrayList<String> collectionModeArray = new ArrayList<>();
                String collectionMode = "";
                Print print = new Print();
                print.setLogo(printer.getLogo());
                print.setCertisAddress(Preferences.getPrintHeader(context));
                //Service details
                print.setDate(CommonMethods.getDateForPrint(branch.StartTime));

                if (TextUtils.isEmpty(job.ActualFromTime)) {
                    print.setServiceStartTime("");
                } else {
                    print.setServiceStartTime(CommonMethods.getTimeIn12Hour(job.ActualFromTime));
                }
                if (TextUtils.isEmpty(job.ActualToTime)) {
                    print.setServiceEndTime("");
                } else {
                    print.setServiceEndTime(CommonMethods.getTimeIn12Hour(job.ActualToTime));
                }
                //Transaction Details
                String receiptNo = Job.getSingle(job.TransportMasterId).ReceiptNo;
                print.setTransactionId(receiptNo);
                print.setDeliveryPoint(job.BranchCode);

                if (job.IsFloatDeliveryOrder && !job.IsCollectionOrder) {
                    print.setCollection(false);
                }
                if (!job.IsFloatDeliveryOrder && job.IsCollectionOrder) {
                    print.setCollection(true);
                }
                if (job.CanCollectedBag) {
//                    List<Bags> bags = Bags.getByTransportMasterId(jobs.get(j).TransportMasterId);
//                    if(!bags.isEmpty())
                    collectionModeArray.add("Sealed Duffel Bag");
                }
                if (job.CanCollectCoinBox) {
//                    List<BoxBag> boxBags = BoxBag.getByTransportMasterId(jobs.get(j).TransportMasterId);
//                    if (!boxBags.isEmpty()) {
                    collectionModeArray.add("Coin Bag");
                    //  }
                }
                if (job.CanCollectedBox) {
//                    List<Box> boxes = Box.getBoxByTransportMasterId(jobs.get(j).TransportMasterId);
//                    if(!boxes.isEmpty()) {
                    collectionModeArray.add("Box");
                    // }
                }
                if (job.CanCollectedEnvelop) {
//                    List<EnvelopeBag> envelopeBags = EnvelopeBag.getEnvelopesByTransportMasterId(jobs.get(j).TransportMasterId);
//                    if (!envelopeBags.isEmpty()) {
                    collectionModeArray.add("Envelopes");
                    //    }
                }
                if (job.CanCollectedEnvelopInBag) {
//                    List<EnvelopeBag> envelopeBags = EnvelopeBag.getEnvelopesInBagByTransportMasterId(jobs.get(j).TransportMasterId);
//                    if (!envelopeBags.isEmpty()) {
                    collectionModeArray.add("Envelopes In Bag");
                    // }
                }
                if (job.CanCollectPallet) {
//                    int palletCount = Job.getSingle(jobs.get(j).TransportMasterId).palletCount;
//                    if (palletCount > 0) {
                    collectionModeArray.add("Pallet");
                    //  }
                }

                if (collectionModeArray.size() == 0 || collectionModeArray == null) {
                    collectionMode = "No Collection";
                } else {
                    collectionMode = TextUtils.join(", ", collectionModeArray);
                }

                print.setCollectionMode(collectionMode);
                // print.setBank(jobs.get(j).CreditTo);
                if (TextUtils.isEmpty(job.CreditTo)) {
                    print.setBank("");
                } else {
                    print.setBank(job.CreditTo);
                }

                //Customer Details
                print.setCustomerName(job.CustomerName);

                print.setBranchName(branch.BranchName);

                if (job.IsCollectionOrder) {
                   // print.setBranchName(job.PFunctionalCode);
                    print.setFunctionalLocation(job.PDFunctionalCode);
                    print.setCustomerLocation(job.PStreetName + " " + job.PTower + " " + job.PTown + " " + job.PPinCode);
                } else {
                    print.setFunctionalLocation(job.PDFunctionalCode);
                    print.setCustomerLocation(job.StreetName + " " + job.Tower + " " + job.Town + " " + job.PinCode);
                    //print.setBranchName(branch.BranchName);
                }
                Job.getSelectedPrintContentCounter(job.GroupKey, isDelivery, job.BranchCode, job.ActualFromTime, job.ActualToTime);
                print.setContentList(Job.getSelectedPrintContent(job.GroupKey, isDelivery, job.BranchCode, job.ActualFromTime, job.ActualToTime));
                print.setCageContentList(Job.getCageSelectedPrintContent(job.GroupKey, isDelivery, job.BranchCode, job.ActualFromTime, job.ActualToTime));

                String ackSign = job.CustomerSign;
                String customerSign = job.CustomerSignature;

                if (TextUtils.isEmpty(ackSign)) {
                    print.setCustomerAcknowledgment("");
                } else {
                    print.setCustomerAcknowledgment(printer.getSign(ackSign));
                }
                //  }

                if (TextUtils.isEmpty(customerSign)) {
                    print.setCustomerSignature("");
                } else {
                    print.setCustomerSignature(printer.getSign(customerSign));
                }

                if (TextUtils.isEmpty(job.CName)) {
                    print.setCName("");
                } else {
                    print.setCName(job.CName);
                }

                if (TextUtils.isEmpty(job.StaffID)) {
                    print.setStaffId("");
                } else {
                    print.setStaffId(job.StaffID);
                }

                //Handed/received
                print.setCertisTransactionOfficer(Preferences.getString("TrasactionOfficerName", context));
                print.setTransactionOfficerId(Preferences.getString("UserCode", context));
                print.setFooter(Preferences.getPrintFooter(context));
                printList.add(print);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (dialog != null) {
                dialog.dismiss();
            }
            return printList;
        }
        return printList;
    }


    @Override
    protected void onPostExecute(List<Print> bulkPrintData) {
        super.onPostExecute(bulkPrintData);
        ((PrintSelectedJobActivity) context).setprintDataArray(bulkPrintData);
    }
}
