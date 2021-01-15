package com.novigosolutions.certiscisco_pcsbr.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "bags")
public class Bags extends Model {
    @Column(name = "TransportMasterId")
    public int TransportMasterId;

    @Column(name = "firstbarcode")
    public String firstbarcode;

    @Column(name = "secondbarcode")
    public String secondbarcode;


    public static List<Bags> getByTransportMasterId(int TransportMasterId) {
        return new Select().from(Bags.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static void removeSingle(long id) {
        new Delete().from(Bags.class)
                .where("id=?",id)
                .execute();
    }

    public static void remove() {
        new Delete().from(Bags.class)
                .execute();
    }

}
