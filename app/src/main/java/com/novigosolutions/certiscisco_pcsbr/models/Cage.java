package com.novigosolutions.certiscisco_pcsbr.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "cage")
public class Cage extends Model {
    @Column(name = "TransportMasterId")
    public int TransportMasterId;

    @Column(name = "CageNo")
    public String CageNo;

    @Column(name = "CageSeal")
    public String CageSeal;


    public static List<Cage> getByTransportMasterId(int TransportMasterId) {
        return new Select().from(Cage.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static void removeByCageNoCageSeal(String cageNo , String cageSeal){
        new Delete().from(Cage.class)
                .where("CageNo=? AND CageSeal=?", cageNo , cageSeal)
                .execute();
    }

    public static void removeSingle(long id) {
        new Delete().from(Cage.class)
                .where("id=?",id)
                .execute();
    }

    public static void remove() {
        new Delete().from(Cage.class)
                .execute();
    }

}
