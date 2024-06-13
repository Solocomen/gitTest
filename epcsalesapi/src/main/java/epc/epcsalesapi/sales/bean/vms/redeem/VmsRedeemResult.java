package epc.epcsalesapi.sales.bean.vms.redeem;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import epc.epcsalesapi.sales.bean.vms.VmsVoucher;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VmsRedeemResult {
    private int statusCode;
    private String statusDesc;
    private String quoteId;
    private ArrayList<VmsVoucher> vouchers;

    public VmsRedeemResult() {}

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public ArrayList<VmsVoucher> getVouchers() {
        return vouchers;
    }

    public void setVouchers(ArrayList<VmsVoucher> vouchers) {
        this.vouchers = vouchers;
    }

    
}
