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
public class EpcDeliveryInfo {
    private String custId;
    private int orderId;
    private ArrayList<EpcDeliveryDetail> details;
    private String result;
    private String errMsg;
	private String createUser;
	private String createSalesman;
	private String createChannel;
	private String createLocation;

    public EpcDeliveryInfo() {
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

	public ArrayList<EpcDeliveryDetail> getDetails() {
		return details;
	}

	public void setDetails(ArrayList<EpcDeliveryDetail> details) {
		this.details = details;
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

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateSalesman() {
		return createSalesman;
	}

	public void setCreateSalesman(String createSalesman) {
		this.createSalesman = createSalesman;
	}

	public String getCreateChannel() {
		return createChannel;
	}

	public void setCreateChannel(String createChannel) {
		this.createChannel = createChannel;
	}

	public String getCreateLocation() {
		return createLocation;
	}

	public void setCreateLocation(String createLocation) {
		this.createLocation = createLocation;
	}    
    
	
}
