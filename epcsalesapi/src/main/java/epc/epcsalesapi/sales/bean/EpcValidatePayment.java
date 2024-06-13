package epc.epcsalesapi.sales.bean;

import java.util.List;

public class EpcValidatePayment {
    
    private String orderId;
    private List<EpcPayment> paymentList;
    private List<EpcCharge> chargeList;
    private String salesman;     // added by Danny Chan on 2023-9-4: check salesman in validatePayment API

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<EpcPayment> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<EpcPayment> paymentList) {
        this.paymentList = paymentList;
    }

    public List<EpcCharge> getChargeList() {
        return chargeList;
    }

    public void setChargeList(List<EpcCharge> chargeList) {
        this.chargeList = chargeList;
    }

    // added by Danny Chan on 2023-9-4: check salesman in validatePayment API - start
    public String getSalesman() {
       return salesman;
    }

    public void setSalesman(String salesman) {
       this.salesman = salesman;
    }
    // added by Danny Chan on 2023-9-4: check salesman in validatePayment API - end
}
