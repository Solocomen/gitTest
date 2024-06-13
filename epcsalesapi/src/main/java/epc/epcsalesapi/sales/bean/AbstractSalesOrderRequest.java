/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.sales.bean.AbstractSalesOrderRequest
 * @author	TedKwan
 * @date	23-Sep-2022
 * Description:
 *
 * History:
 * 20220923-TedKwan: Created
 */
package epc.epcsalesapi.sales.bean;

public abstract class AbstractSalesOrderRequest extends AbstractEPCAPIRequest {
	
	private String custId;
	private int orderId;
	
	public boolean validateOrderInfo() throws Exception {
    	if( custId == null 	 || custId.trim().equals("") || 
    		orderId == 0 ) {
    		throw new Exception("Invalid custId/orderId");
    	}
    	
    	return true;
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
	 * @return the orderId
	 */
	public int getOrderId() {
		return orderId;
	}
	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
}
