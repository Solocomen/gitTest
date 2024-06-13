package epc.epcsalesapi.sales.bean;

public class EpcConvertQuoteResult2 {
	private String result;
	private String errMsg;
	private String originQuoteGuid;
	private String newQuoteGuid;
	private String orderLastUpdated;
	
	public EpcConvertQuoteResult2() {}

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

	public String getOriginQuoteGuid() {
		return originQuoteGuid;
	}

	public void setOriginQuoteGuid(String originQuoteGuid) {
		this.originQuoteGuid = originQuoteGuid;
	}

	public String getNewQuoteGuid() {
		return newQuoteGuid;
	}

	public void setNewQuoteGuid(String newQuoteGuid) {
		this.newQuoteGuid = newQuoteGuid;
	}

	public String getOrderLastUpdated() {
		return orderLastUpdated;
	}

	public void setOrderLastUpdated(String orderLastUpdated) {
		this.orderLastUpdated = orderLastUpdated;
	}
	
	
}
