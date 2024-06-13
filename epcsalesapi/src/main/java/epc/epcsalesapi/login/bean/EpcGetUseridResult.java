/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.login.bean;

/**
 *
 * @author KerryTsang
 */
public class EpcGetUseridResult {
    private int resultCode;
    private int[] userID;

    public EpcGetUseridResult() {
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int[] getUserID() {
        return userID;
    }

    public void setUserID(int[] userID) {
        this.userID = userID;
    }
    
    
}
