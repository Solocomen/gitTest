/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.fes.user;

/**
 *
 * @author KerryTsang
 */
public class FesUser {
    private int userid;
    private String username;
    private int groupid;
    private int subgroupId;
    private String accessrights;
    private String salesman;
    private String acctPosLoc;
    private String streetFighter;
    private String dummyAccount;
    private String lang;
    private String staffId;
    private int rbdUnitId;
    private String groupName;
    private String emailAddress;

    public FesUser() {
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public int getSubgroupId() {
        return subgroupId;
    }

    public void setSubgroupId(int subgroupId) {
        this.subgroupId = subgroupId;
    }

    public String getAccessrights() {
        return accessrights;
    }

    public void setAccessrights(String accessrights) {
        this.accessrights = accessrights;
    }

    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public String getAcctPosLoc() {
        return acctPosLoc;
    }

    public void setAcctPosLoc(String acctPosLoc) {
        this.acctPosLoc = acctPosLoc;
    }

    public String getStreetFighter() {
        return streetFighter;
    }

    public void setStreetFighter(String streetFighter) {
        this.streetFighter = streetFighter;
    }

    public String getDummyAccount() {
        return dummyAccount;
    }

    public void setDummyAccount(String dummyAccount) {
        this.dummyAccount = dummyAccount;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public int getRbdUnitId() {
        return rbdUnitId;
    }

    public void setRbdUnitId(int rbdUnitId) {
        this.rbdUnitId = rbdUnitId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    
}
