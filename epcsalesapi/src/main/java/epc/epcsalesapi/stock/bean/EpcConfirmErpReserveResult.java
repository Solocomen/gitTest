package epc.epcsalesapi.stock.bean;

public class EpcConfirmErpReserveResult {
	private int caseID;
	private int result;
	private String errorMessage;
	private String status;
	
	public EpcConfirmErpReserveResult() {}

	public int getCaseID() {
		return caseID;
	}

	public void setCaseID(int caseID) {
		this.caseID = caseID;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
