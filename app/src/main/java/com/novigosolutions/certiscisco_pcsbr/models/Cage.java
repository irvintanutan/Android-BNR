package com.novigosolutions.certiscisco_pcsbr.models;


import android.text.TextUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

@Table(name = "cage")
public class Cage extends Model {
    @Column(name = "TransportMasterId")
    public int TransportMasterId;

    @Column(name = "CageNo")
    public String CageNo;

    @Column(name = "CageSeal")
    public String CageSeal;

    @Column(name = "IsCageNoScanned")
    public boolean IsCageNoScanned;

    @Column(name = "IsCageSealScanned")
    public boolean IsCageSealScanned;

    public static List<Cage> getByTransportMasterId(int TransportMasterId) {
        return new Select().from(Cage.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static List<Cage> getByTransportMasterId(String [] TransportMasterId) {

        String placeholders = TextUtils.join(",", TransportMasterId);

        return new Select().from(Cage.class)
                .where("TransportMasterId IN (" + placeholders +")")
                .execute();
    }

    public static List<Cage> getAll() {
        return new Select().from(Cage.class)
                .execute();
    }

    public static List<Cage> cageExist(String cageNo, String cageSeal) {
        return new Select().from(Cage.class)
                .where("CageNo=? AND CageSeal=?", cageNo, cageSeal)
                .execute();
    }


    public static void removeByCageNoCageSeal(String cageNo, String cageSeal) {
        new Delete().from(Cage.class)
                .where("CageNo=? AND CageSeal=?", cageNo, cageSeal)
                .execute();
    }

    public static void removeSingle(long id) {
        new Delete().from(Cage.class)
                .where("id=?", id)
                .execute();
    }

    public static void remove() {
        new Delete().from(Cage.class)
                .execute();
    }

    public static boolean isCageScanned (int transportMasterId , String cageNo , String cageSeal) {
        List<Cage> cage =  new Select().from(Cage.class)
                .where("CageNo=? AND CageSeal=? AND TransportMasterId=? AND IsCageNoScanned=1 AND IsCageSealScanned=1", cageNo, cageSeal, transportMasterId)
                .execute();

        if (cage.isEmpty()) {
            return false;
        } else return true;
    }

    public static Cage getSingle(int TransportMasterId , String cageNo , String cageSeal) {
        return new Select().from(Cage.class)
                .where("TransportMasterId=? AND CageNo=? AND CageSeal=?", TransportMasterId, cageNo, cageSeal)
                .executeSingle();

    }

    public static void setCageNoScanned(int TransportMasterId , String cageNo , String cageSeal) {
        new Update(Cage.class)
                .set("IsCageNoScanned=?", 1)
                .where("TransportMasterId=? AND CageNo=? AND CageSeal=?", TransportMasterId, cageNo, cageSeal)
                .execute();
    }

    public static void setCageSealScanned(int TransportMasterId , String cageNo , String cageSeal) {
        new Update(Cage.class)
                .set("IsCageSealScanned=?", 1)
                .where("TransportMasterId=? AND CageNo=? AND CageSeal=?", TransportMasterId, cageNo, cageSeal)
                .execute();
    }
}
