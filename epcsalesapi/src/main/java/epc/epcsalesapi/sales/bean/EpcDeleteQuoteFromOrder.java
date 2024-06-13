package epc.epcsalesapi.sales.bean;

public class EpcDeleteQuoteFromOrder {
	private String custId;
	private int orderId;
	private int quoteId;
    private String deleteChannel;
    private String deleteUser;
    private String deleteSalesman;
    private String deleteLocation;
    private String result;
    private String errMsg;
    
    public EpcDeleteQuoteFromOrder() {}

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

	public int getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(int quoteId) {
		this.quoteId = quoteId;
	}

	public String getDeleteChannel() {
		return deleteChannel;
	}

	public void setDeleteChannel(String deleteChannel) {
		this.deleteChannel = deleteChannel;
	}

	public String getDeleteUser() {
		return deleteUser;
	}

	public void setDeleteUser(String deleteUser) {
		this.deleteUser = deleteUser;
	}

	public String getDeleteSalesman() {
		return deleteSalesman;
	}

	public void setDeleteSalesman(String deleteSalesman) {
		this.deleteSalesman = deleteSalesman;
	}

	public String getDeleteLocation() {
		return deleteLocation;
	}

	public void setDeleteLocation(String deleteLocation) {
		this.deleteLocation = deleteLocation;
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
