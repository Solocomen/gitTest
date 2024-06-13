/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

/**
 *
 * @author KerryTsang
 */
public class EpcCreatePayment {
    private String custId;
    private int orderId;
    private ArrayList<EpcPayment> paymentList;
    private String saveStatus;
    private String errMsg;

    public EpcCreatePayment() {
    }

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

	public ArrayList<EpcPayment> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(ArrayList<EpcPayment> paymentList) {
        this.paymentList = paymentList;
    }

    public String getSaveStatus() {
        return saveStatus;
    }

    public void setSaveStatus(String saveStatus) {
        this.saveStatus = saveStatus;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
    
    
}
