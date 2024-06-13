package epc.epcsalesapi.sales.bean;

public class EpcSmcQuote {
	private int orderId;
	private int quoteId;
	private String quoteGuid;
	private EpcQuote epcQuote;
	
	public EpcSmcQuote() {}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getQuoteId() {
		return quoteId;
	}

	public String getQuoteGuid() {
		return quoteGuid;
	}

	public void setQuoteGuid(String quoteGuid) {
		this.quoteGuid = quoteGuid;
	}

	public void setQuoteId(int quoteId) {
		this.quoteId = quoteId;
	}

	public EpcQuote getEpcQuote() {
		return epcQuote;
	}

	public void setEpcQuote(EpcQuote epcQuote) {
		this.epcQuote = epcQuote;
	}
	
	
}
