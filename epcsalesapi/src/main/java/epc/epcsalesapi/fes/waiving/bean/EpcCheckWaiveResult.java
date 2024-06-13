package epc.epcsalesapi.fes.waiving.bean;

public class EpcCheckWaiveResult {

	private String result;
	private String errorMessage;

	public EpcCheckWaiveResult() {}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
