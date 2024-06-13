package epc.epcsalesapi.sales.bean;

import java.util.List;

public class EpcExtendAllReserveTmpTicket {
	private String custId;
	private int orderId;
	private int extendMins;
	private String createUser;
	private String createSalesman;
	private String createChannel;
	private String createLocation;
	private String result;
	private String errMsg;
	private List<String> failItemList;
	
	public EpcExtendAllReserveTmpTicket() {}

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

	public int getExtendMins() {
		return extendMins;
	}

	public void setExtendMins(int extendMins) {
		this.extendMins = extendMins;
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

	public List<String> getFailItemList() {
		return failItemList;
	}

	public void setFailItemList(List<String> failItemList) {
		this.failItemList = failItemList;
	}
	
	
}
