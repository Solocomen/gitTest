package epc.epcsalesapi.stock.bean;

public class EpcOsReserveResult {
	private String queueType;
	private String caseId;
	private String allowIssueInvoice;
	private String originalOrderDate;
	private String media;
	private String errorMessage;
	private String flashBuy;
	
	public EpcOsReserveResult() {}

	public String getQueueType() {
		return queueType;
	}

	public void setQueueType(String queueType) {
		this.queueType = queueType;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getAllowIssueInvoice() {
		return allowIssueInvoice;
	}

	public void setAllowIssueInvoice(String allowIssueInvoice) {
		this.allowIssueInvoice = allowIssueInvoice;
	}

	public String getOriginalOrderDate() {
		return originalOrderDate;
	}

	public void setOriginalOrderDate(String originalOrderDate) {
		this.originalOrderDate = originalOrderDate;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getFlashBuy() {
		return flashBuy;
	}

	public void setFlashBuy(String flashBuy) {
		this.flashBuy = flashBuy;
	}
	
	

}
