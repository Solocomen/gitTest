package epc.epcsalesapi.fes.waiving.bean;

public class EpcChgCheckWaiveResult {
	private String result;
	private String errorMessage;
	private EpcChgWaiveDetail chgWaiveDetailList;
	
	public EpcChgCheckWaiveResult() {}

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

	public EpcChgWaiveDetail getChgWaiveDetailList() {
		return chgWaiveDetailList;
	}

	public void setChgWaiveDetailList(EpcChgWaiveDetail chgWaiveDetailList) {
		this.chgWaiveDetailList = chgWaiveDetailList;
	}
}
