/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.util.HashMap;

/**
 *
 * @author KerryTsang
 */
public class EpcUpdateModifiedItemToQuoteResult {
    private String result;
    private String errMsg;
    private EpcQuoteItem epcQuoteItem;
    private HashMap<String, Object> errMsg2;

    public EpcUpdateModifiedItemToQuoteResult() {
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

    public HashMap<String, Object> getErrMsg2() {
        return errMsg2;
    }

    public void setErrMsg2(HashMap<String, Object> errMsg2) {
        this.errMsg2 = errMsg2;
    }
    
    
}
