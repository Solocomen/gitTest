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
public class EpcCreateOrder {
    private String custId;
    private String orderChannel;
    private String orderUser;
    private String orderSalesman;
    private String orderLocation;
    private int orderId;
    private String orderType; // NORMAL / FAST_CHECKOUT

    public EpcCreateOrder() {
    }

    public String getCustId() {
        return custId;
    }

    public String getOrderChannel() {
        return orderChannel;
    }

    public String getOrderUser() {
        return orderUser;
    }

    public String getOrderSalesman() {
        return orderSalesman;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public void setOrderChannel(String orderChannel) {
        this.orderChannel = orderChannel;
    }

    public void setOrderUser(String orderUser) {
        this.orderUser = orderUser;
    }

    public void setOrderSalesman(String orderSalesman) {
        this.orderSalesman = orderSalesman;
    }

	public String getOrderLocation() {
		return orderLocation;
	}

	public void setOrderLocation(String orderLocation) {
		this.orderLocation = orderLocation;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
}
