package com.novigosolutions.certiscisco_pcsbr.zebra;

import com.novigosolutions.certiscisco_pcsbr.expandable.Items;

import java.util.List;

public class CageContent {

    public CageContent(String cageNo, String cageSeal, List<Items> sealNoList) {
        this.cageNo = cageNo;
        this.cageSeal = cageSeal;
        this.sealNoList = sealNoList;
    }

    public CageContent(){

    }

    public String getCageNo() {
        return cageNo;
    }

    public void setCageNo(String cageNo) {
        this.cageNo = cageNo;
    }

    public String getCageSeal() {
        return cageSeal;
    }

    public void setCageSeal(String cageSeal) {
        this.cageSeal = cageSeal;
    }

    public List<Items> getSealNoList() {
        return sealNoList;
    }

    public void setSealNoList(List<Items> sealNoList) {
        this.sealNoList = sealNoList;
    }

    private String cageNo;
    private String cageSeal;
    private List<Items> sealNoList;

}

