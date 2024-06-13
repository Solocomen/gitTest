/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.login.bean;

import java.util.ArrayList;

import epc.epcsalesapi.fes.user.FesUser;

/**
 *
 * @author KerryTsang
 */
public class EpcLoginResult {
    private String result;
    private String errMsg;
    private String staffNo;
    private ArrayList<FesUser> userList;

    public EpcLoginResult() {
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getStaffNo() {
        return staffNo;
    }

    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
    }

    public ArrayList<FesUser> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<FesUser> userList) {
        this.userList = userList;
    }
    
    
}
