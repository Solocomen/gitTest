package epc.epcsalesapi.stock.bean;

import java.util.ArrayList;

public class EpcConfirmErpReserveResult2 {
    private String returnCode;
	private String errorMessage;
    private ArrayList<EpcConfirmReserveItemReturn> orders;
    private ArrayList<String> failedReferenceNoList;
	
	
	public EpcConfirmErpReserveResult2() {}


    public String getReturnCode() {
        return returnCode;
    }


    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }


    public String getErrorMessage() {
        return errorMessage;
    }


    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    public ArrayList<EpcConfirmReserveItemReturn> getOrders() {
        return orders;
    }


    public void setOrders(ArrayList<EpcConfirmReserveItemReturn> orders) {
        this.orders = orders;
    }


    public ArrayList<String> getFailedReferenceNoList() {
        return failedReferenceNoList;
    }


    public void setFailedReferenceNoList(ArrayList<String> failedReferenceNoList) {
        this.failedReferenceNoList = failedReferenceNoList;
    }

}
