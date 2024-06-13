package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;
import java.util.HashMap;

public class EpcGetCustProfileResult {

	private String custId;
	private int orderId;
	private String result;
	private String errMsg;
	private ArrayList<HashMap<String, Object>> custList;
	
	public EpcGetCustProfileResult() {}

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

	public ArrayList<HashMap<String, Object>> getCustList() {
		return custList;
	}

	public void setCustList(ArrayList<HashMap<String, Object>> custList) {
		this.custList = custList;
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
	
	
}
