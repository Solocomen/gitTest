package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcOrderQuoteInfo {
	private int quoteId;
	private String quoteGuid;
	private EpcQuote epcQuote;
	private ArrayList<EpcOrderCaseInfo> epcOrderCaseInfoList;
	
	public EpcOrderQuoteInfo() {}

	public int getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(int quoteId) {
		this.quoteId = quoteId;
	}

	public String getQuoteGuid() {
		return quoteGuid;
	}

	public void setQuoteGuid(String quoteGuid) {
		this.quoteGuid = quoteGuid;
	}

	public ArrayList<EpcOrderCaseInfo> getEpcOrderCaseInfoList() {
		return epcOrderCaseInfoList;
	}

	public void setEpcOrderCaseInfoList(ArrayList<EpcOrderCaseInfo> epcOrderCaseInfoList) {
		this.epcOrderCaseInfoList = epcOrderCaseInfoList;
	}

	public EpcQuote getEpcQuote() {
		return epcQuote;
	}

	public void setEpcQuote(EpcQuote epcQuote) {
		this.epcQuote = epcQuote;
	}
}
