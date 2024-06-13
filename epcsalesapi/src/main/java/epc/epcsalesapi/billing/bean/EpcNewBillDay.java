package epc.epcsalesapi.billing.bean;

public class EpcNewBillDay {
	private int errorCode;
	private String errorMessage;
	private String nextBillDay;
	
	public EpcNewBillDay() {}

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

	public String getNextBillDay() {
		return nextBillDay;
	}

	public void setNextBillDay(String nextBillDay) {
		this.nextBillDay = nextBillDay;
	}

}
