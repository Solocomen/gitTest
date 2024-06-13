package epc.epcsalesapi.sales.bean;

/**
 *
 *   created by Danny Chan on 2023-12-8 (create a new api getting valid offer(s) under an order for later trade-in)
 */
public class EpcOffer {
	private String caseId;
	private String offerDesc;
	private String offerDescChi;

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getOfferDesc() {
		return offerDesc;
	}

	public void setOfferDesc(String offerDesc) {
		this.offerDesc = offerDesc;
	}

	public String getOfferDescChi() {
		return offerDescChi;
	}

	public void setOfferDescChi(String offerDescChi) {
		this.offerDescChi = offerDescChi;
	}
}
