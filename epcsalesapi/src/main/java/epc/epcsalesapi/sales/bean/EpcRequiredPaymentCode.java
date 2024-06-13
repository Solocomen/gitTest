package epc.epcsalesapi.sales.bean;

import java.util.List;

public class EpcRequiredPaymentCode extends EpcValidateItem {
    
    private List<String> paymentCodeList;

    public List<String> getPaymentCodeList() {
        return paymentCodeList;
    }

    public void setPaymentCodeList(List<String> paymentCodeList) {
        this.paymentCodeList = paymentCodeList;
    }

}
