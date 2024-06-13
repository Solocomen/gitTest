/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

/**
 *
 * @author AdaTsang
 */
public class EpcUpdateOrderContact {
	private int orderId;
	private String contactNo;
	private String contactEmail;
    private String createChannel;
    private String createUser;
    private String createSalesman;
    private String createLocation;
    
    private String result;
    private String errMsg;

    public EpcUpdateOrderContact() {
    }

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getCreateChannel() {
		return createChannel;
	}

	public void setCreateChannel(String createChannel) {
		this.createChannel = createChannel;
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

	public String getCreateLocation() {
		return createLocation;
	}

	public void setCreateLocation(String createLocation) {
		this.createLocation = createLocation;
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
