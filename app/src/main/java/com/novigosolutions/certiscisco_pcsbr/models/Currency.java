package com.novigosolutions.certiscisco_pcsbr.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "currency")
public class Currency extends Model {

    @Column(name = "TransportMasterId")
    public int TransportMasterId;

    @Column(name = "ProductId")
    public int ProductId;

    @Column(name = "ProductName")
    public String ProductName;

    @Column(name = "ProductCode")
    public String ProductCode;

    @Column(name = "IsCoinValue")
    public String IsCoinValue;


    public static List<Currency> getByCustomerId(int transportMasterId) {
        return new Select().from(Currency.class)
                .where("TransportMasterId=?", transportMasterId)
                .execute();
    }

    public static void removeByCustomerId(int transportMasterId) {
        new Delete().from(Currency.class)
                .where("TransportMasterId=?", transportMasterId)
                .execute();
    }

    public static void remove() {
        new Delete().from(Currency.class)
                .execute();
    }

}
