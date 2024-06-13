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
public class EpcPlaceOrder {
    private String custId;
    private int orderId;
    private String fulfillUser;
    private String fulfillSalesman;
    private String fulfillLocation;
    private String fulfillChannel;

    public EpcPlaceOrder() {
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

    public String getFulfillUser() {
        return fulfillUser;
    }

    public void setFulfillUser(String fulfillUser) {
        this.fulfillUser = fulfillUser;
    }

    public String getFulfillSalesman() {
        return fulfillSalesman;
    }

    public void setFulfillSalesman(String fulfillSalesman) {
        this.fulfillSalesman = fulfillSalesman;
    }

    public String getFulfillLocation() {
        return fulfillLocation;
    }

    public void setFulfillLocation(String fulfillLocation) {
        this.fulfillLocation = fulfillLocation;
    }

	public String getFulfillChannel() {
		return fulfillChannel;
	}

	public void setFulfillChannel(String fulfillChannel) {
		this.fulfillChannel = fulfillChannel;
	}
    
}
