package epc.epcsalesapi.stock.bean;

import java.util.ArrayList;

public class EpcStockReserveStatusResult {
	private ArrayList<EpcStockReserveStatus> statusList;
	
	public EpcStockReserveStatusResult() {}

	public ArrayList<EpcStockReserveStatus> getStatusList() {
		return statusList;
	}

	public void setStatusList(ArrayList<EpcStockReserveStatus> statusList) {
		this.statusList = statusList;
	}
	
	
}
