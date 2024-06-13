/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.util.List;

public class EpcPaymentCodeResult {
    
    private List<EpcPaymentCode> paymentCodeList;

    public List<EpcPaymentCode> getPaymentCodeList() {
        return paymentCodeList;
    }

    public void setPaymentCodeList(List<EpcPaymentCode> paymentCodeList) {
        this.paymentCodeList = paymentCodeList;
    }
    
}
