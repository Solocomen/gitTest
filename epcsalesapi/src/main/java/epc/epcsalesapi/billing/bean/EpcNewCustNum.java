package epc.epcsalesapi.billing.bean;

public class EpcNewCustNum {
	private int errorCode;
	private String errorMessage;
	private String customerRef;
	
	public EpcNewCustNum() {}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getCustomerRef() {
		return customerRef;
	}

	public void setCustomerRef(String customerRef) {
		this.customerRef = customerRef;
	}
	
	
}
