package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcNotificationMessage {
	private String msgId;
	private String sendType; // EMAIL / SMS
	private String senderName;
	private String senderEmail;
	private String toAddr;
	private String ccAddr;
	private String bccAddr;
	private String subject;
	private String content;
	private String requestSystem;
	private String requestId;
	private String result;
	private String errMsg;
	private ArrayList<EpcNotificationAttachment> attachmentList;
	private String orderId;		// added by Danny Chan on 2022-12-1 (SHK Point Payment Enhancement) 
	private String completeDate;
	private String status;
	private String requestSystemDesc;

	public EpcNotificationMessage() {}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getToAddr() {
		return toAddr;
	}

	public void setToAddr(String toAddr) {
		this.toAddr = toAddr;
	}

	public String getCcAddr() {
		return ccAddr;
	}

	public void setCcAddr(String ccAddr) {
		this.ccAddr = ccAddr;
	}

	public String getBccAddr() {
		return bccAddr;
	}

	public void setBccAddr(String bccAddr) {
		this.bccAddr = bccAddr;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRequestSystem() {
		return requestSystem;
	}

	public void setRequestSystem(String requestSystem) {
		this.requestSystem = requestSystem;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
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

	public ArrayList<EpcNotificationAttachment> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(ArrayList<EpcNotificationAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(String completeDate) {
		this.completeDate = completeDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRequestSystemDesc() {
		return requestSystemDesc;
	}

	public void setRequestSystemDesc(String requestSystemDesc) {
		this.requestSystemDesc = requestSystemDesc;
	}
	
}
