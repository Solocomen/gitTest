package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

/**
 *
 *   created by Danny Chan on 2023-12-8 (create a new api getting valid offer(s) under an order for later trade-in)
 */
public class EpcGetValidOfferResult {
	private String result;
	private String errMsg;
	private int orderId;
	private ArrayList<EpcOffer> offers;

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

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public ArrayList<EpcOffer> getOffers() {
		return offers;
	}

	public void setOffers(ArrayList<EpcOffer> offers) {
		this.offers = offers;
	}
}
