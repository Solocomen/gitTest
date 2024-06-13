package epc.epcsalesapi.sales.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcCopyQuoteResult {
	private String quoteLastUpdated;
	private String newQuoteId;
	
	public EpcCopyQuoteResult() {}

	public String getQuoteLastUpdated() {
		return quoteLastUpdated;
	}

	public void setQuoteLastUpdated(String quoteLastUpdated) {
		this.quoteLastUpdated = quoteLastUpdated;
	}

	public String getNewQuoteId() {
		return newQuoteId;
	}

	public void setNewQuoteId(String newQuoteId) {
		this.newQuoteId = newQuoteId;
	}
	
	
}
