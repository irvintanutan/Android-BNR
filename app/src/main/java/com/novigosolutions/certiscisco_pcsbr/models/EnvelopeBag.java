package com.novigosolutions.certiscisco_pcsbr.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

@Table(name = "envelopebag")
public class EnvelopeBag extends Model {
    @Column(name = "TransportMasterId")
    public int TransportMasterId;

    @Column(name = "envolpeType")
    public String envolpeType;

    @Column(name = "bagcode")
    public String bagcode;


    @Column(name = "CageNo")
    public String CageNo;

    @Column(name = "CageSeal")
    public String CageSeal;

    public static void setCageNoCageSeal(int TransportMasterId, String cageNo, String cageSeal) {
        new Update(EnvelopeBag.class)
                .set("CageNo=? , CageSeal=?", cageNo, cageSeal)
                .where("TransportMasterId=? and CageNo IS NULL and CageSeal IS NULL", TransportMasterId)
                .execute();
    }

    public static List<Bags> getByTransportMasterIdWithOutCage(int TransportMasterId) {
        return new Select().from(EnvelopeBag.class)
                .where("TransportMasterId=? and CageNo IS NULL and CageSeal IS NULL", TransportMasterId)
                .execute();
    }

    public static List<Bags> getByTransportMasterIdWithCage(int TransportMasterId, String cageNo , String cageSeal) {
        return new Select().from(EnvelopeBag.class)
                .where("TransportMasterId=? and CageNo=? and CageSeal=?", TransportMasterId, cageNo , cageSeal)
                .execute();
    }

    public static List<Bags> getByTransportMasterId(int TransportMasterId , String cageNo , String cageSeal) {
        return new Select().from(EnvelopeBag.class)
                .where("TransportMasterId=? AND CageNo!=? AND CageSeal!=?", TransportMasterId, cageNo, cageSeal)
                .execute();
    }

    public static EnvelopeBag getById(long id) {
        return new Select().from(EnvelopeBag.class)
                .where("id=?", id)
                .executeSingle();
    }

    public static List<EnvelopeBag> getByTransportMasterId(int TransportMasterId) {
        return new Select().from(EnvelopeBag.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static List<EnvelopeBag> getEnvelopesByTransportMasterId(int TransportMasterId) {
        return new Select().from(EnvelopeBag.class)
                .where("TransportMasterId=? AND envolpeType=?", TransportMasterId,"Envelopes")
                .execute();
    }

    public static List<EnvelopeBag> getEnvelopesInBagByTransportMasterId(int TransportMasterId) {
        return new Select().from(EnvelopeBag.class)
                .where("TransportMasterId=? AND envolpeType=?", TransportMasterId,"EnvelopeBag")
                .execute();
    }

    public static void removeSingle(long id) {
        new Delete().from(EnvelopeBag.class)
                .where("id=?",id)
                .execute();
    }

    public static void remove() {
        new Delete().from(EnvelopeBag.class)
                .execute();
    }

}
