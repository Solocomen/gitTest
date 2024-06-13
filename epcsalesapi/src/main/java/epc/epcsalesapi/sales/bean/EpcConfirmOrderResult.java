package epc.epcsalesapi.sales.bean;

public class EpcConfirmOrderResult {
    
    private String CustID;
    private String OrderID;
    private String result;
    private String errorCode;
    private String errorMessage;
    
    public EpcConfirmOrderResult() {}

    public void setCustID(String CustID) {
        this.CustID = CustID;
    }

    public String getCustID() {
        return CustID;
    }

    public void setOrderID(String OrderID) {
        this.OrderID = OrderID;
    }

    public String getOrderID() {
        return OrderID;
    }

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
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

