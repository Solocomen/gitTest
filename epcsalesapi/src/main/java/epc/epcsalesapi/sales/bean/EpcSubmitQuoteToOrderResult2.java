package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcSubmitQuoteToOrderResult2 {
	private String result;
    private String errMsg;
    private ArrayList<EpcSubmitQuoteToOrderResult> epcSubmitQuoteToOrderResultList;
    
    public EpcSubmitQuoteToOrderResult2() {}

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

	public ArrayList<EpcSubmitQuoteToOrderResult> getEpcSubmitQuoteToOrderResultList() {
		return epcSubmitQuoteToOrderResultList;
	}

	public void setEpcSubmitQuoteToOrderResultList(ArrayList<EpcSubmitQuoteToOrderResult> epcSubmitQuoteToOrderResultList) {
		this.epcSubmitQuoteToOrderResultList = epcSubmitQuoteToOrderResultList;
	}
    
    
}
