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

@Table(name = "Break")
public class Break extends Model {

    @Column(name = "BreakId")
    public int BreakId;

    @Column(name = "Duration")
    public String Duration;

    @Column(name = "Remarks")
    public String Remarks;

    @Column(name = "TeamName")
    public String TeamName;

    @Column(name = "StartTime")
    public String StartTime;

    @Column(name = "EndTime")
    public String EndTime;

    @Column(name = "Consumed")
    public boolean Consumed;

    @Column(name = "Expired")
    public boolean Expired;

    @Column(name = "completed")
    public boolean completed;

    public Break() {

    }

    public static Break getSingle(int BreakId) {
        return new Select().from(Break.class)
                .where("BreakId=?", BreakId)
                .executeSingle();
    }

    public static List<Break> getAllBreak() {
        return new Select().from(Break.class)
                .orderBy("StartTime")
                .execute();
    }

    public static List<Break> getPendingBreak() {
        return new Select().from(Break.class)
                .where("Expired=? AND completed=?", 0, 0)
                .execute();
    }

    public static int getOnGoingBreak(Context context) {
        List<Break> breaks = new Select().from(Break.class)
                .where("Expired=? AND completed=? AND Consumed=?", 0, 0, 1)
                .execute();
        long serverTime = CommonMethods.getServerTimeInms(context);
        for (int i = 0; i < breaks.size(); i++) {
            Date StartTime = CommonMethods.getBreakTime(breaks.get(i).StartTime);
            Date EndTime = CommonMethods.getBreakTime(breaks.get(i).EndTime);
            if (StartTime != null && StartTime.getTime() < serverTime && EndTime != null && EndTime.getTime() > serverTime) {
                return breaks.get(i).BreakId;
            }
        }
        return -1;
    }

    public static void setConsumed(int BreakId) {
        new Update(Break.class)
                .set("Consumed=?", 1)
                .where("BreakId=?", BreakId)
                .execute();
    }

    public static void setCompleted(int BreakId) {
        new Update(Break.class)
                .set("completed=?", 1)
                .where("BreakId=?", BreakId)
                .execute();
    }


    public static void setexpired(Context context) {
        List<Break> breaks = getPendingBreak();
        long serverTime = CommonMethods.getServerTimeInms(context);
        for (int i = 0; i < breaks.size(); i++) {
            Date EndTime = CommonMethods.getBreakTime(breaks.get(i).EndTime);
            if (EndTime != null && EndTime.getTime() < serverTime) {
                if (breaks.get(i).Consumed) {
                    setCompleted(breaks.get(i).BreakId);
                } else {
                    new Update(Break.class)
                            .set("Expired=?", 1)
                            .where("BreakId=?", breaks.get(i).BreakId)
                            .execute();
                }
            }
        }
    }

    public static boolean isExist(int BreakId) {
        return null != getSingle(BreakId);
    }

    public static void remove() {
        new Delete().from(Break.class)
                .execute();
    }
}