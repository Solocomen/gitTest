package epc.epcsalesapi.notification.bean;

import java.util.ArrayList;

public class EpcNotification {
	
	private int sigmaOrderId;
	private String acctNum;
	private String msgTemplateId;
	private ArrayList<EpcNotificationCharacteristic> characteristics;
	private String result;
	private String errMsg;
	
	public EpcNotification() {}

	public int getSigmaOrderId() {
		return sigmaOrderId;
	}

	public void setSigmaOrderId(int sigmaOrderId) {
		this.sigmaOrderId = sigmaOrderId;
	}

	public String getAcctNum() {
		return acctNum;
	}

	public void setAcctNum(String acctNum) {
		this.acctNum = acctNum;
	}

	public String getMsgTemplateId() {
		return msgTemplateId;
	}

	public void setMsgTemplateId(String msgTemplateId) {
		this.msgTemplateId = msgTemplateId;
	}

	public ArrayList<EpcNotificationCharacteristic> getCharacteristics() {
		return characteristics;
	}

	public void setCharacteristics(ArrayList<EpcNotificationCharacteristic> characteristics) {
		this.characteristics = characteristics;
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
	
	
}
