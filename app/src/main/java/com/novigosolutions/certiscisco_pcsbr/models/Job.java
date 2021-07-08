package com.novigosolutions.certiscisco_pcsbr.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.google.gson.JsonObject;
import com.novigosolutions.certiscisco_pcsbr.objects.Summary;
import com.novigosolutions.certiscisco_pcsbr.zebra.Content;
import com.novigosolutions.certiscisco_pcsbr.zebra.Denomination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Table(name = "job")
public class Job extends Model implements Comparable<Job> {

    @Column(name = "TransportMasterId")
    public int TransportMasterId;

    @Column(name = "BranchPointId")
    public int BranchPointId;

    @Column(name = "FloatDeliveryOrderId")
    public int FloatDeliveryOrderId;

    @Column(name = "CollectionOrderId")
    public int CollectionOrderId;

    @Column(name = "OrderNo")
    public String OrderNo;

    @Column(name = "OrderDt")
    public String OrderDt;

    @Column(name = "Version")
    public String Version;

    @Column(name = "IsFloatDeliveryOrder")
    public boolean IsFloatDeliveryOrder;

    @Column(name = "IsCollectionOrder")
    public boolean IsCollectionOrder;

    @Column(name = "TeamId")
    public int TeamId;

    @Column(name = "TeamName")
    public String TeamName;

    @Column(name = "DeliveryDt")
    public String DeliveryDt;

    @Column(name = "DCustomerCode")
    public String DCustomerCode;

    @Column(name = "CustomerName")
    public String CustomerName;

    @Column(name = "CustomerId")
    public int CustomerId;

    @Column(name = "PointId")
    public int PointId;

    @Column(name = "BranchCode")
    public String BranchCode;

    @Column(name = "FunctionalCode")
    public String FunctionalCode;

    @Column(name = "StreetName")
    public String StreetName;

    @Column(name = "Tower")
    public String Tower;

    @Column(name = "Town")
    public String Town;

    @Column(name = "PinCode")
    public String PinCode;

    @Column(name = "ContactName")
    public String ContactName;

    @Column(name = "ContactNo")
    public String ContactNo;

    @Column(name = "VersionNo")
    public String VersionNo;

    @Column(name = "CanCollectedBag")
    public boolean CanCollectedBag;

    @Column(name = "CanCollectedEnvelop")
    public boolean CanCollectedEnvelop;

    @Column(name = "CanCollectedEnvelopInBag")
    public boolean CanCollectedEnvelopInBag;

    @Column(name = "CanCollectedBox")
    public boolean CanCollectedBox;

    @Column(name = "CanCollectPallet")
    public boolean CanCollectPallet;

    @Column(name = "CanCollectCoinBox")
    public boolean CanCollectCoinBox;

    @Column(name = "EnableManualEntry") //these for collection
    public boolean EnableManualEntry;

    @Column(name = "requestId") //these for collection
    public String requestId;

    @Column(name = "isOfflineSaved")
    public boolean isOfflineSaved;

    @Column(name = "isNoCollection")
    public boolean isNoCollection;

    @Column(name = "palletCount")
    public int palletCount;

    @Column(name = "Status")
    public String Status;

    @Column(name = "OrderRemarks")
    public String OrderRemarks;

    @Column(name = "GroupKey")
    public String GroupKey;

    @Column(name = "ClientBreak")
    public String ClientBreak;

    @Column(name = "SequenceNo")
    public String SequenceNo = "";

    @Column(name = "JobStartTime")
    public String JobStartTime;

    @Column(name = "JobEndTime")
    public String JobEndTime;

    @Column(name = "isCollected")
    public boolean isCollected;

    @Column(name = "finished")
    public boolean finished;

    @Column(name = "DependentOrderId")
    public String DependentOrderId;

    @Column(name = "BranchStreetName")
    public String BranchStreetName;

    @Column(name = "BranchTower")
    public String BranchTower;

    @Column(name = "BranchTown")
    public String BranchTown;

    @Column(name = "BranchPinCode")
    public String BranchPinCode;


    @Column(name = "PDFunctionalCode")
    public String PDFunctionalCode;

    @Column(name = "CreditTo")
    public String CreditTo;

    @Column(name = "ActualFromTime")
    public String ActualFromTime;

    @Column(name = "ActualToTime")
    public String ActualToTime;

    //Transcation officer signature
    @Column(name = "CustomerSign")
    public String CustomerSign;

    //customer Signature
    @Column(name = "CustomerSignature")
    public String CustomerSignature;

    @Column(name = "CName")
    public String CName;

    @Column(name = "StaffID")
    public String StaffID;

    @Column(name = "ReceiptNo")
    public String ReceiptNo;

    private boolean isSelected;


    public static Job getSingle(int TransportMasterId) {
        return new Select().from(Job.class)
                .where("TransportMasterId=?", TransportMasterId)
                .executeSingle();

    }

    public static Job getSingleByOrderNo(String OrderNo) {
        return new Select().from(Job.class)
                .where("OrderNo=?", OrderNo)
                .executeSingle();

    }

    public static List<Job> getByGroupKey(String GroupKey) {
        return new Select().from(Job.class)
                .where("GroupKey=?", GroupKey)
                .execute();

    }

    public static List<Job> getJobListByGroupKey(String GroupKey) {
        List<Job> jl = new Select().from(Job.class)
                .where("GroupKey=? AND IsCollectionOrder=1", GroupKey)
                .execute();
        List<Job> del = new Select().from(Job.class)
                .where("GroupKey=? AND IsFloatDeliveryOrder=1", GroupKey)
                .execute();
        if (!del.isEmpty()) {
            jl.add(del.get(0));
        }
        return jl;
    }

    public static List<Job> getByGroupKeyAndStatus(String GroupKey, String Status) {
        return new Select().from(Job.class)
                .where("GroupKey=? AND status=?", GroupKey, Status)
                .execute();

    }

    public static List<Job> getJobListByGroupKeyAndStatus(String GroupKey, String Status) {
        List<Job> jl = new Select().from(Job.class)
                .where("GroupKey=? AND status=? AND IsCollectionOrder=1", GroupKey, Status)
                .execute();
        List<Job> del = new Select().from(Job.class)
                .where("GroupKey=? AND status=? AND IsFloatDeliveryOrder=1", GroupKey, Status)
                .execute();
        if (!del.isEmpty()) {
            jl.add(del.get(0));
        }
        return jl;
    }

    public static List<Job> getJobListByType(int isDelivered, int isCollection) {
        List<Job> jl = new Select().from(Job.class)
                .where("status=? AND IsCollectionOrder=? and IsFloatDeliveryOrder=?", "COMPLETED", isCollection, isDelivered)
                .groupBy("GroupKey")
                .orderBy("SequenceNo")
                .execute();

        return jl;
    }

    public static List<Job> getJobListByTypeByGroupKey(int isDelivered, int isCollection, String groupKey) {
        List<Job> jl = new Select().from(Job.class)
                .where("status=? AND IsCollectionOrder=? and IsFloatDeliveryOrder=? and GroupKey=? ", "COMPLETED", isCollection, isDelivered, groupKey)
                .execute();

        return jl;
    }

    public static List<Job> getSpecificJobListByType(int isDelivered, int isCollection, int TransportMasterId) {
        List<Job> jl = new Select().from(Job.class)
                .where("status=? AND IsCollectionOrder=? and IsFloatDeliveryOrder=? and TransportMasterId=?", "COMPLETED", isCollection, isDelivered, TransportMasterId)
                .groupBy("GroupKey")
                .orderBy("SequenceNo")
                .execute();

        return jl;
    }

//    public static Job getSingleJobOfPoint(int PointId) {
//        return new Select().from(Job.class)
//                .where("BranchPointId=?", PointId)
//                .executeSingle();
//
//    }

//    public static List<Job> getCollectionJobsOfPoint(int PointId) {
//        return new Select().from(Job.class)
//                .where("IsCollectionOrder=? AND BranchPointId=?", 1, PointId)
//                .execute();
//    }

    public static List<Job> getCollectionJobsOfPoint(String GroupKey) {
        return new Select().from(Job.class)
                .where("IsCollectionOrder=? AND GroupKey=?", 1, GroupKey)
                .execute();
    }


//    public static List<Job> getIncompleteCollectionJobsOfPoint(int PointId) {
//        return new Select().from(Job.class)
//                .where("IsCollectionOrder=? AND BranchPointId=? AND Status NOT IN ('COMPLETED')", 1, PointId)
//                .execute();
//    }

    public static List<Job> getIncompleteCollectionJobsOfPoint(String GroupKey) {
        return new Select().from(Job.class)
                .where("IsCollectionOrder=? AND GroupKey=? AND Status NOT IN ('COMPLETED')", 1, GroupKey)
                .execute();
    }

    public static List<Job> getFinishedIncompleteCollectionJobsOfPoint(String GroupKey) {
        return new Select().from(Job.class)
                .where("IsCollectionOrder=? AND GroupKey=? AND finished=? AND Status NOT IN ('COMPLETED')", 1, GroupKey, 1)
                .execute();
    }

    public static void setIncompleteCollectionCollected(String GroupKey) {
//        List<Job> jlist = new Select().from(Job.class)
//                .where("IsCollectionOrder=? AND GroupKey=? AND finished=? AND Status NOT IN ('COMPLETED')", 1, GroupKey,1)
//                .execute();
//        if(jlist!=null) {
//            Log.e("^^^^^OfflineUpdate", "setIncompleteCollectionCollected-inside: jlist size,first id:" + jlist.size() + ","+jlist.get(0).TransportMasterId);
//        }else{
//            Log.e("^^^^^OfflineUpdate", "setIncompleteCollectionCollected-inside: jlist null");
//        }
//
//        String s = new Update(Job.class)
//                .set("isOfflineSaved=?, isCollected=?, Status=?", 0, 1,"COMPLETED")
//                .where("IsCollectionOrder=? AND GroupKey=? AND finished=? AND Status NOT IN ('COMPLETED')", 1, GroupKey,1)
//                .toSql();
//
//        Log.e("^^^^^OfflineUpdate", "setIncompleteCollectionCollected-updatequery: "+s);
        new Update(Job.class)
                .set("isOfflineSaved=?, isCollected=?, Status=?", 0, 1, "COMPLETED")
                .where("IsCollectionOrder=? AND GroupKey=? AND finished=? AND Status NOT IN ('COMPLETED')", 1, GroupKey, 1)
                .execute();
    }

    public static void setPendingDeliveryDelivered(String GroupKey) {
        new Update(Job.class)
                .set("isOfflineSaved=?, Status=?", 0, "COMPLETED")
                .where("IsFloatDeliveryOrder=? AND GroupKey=? AND finished=? AND Status NOT IN ('COMPLETED')", 1, GroupKey, 1)
                .execute();
    }

//    public static List<Job> getPendingJobsOfPoint(int PointId) {
//        return new Select().from(Job.class)
//                .where("BranchPointId=? AND Status NOT IN ('COMPLETED')", PointId)
//                .execute();
//    }

    public static List<Job> getPendingJobsOfPoint(String GroupKey) {
        return new Select().from(Job.class)
                .where("GroupKey=? AND Status NOT IN ('COMPLETED')", GroupKey)
                .execute();
    }

//    public static List<Job> getDeliveryJobsOfPoint(int PointId) {
//        return new Select().from(Job.class)
//                .where("IsFloatDeliveryOrder=? AND BranchPointId=?", 1, PointId)
//                .execute();
//    }

    public static List<Job> getDeliveryJobsOfPoint(String GroupKey) {
        return new Select().from(Job.class)
                .where("IsFloatDeliveryOrder=? AND GroupKey=?", 1, GroupKey)
                .execute();
    }


//    public static List<Job> getPendingDeliveryJobsOfPoint(int PointId) {
//        return new Select().from(Job.class)
//                .where("IsFloatDeliveryOrder=? AND BranchPointId=? AND Status NOT IN ('COMPLETED')", 1, PointId)
//                .execute();
//    }

    public static List<Job> getPendingDeliveryJobsOfPoint(String GroupKey) {
        return new Select().from(Job.class)
                .where("IsFloatDeliveryOrder=? AND GroupKey=? AND Status NOT IN ('COMPLETED')", 1, GroupKey)
                .execute();
    }

    public static List<Job> getFinishedPendingDeliveryJobsOfPoint(String GroupKey) {
        return new Select().from(Job.class)
                .where("IsFloatDeliveryOrder=? AND GroupKey=? AND finished=? AND Status NOT IN ('COMPLETED')", 1, GroupKey, 1)
                .execute();
    }

//    public static String getAllOrderNos(int PointId) {
//        String ordernos = "";
//        List<Job> jobs = new Select().from(Job.class)
//                .where("BranchPointId=?", PointId)
//                .execute();
//        List<String> orderlist = new ArrayList<>();
//        for (int i = 0; i < jobs.size(); i++) {
//            if (!orderlist.contains(jobs.get(i).OrderNo)) {
//                orderlist.add(jobs.get(i).OrderNo);
//            }
//        }
//        for (int i = 0; i < orderlist.size(); i++) {
//            ordernos = ordernos + "-" + orderlist.get(i) + "\n";
//        }
//        if (ordernos.length() > 0)
//            ordernos = ordernos.substring(0, ordernos.length() - 1);
//        return ordernos;
//    }

    public static String getAllOrderNos(String GroupKey, String status) {
        String ordernos = "";
        List<Job> jobs;
        if ("ALL".equals(status)) {
            jobs = new Select().from(Job.class)
                    .where("GroupKey=?", GroupKey)
                    .execute();
        } else {
            jobs = new Select().from(Job.class)
                    .where("GroupKey=? AND status=?", GroupKey, status)
                    .execute();
        }
        List<String> orderlist = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            if (!orderlist.contains(jobs.get(i).OrderNo)) {
                orderlist.add(jobs.get(i).OrderNo);
            }
        }
        for (int i = 0; i < orderlist.size(); i++) {
            ordernos = ordernos + "-" + orderlist.get(i) + "\n";
        }
        if (ordernos.length() > 0)
            ordernos = ordernos.substring(0, ordernos.length() - 1);
        return ordernos;
    }

    public static String getAllDeliveryOrderNos(String GroupKey) {
        String ordernos = "";
        List<Job> jobs;
        jobs = new Select().from(Job.class)
                .where("GroupKey=? AND IsFloatDeliveryOrder=1", GroupKey)
                .execute();
        List<String> orderlist = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            if (!orderlist.contains(jobs.get(i).OrderNo)) {
                orderlist.add(jobs.get(i).OrderNo);
            }
        }
        for (int i = 0; i < orderlist.size(); i++) {
            ordernos = ordernos + "-" + orderlist.get(i) + "\n";
        }
        if (ordernos.length() > 0)
            ordernos = ordernos.substring(0, ordernos.length() - 1);
        return ordernos;
    }

    public static String getMinimumSequenceNo(String GroupKey, String status) {
        List<Job> jobs;
        if ("ALL".equals(status)) {
            jobs = new Select().from(Job.class)
                    .where("GroupKey=?", GroupKey)
                    .execute();
        } else {
            jobs = new Select().from(Job.class)
                    .where("GroupKey=? AND status=?", GroupKey, status)
                    .execute();
        }
        Job j = Collections.min(jobs);
        return j.SequenceNo;
    }

//    public static String getDeliveryOrderNos(int PointId) {
////        String ordernos = "";
////        List<Job> jobs = new Select().from(Job.class)
////                .where("IsFloatDeliveryOrder=? AND BranchPointId=?", 1, PointId)
////                .execute();
////        List<String> orderlist = new ArrayList<>();
////        for (int i = 0; i < jobs.size(); i++) {
////            if (!orderlist.contains(jobs.get(i).OrderNo)) {
////                orderlist.add(jobs.get(i).OrderNo);
////            }
////        }
////        for (int i = 0; i < orderlist.size(); i++) {
////            ordernos = ordernos + orderlist.get(i) + ",";
////        }
////        if (ordernos.length() > 0)
////            ordernos = ordernos.substring(0, ordernos.length() - 1);
////        return ordernos;
////    }

    public static String getDeliveryOrderNos(String GroupKey) {
        String ordernos = "";
        List<Job> jobs = new Select().from(Job.class)
                .where("IsFloatDeliveryOrder=? AND GroupKey=?", 1, GroupKey)
                .execute();
        List<String> orderlist = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            if (!orderlist.contains(jobs.get(i).OrderNo)) {
                orderlist.add(jobs.get(i).OrderNo);
            }
        }
        for (int i = 0; i < orderlist.size(); i++) {
            ordernos = ordernos + orderlist.get(i) + ",";
        }
        if (ordernos.length() > 0)
            ordernos = ordernos.substring(0, ordernos.length() - 1);
        return ordernos;
    }

    public static List<Job> getAll() {
        return new Select().from(Job.class)
                .execute();

    }

//    public static boolean hasCollectionJob(int PointId) {
//        return (new Select().from(Job.class)
//                .where("IsCollectionOrder=? AND BranchPointId=?", 1, PointId)
//                .executeSingle() != null);
//    }

    public static boolean hasCollectionJob(String GroupKey) {
        return (new Select().from(Job.class)
                .where("IsCollectionOrder=? AND GroupKey=?", 1, GroupKey)
                .executeSingle() != null);
    }

    public static boolean hasCollectionJobByStatus(String GroupKey, String Status) {
        return (new Select().from(Job.class)
                .where("IsCollectionOrder=? AND GroupKey=? AND Status=?", 1, GroupKey, Status)
                .executeSingle() != null);
    }

    //    public static boolean hasDeliveryJob(int PointId) {
//        return (new Select().from(Job.class)
//                .where("IsFloatDeliveryOrder=? AND BranchPointId=?", 1, PointId)
//                .executeSingle() != null);
//    }
    public static boolean hasDeliveryJob(String GroupKey) {
        return (new Select().from(Job.class)
                .where("IsFloatDeliveryOrder=? AND GroupKey=?", 1, GroupKey)
                .executeSingle() != null);
    }

    public static boolean hasDeliveryJobByStatus(String GroupKey, String Status) {
        return (new Select().from(Job.class)
                .where("IsFloatDeliveryOrder=? AND GroupKey=? AND Status=?", 1, GroupKey, Status)
                .executeSingle() != null);
    }

    public static List<Job> hasCompletedJob(String groupKey) {
        return new Select().from(Job.class)
                .where("GroupKey=? AND Status=?", groupKey, "COMPLETED")
                .execute();
    }

    public static void UpdateRequestId(int TransportMasterId, String requestId) {
        new Update(Job.class)
                .set("requestId=?", requestId)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static void UpdateStatus(int TransportMasterId) {
        new Update(Job.class)
                .set("Status=?", "COMPLETED")
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static void UpdateVersion(int TransportMasterId, String VersionNo) {
        new Update(Job.class)
                .set("VersionNo=?", VersionNo)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static void UpdateTime(int TransportMasterId, String startTime, String endTime) {
        new Update(Job.class)
                .set("ActualFromTime=? , ActualToTime=? ", startTime, endTime)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    //update Transaction officer Signature
    public static void UpdateSignature(int TransportMasterID, String signature) {
        new Update(Job.class)
                .set("CustomerSign=?", signature)
                .where("TransportMasterId=?", TransportMasterID)
                .execute();
    }

    //update customert signature part
    public static void UpdateCustomerSignature(int TransportMasterID, String signature) {
        new Update(Job.class)
                .set("CustomerSignature=?", signature)
                .where("TransportMasterId=?", TransportMasterID)
                .execute();
    }


    public static void UpdateNameAndStaffID(int TransportMasterId, String name, String id) {
        new Update(Job.class)
                .set("CName=?, StaffID=? ", name, id)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }


//    public static void UpdateCollectionCompleted(int PointId) {
//        new Update(Job.class)
//                .set("Status=?", "COMPLETED")
//                .where("BranchPointId=? AND IsCollectionOrder=?", PointId, 1)
//                .execute();
//    }

//    public static void UpdateCollectionCompleted(String GroupKey) {
//        new Update(Job.class)
//                .set("Status=?", "COMPLETED")
//                .where("GroupKey=? AND IsCollectionOrder=?", GroupKey, 1)
//                .execute();
//    }

//    public static void UpdatDeliveryCompleted(int PointId) {
//        new Update(Job.class)
//                .set("Status=?", "COMPLETED")
//                .where("IsFloatDeliveryOrder=1 AND BranchPointId=?", PointId)
//                .execute();
//    }

    public static void UpdatDeliveryCompleted(String GroupKey) {
        new Update(Job.class)
                .set("Status=?", "COMPLETED")
                .where("IsFloatDeliveryOrder=1 AND GroupKey=?", GroupKey)
                .execute();
    }

    public static void EnableManualEntry(int TransportMasterId) {
        new Update(Job.class)
                .set("EnableManualEntry=?", 1)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static List<String> getCollectionTypes(int TransportMasterId) {
        List<String> collection_types = new ArrayList<>();
        Job job = new Select().from(Job.class)
                .where("TransportMasterId=?", TransportMasterId)
                .executeSingle();
        if (job.CanCollectedBag) collection_types.add("Bag");
        if (job.CanCollectedEnvelop) collection_types.add("Envelope(s)");
        if (job.CanCollectedEnvelopInBag) collection_types.add("Envelope(s) In Bag");
        if (job.CanCollectedBox) collection_types.add("Box");
        if (job.CanCollectPallet) collection_types.add("Pallet");
        if (job.CanCollectCoinBox) collection_types.add("Coin Bag");
        return collection_types;
    }


    public static List<Summary> getCollectionSummary(int TransportMasterId) {
        List<Summary> collection_summary = new ArrayList<>();
        List<Bags> bags = Bags.getByTransportMasterId(TransportMasterId);
        for (int i = 0; i < bags.size(); i++) {
            Summary collectionSummary = new Summary();
            collectionSummary.Collection_type = "Bag";
            collectionSummary.id = bags.get(i).getId();
            collectionSummary.Head = "Bag";
            String message = bags.get(i).firstbarcode;
            if (!bags.get(i).secondbarcode.isEmpty()) message += ", " + bags.get(i).secondbarcode;
            collectionSummary.Message = message;
            collection_summary.add(collectionSummary);
        }
        List<EnvelopeBag> envelopebags = EnvelopeBag.getByTransportMasterId(TransportMasterId);
        for (int i = 0; i < envelopebags.size(); i++) {
            List<Envelope> envelopes = Envelope.getByBagId(envelopebags.get(i).getId());
            if (envelopebags.get(i).envolpeType.equals("Envelopes")) {
                for (int j = 0; j < envelopes.size(); j++) {
                    Summary collectionSummary = new Summary();
                    collectionSummary.Collection_type = "Envelopes";
                    collectionSummary.id = envelopes.get(j).getId();
                    collectionSummary.Head = "Envelope";
                    collectionSummary.Message = envelopes.get(j).barcode;
                    collection_summary.add(collectionSummary);
                }
            } else {
                StringBuilder message = new StringBuilder();
                for (int j = 0; j < envelopes.size(); j++) {
                    message.append(envelopes.get(j).barcode);
                    message.append(", ");
                }
                if (message.length() > 0) message.delete(message.length() - 2, message.length());
                Summary collectionSummary = new Summary();
                collectionSummary.Collection_type = "EnvelopeBag";
                collectionSummary.id = envelopebags.get(i).getId();
                collectionSummary.Head = "Envelope(s) In Bag (" + envelopebags.get(i).bagcode + ")";
                collectionSummary.Message = message.toString();
                collection_summary.add(collectionSummary);
            }
        }

        List<Box> boxes = Box.getBoxByTransportMasterId(TransportMasterId);
        for (int i = 0; i < boxes.size(); i++) {
            Summary collectionSummary = new Summary();
            collectionSummary.Collection_type = "Box";
            collectionSummary.id = boxes.get(i).getId();
            collectionSummary.Head = "Box";
            // collectionSummary.Message = boxes.get(i).ProductName + "(" + boxes.get(i).count + ")";
            if ((boxes.get(i).CoinSeriesId) == 0) {
                collectionSummary.Message = boxes.get(i).ProductName + "(" + boxes.get(i).count + ")";
            } else {
                collectionSummary.Message = boxes.get(i).ProductName + "(" + boxes.get(i).count + ")" + "(" + boxes.get(i).CoinSeries + ")";
            }
            collection_summary.add(collectionSummary);
        }

        List<BoxBag> boxBags = BoxBag.getByTransportMasterId(TransportMasterId);
        for (int i = 0; i < boxBags.size(); i++) {
            Summary collectionSummary = new Summary();
            collectionSummary.Collection_type = "CoinBox";
            collectionSummary.id = boxBags.get(i).getId();
            collectionSummary.Head = "Coin Bag(" + boxBags.get(i).bagcode + ")";
//            collectionSummary.Message = boxBags.get(i).ProductName;
            if (boxBags.get(i).CoinSeriesId == 0)
                collectionSummary.Message = boxBags.get(i).ProductName;
            else
                collectionSummary.Message = boxBags.get(i).ProductName + "(" + boxBags.get(i).CoinSeries + ")";
            collection_summary.add(collectionSummary);
        }

        int palletCount = getSingle(TransportMasterId).palletCount;
        if (palletCount > 0) {
            Summary collectionSummary = new Summary();
            collectionSummary.Collection_type = "Pallet";
            collectionSummary.id = TransportMasterId;
            collectionSummary.Head = "Pallet";
            collectionSummary.Message = String.valueOf(palletCount);
            collection_summary.add(collectionSummary);
        }
//did by dibin
//        if (collection_summary.size() == 0) {
//            List<Delivery> delivery = Delivery.getByTransportMasterId(TransportMasterId);
//            for (int i = 0; i < delivery.size(); i++) {
//
//                Summary collectionSummary = new Summary();
//                collectionSummary.Collection_type = delivery.get(i).ItemType;
//                collectionSummary.id = TransportMasterId;
//                collectionSummary.Head = delivery.get(i).ItemType + "(" + delivery.get(i).SealNo + ")";
//                collectionSummary.Message = delivery.get(i).SealNo;
//
//                collection_summary.add(collectionSummary);
//            }
//        }

        return collection_summary;
    }


    public static List<Content> getPrintContent(int transportMasterId) {
        List<Content> printContent = new ArrayList<>();

        //adding the box
        List<Box> boxes = Box.getBoxByTransportMasterId(transportMasterId);
        if (!boxes.isEmpty()) {
            Content boxContent = new Content();
            boxContent.setDescription("Box");
            int boxQty = 0;
            List<String> boxSealNoList = new ArrayList<>();
            for (int i = 0; i < boxes.size(); i++) {
                if (boxes.get(i).CoinSeriesId == 0)
                    boxSealNoList.add(boxes.get(i).ProductName + "*" + boxes.get(i).count);
                else
                    boxSealNoList.add(boxes.get(i).ProductName + "(" + boxes.get(i).CoinSeries + ")*" + boxes.get(i).count);
                boxQty += boxes.get(i).count;
            }
            boxContent.setQty(boxQty);
            boxContent.setSealNoList(boxSealNoList);

            printContent.add(boxContent);
        }


        //adding coinBag
        List<BoxBag> boxBags = BoxBag.getByTransportMasterId(transportMasterId);
        List<String> boxBagSealNoList = new ArrayList<>();
        Content boxBagContent = new Content();
        int coinBagQty = 0;
        if (!boxBags.isEmpty()) {
            for (int i = 0; i < boxBags.size(); i++) {
                coinBagQty++;
                boxBagSealNoList.add(boxBags.get(i).bagcode + ":" + boxBags.get(i).ProductName + "(" + boxBags.get(i).CoinSeries + ")");
            }
            boxBagContent.setDescription("Coin Bag");
            boxBagContent.setQty(coinBagQty);
            boxBagContent.setSealNoList(boxBagSealNoList);
            printContent.add(boxBagContent);
        }


        //pallet
        int palletCount = getSingle(transportMasterId).palletCount;
        if (palletCount > 0) {
            Content palletContent = new Content();
            List<String> palletSealNoList = new ArrayList<>();
            palletContent.setDescription("Pallet");
            palletContent.setQty(palletCount);
            palletContent.setSealNoList(palletSealNoList);
            printContent.add(palletContent);
        }

        //bags
        List<Bags> bags = Bags.getByTransportMasterId(transportMasterId);
        if (!bags.isEmpty()) {
            Content bagContent = new Content();
            List<String> bagSealNoList = new ArrayList<>();
            int bagQty = 0;
            for (int i = 0; i < bags.size(); i++) {
                String message = bags.get(i).firstbarcode;
                if (!bags.get(i).secondbarcode.isEmpty())
                    message += ", " + bags.get(i).secondbarcode;
                bagQty++;
                bagSealNoList.add(message);

            }
            bagContent.setDescription("Bag");
            bagContent.setQty(bagQty);
            bagContent.setSealNoList(bagSealNoList);
            printContent.add(bagContent);
        }


        List<EnvelopeBag> envelopeBags = EnvelopeBag.getByTransportMasterId(transportMasterId);
        if (!envelopeBags.isEmpty()) {
            Content envelopContent = new Content();
            Content envelopInBagContent = new Content();
            List<String> envSealNoList = new ArrayList<>();
            List<Denomination> envelopDenomination = new ArrayList<>();
            int envQty = 0;
            int enveInBagQty = 0;


            for (int j = 0; j < envelopeBags.size(); j++) {
                EnvelopeBag envelopeBag = envelopeBags.get(j);
                List<Envelope> envelopes = Envelope.getByBagId(envelopeBag.getId());
                if (envelopeBag.bagcode.equals("none")) {
                    for (int k = 0; k < envelopes.size(); k++) {
                        JsonObject Detail = new JsonObject();
                        Detail.addProperty("ItemType", "Envelopes");
                        envSealNoList.add(envelopes.get(k).barcode);
                        envQty++;
                    }
                } else {
                    List<String> envelopList = new ArrayList<>();
                    for (int k = 0; k < envelopes.size(); k++) {
                        envelopList.add(envelopes.get(k).barcode);
                    }
                    Denomination envelopDeno = new Denomination();
                    enveInBagQty++;
                    envelopDeno.setBagName("Bag " + enveInBagQty);
                    envelopDeno.setSealNo(envelopeBag.bagcode);
                    envelopDeno.setEnvelopsList(envelopList);
                    envelopDenomination.add(envelopDeno);

                }
            }
            if (envQty > 0) {
                envelopContent.setDescription("Envelopes");
                envelopContent.setQty(envQty);
                envelopContent.setSealNoList(envSealNoList);
                printContent.add(envelopContent);
            }
            if (enveInBagQty > 0) {
                envelopInBagContent.setDescription("Envelope In Bag");
                envelopInBagContent.setQty(enveInBagQty);
                envelopInBagContent.setDenominationList(envelopDenomination);
                printContent.add(envelopInBagContent);
            }

        }

        return printContent;
    }

    public static List<Content> getSelectedPrintContent(String groupKey, int isDelivery) {
        List<Content> printContent = new ArrayList<>();
        int isCollection = isDelivery == 0 ? 1 : 0;
        List<Job> list = Job.getJobListByTypeByGroupKey(isDelivery, isCollection, groupKey);

        int bagQty = 0, boxQty = 0, coinBagQty = 0;
        List<String> boxSealNoList = new ArrayList<>();
        List<String> boxBagSealNoList = new ArrayList<>();
        List<String> bagSealNoList = new ArrayList<>();

        for (int a = 0; a < list.size(); a++) {
            int transportMasterId = list.get(a).TransportMasterId;
            //adding the box
            List<Box> boxes = Box.getBoxByTransportMasterId(transportMasterId);
            if (!boxes.isEmpty()) {


                for (int i = 0; i < boxes.size(); i++) {
                    if (boxes.get(i).CoinSeriesId == 0)
                        boxSealNoList.add(boxes.get(i).ProductName + "*" + boxes.get(i).count);
                    else
                        boxSealNoList.add(boxes.get(i).ProductName + "(" + boxes.get(i).CoinSeries + ")*" + boxes.get(i).count);
                    boxQty += boxes.get(i).count;
                }
            }


            //adding coinBag
            List<BoxBag> boxBags = BoxBag.getByTransportMasterId(transportMasterId);
            if (!boxBags.isEmpty()) {
                for (int i = 0; i < boxBags.size(); i++) {
                    coinBagQty++;
                    boxBagSealNoList.add(boxBags.get(i).bagcode + ":" + boxBags.get(i).ProductName + "(" + boxBags.get(i).CoinSeries + ")");
                }

            }


            //pallet
            int palletCount = getSingle(transportMasterId).palletCount;
            if (palletCount > 0) {
                Content palletContent = new Content();
                List<String> palletSealNoList = new ArrayList<>();
                palletContent.setDescription("Pallet");
                palletContent.setQty(palletCount);
                palletContent.setSealNoList(palletSealNoList);
                printContent.add(palletContent);
            }

            //bags
            List<Bags> bags = Bags.getByTransportMasterId(transportMasterId);
            if (!bags.isEmpty()) {
                for (int i = 0; i < bags.size(); i++) {
                    String message = bags.get(i).firstbarcode;
                    if (!bags.get(i).secondbarcode.isEmpty())
                        message += ", " + bags.get(i).secondbarcode;
                    bagQty++;
                    bagSealNoList.add(message);

                }
            }


            List<EnvelopeBag> envelopeBags = EnvelopeBag.getByTransportMasterId(transportMasterId);
            if (!envelopeBags.isEmpty()) {
                Content envelopContent = new Content();
                Content envelopInBagContent = new Content();
                List<String> envSealNoList = new ArrayList<>();
                List<Denomination> envelopDenomination = new ArrayList<>();
                int envQty = 0;
                int enveInBagQty = 0;


                for (int j = 0; j < envelopeBags.size(); j++) {
                    EnvelopeBag envelopeBag = envelopeBags.get(j);
                    List<Envelope> envelopes = Envelope.getByBagId(envelopeBag.getId());
                    if (envelopeBag.bagcode.equals("none")) {
                        for (int k = 0; k < envelopes.size(); k++) {
                            JsonObject Detail = new JsonObject();
                            Detail.addProperty("ItemType", "Envelopes");
                            envSealNoList.add(envelopes.get(k).barcode);
                            envQty++;
                        }
                    } else {
                        List<String> envelopList = new ArrayList<>();
                        for (int k = 0; k < envelopes.size(); k++) {
                            envelopList.add(envelopes.get(k).barcode);
                        }
                        Denomination envelopDeno = new Denomination();
                        enveInBagQty++;
                        envelopDeno.setBagName("Bag " + enveInBagQty);
                        envelopDeno.setSealNo(envelopeBag.bagcode);
                        envelopDeno.setEnvelopsList(envelopList);
                        envelopDenomination.add(envelopDeno);

                    }
                }
                if (envQty > 0) {
                    envelopContent.setDescription("Envelopes");
                    envelopContent.setQty(envQty);
                    envelopContent.setSealNoList(envSealNoList);
                    printContent.add(envelopContent);
                }
                if (enveInBagQty > 0) {
                    envelopInBagContent.setDescription("Envelope In Bag");
                    envelopInBagContent.setQty(enveInBagQty);
                    envelopInBagContent.setDenominationList(envelopDenomination);
                    printContent.add(envelopInBagContent);
                }

            }
        }
        if (!bagSealNoList.isEmpty()) {
            Content bagContent = new Content();
            bagContent.setDescription("Bag");
            bagContent.setQty(bagQty);
            bagContent.setSealNoList(bagSealNoList);
            printContent.add(bagContent);
        }

        if (!boxBagSealNoList.isEmpty()) {
            Content boxBagContent = new Content();
            boxBagContent.setDescription("Coin Bag");
            boxBagContent.setQty(coinBagQty);
            boxBagContent.setSealNoList(boxBagSealNoList);
            printContent.add(boxBagContent);
        }

        if (!boxSealNoList.isEmpty()) {
            Content boxContent = new Content();
            boxContent.setDescription("Box");
            boxContent.setQty(boxQty);
            boxContent.setSealNoList(boxSealNoList);
            printContent.add(boxContent);
        }
        return printContent;
    }


//    public static boolean isAllDeliveryScanned(int PointId) {
//        boolean isAllScanned = true;
//        List<Job> deliveryJobs = getPendingDeliveryJobsOfPoint(PointId);
//        for (int i = 0; i < deliveryJobs.size(); i++) {
//            if (!Delivery.isAllDeliveryScanned(deliveryJobs.get(i).TransportMasterId)) {
//                isAllScanned = false;
//                break;
//            }
//        }
//        return isAllScanned;
//    }

    public static boolean isAllDeliveryScanned(String GroupKey) {
        boolean isAllScanned = true;
        List<Job> deliveryJobs = getPendingDeliveryJobsOfPoint(GroupKey);
        for (int i = 0; i < deliveryJobs.size(); i++) {
            if (!Delivery.isAllDeliveryScanned(deliveryJobs.get(i).TransportMasterId)) {
                isAllScanned = false;
                break;
            }
        }
        return isAllScanned;
    }

    public static void updatePalletCount(int TransportMasterId, int palletCount) {
        new Update(Job.class)
                .set("palletCount=?", palletCount)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }


    public static void setNoCollection(int TransportMasterId) {
        new Update(Job.class)
                .set("isNoCollection=?", 1)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static Boolean isCollected(int TransportMasterId) {
        Bags bags = new Select().from(Bags.class)
                .where("TransportMasterId=?", TransportMasterId)
                .executeSingle();
        if (bags != null) return true;

        EnvelopeBag envelopeBag = new Select().from(EnvelopeBag.class)
                .where("TransportMasterId=?", TransportMasterId)
                .executeSingle();
        if (envelopeBag != null) return true;

        Box box = new Select().from(Box.class)
                .where("TransportMasterId=?", TransportMasterId)
                .executeSingle();
        if (box != null) return true;

        try {
            if (getSingle(TransportMasterId).palletCount > 0)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        BoxBag boxBag = new Select().from(BoxBag.class)
                .where("TransportMasterId=?", TransportMasterId)
                .executeSingle();
        if (boxBag != null) return true;
        return false;
    }

//    public static void clearBranchCollecion(int PointId) {
//        List<Job> collectionjobs = getCollectionJobsOfPoint(PointId);
//        for (int i = 0; i < collectionjobs.size(); i++) {
//            clearSingleCollection(collectionjobs.get(i).TransportMasterId);
//        }
//    }

    public static void clearBranchCollecion(String GroupKey) {
        List<Job> collectionjobs = getCollectionJobsOfPoint(GroupKey);
        for (int i = 0; i < collectionjobs.size(); i++) {
            clearSingleCollection(collectionjobs.get(i).TransportMasterId);
        }
    }

    public static void clearSingleCollection(int TransportMasterId) {
        new Delete().from(Bags.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
        List<EnvelopeBag> envelopeBags = EnvelopeBag.getByTransportMasterId(TransportMasterId);
        for (int i = 0; i < envelopeBags.size(); i++) {
            new Delete().from(Envelope.class)
                    .where("bagid=?", envelopeBags.get(i).getId())
                    .execute();
        }
        new Delete().from(EnvelopeBag.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
        new Delete().from(Box.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
        new Delete().from(BoxBag.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
        new Update(Job.class)
                .set("palletCount=?,isNoCollection=?", 0, 0)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }


    public static Job removeSingleJob(int TransportMasterId) {
        Job job = getSingle(TransportMasterId);
        if (job != null) {
            new Delete().from(Bags.class)
                    .where("TransportMasterId=?", TransportMasterId)
                    .execute();
            new Delete().from(Box.class)
                    .where("TransportMasterId=?", TransportMasterId)
                    .execute();
            new Delete().from(BoxBag.class)
                    .where("TransportMasterId=?", TransportMasterId)
                    .execute();
//            new Delete().from(Currency.class)
//                    .where("TransportMasterId=?", TransportMasterId)
//                    .execute();

            List<EnvelopeBag> envelopeBags = EnvelopeBag.getByTransportMasterId(TransportMasterId);
            for (int i = 0; i < envelopeBags.size(); i++) {
                new Delete().from(Envelope.class)
                        .where("bagid=?", envelopeBags.get(0).getId())
                        .execute();
            }

            new Delete().from(EnvelopeBag.class)
                    .where("TransportMasterId=?", TransportMasterId)
                    .execute();
            new Delete().from(Delivery.class)
                    .where("TransportMasterId=?", TransportMasterId)
                    .execute();
            new Delete().from(Job.class)
                    .where("TransportMasterId=?", TransportMasterId)
                    .execute();
        }
        return job;
    }

    public static void remove() {
        new Delete().from(Job.class)
                .execute();
    }

    public static void updateJobStartTime(int TransportMasterId, String JobStartTime) {
        new Update(Job.class)
                .set("JobStartTime=?", JobStartTime)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static void updateDeliveryJobsStartTime(String GroupKey, String JobStartTime) {
        new Update(Job.class)
                .set("JobStartTime=?", JobStartTime)
                .where("IsFloatDeliveryOrder=1 AND GroupKey=?", GroupKey)
                .execute();
    }

    public static void updateJobEndTime(int TransportMasterId, String JobEndTime) {
        new Update(Job.class)
                .set("JobEndTime=?", JobEndTime)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static void updateDeliveryJobsEndTime(String GroupKey, String JobEndTime) {
        new Update(Job.class)
                .set("JobEndTime=?", JobEndTime)
                .where("IsFloatDeliveryOrder=1 AND GroupKey=?", GroupKey)
                .execute();
    }

    public static void setFinished(int TransportMasterId) {
        new Update(Job.class)
                .set("finished=1")
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static void setDeliveryJobsFinished(String GroupKey) {
        new Update(Job.class)
                .set("finished=1")
                .where("IsFloatDeliveryOrder=1 AND GroupKey=?", GroupKey)
                .execute();
    }

    public static void setCollected(int TransportMasterId) {
        new Update(Job.class)
                .set("isCollected=?,Status=?", 1, "COMPLETED")
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
//        Job.UpdateCollectionCompleted(TransportMasterId);
//        resetStatus(GroupKey);

    }

//    public static void setDelivered(int TransportMasterId) {
//        new Update(Job.class)
//                .set("Status=?", "COMPLETED")
//                .where("TransportMasterId=?", TransportMasterId)
//                .execute();
//    }

    public static void setDelivered(String GroupKey) {
        new Update(Job.class)
                .set("Status=?", "COMPLETED")
                .where("IsFloatDeliveryOrder=1 AND GroupKey=?", GroupKey)
                .execute();
    }

    public static List<Job> checkPendingDependentCollections(String DependentOrderId) {
        return new Select().from(Job.class)
                .where(" OrderNo=? AND Status NOT IN ('COMPLETED')", DependentOrderId)
                .execute();
    }

    public static void setOfflineSaved(int TransportMasterId, int status) {
        new Update(Job.class)
                .set("isOfflineSaved=?", status)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static Boolean isOfflineExist() {
        if (getOfflineSingleJob() != null) return true;
        return false;
    }

    public static Job getOfflineSingleJob() {
        return new Select().from(Job.class)
                .where("isOfflineSaved=?", 1)
                .executeSingle();
    }

//    @Override
//    public int compare(Job o1, Job o2) {
//        int a = TextUtils.isEmpty(o1.SequenceNo)?0:Integer.parseInt(o1.SequenceNo);
//        int b = TextUtils.isEmpty(o2.SequenceNo)?0:Integer.parseInt(o2.SequenceNo);
//        return a - b ;
//    }


    @Override
    public int compareTo(Job o) {
//        if(TextUtils.isEmpty(this.SequenceNo)){
//            if(TextUtils.isEmpty(o.SequenceNo)){
//                return 0;
//            }else{
//                return 1;
//            }
//        } else if(TextUtils.isEmpty(o.SequenceNo)){
//            return -1;
//        }else {
        return this.SequenceNo.compareTo(o.SequenceNo);
//        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
