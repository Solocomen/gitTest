package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcAddOrderAttrs {
	private String custId;
	private int orderId;
	private ArrayList<EpcOrderAttr> attrs;
	private String result;
	private String errMsg;
	
	public EpcAddOrderAttrs() {}

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

	public ArrayList<EpcOrderAttr> getAttrs() {
		return attrs;
	}

	public void setAttrs(ArrayList<EpcOrderAttr> attrs) {
		this.attrs = attrs;
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
