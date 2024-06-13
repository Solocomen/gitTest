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
public class EpcChangeBagResult {
    private String result;
    private String errMsg;
    private String oldCustId;
    private String oldQuoteGuid;
    private String newCustId;
    private String newQuoteGuid;

    public EpcChangeBagResult() {
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

    public String getOldCustId() {
        return oldCustId;
    }

    public void setOldCustId(String oldCustId) {
        this.oldCustId = oldCustId;
    }

    public String getOldQuoteGuid() {
        return oldQuoteGuid;
    }

    public void setOldQuoteGuid(String oldQuoteGuid) {
        this.oldQuoteGuid = oldQuoteGuid;
    }

    public String getNewCustId() {
        return newCustId;
    }

    public void setNewCustId(String newCustId) {
        this.newCustId = newCustId;
    }

    public String getNewQuoteGuid() {
        return newQuoteGuid;
    }

    public void setNewQuoteGuid(String newQuoteGuid) {
        this.newQuoteGuid = newQuoteGuid;
    }
    
    
}
