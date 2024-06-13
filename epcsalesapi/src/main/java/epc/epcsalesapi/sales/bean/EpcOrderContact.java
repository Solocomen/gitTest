package epc.epcsalesapi.sales.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcOrderContact {
	private String custId;
	private int orderId;
	private String contactEmail;
	private String contactNo;
	private String orderLang;
	private String contactPersonFirstName;
	private String contactPersonLastName;
	private String contactPersonTitle;
	private String isExistingCust;
	private String result;
	private String errMsg;
	
	public EpcOrderContact() {}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getOrderLang() {
		return orderLang;
	}

	public void setOrderLang(String orderLang) {
		this.orderLang = orderLang;
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

	public String getContactPersonFirstName() {
		return contactPersonFirstName;
	}

	public void setContactPersonFirstName(String contactPersonFirstName) {
		this.contactPersonFirstName = contactPersonFirstName;
	}

	public String getContactPersonLastName() {
		return contactPersonLastName;
	}

	public void setContactPersonLastName(String contactPersonLastName) {
		this.contactPersonLastName = contactPersonLastName;
	}

	public String getContactPersonTitle() {
		return contactPersonTitle;
	}

	public void setContactPersonTitle(String contactPersonTitle) {
		this.contactPersonTitle = contactPersonTitle;
	}

	public String getIsExistingCust() {
		return isExistingCust;
	}

	public void setIsExistingCust(String isExistingCust) {
		this.isExistingCust = isExistingCust;
	}
	
	
}
