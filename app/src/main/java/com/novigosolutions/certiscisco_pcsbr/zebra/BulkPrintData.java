package com.novigosolutions.certiscisco_pcsbr.zebra;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.activites.PrintActivity;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

public class BulkPrintData extends AsyncTask<Void,Void, List<Print>> {
    private Context context;
    private List<String> list;
    private ProgressDialog dialog;
    private String status;
    Printer printer;

    public BulkPrintData(Context context, String status, List<String> list){
        this.context=context;
        this.list=list;
        this.status=status;
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
        printer=new Printer(context);
        List<Print> printList=new ArrayList<>();
        this.list.add("\n\n");
        try {
            for (int i = 0; i < list.size(); i++) {
                Branch branch = Branch.getSingle(String.valueOf(list.get(i)));
                List<Job> jobs = Job.hasCompletedJob(String.valueOf(branch.GroupKey));
                for (int j = 0; j < jobs.size(); j++) {
                    ArrayList<String> collectionModeArray = new ArrayList<>();
                    String collectionMode = "";
                    Print print = new Print();
                    print.setLogo(printer.getLogo());
                    print.setCertisAddress(Preferences.getPrintHeader(context));
                    //Service details
                    print.setDate(CommonMethods.getDateForPrint(branch.StartTime));

                    if (TextUtils.isEmpty(jobs.get(j).ActualFromTime)) {
                        print.setServiceStartTime("");
                    } else {
                        print.setServiceStartTime(CommonMethods.getTimeIn12Hour(jobs.get(j).ActualFromTime));
                    }
                    if (TextUtils.isEmpty(jobs.get(j).ActualToTime)) {
                        print.setServiceEndTime("");
                    } else {
                        print.setServiceEndTime(CommonMethods.getTimeIn12Hour(jobs.get(j).ActualToTime));
                    }
                    //Transaction Details
                    print.setTransactionId(jobs.get(j).ReceiptNo);
                    print.setFunctionalLocation(jobs.get(j).PDFunctionalCode);
                    print.setDeliveryPoint(jobs.get(j).BranchCode);

                    if (jobs.get(j).IsFloatDeliveryOrder && !jobs.get(j).IsCollectionOrder) {
                        print.setCollection(false);
                    }
                    if (!jobs.get(j).IsFloatDeliveryOrder && jobs.get(j).IsCollectionOrder) {
                        print.setCollection(true);
                    }
                    if (jobs.get(j).CanCollectedBag) {
//                    List<Bags> bags = Bags.getByTransportMasterId(jobs.get(j).TransportMasterId);
//                    if(!bags.isEmpty())
                        collectionModeArray.add("Sealed Duffel Bag");
                    }
                    if (jobs.get(j).CanCollectCoinBox) {
//                    List<BoxBag> boxBags = BoxBag.getByTransportMasterId(jobs.get(j).TransportMasterId);
//                    if (!boxBags.isEmpty()) {
                        collectionModeArray.add("Coin Bag");
                        //  }
                    }
                    if (jobs.get(j).CanCollectedBox) {
//                    List<Box> boxes = Box.getBoxByTransportMasterId(jobs.get(j).TransportMasterId);
//                    if(!boxes.isEmpty()) {
                        collectionModeArray.add("Box");
                        // }
                    }
                    if (jobs.get(j).CanCollectedEnvelop) {
//                    List<EnvelopeBag> envelopeBags = EnvelopeBag.getEnvelopesByTransportMasterId(jobs.get(j).TransportMasterId);
//                    if (!envelopeBags.isEmpty()) {
                        collectionModeArray.add("Envelopes");
                        //    }
                    }
                    if (jobs.get(j).CanCollectedEnvelopInBag) {
//                    List<EnvelopeBag> envelopeBags = EnvelopeBag.getEnvelopesInBagByTransportMasterId(jobs.get(j).TransportMasterId);
//                    if (!envelopeBags.isEmpty()) {
                        collectionModeArray.add("Envelopes In Bag");
                        // }
                    }
                    if (jobs.get(j).CanCollectPallet) {
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
                    if (TextUtils.isEmpty(jobs.get(j).CreditTo)) {
                        print.setBank("");
                    } else {
                        print.setBank(jobs.get(j).CreditTo);
                    }

                    //Customer Details
                    print.setCustomerName(jobs.get(j).CustomerName);
                    print.setBranchName(branch.BranchName);
                    print.setCustomerLocation(jobs.get(j).BranchStreetName + " " + jobs.get(j).BranchTower + " " + jobs.get(j).BranchTown + " " + jobs.get(j).BranchPinCode);

                    print.setContentList(Job.getPrintContent(jobs.get(j).TransportMasterId));
//                    if (status.equals("SINGLE")) {
//                        print.setCustomerAcknowledgment(printer.getSign());
//                    } else {
                        if (TextUtils.isEmpty(jobs.get(j).CustomerSign)) {
                            print.setCustomerAcknowledgment("");
                        } else {
                            print.setCustomerAcknowledgment(printer.getSign(jobs.get(j).CustomerSign));
                        }
                  //  }

                    if(TextUtils.isEmpty(jobs.get(j).CustomerSignature)){
                        print.setCustomerSignature("");
                    }else {
                        print.setCustomerSignature(printer.getSign(jobs.get(j).CustomerSignature));
                    }

                    if(TextUtils.isEmpty(jobs.get(j).CName)){
                        print.setCName("");
                    }else {
                        print.setCName(jobs.get(j).CName);
                    }

                    if (TextUtils.isEmpty(jobs.get(j).StaffID)){
                        print.setStaffId("");
                    }else {
                        print.setStaffId(jobs.get(j).StaffID);
                    }

                    //Handed/received
                    print.setCertisTransactionOfficer(Preferences.getString("TrasactionOfficerName", context));
                    print.setTransactionOfficerId(Preferences.getString("UserCode", context));
                    print.setFooter(Preferences.getPrintFooter(context));
                    printList.add(print);
                }
            }
        }catch (Exception e){
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
        if (dialog != null) {
            dialog.dismiss();
        }
       ((PrintActivity)context).setprintDataArray(bulkPrintData);
    }
}
