package epc.epcsalesapi.sales.bean;

/**
 *
 * @author KerryTsang
 */
public class EpcPlaceOrderResult {
    private String custId;
    private int orderId;
    private String saveStatus;
    private String errorCode;
    private String errorMessage;
    private EpcOrderInfo epcOrderInfo;
    
    private String receiptNo;		// added by Danny Chan on 2022-11-15 (SHK Point Payment Enhancement)

    public EpcPlaceOrderResult() {
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

    public EpcOrderInfo getEpcOrderInfo() {
        return epcOrderInfo;
    }

    public void setEpcOrderInfo(EpcOrderInfo epcOrderInfo) {
        this.epcOrderInfo = epcOrderInfo;
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
