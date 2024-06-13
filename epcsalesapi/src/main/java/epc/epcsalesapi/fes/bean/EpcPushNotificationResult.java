package epc.epcsalesapi.fes.bean;

public class EpcPushNotificationResult {
	private String result;
	private String barcode;
	private String errMsg;
	
	public EpcPushNotificationResult() {}

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

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	
	
}