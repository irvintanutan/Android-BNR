package com.novigosolutions.certiscisco_pcsbr.models;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;

import java.util.Date;
import java.util.List;

@Table(name = "Reschedule")
public class Reschedule extends Model {

    @Column(name = "PointId")
    public int PointId;

    @Column(name = "TransportId")
    public String TransportId;

    @Column(name = "RescheduleDt")
    public String RescheduleDt;

    @Column(name = "Reason")
    public String Reason;

    @Column(name = "SignImg")
    public String SignImg;

    @Column(name = "GroupKey")
    public String GroupKey;


    public static Reschedule getSingle() {
        return new Select().from(Reschedule.class)
                .executeSingle();
    }

    public static boolean isOfflineRescheduled(int PointId) {
        return null!=new Select().from(Reschedule.class)
                .where("PointId=?",PointId)
                .executeSingle();
    }

    public static boolean isOfflineRescheduled(String GroupKey) {
        return null!=new Select().from(Reschedule.class)
                .where("GroupKey=?",GroupKey)
                .executeSingle();
    }

    public static void removeSingle(long id) {
        new Delete().from(Reschedule.class)
                .where("id=?", id)
                .execute();
    }

    public static void remove() {
        new Delete().from(Reschedule.class)
                .execute();
    }
}