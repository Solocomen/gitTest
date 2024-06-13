/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.crm.bean;

import java.util.Date;

/**
 *
 * @author KenTKChung
 */
public class EpcCustomerProfile {
    private String requesterID;
    private String custId;
    private String email;
    private String[] emailList;
    private Date effectiveDate;
    private String accountType;
    private String custTypeCode;
    private String paymentMethod;
    private String divertCode;
    private String dmConsent;
    private String dmCompanyConsent;
    private String commChannel;
    private EpcContact contact;
    private EpcAddress[] address;
    private EpcSubscriber subscriber; 
    
    public String getRequesterID() {
        return requesterID;
    }

    public void setRequesterID(String requesterID) {
        this.requesterID = requesterID;
    }
    
    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String[] getEmailList() {
        return emailList;
    }

    public void setEmailList(String[] emailList) {
        this.emailList = emailList;
    }
    
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    
    public String getCustTypeCode() {
        return custTypeCode;
    }

    public void setCustTypeCode(String custTypeCode) {
        this.custTypeCode = custTypeCode;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getDivertCode() {
        return divertCode;
    }

    public void setDivertCode(String divertCode) {
        this.divertCode = divertCode;
    }
    
    public String getDmConsent() {
        return dmConsent;
    }

    public void setDmConsent(String dmConsent) {
        this.dmConsent = dmConsent;
    }
    
    public String getDmCompanyConsent() {
        return dmCompanyConsent;
    }

    public void setDmCompanyConsent(String dmCompanyConsent) {
        this.dmCompanyConsent = dmCompanyConsent;
    }
    
    public String getCommChannel() {
        return commChannel;
    }

    public void setCommChannel(String commChannel) {
        this.commChannel = commChannel;
    }
    
    public EpcContact getContact() {
        return contact;
    }

    public void setContact(EpcContact contact) {
        this.contact = contact;
    }
    
    public EpcAddress[] getAddress() {
        return address;
    }

    public void setAddress(EpcAddress[] address) {
        this.address = address;
    }
    
    public EpcSubscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(EpcSubscriber subscriber) {
        this.subscriber = subscriber;
    }
    
    @Override
    public String toString() {
        return "epc.crm.EpcCustomerProfile[ contact=" + contact + " ,address=" + address + " ]";
    }
}
