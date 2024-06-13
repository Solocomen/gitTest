/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.util.List;
/**
 *
 * @author williamtam
 */
public class EpcGetCharge {
    private String orderId;
    private List<EpcPayment> paymentList;
    private String fromPlaceOrder;
    private boolean savePayment;
    private String salesActionType;

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

    public String getFromPlaceOrder() {
        return fromPlaceOrder;
    }

    public void setFromPlaceOrder(String fromPlaceOrder) {
        this.fromPlaceOrder = fromPlaceOrder;
    }

    public boolean isSavePayment() {
        return savePayment;
    }

    public void setSavePayment(boolean savePayment) {
        this.savePayment = savePayment;
    }

    public String getSalesActionType() {
        return salesActionType;
    }

    public void setSalesActionType(String salesActionType) {
        this.salesActionType = salesActionType;
    }

}
