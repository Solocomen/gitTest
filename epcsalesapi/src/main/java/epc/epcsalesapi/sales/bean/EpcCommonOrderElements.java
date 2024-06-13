/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.sales.bean.EpcCommonOrderElements
 * @author	TedKwan
 * @date	21-Dec-2022
 * Description:
 *
 * History:
 * 20221221-TedKwan: Created
 */
package epc.epcsalesapi.sales.bean;

public class EpcCommonOrderElements {

	private Integer orderId;
	
	private Integer quoteId;
	
	private String caseId;
	
	private String custId;
	
	private String custNum;
	
	private String subrNum;

	/**
	 * @return the orderId
	 */
	public Integer getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the quoteId
	 */
	public Integer getQuoteId() {
		return quoteId;
	}

	/**
	 * @param quoteId the quoteId to set
	 */
	public void setQuoteId(Integer quoteId) {
		this.quoteId = quoteId;
	}

	/**
	 * @return the caseId
	 */
	public String getCaseId() {
		return caseId;
	}

	/**
	 * @param caseId the caseId to set
	 */
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	/**
	 * @return the custId
	 */
	public String getCustId() {
		return custId;
	}

	/**
	 * @param custId the custId to set
	 */
	public void setCustId(String custId) {
		this.custId = custId;
	}

	/**
	 * @return the custNum
	 */
	public String getCustNum() {
		return custNum;
	}

	/**
	 * @param custNum the custNum to set
	 */
	public void setCustNum(String custNum) {
		this.custNum = custNum;
	}

	/**
	 * @return the subrNum
	 */
	public String getSubrNum() {
		return subrNum;
	}

	/**
	 * @param subrNum the subrNum to set
	 */
	public void setSubrNum(String subrNum) {
		this.subrNum = subrNum;
	}
	
}
