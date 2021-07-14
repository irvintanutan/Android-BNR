package com.novigosolutions.certiscisco_pcsbr.zebra;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.novigosolutions.certiscisco_pcsbr.activites.PrintActivity;
import com.novigosolutions.certiscisco_pcsbr.activites.PrintSelectedJobActivity;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

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
                Branch branch = Branch.getSingle(String.valueOf(jobs.get(j).GroupKey));
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

                print.setContentList(Job.getSelectedPrintContent(jobs.get(j).GroupKey, isDelivery));
//                    if (status.equals("SINGLE")) {
//                        print.setCustomerAcknowledgment(printer.getSign());
//                    } else {

                String ackSign = Job.getSingleByReceiptNo(jobs.get(j).ReceiptNo).CustomerSign;
                String customerSign = Job.getSingleByReceiptNo(jobs.get(j).ReceiptNo).CustomerSignature;


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

                if (TextUtils.isEmpty(jobs.get(j).CName)) {
                    print.setCName("");
                } else {
                    print.setCName(jobs.get(j).CName);
                }

                if (TextUtils.isEmpty(jobs.get(j).StaffID)) {
                    print.setStaffId("");
                } else {
                    print.setStaffId(jobs.get(j).StaffID);
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
