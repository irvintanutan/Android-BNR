package com.novigosolutions.certiscisco_pcsbr.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

@Table(name = "box")
public class Box extends Model {

    @Column(name = "TransportMasterId")
    public int TransportMasterId;

    @Column(name = "ProductId")
    public int ProductId;

    @Column(name = "ProductName")
    public String ProductName;

    @Column(name = "count")
    public int count;

    @Column(name = "CoinSeries")
    public String  CoinSeries;

    @Column(name = "CoinSeriesId")
    public int  CoinSeriesId;


    public static List<Box> getBoxByTransportMasterId(int TransportMasterId) {
        return new Select().from(Box.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static void updateCount(int TransportMasterId, int ProductId, String productName, int count) {
        Box box = new Select().from(Box.class)
                .where("TransportMasterId=? AND ProductId=?",TransportMasterId, ProductId)
                .executeSingle();
        if (box == null) {
            Box newbox = new Box();
            newbox.TransportMasterId = TransportMasterId;
            newbox.ProductId = ProductId;
            newbox.ProductName = productName;
            newbox.count = count;
            newbox.save();
        } else {
            new Update(Box.class)
                    .set("count=?", count)
                    .where("id=?", box.getId())
                    .execute();
        }
    }

    public static void updateCountNew(int TransportMasterId, int ProductId, String productName, int count,String description,int coinseriesid) {
        Box box = new Select().from(Box.class)
                .where("TransportMasterId=? AND ProductId=? AND CoinSeriesId=?",TransportMasterId, ProductId,coinseriesid)
                .executeSingle();
        if (box == null) {
            Box newbox = new Box();
            newbox.TransportMasterId = TransportMasterId;
            newbox.ProductId = ProductId;
            newbox.ProductName = productName;
            newbox.count = count;
            newbox.CoinSeries=description;
            newbox.CoinSeriesId=coinseriesid;
            newbox.save();
        } else {
            new Update(Box.class)
                    .set("count=? ,CoinSeries=? ,CoinSeriesId=?", count,description,coinseriesid)
                    .where("id=?", box.getId())
                    .execute();
        }
    }

    public static void removeSingle(long id) {
        new Delete().from(Box.class)
                .where("id=?", id)
                .execute();
    }


    public static void removeByTransportMasterId(long TransportMasterId) {
        new Delete().from(Box.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static void remove() {
        new Delete().from(Box.class)
                .execute();
    }

}
