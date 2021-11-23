package com.novigosolutions.certiscisco_pcsbr.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

@Table(name = "wagon")
public class Wagon extends Model {
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

    public static void setCageNoCageSeal(int TransportMasterId, String cageNo, String cageSeal) {
        new Update(Wagon.class)
                .set("CageNo=? , CageSeal=?", cageNo, cageSeal)
                .where("TransportMasterId=? and CageNo IS NULL and CageSeal IS NULL", TransportMasterId)
                .execute();
    }
    public static List<Wagon> getByTransportMasterIdWithOutCage(int TransportMasterId) {
        return new Select().from(Wagon.class)
                .where("TransportMasterId=? and CageNo IS NULL and CageSeal IS NULL", TransportMasterId)
                .execute();
    }

    public static List<Wagon> getByTransportMasterIdWithCage(int TransportMasterId, String cageNo , String cageSeal) {
        return new Select().from(Wagon.class)
                .where("TransportMasterId=? and CageNo=? and CageSeal=?", TransportMasterId, cageNo , cageSeal)
                .execute();
    }

    public static List<Bags> getByTransportMasterId(int TransportMasterId , String cageNo , String cageSeal) {
        return new Select().from(Wagon.class)
                .where("TransportMasterId=? AND CageNo!=? AND CageSeal!=?", TransportMasterId, cageNo, cageSeal)
                .execute();
    }

    public static List<Wagon> getByTransportMasterId(int TransportMasterId) {
        return new Select().from(Wagon.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static void removeByCageNoCageSeal(String cageNo , String cageSeal){
        new Delete().from(Wagon.class)
                .where("CageNo=? AND CageSeal=?", cageNo , cageSeal)
                .execute();
    }

    public static void removeSingle(long id) {
        new Delete().from(Wagon.class)
                .where("id=?",id)
                .execute();
    }

    public static void remove() {
        new Delete().from(Wagon.class)
                .execute();
    }

}
