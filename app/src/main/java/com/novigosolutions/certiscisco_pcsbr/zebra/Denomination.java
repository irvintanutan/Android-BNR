package com.novigosolutions.certiscisco_pcsbr.zebra;

import java.util.List;

public class Denomination {

    private String bagName;
    private String sealNo;
    private List<String> envelopsList;
    private String type;

    public String getBagName() {
        return bagName;
    }

    public void setBagName(String bagName) {
        this.bagName = bagName;
    }

    public String getSealNo() {
        return sealNo;
    }

    public void setSealNo(String sealNo) {
        this.sealNo = sealNo;
    }

    public List<String> getEnvelopsList() {
        return envelopsList;
    }

    public void setEnvelopsList(List<String> envelopsList) {
        this.envelopsList = envelopsList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
