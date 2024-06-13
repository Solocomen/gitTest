package epc.epcsalesapi.stock.bean;

public class EpcDoaStockResult {
	private String result;
	private String errMsg;
	private String transferNote;

	public EpcDoaStockResult() {}

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

	public String getTransferNote() {
		return transferNote;
	}

	public void setTransferNote(String transferNote) {
		this.transferNote = transferNote;
	}


}
