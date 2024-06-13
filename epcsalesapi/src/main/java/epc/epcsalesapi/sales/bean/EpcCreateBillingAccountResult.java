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
public class EpcCreateBillingAccountResult {
    String result;
    String errMsg;
//    HashMap<String, String> subrKeyMap; // case_id, subr key

    public EpcCreateBillingAccountResult() {
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

//    public HashMap<String, String> getSubrKeyMap() {
//        return subrKeyMap;
//    }
//
//    public void setSubrKeyMap(HashMap<String, String> subrKeyMap) {
//        this.subrKeyMap = subrKeyMap;
//    }
    
}
