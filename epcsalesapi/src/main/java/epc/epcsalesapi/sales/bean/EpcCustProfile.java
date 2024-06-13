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
public class EpcCustProfile {
    private String custId;
	private int orderId;
	private int quoteId;
    private String caseId;
    private String itemId;
    private String custNum;
    private String subrNum;
    private String custFirstName;
    private String custLastName;
    private String custTitle;
    private String hkidbr;
    private String idType;
    private String dob;
    private String contactNo1;
    private String contactNo2;
    private String email;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String effectiveDate;
    private String paymentMethod;
    private String dno; // for MNP / MVNO
    private String mnpPrepaidSim;
    private String mnpHkidbr;
    private String lang;
    private String contactPerson;
    private String dm;
    private String dmCompany;
    private String dealerCode;
    private String action; // ADD / DELETE
    private String result;
    private String errMsg;

    public EpcCustProfile() {}

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

	public int getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(int quoteId) {
		this.quoteId = quoteId;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getCustNum() {
		return custNum;
	}

	public void setCustNum(String custNum) {
		this.custNum = custNum;
	}

	public String getSubrNum() {
		return subrNum;
	}

	public void setSubrNum(String subrNum) {
		this.subrNum = subrNum;
	}

	public String getCustFirstName() {
		return custFirstName;
	}

	public void setCustFirstName(String custFirstName) {
		this.custFirstName = custFirstName;
	}

	public String getCustLastName() {
		return custLastName;
	}

	public void setCustLastName(String custLastName) {
		this.custLastName = custLastName;
	}

	public String getCustTitle() {
		return custTitle;
	}

	public void setCustTitle(String custTitle) {
		this.custTitle = custTitle;
	}

	public String getHkidbr() {
		return hkidbr;
	}

	public void setHkidbr(String hkidbr) {
		this.hkidbr = hkidbr;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getContactNo1() {
		return contactNo1;
	}

	public void setContactNo1(String contactNo1) {
		this.contactNo1 = contactNo1;
	}

	public String getContactNo2() {
		return contactNo2;
	}

	public void setContactNo2(String contactNo2) {
		this.contactNo2 = contactNo2;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAddress4() {
		return address4;
	}

	public void setAddress4(String address4) {
		this.address4 = address4;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getDno() {
		return dno;
	}

	public void setDno(String dno) {
		this.dno = dno;
	}

	public String getMnpPrepaidSim() {
		return mnpPrepaidSim;
	}

	public void setMnpPrepaidSim(String mnpPrepaidSim) {
		this.mnpPrepaidSim = mnpPrepaidSim;
	}

	public String getMnpHkidbr() {
		return mnpHkidbr;
	}

	public void setMnpHkidbr(String mnpHkidbr) {
		this.mnpHkidbr = mnpHkidbr;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getDm() {
		return dm;
	}

	public void setDm(String dm) {
		this.dm = dm;
	}

	public String getDmCompany() {
		return dmCompany;
	}

	public void setDmCompany(String dmCompany) {
		this.dmCompany = dmCompany;
	}

	public String getDealerCode() {
		return dealerCode;
	}

	public void setDealerCode(String dealerCode) {
		this.dealerCode = dealerCode;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
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
