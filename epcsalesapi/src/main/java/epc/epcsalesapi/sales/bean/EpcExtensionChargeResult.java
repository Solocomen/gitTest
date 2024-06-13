package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcExtensionChargeResult {
    
    private String resultCode;
    private String errorMessage;
    private ArrayList<EpcCharge> extensionChargeList;
    
    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ArrayList<EpcCharge> getExtensionChargeList() {
        return extensionChargeList;
    }

    public void setExtensionChargeList(ArrayList<EpcCharge> extensionChargeList) {
        this.extensionChargeList = extensionChargeList;
    }

}
