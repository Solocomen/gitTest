/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author KerryTsang
 */
public class EpcCreateEPCReceiptResult {
    private String result;
    private String errMsg;
    private String receiptNo;
    private BigDecimal totalAmount;
    private List<EpcOfferCharge> epcChargeList;

    public EpcCreateEPCReceiptResult() {
    }

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

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<EpcOfferCharge> getEpcChargeList() {
        return epcChargeList;
    }

    public void setEpcChargeList(List<EpcOfferCharge> epcChargeList) {
        this.epcChargeList = epcChargeList;
    }
    
    
}
