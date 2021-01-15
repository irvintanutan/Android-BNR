package com.novigosolutions.certiscisco_pcsbr.models;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "coinseries")
public class CoinSeries extends Model {

    @Column(name = "CoinSeriesId")
    public int CoinSeriesId;

    @Column(name = "DataAbbreviation")
    public String DataAbbreviation;

    @Column(name = "DataDescription")
    public String DataDescription;

    public static List<CoinSeries> getAllCoinSeries() {
        List<CoinSeries> coinseries = new Select().from(CoinSeries.class)
                .orderBy("DataDescription")
                .execute();
        return coinseries;
    }


    public static List<CoinSeries> getSingleCionSeries(int id) {
        List<CoinSeries> coinseries = new Select().from(CoinSeries.class)
                .where("CoinSeriesId=?",id)
                .execute();
        return coinseries;
    }

    public static void remove() {
        new Delete().from(CoinSeries.class)
                .execute();
    }
}



