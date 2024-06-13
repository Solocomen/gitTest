/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author KerryTsang
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcFulfillOrder {
    private int orderId; // smc order id
    private ArrayList<EpcOrderItem> items;
    private String fulfillUser;
    private String fulfillSalesman;
    private String fulfillLocation;
    private String fulfillChannel;

    public EpcFulfillOrder() {
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public ArrayList<EpcOrderItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<EpcOrderItem> items) {
        this.items = items;
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
