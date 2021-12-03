package com.novigosolutions.certiscisco_pcsbr.models;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.novigosolutions.certiscisco_pcsbr.utils.Constants;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;

import java.util.Collections;
import java.util.List;

@Table(name = "Branch")
public class Branch extends Model implements Comparable<Branch> {

    @Column(name = "PointId")
    public int PointId;

//    @Column(name = "SequenceNo")
//    public String SequenceNo;


    @Column(name = "BranchName")
    public String BranchName;

    @Column(name = "BranchCode")
    public String BranchCode;

    @Column(name = "FunctionalCode")
    public String FunctionalCode;
//
//    @Column(name = "StreetName")
//    public String StreetName;

//    @Column(name = "Tower")
//    public String Tower;
//
//    @Column(name = "Town")
//    public String Town;

//    @Column(name = "PinCode")
//    public String PinCode;
//
//    @Column(name = "CustomerCode")
//    public String CustomerCode;

    @Column(name = "CustomerName")
    public String CustomerName;

    @Column(name = "CustomerId")
    public int CustomerId;

    @Column(name = "EnableManualEntry") //these for delivery
    public boolean EnableManualEntry;

    @Column(name = "requestId") //these for delivery
    public String requestId;

//    @Column(name = "Status")
//    public String Status;

    @Column(name = "StartTime")
    public String StartTime;

    @Column(name = "EndTime")
    public String EndTime;

    @Column(name = "JobStartTime")
    public String JobStartTime;

    @Column(name = "JobEndTime")
    public String JobEndTime;

//    @Column(name = "ClientBreak")
//    public String ClientBreak;

//    @Column(name = "isCollected")
//    public boolean isCollected;

    @Column(name = "isColOffline")
    public boolean isColOffline;

    @Column(name = "isDelOffline")
    public boolean isDelOffline;

    @Column(name = "isRescheduled")
    public boolean isRescheduled;

    //Transaction officer Signature Collection
    @Column(name = "colSignature")
    public String colSignature;
    //Transacation Officer signature delivery
    @Column(name = "delSignature")
    public String delSignature;

    @Column(name = "GroupKey")
    public String GroupKey;

    public String tempSeq = "";
    private boolean isSelected;


    //Customer Signature
    @Column(name = "ColCustomerSignature")
    public String ColCustomerSignature;

    @Column(name = "DelCustomerSignature")
    public String DelCustomerSignature;

    @Column(name = "CName")
    public String CName;

    @Column(name = "StaffID")
    public String StaffID;


    public static Branch getSingle(String GroupKey) {
        return new Select().from(Branch.class)
                .where("GroupKey=?", GroupKey)
                .executeSingle();

    }
    public static List<Branch> getAllBranch() {
        return new Select().from(Branch.class)
               .execute();

    }
    public static int getAllCount() {
        return new Select().from(Job.class)
                .execute().size();
    }

    public static int getCountByStatus(String Status) {
        return new Select().from(Job.class)
                .where("Status=?", Status)
                .execute().size();
    }

    public static List<Branch> getAllBranches() {
        List<Branch> branches = new Select().from(Branch.class).innerJoin(Job.class)
                .on("Job.GroupKey=Branch.GroupKey")
                .execute();
        for (Branch b : branches) {
            String minseq = Job.getMinimumSequenceNo(b.GroupKey, "ALL");
            if (!TextUtils.isEmpty(minseq))
                b.tempSeq = minseq;
        }
        Collections.sort(branches);
        return branches;
    }

//    public static List<Branch> getBranchesByStatus(String Status) {
//        return new Select().from(Branch.class)
//                .where("Status=?", Status)
//                .orderBy("length(SequenceNo),SequenceNo asc")
//                .execute();
//    }

    public static List<Branch> getCompletedBranches() {
        List<Branch> branches = new Select().from(Branch.class)
                .where("GroupKey IN (select DISTINCT GroupKey from job where Status = 'COMPLETED' GROUP BY GroupKey)")
                .execute();
        for (Branch b : branches) {
            b.setSelected(false);
            String minseq = Job.getMinimumSequenceNo(b.GroupKey, "COMPLETED");
            if (!TextUtils.isEmpty(minseq))
                b.tempSeq = minseq;
        }
        Collections.sort(branches);
        return branches;
    }

    public static List<Branch> getPendingBranches() {
        List<Branch> branches = new Select().from(Branch.class)
                .where("GroupKey IN (select DISTINCT GroupKey from job where Status = 'PENDING')")
                .groupBy("BranchCode")
                .execute();

        for (Branch b : branches) {
            String minseq = Job.getMinimumSequenceNo(b.GroupKey, "PENDING");
            if (!TextUtils.isEmpty(minseq))
                b.tempSeq = minseq;
        }
        Collections.sort(branches);
        return branches;
    }

//    public static List<Branch> getBranchesByStatus(String Status) {
//        List<Branch> branches = new Select().from(Branch.class)
//                .execute();
//        List<Branch> filteredbranches = new ArrayList<>();
//        for (int i = 0; i < branches.size(); i++) {
//            if(branches.get(i).Status.equals(Status)) {
//                branches.get(i).pos = i+1;
//                filteredbranches.add(branches.get(i));
//            }
//        }
//        return filteredbranches;
//    }

//    public static Branch getOfflineSingleBranche() {
//        return new Select().from(Branch.class)
//                .where("isColOffline=? OR isDelOffline=?", 1, 1)
//                .executeSingle();
//    }

    public boolean hasMultipleJobs(String GroupKey) {
        List<Job> jobs = Job.getByGroupKey(GroupKey);
        if (jobs.size() > 1) {
            return true;
        } else {
            return false;
        }
    }

    public int getBranchJobType() {
        Boolean isCollection = false, isDelivery = false;
        if (Job.hasCollectionJob(GroupKey)) {
            isCollection = true;
        }
        if (Job.hasDeliveryJob(GroupKey)) {
            isDelivery = true;
        }
        if (isCollection && isDelivery) {
            return 3;
        } else if (isDelivery) {
            return 2;
        } else if (isCollection) return 1;
        else return 0;
    }

    public int getBranchJobTypeByStatus(String Status) {
        Boolean isCollection = false, isDelivery = false;
        if (Job.hasCollectionJobByStatus(GroupKey, Status)) {
            isCollection = true;
        }
        if (Job.hasDeliveryJobByStatus(GroupKey, Status)) {
            isDelivery = true;
        }
        if (isCollection && isDelivery) {
            return 3;
        } else if (isDelivery) {
            return 2;
        } else if (isCollection) return 1;
        else return 0;
    }

//    public static void resetStatus(int PointId) {
//        String Status = (Job.getPendingJobsOfPoint(PointId).size() > 0) ? "PENDING" : "COMPLETED";
//        new Update(Branch.class)
//                .set("Status=?", Status)
//                .where("PointId=?", PointId)
//                .execute();
//    }

//    public static void resetStatus(String GroupKey) {
//        String Status = (Job.getPendingJobsOfPoint(GroupKey).size() > 0) ? "PENDING" : "COMPLETED";
//        new Update(Branch.class)
//                .set("Status=?", Status)
//                .where("GroupKey=?", GroupKey)
//                .execute();
//    }

//    public static void resetBranchValues(int PointId) {
//        new Update(Branch.class)
//                .set("isRescheduled=?", 0)
//                .where("PointId=?", PointId)
//                .execute();
//    }

    public static void resetBranchValues(String GroupKey) {
        new Update(Branch.class)
                .set("isRescheduled=?", 0)
                .where("GroupKey=?", GroupKey)
                .execute();
    }

//    public static void resetSequence(int PointId, String SequenceNo) {
//        new Update(Branch.class)
//                .set("SequenceNo=?", SequenceNo)
//                .where("PointId=?", PointId)
//                .execute();
//    }

//    public static void resetSequence(String GroupKey, String SequenceNo) {
//        new Update(Branch.class)
//                .set("SequenceNo=?", SequenceNo)
//                .where("GroupKey=?", GroupKey)
//                .execute();
//    }

//    public static void UpdateRequestId(int PointId, String requestId) {
//        new Update(Branch.class)
//                .set("requestId=?", requestId)
//                .where("PointId=?", PointId)
//                .execute();
//    }

    public static void UpdateRequestId(String GroupKey, String requestId) {
        new Update(Branch.class)
                .set("requestId=?", requestId)
                .where("GroupKey=?", GroupKey)
                .execute();
    }

//    public static void EnableManualEntry(int PointId) {
//        new Update(Branch.class)
//                .set("EnableManualEntry=?", 1)
//                .where("PointId=?", PointId)
//                .execute();
//    }

    public static void EnableManualEntry(String GroupKey) {
        new Update(Branch.class)
                .set("EnableManualEntry=?", 1)
                .where("GroupKey=?", GroupKey)
                .execute();
    }


    public static Boolean isExist(String ScanValue) {

        Bags bags = new Select().from(Bags.class)
                .where("firstbarcode=? OR secondbarcode=?", ScanValue, ScanValue)
                .executeSingle();
        if (bags != null) return true;

        Wagon wagon = new Select().from(Wagon.class)
                .where("firstbarcode=? OR secondbarcode=?", ScanValue, ScanValue)
                .executeSingle();

        Cage cage = new Select().from(Cage.class)
                .where("CageNo=? or CageSeal=?", ScanValue , ScanValue)
                .executeSingle();
        if (cage != null) return true;

        if (wagon != null) return true;

        BoxBag boxBag = new Select().from(BoxBag.class)
                .where("bagcode=?", ScanValue)
                .executeSingle();
        if (boxBag != null) return true;
        Envelope envelope = new Select().from(Envelope.class)
                .where("barcode=?", ScanValue)
                .executeSingle();
        if (envelope != null) return true;
        EnvelopeBag envelopeBag = new Select().from(EnvelopeBag.class)
                .where("bagcode=?", ScanValue)
                .executeSingle();
        if (envelopeBag != null) return true;
        return false;
    }

//    public static void updateStatus(int PointId, String Status) {
//        new Update(Branch.class)
//                .set("Status=?", Status)
//                .where("PointId=?", PointId)
//                .execute();
//    }

//    public static void setColOfflineStatus(int PointId, int status) {
//        new Update(Branch.class)
//                .set("isColOffline=?", status)
//                .where("PointId=?", PointId)
//                .execute();
//    }

//    public static void setColOfflineStatus(String GroupKey, int status) {
//        new Update(Branch.class)
//                .set("isColOffline=?", status)
//                .where("GroupKey=?", GroupKey)
//                .execute();
//    }

//    public static void setAsReschedule(int PointId) {
//        new Update(Branch.class)
//                .set("isRescheduled=?", 1)
//                .where("PointId=?", PointId)
//                .execute();
//    }

    public static void setAsReschedule(String GroupKey) {
        new Update(Branch.class)
                .set("isRescheduled=?", 1)
                .where("GroupKey=?", GroupKey)
                .execute();
    }

//    public static void setDelOfflineStatus(int PointId, int status) {
//        new Update(Branch.class)
//                .set("isDelOffline=?", status)
//                .where("PointId=?", PointId)
//                .execute();
//    }

    public static void setDelOfflineStatus(String GroupKey, int status) {
        new Update(Branch.class)
                .set("isDelOffline=?", status)
                .where("GroupKey=?", GroupKey)
                .execute();
    }

//    public static void setCollected(int PointId) {
//        new Update(Branch.class)
//                .set("isCollected=?", 1)
//                .where("PointId=?", PointId)
//                .execute();
//
////        if (Job.hasDeliveryJob(PointId)) {
////            if (Job.getPendingDeliveryJobsOfPoint(PointId).size() == 0)
////                updateStatus(PointId, "COMPLETED");
////        } else {
////            updateStatus(PointId, "COMPLETED");
////        }
//        Job.UpdateCollectionCompleted(PointId);
//        resetStatus(PointId);
//
//    }

//    public static void setCollected(String GroupKey) {
//        new Update(Branch.class)
//                .set("isCollected=?", 1)
//                .where("GroupKey=?", GroupKey)
//                .execute();
//        Job.UpdateCollectionCompleted(GroupKey);
////        resetStatus(GroupKey);
//
//    }

//    public static void setDelivered(int PointId) {
////        if (Job.hasCollectionJob(PointId)) {
////            if (Branch.getSingle(PointId).isCollected) updateStatus(PointId, "COMPLETED");
////        } else {
////            updateStatus(PointId, "COMPLETED");
////        }
//        Job.UpdatDeliveryCompleted(PointId);
//        resetStatus(PointId);
//
//    }

//    public static void setDelivered(String GroupKey) {
//        Job.UpdatDeliveryCompleted(GroupKey);
////        resetStatus(GroupKey);
//
//    }

//    public static void updatecolSignature(int PointId, String signature) {
//        new Update(Branch.class)
//                .set("colSignature=?", signature)
//                .where("PointId=?", PointId)
//                .execute();
//    }

    //Transaction officer signature update Coollection
    public static void updatecolSignature(String GroupKey, String signature) {
        new Update(Branch.class)
                .set("colSignature=?", signature)
                .where("GroupKey=?", GroupKey)
                .execute();
    }

//    public static void updatedelSignature(int PointId, String signature) {
//        new Update(Branch.class)
//                .set("delSignature=?", signature)
//                .where("PointId=?", PointId)
//                .execute();
//    }

    //Transaction officer signature update delivery
    public static void updatedelSignature(String GroupKey, String signature) {
        new Update(Branch.class)
                .set("delSignature=?", signature)
                .where("GroupKey=?", GroupKey)
                .execute();
    }

    //Customer Signature update
    public static void updateColCustomerSignature(String GroupKey, String signature) {
        new Update(Branch.class)
                .set("ColCustomerSignature=?", signature)
                .where("GroupKey=?", GroupKey)
                .execute();
    }

    public static void updateDelCustomerSignature(String GroupKey, String signature) {
        new Update(Branch.class)
                .set("DelCustomerSignature=?", signature)
                .where("GroupKey=?", GroupKey)
                .execute();
    }

    public static void UpdateNameandStaffIdD(String GroupKey, String name, String id) {
        new Update(Branch.class)
                .set("CName=? , StaffID=? ", name, id)
                .where("GroupKey=?", GroupKey)
                .execute();
    }


//    public void updateJobStartTime(String JobStartTime) {
//        new Update(Branch.class)
//                .set("JobStartTime=?", JobStartTime)
//                .where("PointId=?", PointId)
//                .execute();
//    }

    public void updateJobStartTime(String JobStartTime) {
        new Update(Branch.class)
                .set("JobStartTime=?", JobStartTime)
                .where("GroupKey=?", GroupKey)
                .execute();
    }

    //    public static void updateJobEndTime(int PointId,String JobEndTime) {
//        new Update(Branch.class)
//                .set("JobEndTime=?", JobEndTime)
//                .where("PointId=?", PointId)
//                .execute();
//    }
    public static void updateJobEndTime(String GroupKey, String JobEndTime) {
        new Update(Branch.class)
                .set("JobEndTime=?", JobEndTime)
                .where("GroupKey=?", GroupKey)
                .execute();
    }

//    public static Boolean isOfflineExist() {
//        if (getOfflineSingleBranche() != null) return true;
//        return false;
//    }

    public static JsonObject getCollection(String GroupKey, Context context, String BranchCode, String PFunctionalCode, String actualFromTime, String actualToTime) {
        List<Job> jobs = Job.getFinishedIncompleteCollectionJobsOfPoint(GroupKey, BranchCode, PFunctionalCode, actualFromTime , actualToTime);

        String receiptNo = "";
        //List<Job> jobs = Job.getCollectionJobsOfPoint(PointId);
        JsonObject jsonObject = new JsonObject();
        JsonArray CollectionHeaderList = new JsonArray();

        String startTime = Constants.startTime;
        String endTime = Constants.endTime;

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            int TransportMasterId = job.TransportMasterId;
            Job.UpdateReceiptNo(GroupKey, job.BranchCode, job.PDFunctionalCode, job.ActualFromTime, job.ActualToTime, context);
            JsonObject jobjson = new JsonObject();
            receiptNo = Job.getSingle(TransportMasterId).ReceiptNo;
            jobjson.addProperty("TransportId", TransportMasterId);
            jobjson.addProperty("CollectionId", job.CollectionOrderId);
            jobjson.addProperty("ReceiptNo", receiptNo);
            JsonArray Details = new JsonArray();

            List<Bags> bags = Bags.getByTransportMasterId(TransportMasterId);
            for (int j = 0; j < bags.size(); j++) {
                Bags bag = bags.get(j);
                JsonObject Detail = new JsonObject();
                Detail.addProperty("ItemType", "BAG");
                Detail.addProperty("DenoId", 0);
                Detail.addProperty("SealNo1", bag.firstbarcode);
                Detail.addProperty("SealNo2", bag.secondbarcode);
                Detail.addProperty("CageNo", bag.CageNo);
                Detail.addProperty("CageSeal", bag.CageSeal);
                Detail.addProperty("Qty", 1);
                Detail.addProperty("CoinSeriesId", 0);
                Detail.add("EnvelopeSeal", null);
                Details.add(Detail);
            }

            List<Wagon> wagons = Wagon.getByTransportMasterId(TransportMasterId);
            for (int j = 0; j < wagons.size(); j++) {
                Wagon wagon = wagons.get(j);
                JsonObject Detail = new JsonObject();
                Detail.addProperty("ItemType", "Wagon");
                Detail.addProperty("DenoId", 0);
                Detail.addProperty("SealNo1", wagon.firstbarcode);
                Detail.addProperty("SealNo2", wagon.secondbarcode);
                Detail.addProperty("CageNo", wagon.CageNo);
                Detail.addProperty("CageSeal", wagon.CageSeal);
                Detail.addProperty("Qty", 1);
                Detail.addProperty("CoinSeriesId", 0);
                Detail.add("EnvelopeSeal", null);
                Details.add(Detail);
            }

            List<EnvelopeBag> envelopeBags = EnvelopeBag.getByTransportMasterId(TransportMasterId);
            for (int j = 0; j < envelopeBags.size(); j++) {
                EnvelopeBag envelopeBag = envelopeBags.get(j);
                List<Envelope> envelopes = Envelope.getByBagId(envelopeBag.getId());
                if (envelopeBag.bagcode.equals("none")) {
                    for (int k = 0; k < envelopes.size(); k++) {
                        JsonObject Detail = new JsonObject();
                        Detail.addProperty("ItemType", "Envelopes");
                        Detail.addProperty("DenoId", 0);
                        Detail.addProperty("SealNo1", envelopes.get(k).barcode);
                        Detail.addProperty("SealNo2", "");
                        Detail.addProperty("CageNo", envelopes.get(k).CageNo);
                        Detail.addProperty("CageSeal", envelopes.get(k).CageSeal);
                        Detail.addProperty("Qty", 1);
                        Detail.addProperty("CoinSeriesId", 0);
                        Detail.add("EnvelopeSeal", null);
                        Details.add(Detail);
                    }
                } else {
                    JsonArray seals = new JsonArray();
                    for (int k = 0; k < envelopes.size(); k++) {
                        seals.add(envelopes.get(k).barcode);
                    }
                    JsonObject Detail = new JsonObject();
                    Detail.addProperty("ItemType", "Envelope In Bag");
                    Detail.addProperty("DenoId", 0);
                    Detail.addProperty("SealNo1", envelopeBag.bagcode);
                    Detail.addProperty("SealNo2", "");
                    Detail.addProperty("CageNo", envelopeBag.CageNo);
                    Detail.addProperty("CageSeal", envelopeBag.CageSeal);
                    Detail.addProperty("Qty", envelopes.size());
                    Detail.addProperty("CoinSeriesId", 0);
                    Detail.add("EnvelopeSeal", seals);
                    Details.add(Detail);
                }
            }

            List<Box> boxes = Box.getBoxByTransportMasterId(TransportMasterId);
            for (int j = 0; j < boxes.size(); j++) {
                Box box = boxes.get(j);
                JsonObject Detail = new JsonObject();
                Detail.addProperty("ItemType", "Box");
                Detail.addProperty("DenoId", box.ProductId);
                Detail.addProperty("SealNo1", "");
                Detail.addProperty("SealNo2", "");
                Detail.addProperty("CageNo", box.CageNo);
                Detail.addProperty("CageSeal", box.CageSeal);
                Detail.addProperty("Qty", box.count);
                Detail.addProperty("CoinSeriesId", box.CoinSeriesId);
                Detail.add("EnvelopeSeal", null);
                Details.add(Detail);
            }

            List<BoxBag> boxBags = BoxBag.getByTransportMasterId(TransportMasterId);
            for (int j = 0; j < boxBags.size(); j++) {
                BoxBag boxBag = boxBags.get(j);
                JsonObject Detail = new JsonObject();
                Detail.addProperty("ItemType", "Coin Bag");
                Detail.addProperty("DenoId", boxBag.ProductId);
                Detail.addProperty("SealNo1", boxBag.bagcode);
                Detail.addProperty("SealNo2", "");
                Detail.addProperty("CageNo", boxBag.CageNo);
                Detail.addProperty("CageSeal", boxBag.CageSeal);
                Detail.addProperty("Qty", 1);
                Detail.addProperty("CoinSeriesId", boxBag.CoinSeriesId);
                Detail.add("EnvelopeSeal", null);
                Details.add(Detail);
            }
            if (job.palletCount > 0) {
                JsonObject Detail = new JsonObject();
                Detail.addProperty("ItemType", "Pallet");
                Detail.addProperty("DenoId", 0);
                Detail.addProperty("SealNo1", "");
                Detail.addProperty("SealNo2", "");
                Detail.addProperty("Qty", job.palletCount);
                Detail.addProperty("CoinSeriesId", 0);
                Detail.add("EnvelopeSeal", null);
                Details.add(Detail);
            }
            jobjson.add("Details", Details);
            Job.UpdateTime(TransportMasterId, startTime , endTime);
            CollectionHeaderList.add(jobjson);
        }
        jsonObject.add("CollectionHeaderList", CollectionHeaderList);
        jsonObject.addProperty("ReceiptNo", receiptNo);
        jsonObject.addProperty("Sign", Branch.getSingle(GroupKey).colSignature);
        jsonObject.addProperty("JobStartTime", startTime);
        jsonObject.addProperty("JobEndTime", endTime);
        jsonObject.addProperty("UserId", Preferences.getInt("UserId", context));
        jsonObject.addProperty("StaffID", Branch.getSingle(GroupKey).StaffID);
        jsonObject.addProperty("CName", Branch.getSingle(GroupKey).CName);
        jsonObject.addProperty("CustomerSignature", Branch.getSingle(GroupKey).ColCustomerSignature);
        int maxLogSize = 1000;
        String veryLongString = jsonObject.toString();
        for (int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > veryLongString.length() ? veryLongString.length() : end;
            Log.e("Json result", veryLongString.substring(start, end));
        }
        return jsonObject;
    }

    public static JsonObject getDelivery(String GroupKey, Context context, String BranchCode, String PFunctionalCode, String actualFromTime, String actualToTime) {
        JsonObject jsonObject = new JsonObject();
        JsonArray DeliveryList = new JsonArray();
        String receiptNo = "";

        String startTime = Constants.startTime;
        String endTime = Constants.endTime;

        List<Job> deliveryjobs = Job.getPendingDeliveryJobsOfPoint(GroupKey, BranchCode, PFunctionalCode, actualFromTime, actualToTime);
        for (int i = 0; i < deliveryjobs.size(); i++) {
            JsonObject Delivery = new JsonObject();
            Job job = deliveryjobs.get(i);
            Delivery.addProperty("TransportMasterId", deliveryjobs.get(i).TransportMasterId);
            Delivery.addProperty("FloatDeliveryOrderId", deliveryjobs.get(i).FloatDeliveryOrderId);
            DeliveryList.add(Delivery);
            Job.UpdateTime(job.TransportMasterId, startTime, endTime);
            Job.UpdateReceiptNoDelivery(GroupKey, job.BranchCode, job.PFunctionalCode, context);
            receiptNo = Job.getSingle(job.TransportMasterId).ReceiptNo;
        }
        jsonObject.add("DeliveryList", DeliveryList);
        jsonObject.addProperty("ReceiptNo", receiptNo);
        jsonObject.addProperty("Sign", Branch.getSingle(GroupKey).delSignature);
        jsonObject.addProperty("JobStartTime", startTime);
        jsonObject.addProperty("JobEndTime", endTime);
        jsonObject.addProperty("UserId", Preferences.getInt("UserId", context));
        jsonObject.addProperty("StaffID", Branch.getSingle(GroupKey).StaffID);
        jsonObject.addProperty("CName", Branch.getSingle(GroupKey).CName);
        jsonObject.addProperty("CustomerSignature", Branch.getSingle(GroupKey).DelCustomerSignature);

        int maxLogSize = 1000;
        String veryLongString = jsonObject.toString();
        for (int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > veryLongString.length() ? veryLongString.length() : end;
            Log.e("Json result", veryLongString.substring(start, end));
        }

        return jsonObject;
    }

    public static void clearAllTables() {
        remove();
        Job.remove();
        Bags.remove();
        Wagon.remove();
        Box.remove();
        Cage.remove();
        BoxBag.remove();
        Currency.remove();
        Delivery.remove();
        Envelope.remove();
        EnvelopeBag.remove();
        ChatMessage.remove();
        Break.remove();
        Reschedule.remove();
        CoinSeries.remove();
    }

//    public static void removeSingle(int PointId) {
//        new Delete().from(Branch.class)
//                .where("PointId=?", PointId)
//                .execute();
//    }

    public static void removeSingle(String GroupKey) {
        new Delete().from(Branch.class)
                .where("GroupKey=?", GroupKey)
                .execute();
    }

    public static void remove() {
        new Delete().from(Branch.class)
                .execute();
    }

    @Override
    public int compareTo(Branch o) {
        if (TextUtils.isEmpty(this.tempSeq)) {
            if (TextUtils.isEmpty(o.tempSeq)) {
                return 0;
            } else {
                return 1;
            }
        } else if (TextUtils.isEmpty(o.tempSeq)) {
            return -1;
        } else {
            return this.tempSeq.compareTo(o.tempSeq);
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
