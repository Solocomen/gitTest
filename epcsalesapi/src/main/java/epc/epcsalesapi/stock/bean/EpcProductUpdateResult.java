package epc.epcsalesapi.stock.bean;

public class EpcProductUpdateResult {
	
	private int result;
    private String errorMessage;
    private String transferNote;
    
    public EpcProductUpdateResult() {}

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

	public String getTransferNote() {
		return transferNote;
	}

	public void setTransferNote(String transferNote) {
		this.transferNote = transferNote;
	}

}
