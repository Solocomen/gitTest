package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcSettlePayment {
    private String custId;
    private int orderId;
    private ArrayList<String> itemIdList;
    private ArrayList<EpcPayment> paymentList;
    private String createUser;
    private String createSalesman;
    private String createChannel;
    private String createLocation;
    private String result;
    private String errMsg;
    private String receiptNo;       // added by Danny Chan on 2022-11-15 (SHK Point Payment Enhancement)

    public EpcSettlePayment() {
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

    public ArrayList<String> getItemIdList() {
        return itemIdList;
    }

    public void setItemIdList(ArrayList<String> itemIdList) {
        this.itemIdList = itemIdList;
    }

    public ArrayList<EpcPayment> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(ArrayList<EpcPayment> paymentList) {
        this.paymentList = paymentList;
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

	/**
	 * @return the receiptNo
	 */
	public String getReceiptNo() {
		return receiptNo;
	}

	/**
	 * @param receiptNo the receiptNo to set
	 */
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

}
