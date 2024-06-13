package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcValidatePaymentResult {

    private String result;
    private ArrayList<EpcValidatePaymentError> errorList;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ArrayList<EpcValidatePaymentError> getErrorList() {
        return errorList;
    }

    public void setErrorList(ArrayList<EpcValidatePaymentError> errorList) {
        this.errorList = errorList;
    }
}
