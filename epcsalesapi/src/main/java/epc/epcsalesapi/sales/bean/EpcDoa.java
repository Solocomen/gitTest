package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcDoa {
	private int orderId;
	private String custId;
	private ArrayList<EpcOrderItem> items;
	private String doaLocation;
	private String result;
	private String errMsg;
	private String user;
    private String salesman;
    private String location;
    private String channel;
    private String symptom;
	private ArrayList<String> transferNotes;
	private String approveBy;
	private String waiveFormCode;
	
	public EpcDoa() {}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public ArrayList<EpcOrderItem> getItems() {
		return items;
	}

	public void setItems(ArrayList<EpcOrderItem> items) {
		this.items = items;
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

	public String getDoaLocation() {
		return doaLocation;
	}

	public void setDoaLocation(String doaLocation) {
		this.doaLocation = doaLocation;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getSalesman() {
		return salesman;
	}

	public void setSalesman(String salesman) {
		this.salesman = salesman;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * @return the symptom
	 */
	public String getSymptom() {
		return symptom;
	}

	/**
	 * @param symptom the symptom to set
	 */
	public void setSymptom(String symptom) {
		this.symptom = symptom;
	}

	public ArrayList<String> getTransferNotes() {
		return transferNotes;
	}

	public void setTransferNotes(ArrayList<String> transferNotes) {
		this.transferNotes = transferNotes;
	}

	public String getApproveBy() {
		return approveBy;
	}

	public void setApproveBy(String approveBy) {
		this.approveBy = approveBy;
	}

	public String getWaiveFormCode() {
		return waiveFormCode;
	}

	public void setWaiveFormCode(String waiveFormCode) {
		this.waiveFormCode = waiveFormCode;
	}


	
}
