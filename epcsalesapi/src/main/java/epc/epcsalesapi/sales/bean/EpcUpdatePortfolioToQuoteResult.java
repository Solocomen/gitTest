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
public class EpcUpdatePortfolioToQuoteResult {
    String result;
    String errMsg;
    EpcQuoteItem epcQuoteItem;

    public EpcUpdatePortfolioToQuoteResult() {
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

    public EpcQuoteItem getEpcQuoteItem() {
        return epcQuoteItem;
    }

    public void setEpcQuoteItem(EpcQuoteItem epcQuoteItem) {
        this.epcQuoteItem = epcQuoteItem;
    }
    
    
}
