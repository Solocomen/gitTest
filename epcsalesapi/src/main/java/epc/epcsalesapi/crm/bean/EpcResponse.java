/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.crm.bean;

/**
 *
 * @author KenTKChung
 */
public class EpcResponse {
    private String resultCode;
    private String resultMsg;
    private String subscriberRecId;

   
    public EpcResponse() {
	super();
    }
	
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getResultMsg() {
        return resultMsg;
    }
    
    public void setSubscriberRecId(String subscriberRecId) {
    	this.subscriberRecId = subscriberRecId;
    }

    public String getSubscriberRecId() {
    	return subscriberRecId;
    }
}
