package com.novigosolutions.certiscisco_pcsbr.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "envelope")
public class Envelope extends Model {

    @Column(name = "bagid")
    public long bagid;

    @Column(name = "barcode")
    public String barcode;


    public static List<Envelope> getByBagId(long BagId) {
        return new Select().from(Envelope.class)
                .where("bagid=?", BagId)
                .execute();
    }

    public static void removeSingle(long id) {
        new Delete().from(Envelope.class)
                .where("id=?", id)
                .execute();
    }

    public static void removeByBagid(long bagid) {
        new Delete().from(Envelope.class)
                .where("bagid=?", bagid)
                .execute();
    }

    public static void remove() {
        new Delete().from(Envelope.class)
                .execute();
    }

}
