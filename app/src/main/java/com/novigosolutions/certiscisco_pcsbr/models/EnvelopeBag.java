package com.novigosolutions.certiscisco_pcsbr.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "envelopebag")
public class EnvelopeBag extends Model {
    @Column(name = "TransportMasterId")
    public int TransportMasterId;

    @Column(name = "envolpeType")
    public String envolpeType;

    @Column(name = "bagcode")
    public String bagcode;

    public static EnvelopeBag getById(long id) {
        return new Select().from(EnvelopeBag.class)
                .where("id=?", id)
                .executeSingle();
    }

    public static List<EnvelopeBag> getByTransportMasterId(int TransportMasterId) {
        return new Select().from(EnvelopeBag.class)
                .where("TransportMasterId=?", TransportMasterId)
                .execute();
    }

    public static List<EnvelopeBag> getEnvelopesByTransportMasterId(int TransportMasterId) {
        return new Select().from(EnvelopeBag.class)
                .where("TransportMasterId=? AND envolpeType=?", TransportMasterId,"Envelopes")
                .execute();
    }

    public static List<EnvelopeBag> getEnvelopesInBagByTransportMasterId(int TransportMasterId) {
        return new Select().from(EnvelopeBag.class)
                .where("TransportMasterId=? AND envolpeType=?", TransportMasterId,"EnvelopeBag")
                .execute();
    }

    public static void removeSingle(long id) {
        new Delete().from(EnvelopeBag.class)
                .where("id=?",id)
                .execute();
    }

    public static void remove() {
        new Delete().from(EnvelopeBag.class)
                .execute();
    }

}
