package com.novigosolutions.certiscisco_pcsbr.zebra;

import java.util.List;

public class Print {

    private String logo;    // logo image
    private String certisAddress; // header

    private String date;
    private String serviceStartTime;
    private String serviceEndTime;

    private String transactionId;
    private String functionalLocation;
    private String deliveryPoint;
    private String natureOfTransaction;
    private String collectionMode;
    private String bank;

    private String customerName;

    private String branchName;
    private String customerLocation;

    private List<Content> contentList;
    private List<CageContent> cageContentList;

    //Transaction officer Signature
    private String certisTransactionOfficer;
    private String transactionOfficerId;

    private String handedOverBy;
    private String customerAcknowledgment;  // signature image
    //customer Signature
    private String CustomerSignature;
    private String StaffId;
    private String CName;

    private String inquiryContact;

    private boolean isCollection;

    private String footer;

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCertisAddress() {
        return certisAddress;
    }

    public void setCertisAddress(String certisAddress) {
        this.certisAddress = certisAddress;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getServiceStartTime() {
        return serviceStartTime;
    }

    public void setServiceStartTime(String serviceStartTime) {
        this.serviceStartTime = serviceStartTime;
    }

    public String getServiceEndTime() {
        return serviceEndTime;
    }

    public void setServiceEndTime(String serviceEndTime) {
        this.serviceEndTime = serviceEndTime;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getFunctionalLocation() {
        return functionalLocation;
    }

    public void setFunctionalLocation(String functionalLocation) {
        this.functionalLocation = functionalLocation;
    }

    public String getDeliveryPoint() {
        return deliveryPoint;
    }

    public void setDeliveryPoint(String deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    public String getNatureOfTransaction() {
        return isCollection ? "Collection" : "Delivery";
    }

    public void setNatureOfTransaction(String natureOfTransaction) {
        this.natureOfTransaction = natureOfTransaction;
    }

    public String getCollectionMode() {
        return collectionMode;
    }

    public void setCollectionMode(String collectionMode) {
        this.collectionMode = collectionMode;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerLocation() {
        return customerLocation;
    }

    public void setCustomerLocation(String customerLocation) {
        this.customerLocation = customerLocation;
    }

    public List<Content> getContentList() {
        return contentList;
    }

    public void setContentList(List<Content> contentList) {
        this.contentList = contentList;
    }

    public String getCertisTransactionOfficer() {
        return certisTransactionOfficer;
    }

    public void setCertisTransactionOfficer(String certisTransactionOfficer) {
        this.certisTransactionOfficer = certisTransactionOfficer;
    }

    public String getTransactionOfficerId() {
        return transactionOfficerId;
    }

    public void setTransactionOfficerId(String transactionOfficerId) {
        this.transactionOfficerId = transactionOfficerId;
    }

    public String getHandedOverBy() {
        return handedOverBy;
    }

    public void setHandedOverBy(String handedOverBy) {
        this.handedOverBy = handedOverBy;
    }

    public String getCustomerAcknowledgment() {
        return customerAcknowledgment;
    }

    public void setCustomerAcknowledgment(String customerAcknowledgment) {
        this.customerAcknowledgment = customerAcknowledgment;
    }

    public String getInquiryContact() {
        return inquiryContact;
    }

    public void setInquiryContact(String inquiryContact) {
        this.inquiryContact = inquiryContact;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getCustomerSignature() {
        return CustomerSignature;
    }

    public void setCustomerSignature(String customerSignature) {
        CustomerSignature = customerSignature;
    }

    public String getStaffId() {
        return StaffId;
    }

    public void setStaffId(String staffId) {
        StaffId = staffId;
    }

    public String getCName() {
        return CName;
    }

    public void setCName(String CName) {
        this.CName = CName;
    }


    public List<CageContent> getCageContentList() {
        return cageContentList;
    }

    public void setCageContentList(List<CageContent> cageContentList) {
        this.cageContentList = cageContentList;
    }
}
