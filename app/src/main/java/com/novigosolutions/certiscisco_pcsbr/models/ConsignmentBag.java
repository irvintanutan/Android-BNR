package com.novigosolutions.certiscisco_pcsbr.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

@Table(name = "consignmentbag")
public class ConsignmentBag extends Model {
    @Column(name = "TransportMasterId")
    public int TransportMasterId;

    @Column(name = "consignmentType")
    public String consignmentType;

    @Column(name = "bagcode")
    public String bagcode;


    @Column(name = "CageNo")
    public String CageNo;

    @Column(name = "CageSeal")
    public String CageSeal;

    public static void setCageNoCageSeal(int TransportMasterId, String cageNo, String cageSeal) {
        new Update(ConsignmentBag.class)
                .set("CageNo=? , CageSeal=?", cageNo, cageSeal)
                .where("TransportMasterId=? and CageNo IS NULL and CageSeal IS NULL", TransportMasterId)
                .execute();
    }

    public static List<ConsignmentBag> getByTransportMasterIdWithOutCage(int TransportMasterId) {
        return new Select().from(ConsignmentBag.class)
                .where("TransportMasterId=? and CageNo IS NULL and CageSeal IS NULL", TransportMasterId)
                .execute();
    }

    public static List<ConsignmentBag> getByTransportMasterIdWithCage(int TransportMasterId, String cageNo , String cageSeal) {
        return new Select().from(ConsignmentBag.class)
                .where("TransportMasterId=? and CageNo=? and CageSeal=?", TransportMasterId, cageNo , cageSeal)
                .execute();
    }

    public static List<Bags> getByTransportMasterId(int TransportMasterId , String cageNo , String cageSeal) {
        return new Select().from(ConsignmentBag.class)
                .where("TransportMasterId=? AND CageNo!=? AND CageSeal!=?", TransportMasterId, cageNo, cageSeal)
                .execute();
    }

    public static ConsignmentBag getById(long id) {
        return new Select().from(ConsignmentBag.class)
                .where("id=?", id)
                .executeSingle();
    }

    public static List<ConsignmentBag> getByTransportMasterId(int TransportMasterId) {
        return new Select().from(ConsignmentBag.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static List<ConsignmentBag> getEnvelopesByTransportMasterId(int TransportMasterId) {
        return new Select().from(ConsignmentBag.class)
                .where("TransportMasterId=? AND envolpeType=?", TransportMasterId,"Envelopes")
                .execute();
    }

    public static List<ConsignmentBag> getEnvelopesInBagByTransportMasterId(int TransportMasterId) {
        return new Select().from(ConsignmentBag.class)
                .where("TransportMasterId=? AND envolpeType=?", TransportMasterId,"EnvelopeBag")
                .execute();
    }

    public static void removeByCageNoCageSeal(String cageNo , String cageSeal){
        new Delete().from(ConsignmentBag.class)
                .where("CageNo=? AND CageSeal=?", cageNo , cageSeal)
                .execute();
    }

    public static void removeSingle(long id) {
        new Delete().from(ConsignmentBag.class)
                .where("id=?",id)
                .execute();
    }

    public static void remove() {
        new Delete().from(ConsignmentBag.class)
                .execute();
    }

}
