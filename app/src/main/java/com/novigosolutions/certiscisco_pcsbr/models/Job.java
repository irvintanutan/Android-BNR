package com.novigosolutions.certiscisco_pcsbr.models;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.google.gson.JsonObject;
import com.novigosolutions.certiscisco_pcsbr.expandable.Items;
import com.novigosolutions.certiscisco_pcsbr.objects.SecureObject;
import com.novigosolutions.certiscisco_pcsbr.objects.Summary;
import com.novigosolutions.certiscisco_pcsbr.utils.Constants;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.zebra.CageContent;
import com.novigosolutions.certiscisco_pcsbr.zebra.Content;
import com.novigosolutions.certiscisco_pcsbr.zebra.Denomination;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.novigosolutions.certiscisco_pcsbr.models.Delivery.getPendingSealedByPointId;
import static com.novigosolutions.certiscisco_pcsbr.models.Delivery.getUnSealedByPointId;

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

    @Column(name = "CanCollectWagon")
    public boolean CanCollectWagon;

    @Column(name = "CanCollectConsignmentInBag")
    public boolean CanCollectConsignmentInBag;

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

    @Column(name = "PFunctionalCode")
    public String PFunctionalCode;

    @Column(name = "PPinCode")
    public String PPinCode;

    @Column(name = "PStreetName")
    public String PStreetName;

    @Column(name = "PTower")
    public String PTower;

    @Column(name = "PTown")
    public String PTown;

    private boolean isSelected;

    @Column(name = "IsSecured")
    public boolean IsSecured;


    public static Job getSingle(int TransportMasterId) {
        return new Select().from(Job.class)
                .where("TransportMasterId=?", TransportMasterId)
                .executeSingle();

    }

    public static List<Job> getAllJobs() {
        return new Select().from(Job.class)
                .where("Status=?", "COMPLETED").execute();
    }

    public static List<Job> getAllJobsSecureVehicle() {
        return new Select().from(Job.class)
                .where("Status=?", "COMPLETED").execute();
    }

    public static Job getSingleByReceiptNo(String receiptNo) {
        return new Select().from(Job.class)
                .where("ReceiptNo=?", receiptNo)
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
                .orderBy("OrderNo")
                .execute();

    }

    public static List<Job> getUnSecuredJobs() {
        return new Select().from(Job.class)
                .where("IsSecured=? AND Status=?", false, "COMPLETED")
                .orderBy("OrderNo")
                .execute();

    }

    public static List<Job> getSecuredJobs() {
        return new Select().from(Job.class)
                .where("IsSecured=? AND Status=?", true, "COMPLETED")
                .orderBy("OrderNo")
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
                .orderBy("OrderNo")
                .execute();

    }

    public static List<Job> getJobListByGroupKeyAndStatus(String GroupKey, String Status) {
        List<Job> jl = new Select().from(Job.class)
                .where("GroupKey=? AND status=? AND IsCollectionOrder=1", GroupKey, Status)
                .groupBy("BranchCode, PFunctionalCode")
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
                .groupBy("BranchCode, PFunctionalCode, GroupKey, ActualFromTime ,ActualToTime")
                .orderBy("SequenceNo")
                .execute();

        return jl;
    }

    public static List<Job> getPendingJobList() {
        List<Job> jl = new Select().from(Job.class)
                .where("status=?", "PENDING")
                .groupBy("GroupKey")
                .orderBy("SequenceNo")
                .execute();

        return jl;
    }

    public static List<Job> getJobListByType(int isDelivered, int isCollection, String GroupKey, String BranchCode, String PFunctionalCode, String startTime, String endTime) {
        List<Job> jl;

        if (PFunctionalCode == null) {
            jl = new Select().from(Job.class)
                    .where("status=? AND IsCollectionOrder=? and IsFloatDeliveryOrder=? and GroupKey=? AND BranchCode=? AND ActualFromTime=? " +
                                    "AND ActualToTime=?", "COMPLETED", isCollection, isDelivered,
                            GroupKey, BranchCode, startTime, endTime)
                    .groupBy("BranchCode, GroupKey, ActualFromTime, ActualToTime")
                    .orderBy("SequenceNo")
                    .execute();
        } else {
            jl = new Select().from(Job.class)
                    .where("status=? AND IsCollectionOrder=? and IsFloatDeliveryOrder=? and GroupKey=? AND BranchCode=? AND PFunctionalCode=? AND ActualFromTime=? " +
                                    " AND ActualToTime=?", "COMPLETED", isCollection, isDelivered,
                            GroupKey, BranchCode, PFunctionalCode, startTime, endTime)
                    .groupBy("BranchCode, PFunctionalCode, GroupKey, ActualFromTime, ActualToTime")
                    .orderBy("SequenceNo")
                    .execute();
        }


        return jl;
    }


    public static List<Job> getAllJobListByType() {
        List<Job> jl = new Select().from(Job.class)
                .groupBy("GroupKey, BranchCode, Status, PFunctionalCode, ActualFromTime, ActualToTime")
                .orderBy("SequenceNo")
                .execute();

        return jl;
    }


    public static List<Job> getJobListByTypeByGroupKey(int isDelivered, int isCollection, String groupKey, String branchCode, String startTime, String endTime) {
        List<Job> jl = new Select().from(Job.class)
                .where("status=? AND IsCollectionOrder=? and IsFloatDeliveryOrder=? and GroupKey=? and BranchCode=? and ActualFromTime=? and ActualToTime=?",
                        "COMPLETED", isCollection, isDelivered, groupKey, branchCode, startTime, endTime)
                .execute();

        return jl;
    }

    public static List<Job> getSpecificJobListByType(int isDelivered, int isCollection, int TransportMasterId) {
        List<Job> jl = new Select().from(Job.class)
                .where("status=? AND IsCollectionOrder=? and IsFloatDeliveryOrder=? and TransportMasterId=?", "COMPLETED", isCollection, isDelivered, TransportMasterId)
                .execute();

        return jl;
    }

    public static List<Job> getSpecificJobListByType(int isDelivered, int isCollection, String GroupKey) {
        List<Job> jl = new Select().from(Job.class)
                .where("status=? AND IsCollectionOrder=? and IsFloatDeliveryOrder=? and GroupKey=?", "COMPLETED", isCollection, isDelivered, GroupKey)
                .groupBy("BranchCode , GroupKey , PFunctionalCode")
                .execute();

        return jl;
    }

    public static List<Job> getSpecificJobListByType(int TransportMasterId) {
        List<Job> jl = new Select().from(Job.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
        return jl;
    }

    public static List<Job> getCollectionJobsOfPoint(String GroupKey) {
        return new Select().from(Job.class)
                .where("IsCollectionOrder=? AND GroupKey=?", 1, GroupKey)
                .execute();
    }

    public static List<Job> getCollectionJobsOfPoint(String GroupKey, String BranchCode, String PFunctionalCode, String status, String startTime, String endTime) {
        return new Select().from(Job.class)
                .where("IsCollectionOrder=? AND GroupKey=? AND Status=? AND BranchCode=? AND PFunctionalCode=? " +
                        "AND ActualFromTime=? AND ActualToTime=?", 1, GroupKey, status, BranchCode, PFunctionalCode, startTime, endTime)
                .execute();
    }


    public static List<Job> getIncompleteCollectionJobsOfPoint(String GroupKey) {
        return new Select().from(Job.class)
                .where("IsCollectionOrder=? AND GroupKey=? AND Status NOT IN ('COMPLETED')", 1, GroupKey)
                .execute();
    }

    public static List<Job> getFinishedIncompleteCollectionJobsOfPoint(String GroupKey, String BranchCode, String PFunctionalCode, String actualFromTime, String actualToTime) {
        return new Select().from(Job.class)
                .where("IsCollectionOrder=? AND GroupKey=? AND finished=? AND BranchCode=? AND PFunctionalCode=? AND ActualFromTime=? AND ActualToTime=? AND Status NOT IN ('COMPLETED')",
                        1, GroupKey, 1, BranchCode, PFunctionalCode, actualFromTime, actualToTime)
                .execute();
    }

    public static void setIncompleteCollectionCollected(String GroupKey) {

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


    public static List<Job> getPendingJobsOfPoint(String GroupKey) {
        return new Select().from(Job.class)
                .where("GroupKey=? AND Status NOT IN ('COMPLETED')", GroupKey)
                .execute();
    }

    public static List<Job> getDeliveryJobsOfPoint(String GroupKey) {
        List<Job> result;
        result = new Select().from(Job.class)
                .where("IsFloatDeliveryOrder=? AND GroupKey=?",
                        1, GroupKey)
                .execute();

        return result;
    }

    public static List<Job> getDeliveryJobsOfPoint(String GroupKey, String BranchCode, String PFunctionalCode, String actualFromTime, String actualToTime) {
        List<Job> result;

        if (PFunctionalCode == null) {
            result = new Select().from(Job.class)
                    .where("IsFloatDeliveryOrder=? AND GroupKey=? AND BranchCode=? And ActualFromTime=? and ActualToTime=?", 1, GroupKey, BranchCode, actualFromTime, actualToTime)
                    .execute();
        } else {

            result = new Select().from(Job.class)
                    .where("IsFloatDeliveryOrder=? AND GroupKey=? AND BranchCode=? AND PFunctionalCode=? And ActualFromTime=? and ActualToTime=?",
                            1, GroupKey, BranchCode, PFunctionalCode, actualFromTime, actualToTime)
                    .execute();
        }

        return result;
    }


    public static List<Job> getPendingDeliveryJobsOfPoint(String GroupKey, String BranchCode, String PFunctionalCode, String startTime, String endTime) {

        List<Job> result;

        if (PFunctionalCode == null) {
            result = new Select().from(Job.class)
                    .where("IsFloatDeliveryOrder=? AND GroupKey=? AND BranchCode=? AND ActualFromTime=? AND ActualToTime=? " +
                                    "AND Status NOT IN ('COMPLETED')", 1,
                            GroupKey, BranchCode, startTime, endTime)
                    .execute();
        } else {
            result = new Select().from(Job.class)
                    .where("IsFloatDeliveryOrder=? AND GroupKey=? AND BranchCode=? AND PFunctionalCode=?  AND ActualFromTime=? AND ActualToTime=? " +
                                    "AND Status NOT IN ('COMPLETED')",
                            1, GroupKey, BranchCode, PFunctionalCode, startTime, endTime)
                    .execute();

        }
        return result;
    }

    public static List<Job> getFinishedPendingDeliveryJobsOfPoint(String GroupKey, String BranchCode, String PFunctionalCode) {
        List<Job> result;

        if (PFunctionalCode == null) {
            result = new Select().from(Job.class)
                    .where("IsFloatDeliveryOrder=? AND GroupKey=? AND finished=? AND BranchCode=?  AND Status NOT IN ('COMPLETED')", 1, GroupKey, 1, BranchCode)
                    .execute();
        } else {

            result = new Select().from(Job.class)
                    .where("IsFloatDeliveryOrder=? AND GroupKey=? AND finished=? AND BranchCode=? AND PFunctionalCode=? AND Status NOT IN ('COMPLETED')", 1, GroupKey, 1, BranchCode, PFunctionalCode)
                    .execute();
        }

        return result;
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

    public static String getAllOrderNos(String groupKey, String BranchCode, String PFunctionalCode, String status, String PDFunctionalCode
            , String actualFromTime, String actualToTime) {
        String ordernos = "";
        PFunctionalCode = PFunctionalCode != null ? PFunctionalCode : "";
        PDFunctionalCode = PDFunctionalCode != null ? PDFunctionalCode : "";
        List<Job> jobs;
        if ("ALL".equals(status)) {
            jobs = new Select().from(Job.class)
                    .where("GroupKey=? AND BranchCode=? and (PFunctionalCode=? or PDFunctionalCode=?) and ActualFromTime=? and ActualToTime=?", groupKey, BranchCode
                            , PFunctionalCode, PDFunctionalCode, actualFromTime, actualToTime)
                    .execute();
        } else {
            if (PFunctionalCode.isEmpty() && PDFunctionalCode.isEmpty()) {
                jobs = new Select().from(Job.class)
                        .where("GroupKey=? AND BranchCode=? AND status=? and ActualFromTime=? and ActualToTime=?", groupKey, BranchCode, status, actualFromTime, actualToTime)
                        .execute();
            } else {
                jobs = new Select().from(Job.class)
                        .where("GroupKey=? AND BranchCode=? and  (PFunctionalCode=? or PDFunctionalCode=?) AND status=? and ActualFromTime=? and ActualToTime=?", groupKey
                                , BranchCode, PFunctionalCode, PDFunctionalCode, status, actualFromTime, actualToTime)
                        .execute();
            }
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

    public static String[] getAllOrderNosId(String groupKey, String BranchCode, String PFunctionalCode, String status, String PDFunctionalCode
            , String actualFromTime, String actualToTime) {
        PFunctionalCode = PFunctionalCode != null ? PFunctionalCode : "";
        PDFunctionalCode = PDFunctionalCode != null ? PDFunctionalCode : "";
        List<Job> jobs;
        if ("ALL".equals(status)) {
            jobs = new Select().from(Job.class)
                    .where("GroupKey=? AND BranchCode=? and (PFunctionalCode=? or PDFunctionalCode=?) and ActualFromTime=? and ActualToTime=?", groupKey, BranchCode
                            , PFunctionalCode, PDFunctionalCode, actualFromTime, actualToTime)
                    .execute();
        } else {
            if (PFunctionalCode.isEmpty() && PDFunctionalCode.isEmpty()) {
                jobs = new Select().from(Job.class)
                        .where("GroupKey=? AND BranchCode=? AND status=? and ActualFromTime=? and ActualToTime=?", groupKey, BranchCode, status, actualFromTime, actualToTime)
                        .execute();
            } else {
                jobs = new Select().from(Job.class)
                        .where("GroupKey=? AND BranchCode=? and  (PFunctionalCode=? or PDFunctionalCode=?) AND status=? and ActualFromTime=? and ActualToTime=?", groupKey
                                , BranchCode, PFunctionalCode, PDFunctionalCode, status, actualFromTime, actualToTime)
                        .execute();
            }
        }
        String[] orderlist = new String[jobs.size()];
        for (int i = 0; i < jobs.size(); i++) {
            orderlist[i] = Integer.toString(jobs.get(i).TransportMasterId);
        }

        return orderlist;
    }

    public static int getHigherTransportMasterId(String groupKey, String BranchCode, String PFunctionalCode, String status, String PDFunctionalCode
            , String actualFromTime, String actualToTime) {

        int transportMasterId;
        PFunctionalCode = PFunctionalCode != null ? PFunctionalCode : "";
        PDFunctionalCode = PDFunctionalCode != null ? PDFunctionalCode : "";
        List<Job> jobs;
        if ("ALL".equals(status)) {
            jobs = new Select().from(Job.class)
                    .where("GroupKey=? AND BranchCode=? and (PFunctionalCode=? or PDFunctionalCode=?) and ActualFromTime=? and ActualToTime=?", groupKey, BranchCode
                            , PFunctionalCode, PDFunctionalCode, actualFromTime, actualToTime)
                    .orderBy("OrderNo DESC")
                    .execute();
        } else {
            if (PFunctionalCode.isEmpty() && PDFunctionalCode.isEmpty()) {
                jobs = new Select().from(Job.class)
                        .where("GroupKey=? AND BranchCode=? AND status=? and ActualFromTime=? and ActualToTime=?", groupKey,
                                BranchCode, status, actualFromTime, actualToTime)
                        .orderBy("OrderNo DESC")
                        .execute();
            } else {
                jobs = new Select().from(Job.class)
                        .where("GroupKey=? AND BranchCode=? and  (PFunctionalCode=? or PDFunctionalCode=?) AND status=? and ActualFromTime=? and ActualToTime=?", groupKey
                                , BranchCode, PFunctionalCode, PDFunctionalCode, status, actualFromTime, actualToTime)
                        .orderBy("OrderNo DESC")
                        .execute();
            }
        }

        transportMasterId = jobs.get(0).TransportMasterId;

        return transportMasterId;
    }

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

    public static String getAllDeliveryOrderNos(String GroupKey, String BranchCode, String PFunctionalCode) {
        String ordernos = "";
        List<Job> jobs;
        jobs = new Select().from(Job.class)
                .where("GroupKey=? AND IsFloatDeliveryOrder=1 AND BranchCode=? AND PFunctionalCode=?", GroupKey, BranchCode, PFunctionalCode)
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

    public static void UpdateReceiptNo(int TransportMasterId, String receiptNo) {
        new Update(Job.class)
                .set("ReceiptNo=?", receiptNo)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static void UpdateSecureVehicle(int TransportMasterId) {
        new Update(Job.class)
                .set("IsSecured=?", 1)
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


    public static void UpdateReceiptNo(String groupKey, String branchCode, String PFunctionalCode, String startTime, String endTime, Context context) {
        int userId = Preferences.getInt("UserId", context);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String formattedDate = df.format(c);
        String formattedUserId = String.format("%04d", userId);
        Job job = null;

        if (PFunctionalCode == null) {
            job = new Select().from(Job.class)
                    .where("GroupKey=? and BranchCode=? AND ActualFromTime=? AND ActualToTime=?", groupKey, branchCode, startTime, endTime)
                    .executeSingle();
        } else {
            job = new Select().from(Job.class)
                    .where("GroupKey=? and BranchCode=? and PFunctionalCode=? AND ActualFromTime=? AND ActualToTime=?", groupKey, branchCode
                            , PFunctionalCode, startTime, endTime)
                    .executeSingle();
        }

        if (job.ReceiptNo == null) {
            if (PFunctionalCode == null) {
                new Update(Job.class)
                        .set("ReceiptNo=?", "RN" + formattedDate + formattedUserId)
                        .where("GroupKey=? and BranchCode=? AND ActualFromTime=? AND ActualToTime=?", groupKey, branchCode, startTime, endTime)
                        .execute();
            } else {
                new Update(Job.class)
                        .set("ReceiptNo=?", "RN" + formattedDate + formattedUserId)
                        .where("GroupKey=? and BranchCode=? and PFunctionalCode=?  AND ActualFromTime=? AND ActualToTime=?", groupKey, branchCode, PFunctionalCode, startTime, endTime)
                        .execute();
            }
        }
    }

    public static void UpdateReceiptNoDelivery(String groupKey, String branchCode, String PFunctionalCode, Context context) {
        int userId = Preferences.getInt("UserId", context);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String formattedDate = df.format(c);

        Job job = null;

        if (PFunctionalCode == null) {
            job = new Select().from(Job.class)
                    .where("GroupKey=? and BranchCode=?", groupKey, branchCode)
                    .executeSingle();
        } else {
            job = new Select().from(Job.class)
                    .where("GroupKey=? and BranchCode=? and PFunctionalCode=?", groupKey, branchCode
                            , PFunctionalCode)
                    .executeSingle();
        }

        if (job.ReceiptNo == null) {
            if (PFunctionalCode == null) {
                new Update(Job.class)
                        .set("ReceiptNo=?", "RN" + formattedDate + userId)
                        .where("GroupKey=? and BranchCode=?", groupKey, branchCode)
                        .execute();
            } else {
                new Update(Job.class)
                        .set("ReceiptNo=?", "RN" + formattedDate + userId)
                        .where("GroupKey=? and BranchCode=? and PFunctionalCode=?", groupKey, branchCode, PFunctionalCode)
                        .execute();
            }
        }
    }

    public static void UpdateNameAndStaffID(int TransportMasterId, String name, String id) {
        new Update(Job.class)
                .set("CName=?, StaffID=? ", name, id)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

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
        if (job.CanCollectedBag) collection_types.add("Sealed Bag");
        if (job.CanCollectedEnvelop) collection_types.add("Envelope(s)");
        if (job.CanCollectedEnvelopInBag) collection_types.add("Envelope(s) In Bag");
        if (job.CanCollectedBox) collection_types.add("Box");
        if (job.CanCollectPallet) collection_types.add("Pallet");
        if (job.CanCollectCoinBox) collection_types.add("Coin Bag");
        if (job.CanCollectWagon) collection_types.add("Wagon");
        if (job.CanCollectConsignmentInBag) collection_types.add("Consignment(s) In Bag");


        return collection_types;
    }

    public static void saveItemsToCage(int TransportMasterId, String cageNo, String cageSeal) {
        Bags.setCageNoCageSeal(TransportMasterId, cageNo, cageSeal);
        Box.setCageNoCageSeal(TransportMasterId, cageNo, cageSeal);

        EnvelopeBag.setCageNoCageSeal(TransportMasterId, cageNo, cageSeal);
        List<EnvelopeBag> envelopebags = EnvelopeBag.getByTransportMasterId(TransportMasterId);
        for (int a = 0; a < envelopebags.size(); a++) {
            if (envelopebags.get(a).envolpeType.equals("Envelopes"))
                Envelope.setCageNoCageSeal(envelopebags.get(a).getId(), cageNo, cageSeal);
        }

        ConsignmentBag.setCageNoCageSeal(TransportMasterId, cageNo, cageSeal);
        List<ConsignmentBag> consignmentBags = ConsignmentBag.getByTransportMasterId(TransportMasterId);
        for (int a = 0; a < consignmentBags.size(); a++) {
            Consignment.setCageNoCageSeal(consignmentBags.get(a).getId(), cageNo, cageSeal);
        }

        BoxBag.setCageNoCageSeal(TransportMasterId, cageNo, cageSeal);
        Wagon.setCageNoCageSeal(TransportMasterId, cageNo, cageSeal);
    }

    public static List<SecureObject> getSecureObjects(int TransportMasterId) {
        List<SecureObject> secureObjects = new ArrayList<>();

        List<Bags> bags = Bags.getByTransportMasterIdWithOutCage(TransportMasterId);
        for (int i = 0; i < bags.size(); i++) {
            SecureObject secureObject = new SecureObject();
            secureObject.IsSealed = true;
            secureObject.Type = "Bag";
            secureObject.Barcode = bags.get(i).firstbarcode;
            if (!bags.get(i).secondbarcode.isEmpty()) {
                secureObject.SecondBarcode = bags.get(i).secondbarcode;
            }
            secureObjects.add(secureObject);

        }

        List<Wagon> wagons = Wagon.getByTransportMasterIdWithOutCage(TransportMasterId);
        if (!wagons.isEmpty()) {
            for (int i = 0; i < wagons.size(); i++) {
                SecureObject secureObject = new SecureObject();
                secureObject.IsSealed = true;
                secureObject.Type = "Wagon";
                secureObject.Barcode = wagons.get(i).firstbarcode;
                if (!wagons.get(i).secondbarcode.isEmpty()) {
                    secureObject.SecondBarcode = wagons.get(i).secondbarcode;
                }
                secureObjects.add(secureObject);

            }
        }

        List<EnvelopeBag> envelopebags = EnvelopeBag.getByTransportMasterIdWithOutCage(TransportMasterId);
        for (int i = 0; i < envelopebags.size(); i++) {
            List<Envelope> envelopes = Envelope.getByBagId(envelopebags.get(i).getId());
            if (envelopebags.get(i).envolpeType.equals("Envelopes")) {
                for (int j = 0; j < envelopes.size(); j++) {
                    SecureObject secureObjectEnvelope = new SecureObject();
                    secureObjectEnvelope.IsSealed = true;
                    secureObjectEnvelope.Barcode = envelopes.get(j).barcode;
                    secureObjectEnvelope.Type = "Envelopes";
                    secureObjects.add(secureObjectEnvelope);
                }
            } else {
                SecureObject secureObject = new SecureObject();
                secureObject.IsSealed = true;
                secureObject.Type = "EnvelopeBag";
                secureObject.Barcode = envelopebags.get(i).bagcode;
                secureObjects.add(secureObject);
            }
        }

        List<ConsignmentBag> consignmentBags = ConsignmentBag.getByTransportMasterIdWithOutCage(TransportMasterId);
        for (int i = 0; i < consignmentBags.size(); i++) {
            List<Consignment> consignments = Consignment.getByTransportMasterIdWithOutCage(consignmentBags.get(i).getId());

            SecureObject secureObject = new SecureObject();
            secureObject.IsSealed = true;
            secureObject.Type = "ConsignmentBag";
            secureObject.Barcode = consignmentBags.get(i).bagcode;
            secureObjects.add(secureObject);
        }


        List<Box> boxes = Box.getByTransportMasterIdWithOutCage(TransportMasterId);
        for (int i = 0; i < boxes.size(); i++) {
            SecureObject secureObject = new SecureObject();
            secureObject.IsSealed = false;
            secureObject.Type = "Box";
            secureObject.Quantity = boxes.get(i).count;
            if ((boxes.get(i).CoinSeriesId) == 0) {
                secureObject.Barcode = boxes.get(i).ProductName;
            } else {
                secureObject.Barcode = boxes.get(i).ProductName + "(" + boxes.get(i).CoinSeries + ")";
            }
            secureObjects.add(secureObject);
        }

        List<BoxBag> boxBags = BoxBag.getByTransportMasterIdWithOutCage(TransportMasterId);
        for (int i = 0; i < boxBags.size(); i++) {

            SecureObject secureObject = new SecureObject();
            secureObject.IsSealed = true;
            secureObject.Type = "CoinBag";
            secureObject.Barcode = boxBags.get(i).bagcode;
            secureObjects.add(secureObject);
        }

        int palletCount = getSingle(TransportMasterId).palletCount;
        if (palletCount > 0) {
            SecureObject secureObject = new SecureObject();
            secureObject.IsSealed = false;
            secureObject.Type = "Pallet";
            secureObject.Barcode = "Pallet";
            secureObject.Quantity = palletCount;
            secureObjects.add(secureObject);
        }

        return secureObjects;
    }

    public static List<Summary> getCollectionSummaryWithoutCage(int TransportMasterId) {
        List<Summary> collection_summary = new ArrayList<>();
        List<Bags> bags = Bags.getByTransportMasterIdWithOutCage(TransportMasterId);
        for (int i = 0; i < bags.size(); i++) {
            Summary collectionSummary = new Summary();
            collectionSummary.Collection_type = "Bag";
            collectionSummary.id = bags.get(i).getId();
            collectionSummary.Head = "Sealed Bag";
            String message = bags.get(i).firstbarcode;
            if (!bags.get(i).secondbarcode.isEmpty())
                message += ", " + bags.get(i).secondbarcode;
            collectionSummary.Message = message;
            collection_summary.add(collectionSummary);
        }

        List<Wagon> wagons = Wagon.getByTransportMasterIdWithOutCage(TransportMasterId);
        for (int a = 0; a < wagons.size(); a++) {
            Summary collectionSummary = new Summary();
            collectionSummary.Collection_type = "Wagon";
            collectionSummary.id = wagons.get(a).getId();
            collectionSummary.Head = "Wagon";
            String message = wagons.get(a).firstbarcode;
            if (!wagons.get(a).secondbarcode.isEmpty())
                message += ", " + wagons.get(a).secondbarcode;
            collectionSummary.Message = message;
            collection_summary.add(collectionSummary);
        }

        List<EnvelopeBag> envelopebags = EnvelopeBag.getByTransportMasterIdWithOutCage(TransportMasterId);
        for (int i = 0; i < envelopebags.size(); i++) {
            List<Envelope> envelopes = Envelope.getByTransportMasterIdWithOutCage(envelopebags.get(i).getId());
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
                if (message.length() > 0)
                    message.delete(message.length() - 2, message.length());
                Summary collectionSummary = new Summary();
                collectionSummary.Collection_type = "EnvelopeBag";
                collectionSummary.id = envelopebags.get(i).getId();
                collectionSummary.Head = "Envelope(s) In Bag (" + envelopebags.get(i).bagcode + ")";
                collectionSummary.Message = message.toString();
                collection_summary.add(collectionSummary);
            }
        }

        List<ConsignmentBag> consignmentBags = ConsignmentBag.getByTransportMasterIdWithOutCage(TransportMasterId);
        for (int i = 0; i < consignmentBags.size(); i++) {
            List<Consignment> consignments = Consignment.getByTransportMasterIdWithOutCage(consignmentBags.get(i).getId());

            StringBuilder message = new StringBuilder();
            for (int j = 0; j < consignments.size(); j++) {
                message.append(consignments.get(j).barcode);
                message.append(" : ");
            }
            if (message.length() > 0)
                message.delete(message.length() - 2, message.length());
            Summary collectionSummary = new Summary();
            collectionSummary.Collection_type = "ConsignmentBag";
            collectionSummary.id = consignmentBags.get(i).getId();
            collectionSummary.Head = "Consignment(s) In Bag (" + consignmentBags.get(i).bagcode + ")";
            collectionSummary.Message = message.toString();
            collection_summary.add(collectionSummary);
        }

        List<Box> boxes = Box.getByTransportMasterIdWithOutCage(TransportMasterId);
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

        List<BoxBag> boxBags = BoxBag.getByTransportMasterIdWithOutCage(TransportMasterId);
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

        return collection_summary;
    }

    public static List<Summary> getCollectionSummary(int TransportMasterId) {
        List<Summary> collection_summary = new ArrayList<>();
        List<Bags> bags = Bags.getByTransportMasterId(TransportMasterId);
        for (int i = 0; i < bags.size(); i++) {
            Summary collectionSummary = new Summary();
            collectionSummary.Collection_type = "Bag";
            collectionSummary.id = bags.get(i).getId();
            collectionSummary.Head = "Sealed Bag";
            String message = bags.get(i).firstbarcode;
            if (!bags.get(i).secondbarcode.isEmpty())
                message += ", " + bags.get(i).secondbarcode;
            collectionSummary.Message = message;
            collection_summary.add(collectionSummary);
        }

        List<Wagon> wagons = Wagon.getByTransportMasterId(TransportMasterId);
        for (int a = 0; a < wagons.size(); a++) {
            Summary collectionSummary = new Summary();
            collectionSummary.Collection_type = "Wagon";
            collectionSummary.id = wagons.get(a).getId();
            collectionSummary.Head = "Wagon";
            String message = wagons.get(a).firstbarcode;
            if (!wagons.get(a).secondbarcode.isEmpty())
                message += ", " + wagons.get(a).secondbarcode;
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
                if (message.length() > 0)
                    message.delete(message.length() - 2, message.length());
                Summary collectionSummary = new Summary();
                collectionSummary.Collection_type = "EnvelopeBag";
                collectionSummary.id = envelopebags.get(i).getId();
                collectionSummary.Head = "Envelope(s) In Bag (" + envelopebags.get(i).bagcode + ")";
                collectionSummary.Message = message.toString();
                collection_summary.add(collectionSummary);
            }
        }

        List<ConsignmentBag> consignmentBags = ConsignmentBag.getByTransportMasterId(TransportMasterId);
        for (int i = 0; i < consignmentBags.size(); i++) {
            List<Consignment> consignments = Consignment.getByTransportMasterIdWithOutCage(consignmentBags.get(i).getId());

            StringBuilder message = new StringBuilder();
            for (int j = 0; j < consignments.size(); j++) {
                message.append(consignments.get(j).barcode);
                message.append(" : ");
            }
            if (message.length() > 0)
                message.delete(message.length() - 2, message.length());
            Summary collectionSummary = new Summary();
            collectionSummary.Collection_type = "ConsignmentBag";
            collectionSummary.id = consignmentBags.get(i).getId();
            collectionSummary.Head = "Consignment(s) In Bag (" + consignmentBags.get(i).bagcode + ")";
            collectionSummary.Message = message.toString();
            collection_summary.add(collectionSummary);
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

        return collection_summary;
    }

    private static boolean checkIfItemsExists(String barcode, List<Items> items) {
        boolean indicator = false;

        for (Items item : items) {
            if (item.getSummary().equalsIgnoreCase(barcode)) {
                return true;
            }
        }

        return indicator;
    }

    public static List<Items> getCageCollectionSummary(int TransportMasterId, String cageNo, String cageSeal, List<Items> items) {

        List<Bags> bags = Bags.getByTransportMasterIdWithCage(TransportMasterId, cageNo, cageSeal);
        for (int i = 0; i < bags.size(); i++) {

            Items collectionSummary = new Items();
            collectionSummary.setId(bags.get(i).getId());
            collectionSummary.setHead("Sealed Bag");
            String message = bags.get(i).firstbarcode;
            if (!bags.get(i).secondbarcode.isEmpty() && bags.get(i).secondbarcode != null && !bags.get(i).secondbarcode.equalsIgnoreCase("null"))
                message += ", " + bags.get(i).secondbarcode;
            collectionSummary.setSummary(message);

            if (!checkIfItemsExists(message, items))
                items.add(collectionSummary);
        }

        List<Wagon> wagons = Wagon.getByTransportMasterIdWithCage(TransportMasterId, cageNo, cageSeal);
        for (int a = 0; a < wagons.size(); a++) {
            Items collectionSummary = new Items();
            collectionSummary.setId(wagons.get(a).getId());
            collectionSummary.setHead("Wagon");
            String message = wagons.get(a).firstbarcode;
            if (!wagons.get(a).secondbarcode.isEmpty() && wagons.get(a).secondbarcode != null && !wagons.get(a).secondbarcode.equalsIgnoreCase("null"))
                message += ", " + wagons.get(a).secondbarcode;
            collectionSummary.setSummary(message);
            if (!checkIfItemsExists(message, items))
                items.add(collectionSummary);
        }

        List<EnvelopeBag> envelopebags = EnvelopeBag.getByTransportMasterIdWithCage(TransportMasterId, cageNo, cageSeal);
        for (int i = 0; i < envelopebags.size(); i++) {
            List<Envelope> envelopes = Envelope.getByTransportMasterIdWithCage(envelopebags.get(i).getId(), cageNo, cageSeal);
            if (envelopebags.get(i).envolpeType.equals("Envelopes")) {
                for (int j = 0; j < envelopes.size(); j++) {
                    Items collectionSummary = new Items();
                    collectionSummary.setId(envelopes.get(j).getId());
                    collectionSummary.setHead("Envelope");
                    collectionSummary.setSummary(envelopes.get(j).barcode);
                    if (!checkIfItemsExists(envelopes.get(j).barcode, items))
                        items.add(collectionSummary);
                }
            } else {
                StringBuilder message = new StringBuilder();
                for (int j = 0; j < envelopes.size(); j++) {
                    message.append(envelopes.get(j).barcode);
                    message.append(", ");
                }
                if (message.length() > 0)
                    message.delete(message.length() - 2, message.length());
                Items collectionSummary = new Items();
                collectionSummary.setId(envelopebags.get(i).getId());
                collectionSummary.setHead("Envelope(s) In Bag (" + envelopebags.get(i).bagcode + ")");
                collectionSummary.setSummary(message.toString());
                if (!checkIfItemsExists(message.toString(), items))
                    items.add(collectionSummary);
            }
        }

        List<ConsignmentBag> consignmentBags = ConsignmentBag.getByTransportMasterIdWithCage(TransportMasterId, cageNo, cageSeal);
        for (int i = 0; i < consignmentBags.size(); i++) {
            List<Consignment> consignments = Consignment.getByTransportMasterIdWithCage(consignmentBags.get(i).getId(), cageNo, cageSeal);

            StringBuilder message = new StringBuilder();
            for (int j = 0; j < consignments.size(); j++) {
                message.append(consignments.get(j).barcode);
                message.append(" : ");
            }
            if (message.length() > 0)
                message.delete(message.length() - 2, message.length());
            Items collectionSummary = new Items();
            collectionSummary.setId(consignmentBags.get(i).getId());
            collectionSummary.setHead("Consignment(s) In Bag (" + consignmentBags.get(i).bagcode + ")");
            collectionSummary.setSummary(message.toString());
            if (!checkIfItemsExists(message.toString(), items))
                items.add(collectionSummary);
        }

        List<Box> boxes = Box.getByTransportMasterIdWithCage(TransportMasterId, cageNo, cageSeal);
        for (int i = 0; i < boxes.size(); i++) {
            Items collectionSummary = new Items();
            collectionSummary.setId(boxes.get(i).getId());
            collectionSummary.setHead("Box");
            collectionSummary.setQty(boxes.get(i).count);
            String message = "";
            if ((boxes.get(i).CoinSeriesId) == 0) {
                message = boxes.get(i).ProductName + "*" + boxes.get(i).count;
            } else {
                message = boxes.get(i).ProductName + "(" + boxes.get(i).CoinSeries + ")*" + boxes.get(i).count;
            }
            collectionSummary.setSummary(message);
            if (!checkIfItemsExists(message, items))
                items.add(collectionSummary);
        }

        List<BoxBag> boxBags = BoxBag.getByTransportMasterIdWithCage(TransportMasterId, cageNo, cageSeal);
        for (int i = 0; i < boxBags.size(); i++) {
            Items collectionSummary = new Items();
            collectionSummary.setId(boxBags.get(i).getId());
            collectionSummary.setHead("Coin Bag(" + boxBags.get(i).bagcode + ")");
//            collectionSummary.Message = boxBags.get(i).ProductName;
            String message = "";
            if (boxBags.get(i).CoinSeriesId == 0)
                message = boxBags.get(i).ProductName;
            else
                message = boxBags.get(i).ProductName + "(" + boxBags.get(i).CoinSeries + ")";
            collectionSummary.setSummary(message);

            if (!checkIfItemsExists(message, items))
                items.add(collectionSummary);
        }

        int palletCount = getSingle(TransportMasterId).palletCount;
        if (palletCount > 0) {
            Items collectionSummary = new Items();
            collectionSummary.setId(Long.parseLong(String.valueOf(TransportMasterId)));
            collectionSummary.setHead("Pallet");
            collectionSummary.setSummary(String.valueOf(palletCount));
        }

        return items;
    }


    public static List<Items> getCageCollectionSummary(int TransportMasterId, String cageNo, String cageSeal) {
        List<Items> items = new ArrayList<>();
        List<Bags> bags = Bags.getByTransportMasterIdWithCage(TransportMasterId, cageNo, cageSeal);
        for (int i = 0; i < bags.size(); i++) {
            Items collectionSummary = new Items();
            collectionSummary.setId(bags.get(i).getId());
            collectionSummary.setHead("Sealed Bag");
            String message = bags.get(i).firstbarcode;
            if (!bags.get(i).secondbarcode.isEmpty() && bags.get(i).secondbarcode != null && !bags.get(i).secondbarcode.equalsIgnoreCase("null"))
                message += ", " + bags.get(i).secondbarcode;
            collectionSummary.setSummary(message);
            items.add(collectionSummary);
        }

        List<Wagon> wagons = Wagon.getByTransportMasterIdWithCage(TransportMasterId, cageNo, cageSeal);
        for (int a = 0; a < wagons.size(); a++) {
            Items collectionSummary = new Items();
            collectionSummary.setId(wagons.get(a).getId());
            collectionSummary.setHead("Wagon");
            String message = wagons.get(a).firstbarcode;
            if (!wagons.get(a).secondbarcode.isEmpty() && wagons.get(a).secondbarcode != null && !wagons.get(a).secondbarcode.equalsIgnoreCase("null"))
                message += ", " + wagons.get(a).secondbarcode;
            collectionSummary.setSummary(message);
            items.add(collectionSummary);
        }

        List<EnvelopeBag> envelopebags = EnvelopeBag.getByTransportMasterIdWithCage(TransportMasterId, cageNo, cageSeal);
        for (int i = 0; i < envelopebags.size(); i++) {
            List<Envelope> envelopes = Envelope.getByTransportMasterIdWithCage(envelopebags.get(i).getId(), cageNo, cageSeal);
            if (envelopebags.get(i).envolpeType.equals("Envelopes")) {
                for (int j = 0; j < envelopes.size(); j++) {
                    Items collectionSummary = new Items();
                    collectionSummary.setId(envelopes.get(j).getId());
                    collectionSummary.setHead("Envelope");
                    collectionSummary.setSummary(envelopes.get(j).barcode);
                    items.add(collectionSummary);
                }
            } else {
                StringBuilder message = new StringBuilder();
                for (int j = 0; j < envelopes.size(); j++) {
                    message.append(envelopes.get(j).barcode);
                    message.append(", ");
                }
                if (message.length() > 0)
                    message.delete(message.length() - 2, message.length());
                Items collectionSummary = new Items();
                collectionSummary.setId(envelopebags.get(i).getId());
                collectionSummary.setHead("Envelope(s) In Bag (" + envelopebags.get(i).bagcode + ")");
                collectionSummary.setSummary(message.toString());
                items.add(collectionSummary);
            }
        }

        List<ConsignmentBag> consignmentBags = ConsignmentBag.getByTransportMasterIdWithCage(TransportMasterId, cageNo, cageSeal);
        for (int i = 0; i < consignmentBags.size(); i++) {
            List<Consignment> consignments = Consignment.getByTransportMasterIdWithCage(consignmentBags.get(i).getId(), cageNo, cageSeal);

            StringBuilder message = new StringBuilder();
            for (int j = 0; j < consignments.size(); j++) {
                message.append(consignments.get(j).barcode);
                message.append(" : ");
            }
            if (message.length() > 0)
                message.delete(message.length() - 2, message.length());
            Items collectionSummary = new Items();
            collectionSummary.setId(consignmentBags.get(i).getId());
            collectionSummary.setHead("Consignment(s) In Bag (" + consignmentBags.get(i).bagcode + ")");
            collectionSummary.setSummary(message.toString());
            items.add(collectionSummary);
        }

        List<Box> boxes = Box.getByTransportMasterIdWithCage(TransportMasterId, cageNo, cageSeal);
        for (int i = 0; i < boxes.size(); i++) {
            Items collectionSummary = new Items();
            collectionSummary.setId(boxes.get(i).getId());
            collectionSummary.setHead("Box");
            collectionSummary.setQty(boxes.get(i).count);
            if ((boxes.get(i).CoinSeriesId) == 0) {
                collectionSummary.setSummary(boxes.get(i).ProductName + "*" + boxes.get(i).count);
            } else {
                collectionSummary.setSummary(boxes.get(i).ProductName + "(" + boxes.get(i).CoinSeries + ")*" + boxes.get(i).count);
            }
            items.add(collectionSummary);
        }

        List<BoxBag> boxBags = BoxBag.getByTransportMasterIdWithCage(TransportMasterId, cageNo, cageSeal);
        for (int i = 0; i < boxBags.size(); i++) {
            Items collectionSummary = new Items();
            collectionSummary.setId(boxBags.get(i).getId());
            collectionSummary.setHead("Coin Bag(" + boxBags.get(i).bagcode + ")");
//            collectionSummary.Message = boxBags.get(i).ProductName;
            if (boxBags.get(i).CoinSeriesId == 0)
                collectionSummary.setSummary(boxBags.get(i).ProductName);
            else
                collectionSummary.setSummary(boxBags.get(i).ProductName + "(" + boxBags.get(i).CoinSeries + ")");
            items.add(collectionSummary);
        }

        int palletCount = getSingle(TransportMasterId).palletCount;
        if (palletCount > 0) {
            Items collectionSummary = new Items();
            collectionSummary.setId(Long.parseLong(String.valueOf(TransportMasterId)));
            collectionSummary.setHead("Pallet");
            collectionSummary.setSummary(String.valueOf(palletCount));
            items.add(collectionSummary);
        }

        return items;
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
                    envelopDeno.setType("EnvelopInBag");
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


    @SuppressLint("NewApi")
    public static <
            T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @SuppressLint("NewApi")
    public static int getSelectedPrintContentCounter(String groupKey, int isDelivery, String
            branchCode, String startTime, String endTime) {
        Constants.BOX_QUANTITY = 0;
        List<Content> printContent = new ArrayList<>();
        int isCollection = isDelivery == 0 ? 1 : 0;
        List<Job> list = Job.getJobListByTypeByGroupKey(isDelivery, isCollection, groupKey, branchCode, startTime, endTime);

        int bagQty = 0, boxQty = 0, coinBagQty = 0, wagonQty = 0;
        List<String> boxSealNoList = new ArrayList<>();
        List<String> boxBagSealNoList = new ArrayList<>();
        List<String> bagSealNoList = new ArrayList<>();
        List<String> checker = new ArrayList<>();
        List<String> wagonSealNoList = new ArrayList<>();

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
                Constants.BOX_QUANTITY += palletCount;
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

                    if (checker.size() > 0) {
                        if (checker.contains(message))
                            break;
                    }
                    checker.add(message);
                    bagQty++;
                    bagSealNoList.add(message);

                }
            }

            //wagon
            List<Wagon> wagons = Wagon.getByTransportMasterId(transportMasterId);
            if (!wagons.isEmpty()) {
                for (int i = 0; i < wagons.size(); i++) {
                    String message = wagons.get(i).firstbarcode;
                    if (!wagons.get(i).secondbarcode.isEmpty())
                        message += ", " + wagons.get(i).secondbarcode;

                    if (checker.size() > 0) {
                        if (checker.contains(message))
                            break;
                    }
                    checker.add(message);
                    wagonQty++;
                    wagonSealNoList.add(message);

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
                        envelopDeno.setBagName("Sealed Bag " + enveInBagQty);
                        envelopDeno.setSealNo(envelopeBag.bagcode);
                        envelopDeno.setEnvelopsList(envelopList);
                        envelopDeno.setType("EnvelopInBag");
                        envelopDenomination.add(envelopDeno);

                    }
                }
                if (envQty > 0) {
                    envelopContent.setDescription("Envelopes");
                    envelopContent.setQty(envQty);
                    Constants.BOX_QUANTITY += envQty;
                    envelopContent.setSealNoList(envSealNoList);
                    printContent.add(envelopContent);
                }
                if (enveInBagQty > 0) {
                    envelopInBagContent.setDescription("ENVELOP IN BAG");
                    envelopInBagContent.setQty(enveInBagQty);
                    Constants.BOX_QUANTITY += enveInBagQty;
                    envelopInBagContent.setDenominationList(envelopDenomination);
                    printContent.add(envelopInBagContent);
                }

            }

            List<ConsignmentBag> consignmentBags = ConsignmentBag.getByTransportMasterId(transportMasterId);
            if (!consignmentBags.isEmpty()) {
                Content consignmentContent = new Content();
                Content consignmentInBagContent = new Content();
                List<String> consignmentSealNoList = new ArrayList<>();
                List<Denomination> consignmentDenomination = new ArrayList<>();
                int consignmentQty = 0;
                int consignmentInBagQty = 0;


                for (int j = 0; j < consignmentBags.size(); j++) {
                    ConsignmentBag consignmentBag = consignmentBags.get(j);
                    List<Consignment> consignments = Consignment.getByBagId(consignmentBag.getId());
                    if (consignmentBag.bagcode.equals("none")) {
                        for (int k = 0; k < consignments.size(); k++) {
                            JsonObject Detail = new JsonObject();
                            Detail.addProperty("ItemType", "Consignments");
                            consignmentSealNoList.add(consignments.get(k).barcode);
                            consignmentQty++;
                        }
                    } else {
                        List<String> consignmentList = new ArrayList<>();
                        for (int k = 0; k < consignments.size(); k++) {
                            consignmentList.add(consignments.get(k).barcode);
                        }
                        Denomination consignmentDeno = new Denomination();
                        consignmentInBagQty++;
                        consignmentDeno.setBagName("Sealed Bag " + consignmentInBagQty);
                        consignmentDeno.setSealNo(consignmentBag.bagcode);
                        consignmentDeno.setEnvelopsList(consignmentList);
                        consignmentDeno.setType("ConsignmentInBag");
                        consignmentDenomination.add(consignmentDeno);

                    }
                }
                if (consignmentQty > 0) {
                    consignmentContent.setDescription("Consignment");
                    consignmentContent.setQty(consignmentQty);
                    Constants.BOX_QUANTITY += consignmentQty;
                    consignmentContent.setSealNoList(consignmentSealNoList);
                    printContent.add(consignmentContent);
                }
                if (consignmentInBagQty > 0) {
                    consignmentInBagContent.setDescription("CONSIGNMENT IN BAG");
                    consignmentInBagContent.setQty(consignmentInBagQty);
                    Constants.BOX_QUANTITY += consignmentInBagQty;
                    consignmentInBagContent.setDenominationList(consignmentDenomination);
                    printContent.add(consignmentInBagContent);
                }

            }
        }
        if (!bagSealNoList.isEmpty()) {
            Content bagContent = new Content();
            bagContent.setDescription("Sealed Bag");
            bagContent.setQty(bagQty);
            bagContent.setSealNoList(bagSealNoList);
            printContent.add(bagContent);
        }

        if (!wagonSealNoList.isEmpty()) {
            Content wagonContent = new Content();
            wagonContent.setDescription("Wagon");
            wagonContent.setQty(wagonQty);
            wagonContent.setSealNoList(wagonSealNoList);
            printContent.add(wagonContent);
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

        Constants.BOX_QUANTITY += boxQty;
        Constants.BOX_QUANTITY += coinBagQty;
        Constants.BOX_QUANTITY += bagQty;
        Constants.BOX_QUANTITY += wagonQty;
        return Constants.BOX_QUANTITY;
    }

    @SuppressLint("NewApi")
    public static List<Content> getSelectedPrintContent(String groupKey, int isDelivery, String
            branchCode, String startTime, String endTime) {

        List<Content> printContent = new ArrayList<>();
        int isCollection = isDelivery == 0 ? 1 : 0;
        List<Job> list = Job.getJobListByTypeByGroupKey(isDelivery, isCollection, groupKey, branchCode, startTime, endTime);

        int bagQty = 0, boxQty = 0, coinBagQty = 0, wagonQty = 0;
        List<String> boxSealNoList = new ArrayList<>();
        List<String> boxBagSealNoList = new ArrayList<>();
        List<String> bagSealNoList = new ArrayList<>();
        List<String> checker = new ArrayList<>();
        List<String> wagonSealNoList = new ArrayList<>();

        for (int a = 0; a < list.size(); a++) {
            int transportMasterId = list.get(a).TransportMasterId;
            //adding the box
            List<Box> boxes = Box.getByTransportMasterIdWithOutCage(transportMasterId);
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
            List<BoxBag> boxBags = BoxBag.getByTransportMasterIdWithOutCage(transportMasterId);
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
            List<Bags> bags = Bags.getByTransportMasterIdWithOutCage(transportMasterId);
            if (!bags.isEmpty()) {
                for (int i = 0; i < bags.size(); i++) {
                    String message = bags.get(i).firstbarcode;
                    if (!bags.get(i).secondbarcode.isEmpty() && bags.get(i).secondbarcode != null && !bags.get(i).secondbarcode.equalsIgnoreCase("null"))
                        message += ", " + bags.get(i).secondbarcode;

                    if (checker.size() > 0) {
                        if (checker.contains(message))
                            break;
                    }
                    checker.add(message);
                    bagQty++;
                    bagSealNoList.add(message);

                }
            }

            //wagon
            List<Wagon> wagons = Wagon.getByTransportMasterIdWithOutCage(transportMasterId);
            if (!wagons.isEmpty()) {
                for (int i = 0; i < wagons.size(); i++) {
                    String message = wagons.get(i).firstbarcode;
                    if (!wagons.get(i).secondbarcode.isEmpty())
                        message += ", " + wagons.get(i).secondbarcode;

                    if (checker.size() > 0) {
                        if (checker.contains(message))
                            break;
                    }
                    checker.add(message);
                    wagonQty++;
                    wagonSealNoList.add(message);

                }
            }


            List<EnvelopeBag> envelopeBags = EnvelopeBag.getByTransportMasterIdWithOutCage(transportMasterId);
            if (!envelopeBags.isEmpty()) {
                Content envelopContent = new Content();
                Content envelopInBagContent = new Content();
                List<String> envSealNoList = new ArrayList<>();
                List<Denomination> envelopDenomination = new ArrayList<>();
                int envQty = 0;
                int enveInBagQty = 0;


                for (int j = 0; j < envelopeBags.size(); j++) {
                    EnvelopeBag envelopeBag = envelopeBags.get(j);
                    List<Envelope> envelopes = Envelope.getByTransportMasterIdWithOutCage(envelopeBag.getId());
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
                        envelopDeno.setBagName("Sealed Bag " + enveInBagQty);
                        envelopDeno.setSealNo(envelopeBag.bagcode);
                        envelopDeno.setEnvelopsList(envelopList);
                        envelopDeno.setType("EnvelopInBag");
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

            List<ConsignmentBag> consignmentBags = ConsignmentBag.getByTransportMasterIdWithOutCage(transportMasterId);
            if (!consignmentBags.isEmpty()) {
                Content consignmentContent = new Content();
                Content consignmentInBagContent = new Content();
                List<String> consignmentSealNoList = new ArrayList<>();
                List<Denomination> consignmentDenomination = new ArrayList<>();
                int consignmentQty = 0;
                int consignmentInBagQty = 0;


                for (int j = 0; j < consignmentBags.size(); j++) {
                    ConsignmentBag consignmentBag = consignmentBags.get(j);
                    List<Consignment> consignments = Consignment.getByBagId(consignmentBag.getId());
                    if (consignmentBag.bagcode.equals("none")) {
                        for (int k = 0; k < consignments.size(); k++) {
                            JsonObject Detail = new JsonObject();
                            Detail.addProperty("ItemType", "Consignments");
                            consignmentSealNoList.add(consignments.get(k).barcode);
                            consignmentQty++;
                        }
                    } else {
                        List<String> consignmentList = new ArrayList<>();
                        for (int k = 0; k < consignments.size(); k++) {
                            consignmentList.add(consignments.get(k).barcode);
                        }
                        Denomination consignmentDeno = new Denomination();
                        consignmentInBagQty++;
                        consignmentDeno.setBagName("Sealed Bag " + consignmentInBagQty);
                        consignmentDeno.setSealNo(consignmentBag.bagcode);
                        consignmentDeno.setEnvelopsList(consignmentList);
                        consignmentDeno.setType("ConsignmentInBag");
                        consignmentDenomination.add(consignmentDeno);
                    }
                }
                if (consignmentQty > 0) {
                    consignmentContent.setDescription("Consignments");
                    consignmentContent.setQty(consignmentQty);

                    consignmentContent.setSealNoList(consignmentSealNoList);
                    printContent.add(consignmentContent);
                }
                if (consignmentInBagQty > 0) {
                    consignmentInBagContent.setDescription("CONSIGNMENT IN BAG");
                    consignmentInBagContent.setQty(consignmentInBagQty);

                    consignmentInBagContent.setDenominationList(consignmentDenomination);
                    printContent.add(consignmentInBagContent);
                }

            }
        }
        if (!bagSealNoList.isEmpty()) {
            Content bagContent = new Content();
            bagContent.setDescription("Sealed Bag");
            bagContent.setQty(bagQty);
            bagContent.setSealNoList(bagSealNoList);
            printContent.add(bagContent);
        }

        if (!wagonSealNoList.isEmpty()) {
            Content wagonContent = new Content();
            wagonContent.setDescription("Wagon");
            wagonContent.setQty(wagonQty);
            wagonContent.setSealNoList(wagonSealNoList);
            printContent.add(wagonContent);
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

    @SuppressLint("NewApi")
    public static List<CageContent> getCageSelectedPrintContent(String groupKey, int isDelivery, String
            branchCode, String startTime, String endTime) {

        List<CageContent> printContent = new ArrayList<>();
        int isCollection = isDelivery == 0 ? 1 : 0;
        List<Job> list = Job.getJobListByTypeByGroupKey(isDelivery, isCollection, groupKey, branchCode, startTime, endTime);

        for (Job job : list) {
            List<com.novigosolutions.certiscisco_pcsbr.models.Cage> cageList = com.novigosolutions.certiscisco_pcsbr.models.Cage.getByTransportMasterId(job.TransportMasterId);

            for (com.novigosolutions.certiscisco_pcsbr.models.Cage c : cageList) {

                int index = checkIfCageExists(c.CageNo, c.CageSeal, printContent);
                if (index != -1) {

                    CageContent cageContent = printContent.get(index);
                    List<Items> items = cageContent.getSealNoList();
                    cageContent.setSealNoList(Job.getCageCollectionSummary(job.TransportMasterId, c.CageNo, c.CageSeal, items));
                    printContent.set(index, cageContent);

                } else {

                    CageContent cageContent = new CageContent();
                    cageContent.setCageNo(c.CageNo);
                    cageContent.setCageSeal(c.CageSeal);
                    cageContent.setSealNoList(Job.getCageCollectionSummary(job.TransportMasterId, c.CageNo, c.CageSeal));
                    printContent.add(cageContent);
                }
            }
        }

        return printContent;
    }

    private static int checkIfCageExists(String cageNo, String cageSeal, List<CageContent> cageContents) {
        int index = -1;
        for (int a = 0; a < cageContents.size(); a++) {
            CageContent cageContent = cageContents.get(a);

            if (cageContent.getCageSeal().equalsIgnoreCase(cageSeal) && cageContent.getCageNo().equalsIgnoreCase(cageNo)) {
                return a;
            }
        }
        return index;
    }

    public static boolean isAllDeliveryScanned(String GroupKey, String BranchCode, String
            PFunctionalCode, String startTime, String endTime) {
        boolean isAllScanned = true;
        List<Delivery> deliveries = getPendingSealedByPointId(GroupKey, BranchCode, PFunctionalCode, startTime, endTime);
        for (int i = 0; i < deliveries.size(); i++) {
            boolean result = deliveries.get(i).IsScanned;
            if (!result) {
                isAllScanned = false;
                break;
            }
        }

        deliveries.clear();
        deliveries = getUnSealedByPointId(GroupKey, BranchCode, PFunctionalCode, startTime, endTime);
        for (int i = 0; i < deliveries.size(); i++) {
            boolean result = deliveries.get(i).IsScanned;
            if (!result) {
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

        Wagon wagon = new Select().from(Wagon.class)
                .where("TransportMasterId=?", TransportMasterId)
                .executeSingle();
        if (wagon != null) return true;

        EnvelopeBag envelopeBag = new Select().from(EnvelopeBag.class)
                .where("TransportMasterId=?", TransportMasterId)
                .executeSingle();
        if (envelopeBag != null) return true;


        ConsignmentBag consignmentBag = new Select().from(ConsignmentBag.class)
                .where("TransportMasterId=?", TransportMasterId)
                .executeSingle();
        if (consignmentBag != null) return true;

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

        List<ConsignmentBag> consignmentBags = ConsignmentBag.getByTransportMasterId(TransportMasterId);
        for (int i = 0; i < consignmentBags.size(); i++) {
            new Delete().from(Consignment.class)
                    .where("bagid=?", consignmentBags.get(i).getId())
                    .execute();
        }
        new Delete().from(ConsignmentBag.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();

        new Delete().from(Box.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
        new Delete().from(BoxBag.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();

        new Delete().from(Cage.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();

        new Delete().from(Wagon.class)
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

    public static void updateJobEndTime(String groupKey, String BranchCode, String
            PDFunctionalCode, String JobEndTime) {
        new Update(Job.class)
                .set("JobEndTime=?", JobEndTime)
                .where("BranchCode=? and PFunctionalCode=? and GroupKey=?", BranchCode, PDFunctionalCode, groupKey)
                .execute();
    }

    public static void updateDeliveryJobsEndTime(String GroupKey, String JobEndTime) {
        new Update(Job.class)
                .set("JobEndTime=?", JobEndTime)
                .where("IsFloatDeliveryOrder=1 AND GroupKey=?", GroupKey)
                .execute();
    }

    public static void setFinished(String groupKey, String BranchCode, String PDFunctionalCode) {
        new Update(Job.class)
                .set("finished=1")
                .where("BranchCode=? and PFunctionalCode=? and GroupKey=?", BranchCode, PDFunctionalCode, groupKey)
                .execute();
    }

    public static void setDeliveryJobsFinished(String GroupKey) {
        new Update(Job.class)
                .set("finished=1")
                .where("IsFloatDeliveryOrder=1 AND GroupKey=?", GroupKey)
                .execute();
    }

    public static void setCollected(String GroupKey, String BranchCode, String
            PFunctionalCode, String startTime, String endTime) {
        new Update(Job.class)
                .set("isCollected=?,Status=?", 1, "COMPLETED")
                .where("GroupKey=? AND BranchCode=? AND PFunctionalCode=? AND ActualFromTime=? AND ActualToTime=?",
                        GroupKey, BranchCode, PFunctionalCode, startTime, endTime)
                .execute();
    }


    public static void setDelivered(String GroupKey, String BranchCode, String
            PFunctionalCode, String startTime, String endTime) {
        if (PFunctionalCode == null) {
            new Update(Job.class)
                    .set("Status=?", "COMPLETED")
                    .where("IsFloatDeliveryOrder=1 AND GroupKey=? AND BranchCode=? AND ActualFromTime=? AND ActualToTime=?", GroupKey, BranchCode, startTime, endTime)
                    .execute();
        } else {
            new Update(Job.class)
                    .set("Status=?", "COMPLETED")
                    .where("IsFloatDeliveryOrder=1 AND GroupKey=? AND BranchCode=? AND PFunctionalCode=? AND ActualFromTime=? AND ActualToTime=?", GroupKey, BranchCode, PFunctionalCode
                            , startTime, endTime)
                    .execute();
        }

    }


    public static void updateLatestGroupKey(int transportMasterId, String groupKey) {
        new Update(Job.class)
                .set("GroupKey=?", groupKey)
                .where("TransportMasterId=?", transportMasterId)
                .execute();
    }

    public static void UpdateDateFormats() throws ParseException {
        List<Job> result = new Select().from(Job.class)
                .where("Status=?", "COMPLETED")
                .execute();

        boolean indicator = false;
        String actualFromTime = "";
        String actualToTime = "";
        Date fromDate = new Date();
        Date toDate = new Date();

        for (Job job : result) {

            if (job.OrderNo.equals("TJ2109210126")) {

                if (!job.ActualFromTime.isEmpty() || !job.ActualFromTime.equals("")) {
                    DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                    try {
                        fromDate = originalFormat.parse(job.ActualFromTime);
                        toDate = originalFormat.parse(job.ActualToTime);
                    } catch (Exception e) {
                        indicator = true;
                        actualFromTime = job.ActualToTime;
                        actualToTime = job.ActualToTime;
                    }

                    if (!indicator) {
                        actualFromTime = targetFormat.format(fromDate);
                        actualToTime = targetFormat.format(toDate);
                    }
                }
                Log.e(job.OrderNo, actualFromTime);
                Log.e(job.OrderNo, actualToTime);
                new Update(Job.class)
                        .set("ActualFromTime=? , ActualToTime=?", actualFromTime, actualToTime)
                        .where("TransportMasterId=?", job.TransportMasterId)
                        .execute();
            }
        }
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

    @Override
    public int compareTo(Job o) {
        return this.SequenceNo.compareTo(o.SequenceNo);
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
