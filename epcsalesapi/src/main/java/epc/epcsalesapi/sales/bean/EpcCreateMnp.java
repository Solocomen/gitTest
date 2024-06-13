/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

/**
 *
 * @author KerryTsang
 */
public class EpcCreateMnp {
    private String numberType;
    private String msisdn;
    private String rno;
    private String dno;
    private String cutoverDate;
    private String dealerCode;
    private String isPrepaid;
    private String serviceType;
    private String orgService;
    private String existService;
    private String subrName;
    private String subrIDBR;
    private String userName;

    public EpcCreateMnp() {
    }

    public String getNumberType() {
        return numberType;
    }

    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getRno() {
        return rno;
    }

    public void setRno(String rno) {
        this.rno = rno;
    }

    public String getDno() {
        return dno;
    }

    public void setDno(String dno) {
        this.dno = dno;
    }

    public String getCutoverDate() {
        return cutoverDate;
    }

    public void setCutoverDate(String cutoverDate) {
        this.cutoverDate = cutoverDate;
    }

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    public String getIsPrepaid() {
        return isPrepaid;
    }

    public void setIsPrepaid(String isPrepaid) {
        this.isPrepaid = isPrepaid;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getOrgService() {
        return orgService;
    }

    public void setOrgService(String orgService) {
        this.orgService = orgService;
    }

    public String getExistService() {
        return existService;
    }

    public void setExistService(String existService) {
        this.existService = existService;
    }

    public String getSubrName() {
        return subrName;
    }

    public void setSubrName(String subrName) {
        this.subrName = subrName;
    }

    public String getSubrIDBR() {
        return subrIDBR;
    }

    public void setSubrIDBR(String subrIDBR) {
        this.subrIDBR = subrIDBR;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    
}
