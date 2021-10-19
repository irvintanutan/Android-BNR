package com.novigosolutions.certiscisco_pcsbr.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

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


    public static List<Wagon> getByTransportMasterId(int TransportMasterId) {
        return new Select().from(Wagon.class)
                .where("TransportMasterId=?", TransportMasterId)
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
