/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.sales.bean.EpcOrderAttachResponse
 * @author	TedKwan
 * @date	21-Jul-2022
 * Description:
 *
 * History:
 * 20220721-TedKwan: Created
 */
package epc.epcsalesapi.sales.bean.orderAttachment;

import java.util.ArrayList;

import epc.epcsalesapi.sales.bean.AbstractEPCAPIResponse;

public class EpcOrderAttachResponse extends AbstractEPCAPIResponse {
	private Integer recId;
	private String custId;
	private Integer orderId;
	private ArrayList<EpcOrderAttach> epcOrderAttach;
	
	public Integer getRecId() {
		return recId;
	}
	public void setRecId(Integer recId) {
		this.recId = recId;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public ArrayList<EpcOrderAttach> getEpcOrderAttach() {
		return epcOrderAttach;
	}
	public void setEpcOrderAttach(ArrayList<EpcOrderAttach> epcOrderAttach) {
		this.epcOrderAttach = epcOrderAttach;
	}
	
}
