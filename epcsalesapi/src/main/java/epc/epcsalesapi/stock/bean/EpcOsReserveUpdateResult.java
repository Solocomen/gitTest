package epc.epcsalesapi.stock.bean;

public class EpcOsReserveUpdateResult {
	
	private String result;
	private String errorMsg;
	private String receiptNo;
	private String extendReceiptNo;

	public EpcOsReserveUpdateResult() {}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getExtendReceiptNo() {
		return extendReceiptNo;
	}

	public void setExtendReceiptNo(String extendReceiptNo) {
		this.extendReceiptNo = extendReceiptNo;
	}
	
	
}
