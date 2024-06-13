/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.login.bean;

import java.util.Date;

/**
 *
 * @author KerryTsang
 */
public class EpcSSOInfo {
    private String status;
    private String errorMessage;
    private String privatepersonalidentifier;
    private String upn;
    private String windowsaccountname;
    private String passwordexpirationdays;
    private String nameidentifier;
    private String name;
    private Date lastChangePDate;
    private String lastChangePDateTxtYYYYMMDD;
    private Date lastChangePDateGmt;
    private String lastChangePDateGmtTxtYYYYMMDD;
    private String needToChangeP;
    private int needToChangePDateLeft;

    public EpcSSOInfo() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getPrivatepersonalidentifier() {
        return privatepersonalidentifier;
    }

    public void setPrivatepersonalidentifier(String privatepersonalidentifier) {
        this.privatepersonalidentifier = privatepersonalidentifier;
    }

    public String getUpn() {
        return upn;
    }

    public void setUpn(String upn) {
        this.upn = upn;
    }

    public String getWindowsaccountname() {
        return windowsaccountname;
    }

    public void setWindowsaccountname(String windowsaccountname) {
        this.windowsaccountname = windowsaccountname;
    }

    public String getPasswordexpirationdays() {
        return passwordexpirationdays;
    }

    public void setPasswordexpirationdays(String passwordexpirationdays) {
        this.passwordexpirationdays = passwordexpirationdays;
    }

    public String getNameidentifier() {
        return nameidentifier;
    }

    public void setNameidentifier(String nameidentifier) {
        this.nameidentifier = nameidentifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastChangePDate() {
        return lastChangePDate;
    }

    public void setLastChangePDate(Date lastChangePDate) {
        this.lastChangePDate = lastChangePDate;
    }

    public String getLastChangePDateTxtYYYYMMDD() {
        return lastChangePDateTxtYYYYMMDD;
    }

    public void setLastChangePDateTxtYYYYMMDD(String lastChangePDateTxtYYYYMMDD) {
        this.lastChangePDateTxtYYYYMMDD = lastChangePDateTxtYYYYMMDD;
    }

    public Date getLastChangePDateGmt() {
        return lastChangePDateGmt;
    }

    public void setLastChangePDateGmt(Date lastChangePDateGmt) {
        this.lastChangePDateGmt = lastChangePDateGmt;
    }

    public String getLastChangePDateGmtTxtYYYYMMDD() {
        return lastChangePDateGmtTxtYYYYMMDD;
    }

    public void setLastChangePDateGmtTxtYYYYMMDD(String lastChangePDateGmtTxtYYYYMMDD) {
        this.lastChangePDateGmtTxtYYYYMMDD = lastChangePDateGmtTxtYYYYMMDD;
    }

    public String getNeedToChangeP() {
        return needToChangeP;
    }

    public void setNeedToChangeP(String needToChangeP) {
        this.needToChangeP = needToChangeP;
    }

    public int getNeedToChangePDateLeft() {
        return needToChangePDateLeft;
    }

    public void setNeedToChangePDateLeft(int needToChangePDateLeft) {
        this.needToChangePDateLeft = needToChangePDateLeft;
    }
    
    
}
