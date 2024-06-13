package epc.epcsalesapi.sales.bean;

public class EpcSaveDeliveryInfoResult {
	private String custId;
	private int orderId;
	private String saveStatus;
    private String errorCode;
    private String errorMessage;
    
    public EpcSaveDeliveryInfoResult() {}

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

	public String getSaveStatus() {
		return saveStatus;
	}

	public void setSaveStatus(String saveStatus) {
		this.saveStatus = saveStatus;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	
    
    
}
