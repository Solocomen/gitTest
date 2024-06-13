package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcValidateOrderResult {
	private String result;
	private ArrayList<EpcValidateOrderError> epcValidateOrderErrorList;
	private EpcOrderInfo epcOrderInfo;
	
	public EpcValidateOrderResult() {}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public ArrayList<EpcValidateOrderError> getEpcValidateOrderErrorList() {
		return epcValidateOrderErrorList;
	}

	public void setEpcValidateOrderErrorList(ArrayList<EpcValidateOrderError> epcValidateOrderErrorList) {
		this.epcValidateOrderErrorList = epcValidateOrderErrorList;
	}

	public EpcOrderInfo getEpcOrderInfo() {
		return epcOrderInfo;
	}

	public void setEpcOrderInfo(EpcOrderInfo epcOrderInfo) {
		this.epcOrderInfo = epcOrderInfo;
	}

}
