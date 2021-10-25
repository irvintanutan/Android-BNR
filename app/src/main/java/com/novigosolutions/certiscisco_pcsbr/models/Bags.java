package com.novigosolutions.certiscisco_pcsbr.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

@Table(name = "bags")
public class Bags extends Model {
    @Column(name = "TransportMasterId")
    public int TransportMasterId;

    @Column(name = "firstbarcode")
    public String firstbarcode;

    @Column(name = "secondbarcode")
    public String secondbarcode;

    @Column(name = "CageNo")
    public String CageNo;

    @Column(name = "CageSeal")
    public String CageSeal;

    public static List<Bags> getByTransportMasterId(int TransportMasterId) {
        return new Select().from(Bags.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static List<Bags> getByTransportMasterIdWithOutCage(int TransportMasterId) {
        return new Select().from(Bags.class)
                .where("TransportMasterId=? and CageNo IS NULL and CageSeal IS NULL", TransportMasterId)
                .execute();
    }

    public static List<Bags> getByTransportMasterIdWithCage(int TransportMasterId, String cageNo , String cageSeal) {
        return new Select().from(Bags.class)
                .where("TransportMasterId=? and CageNo=? and CageSeal=?", TransportMasterId, cageNo , cageSeal)
                .execute();
    }

    public static void setCageNoCageSeal(int TransportMasterId, String cageNo, String cageSeal) {
        new Update(Bags.class)
                .set("CageNo=? , CageSeal=?", cageNo, cageSeal)
                .where("TransportMasterId=? and CageNo IS NULL and CageSeal IS NULL", TransportMasterId)
                .execute();
    }

    public static List<Bags> getByTransportMasterId(int TransportMasterId , String cageNo , String cageSeal) {
        return new Select().from(Bags.class)
                .where("TransportMasterId=? AND CageNo!=? AND CageSeal!=?", TransportMasterId, cageNo, cageSeal)
                .execute();
    }

    public static void removeByCageNoCageSeal(String cageNo , String cageSeal){
        new Delete().from(Bags.class)
                .where("CageNo=? AND CageSeal=?", cageNo , cageSeal)
                .execute();
    }

    public static void removeSingle(long id) {
        new Delete().from(Bags.class)
                .where("id=?", id)
                .execute();
    }

    public static void remove() {
        new Delete().from(Bags.class)
                .execute();
    }

}
