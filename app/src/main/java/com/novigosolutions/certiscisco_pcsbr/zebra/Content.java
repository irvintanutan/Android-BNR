package com.novigosolutions.certiscisco_pcsbr.zebra;

import java.util.List;

public class Content {

    private String description;
    private int qty;
    private List<String> sealNoList;
    private List<Denomination> denominationList;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public List<String> getSealNoList() {
        return sealNoList;
    }

    public void setSealNoList(List<String> sealNoList) {
        this.sealNoList = sealNoList;
    }

    public List<Denomination> getDenominationList() {
        return denominationList;
    }

    public void setDenominationList(List<Denomination> denominationList) {
        this.denominationList = denominationList;
    }
}

