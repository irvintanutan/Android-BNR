package com.novigosolutions.certiscisco_pcsbr.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

@Table(name = "BoxBag")
public class BoxBag extends Model {
    @Column(name = "TransportMasterId")
    public int TransportMasterId;

    @Column(name = "bagcode")
    public String bagcode;

    @Column(name = "ProductId")
    public int ProductId;

    @Column(name = "ProductName")
    public String ProductName;

    @Column(name = "CoinSeries")
    public String  CoinSeries;

    @Column(name = "CoinSeriesId")
    public int  CoinSeriesId;

    @Column(name = "CageNo")
    public String CageNo;

    @Column(name = "CageSeal")
    public String CageSeal;

    public static void setCageNoCageSeal(int TransportMasterId, String cageNo, String cageSeal) {
        new Update(BoxBag.class)
                .set("CageNo=? , CageSeal=?", cageNo, cageSeal)
                .where("TransportMasterId=? and CageNo IS NULL and CageSeal IS NULL", TransportMasterId)
                .execute();
    }
    public static List<Bags> getByTransportMasterIdWithOutCage(int TransportMasterId) {
        return new Select().from(BoxBag.class)
                .where("TransportMasterId=? and CageNo IS NULL and CageSeal IS NULL", TransportMasterId)
                .execute();
    }

    public static List<Bags> getByTransportMasterIdWithCage(int TransportMasterId, String cageNo , String cageSeal) {
        return new Select().from(BoxBag.class)
                .where("TransportMasterId=? and CageNo=? and CageSeal=?", TransportMasterId, cageNo , cageSeal)
                .execute();
    }
    public static List<Bags> getByTransportMasterId(int TransportMasterId , String cageNo , String cageSeal) {
        return new Select().from(BoxBag.class)
                .where("TransportMasterId=? AND CageNo!=? AND CageSeal!=?", TransportMasterId, cageNo, cageSeal)
                .execute();
    }

    public static BoxBag getById(long id) {
        return new Select().from(BoxBag.class)
                .where("id=?", id)
                .executeSingle();
    }

//    public static long getBagIdOfBox(int TransportMasterId) {
//        BoxBag boxBag = new Select().from(BoxBag.class)
//                .where("TransportMasterId=? AND boxType=?", TransportMasterId, "Box")
//                .executeSingle();
//        if (boxBag != null) {
//            return boxBag.getId();
//        } else {
//            BoxBag newboxBag = new BoxBag();
//            newboxBag.TransportMasterId = TransportMasterId;
//            newboxBag.boxType="Box";
//            newboxBag.bagcode = "none";
//            return newboxBag.save();
//        }
//
//    }

    public static List<BoxBag> getByTransportMasterId(int TransportMasterId) {
        return new Select().from(BoxBag.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static void removeSingle(long id) {
        new Delete().from(BoxBag.class)
                .where("id=?", id)
                .execute();
    }

    public static void remove() {
        new Delete().from(BoxBag.class)
                .execute();
    }

}
