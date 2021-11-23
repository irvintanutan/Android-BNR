package com.novigosolutions.certiscisco_pcsbr.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.ArrayList;
import java.util.List;

@Table(name = "delivery")
public class Delivery extends Model {

    @Column(name = "TransportMasterId")
    public int TransportMasterId;

    @Column(name = "FloatDeliveryOrderId")
    public int FloatDeliveryOrderId;

    @Column(name = "TotalAmount")
    public int TotalAmount;

    @Column(name = "ItemType")
    public String ItemType;

    @Column(name = "IsBox")
    public boolean IsBox;

    @Column(name = "SealNo")
    public String SealNo;

    @Column(name = "Denomination")
    public String Denomination;

    @Column(name = "ItemAmount")
    public int ItemAmount;

    @Column(name = "Qty")
    public int Qty;

    @Column(name = "IsScanned")
    public boolean IsScanned;

    @Column(name = "CoinSeries")
    public String CoinSeries;

    @Column(name = "CoinSeriesId")
    public int CoinSeriesId;

    @Column(name = "CageNo")
    public String CageNo;

    @Column(name = "CageSeal")
    public String CageSeal;


    public static List<Delivery> getByTransportMasterId(int TransportMasterId) {
        return new Select().from(Delivery.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static List<Delivery> getSealedByByTransportMasterId(int TransportMasterId) {
        return new Select().from(Delivery.class)
                .where("TransportMasterId=? AND ItemType IN ('BAG','Envelope In Bag','Envelopes','Coin Box')", TransportMasterId)
                .execute();
    }

    public static List<Delivery> getUnSealedByTransportMasterId(int TransportMasterId) {
        return new Select().from(Delivery.class)
                .where("TransportMasterId=? AND ItemType IN ('BOX','PALLET')", TransportMasterId)
                .execute();
    }

    public static List<Delivery> getSealedNonCageByTransportMasterId(int TransportMasterId) {
        return new Select().from(Delivery.class)
                .where("TransportMasterId=? AND ItemType IN ('BAG','Envelope In Bag','Envelopes','Coin Box') AND " +
                        "(CageNo IS NULL OR CageNo == '') AND (CageSeal IS NULL OR CageSeal == '')", TransportMasterId)
                .execute();
    }

    public static List<Delivery> getUnSealedNonCageByTransportMasterId(int TransportMasterId) {
        return new Select().from(Delivery.class)
                .where("TransportMasterId=? AND ItemType IN ('BOX','PALLET') AND " +
                        "(CageNo IS NULL OR CageNo == '') AND (CageSeal IS NULL OR CageSeal == '')", TransportMasterId)
                .execute();
    }

//    public static List<Delivery> getSealedByPointId(int PointId) {
//        List<Job> jobs = Job.getDeliveryJobsOfPoint(PointId);
//        List<Delivery> deliveries = new ArrayList<>();
//        for (int i = 0; i < jobs.size(); i++) {
//            deliveries.addAll(getSealedByByTransportMasterId(jobs.get(i).TransportMasterId));
//        }
//        return deliveries;
//    }

    public static List<Delivery> getSealedByPointId(String GroupKey, String BranchCode, String PFunctionalCode, String actualFromTime, String actualToTime) {
        List<Job> jobs = Job.getDeliveryJobsOfPoint(GroupKey, BranchCode, PFunctionalCode, actualFromTime, actualToTime);
        List<Delivery> deliveries = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            deliveries.addAll(getSealedByByTransportMasterId(jobs.get(i).TransportMasterId));
        }
        return deliveries;
    }

//    public static List<Delivery> getUnSealedByPointId(int PointId) {
//        List<Job> jobs = Job.getDeliveryJobsOfPoint(PointId);
//        List<Delivery> deliveries = new ArrayList<>();
//        for (int i = 0; i < jobs.size(); i++) {
//            deliveries.addAll(getUnSealedByTransportMasterId(jobs.get(i).TransportMasterId));
//        }
//        return deliveries;
//    }

    public static List<Delivery> getUnSealedByPointId(String GroupKey, String BranchCode, String PFunctionalCode, String actualFromTime, String actualToTime) {
        List<Job> jobs = Job.getDeliveryJobsOfPoint(GroupKey, BranchCode, PFunctionalCode, actualFromTime, actualToTime);
        List<Delivery> deliveries = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            deliveries.addAll(getUnSealedByTransportMasterId(jobs.get(i).TransportMasterId));
        }
        return deliveries;
    }

//    public static List<Delivery> getPendingSealedByPointId(int PointId) {
//        List<Job> jobs = Job.getPendingDeliveryJobsOfPoint(PointId);
//        List<Delivery> deliveries = new ArrayList<>();
//        for (int i = 0; i < jobs.size(); i++) {
//            deliveries.addAll(getSealedByByTransportMasterId(jobs.get(i).TransportMasterId));
//        }
//        return deliveries;
//    }

    public static List<Delivery> getPendingSealedByPointId(String GroupKey, String BranchCode, String PFunctionalCode, String startTime, String endTime) {
        List<Job> jobs = Job.getPendingDeliveryJobsOfPoint(GroupKey, BranchCode, PFunctionalCode, startTime, endTime);
        List<Delivery> deliveries = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            deliveries.addAll(getSealedByByTransportMasterId(jobs.get(i).TransportMasterId));
        }
        return deliveries;
    }

    public static List<Delivery> getPendingCageSealedByPointId(String GroupKey, String BranchCode, String PFunctionalCode, String startTime, String endTime) {
        List<Job> jobs = Job.getPendingDeliveryJobsOfPoint(GroupKey, BranchCode, PFunctionalCode, startTime, endTime);
        List<Delivery> deliveries = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            deliveries.addAll(getSealedNonCageByTransportMasterId(jobs.get(i).TransportMasterId));
        }
        return deliveries;
    }

    public static List<Delivery> getSealedCageDeliveryItems(int TransportMasterId, String cageNo, String cageSeal) {
        return new Select().from(Delivery.class)
                .where("TransportMasterId=? AND CageNo=? AND CageSeal=? AND ItemType IN ('BAG','Envelope In Bag','Envelopes','Coin Box')",
                        TransportMasterId, cageNo, cageSeal)
                .execute();
    }

    public static List<Delivery> getUnSealedCageDeliveryItems(int TransportMasterId, String cageNo, String cageSeal) {
        return new Select().from(Delivery.class)
                .where("TransportMasterId=? AND CageNo=? AND CageSeal=? AND ItemType IN ('BOX','PALLET')",
                        TransportMasterId, cageNo, cageSeal)
                .execute();
    }

    public static List<Delivery> distinct(List<Delivery> deliveries) {
        List<Delivery> result = new ArrayList<>();
        List<String> checker = new ArrayList<>();

        for (int a = 0; a < deliveries.size(); a++) {
            Delivery delivery = deliveries.get(a);
            if (a == 0) {
                checker.add(deliveries.get(a).SealNo);
                result.add(delivery);
            } else {
            }
        }


        return result;
    }

    public static List<Delivery> getPendingSealedByTransportId(int TransportMasterId) {
//        List<Job> jobs = Job.getPendingDeliveryJobsOfPoint(GroupKey);
        List<Delivery> deliveries = new ArrayList<>();
//        for (int i = 0; i < jobs.size(); i++) {
        deliveries.addAll(getSealedByByTransportMasterId(TransportMasterId));
//        }
        return deliveries;
    }

//    public static List<Delivery> getPendingUnSealedByPointId(int PointId) {
//        List<Job> jobs = Job.getPendingDeliveryJobsOfPoint(PointId);
//        List<Delivery> deliveries = new ArrayList<>();
//        for (int i = 0; i < jobs.size(); i++) {
//            deliveries.addAll(getUnSealedByTransportMasterId(jobs.get(i).TransportMasterId));
//        }
//        return deliveries;
//    }

    public static List<Delivery> getPendingUnSealedByPointId(String GroupKey, String BranchCode, String PFunctionalCode, String startTime, String endTime) {
        List<Job> jobs = Job.getPendingDeliveryJobsOfPoint(GroupKey, BranchCode, PFunctionalCode, startTime, endTime);
        List<Delivery> deliveries = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            deliveries.addAll(getUnSealedByTransportMasterId(jobs.get(i).TransportMasterId));
        }
        return deliveries;
    }

    public static List<Delivery> getPendingCageUnSealedByPointId(String GroupKey, String BranchCode, String PFunctionalCode, String startTime, String endTime) {
        List<Job> jobs = Job.getPendingDeliveryJobsOfPoint(GroupKey, BranchCode, PFunctionalCode, startTime, endTime);
        List<Delivery> deliveries = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            deliveries.addAll(getUnSealedNonCageByTransportMasterId(jobs.get(i).TransportMasterId));
        }
        return deliveries;
    }

    public static List<Delivery> getPendingUnSealedByTransportId(int TransportMasterId) {
//        List<Job> jobs = Job.getPendingDeliveryJobsOfPoint(GroupKey);
        List<Delivery> deliveries = new ArrayList<>();
//        for (int i = 0; i < jobs.size(); i++) {
        deliveries.addAll(getUnSealedByTransportMasterId(TransportMasterId));
//        }
        return deliveries;
    }

//    public static boolean hasPendingDeliveryItems(int PointId) {
//        return (getPendingSealedByPointId(PointId).size() > 0 || getPendingUnSealedByPointId(PointId).size() > 0);
//    }

    public static boolean hasPendingDeliveryItems(String GroupKey, String BranchCode, String PFunctionalCode, String startTime, String endTime) {
        return (getPendingSealedByPointId(GroupKey, BranchCode, PFunctionalCode, startTime, endTime).size() > 0 ||
                getPendingUnSealedByPointId(GroupKey, BranchCode, PFunctionalCode, startTime, endTime).size() > 0);
    }

    public static boolean hasPendingDeliveryItems(int TransportMasterId) {
        return (getPendingSealedByTransportId(TransportMasterId).size() > 0 || getPendingUnSealedByTransportId(TransportMasterId).size() > 0);
    }


    public static void setScanned(long id) {
        new Update(Delivery.class)
                .set("IsScanned=?", 1)
                .where("id=?", id)
                .execute();
    }

//    public static boolean setScanned(int PointId, String seal) {
//        List<Delivery> deliveries = getPendingSealedByPointId(PointId);
//        for (int i = 0; i < deliveries.size(); i++) {
//            if (deliveries.get(i).SealNo.equals(seal)) {
//                new Update(Delivery.class)
//                        .set("IsScanned=?", 1)
//                        .where("id=?", deliveries.get(i).getId())
//                        .execute();
//                return true;
//            }
//        }
//        return false;
//    }

    public static boolean setScanned(String GroupKey, String seal, String BranchCode, String PFunctionalCode, String startTime, String endTime) {
        List<Delivery> deliveries = getPendingSealedByPointId(GroupKey, BranchCode, PFunctionalCode, startTime, endTime);
        for (int i = 0; i < deliveries.size(); i++) {
            if (deliveries.get(i).SealNo.equals(seal)) {
                new Update(Delivery.class)
                        .set("IsScanned=?", 1)
                        .where("id=? or SealNo=?", deliveries.get(i).getId(), seal)
                        .execute();
                return true;
            }
        }
        return false;
    }

    public static boolean setCageItemScanned(String seal , int TransportMasterId , String cageNo , String cageSeal) {
        List<Delivery> deliveries = getSealedCageDeliveryItems(TransportMasterId, cageNo , cageSeal);
        for (int i = 0; i < deliveries.size(); i++) {
            if (deliveries.get(i).SealNo.equals(seal)) {
                new Update(Delivery.class)
                        .set("IsScanned=?", 1)
                        .where("id=? or SealNo=?", deliveries.get(i).getId(), seal)
                        .execute();
                return true;
            }
        }
        return false;
    }

    public static boolean setScanned(int TransportMasterId, String seal) {
        List<Delivery> deliveries = getPendingSealedByTransportId(TransportMasterId);
        for (int i = 0; i < deliveries.size(); i++) {
            if (deliveries.get(i).SealNo.equals(seal)) {
                new Update(Delivery.class)
                        .set("IsScanned=?", 1)
                        .where("id=?", deliveries.get(i).getId())
                        .execute();
                return true;
            }
        }
        return false;
    }

    public static boolean isAllDeliveryScanned(int TransportMasterId) {
        return (new Select().from(Delivery.class)
                .where("TransportMasterId=? AND IsScanned=?", TransportMasterId, 1)
                .executeSingle() == null);
    }

//    public static void clearBranchDelivery(int PointId) {
//        List<Job> deliveryjobs = Job.getPendingDeliveryJobsOfPoint(PointId);
//        for (int i = 0; i < deliveryjobs.size(); i++) {
//            new Update(Delivery.class)
//                    .set("IsScanned=?", 0)
//                    .where("TransportMasterId=?", deliveryjobs.get(i).TransportMasterId)
//                    .execute();
//        }
//    }

    public static void clearBranchDelivery(String GroupKey, String BranchCode, String PFunctionalCode, String startTime, String endTime) {
        List<Job> deliveryjobs = Job.getPendingDeliveryJobsOfPoint(GroupKey, BranchCode, PFunctionalCode, startTime, endTime);
        for (int i = 0; i < deliveryjobs.size(); i++) {
            new Update(Delivery.class)
                    .set("IsScanned=?", 0)
                    .where("TransportMasterId=?", deliveryjobs.get(i).TransportMasterId)
                    .execute();
        }
    }

    public static void remove() {
        new Delete().from(Delivery.class)
                .execute();
    }

}
