package com.novigosolutions.certiscisco_pcsbr.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

@Table(name = "consignment")
public class Consignment extends Model {

    @Column(name = "bagid")
    public long bagid;

    @Column(name = "barcode")
    public String barcode;

    @Column(name = "CageNo")
    public String CageNo;

    @Column(name = "CageSeal")
    public String CageSeal;

    public static List<Consignment> getByBagId(long BagId) {
        return new Select().from(Consignment.class)
                .where("bagid=?", BagId)
                .execute();
    }

    public static void setCageNoCageSeal(long bagid, String cageNo, String cageSeal) {
        new Update(Consignment.class)
                .set("CageNo=? , CageSeal=?", cageNo, cageSeal)
                .where("bagid=? and CageNo IS NULL and CageSeal IS NULL", bagid)
                .execute();
    }
    public static List<Consignment> getByTransportMasterIdWithOutCage(long bagId) {
        return new Select().from(Consignment.class)
                .where("bagid=? and CageNo IS NULL and CageSeal IS NULL", bagId)
                .execute();
    }

    public static List<Consignment> getByTransportMasterIdWithCage(long bagId, String cageNo , String cageSeal) {
        return new Select().from(Consignment.class)
                .where("bagid=? and CageNo=? and CageSeal=?", bagId, cageNo , cageSeal)
                .execute();
    }


    public static List<Bags> getByTransportMasterId(long bagId , String cageNo , String cageSeal) {
        return new Select().from(Consignment.class)
                .where("bagid=? AND CageNo!=? AND CageSeal!=?", bagId, cageNo, cageSeal)
                .execute();
    }

    public static void removeByCageNoCageSeal(String cageNo , String cageSeal){
        new Delete().from(Consignment.class)
                .where("CageNo=? AND CageSeal=?", cageNo , cageSeal)
                .execute();
    }

    public static void removeSingle(long id) {
        new Delete().from(Consignment.class)
                .where("id=?", id)
                .execute();
    }

    public static void removeByBagid(long bagid) {
        new Delete().from(Consignment.class)
                .where("bagid=?", bagid)
                .execute();
    }

    public static void remove() {
        new Delete().from(Consignment.class)
                .execute();
    }

}