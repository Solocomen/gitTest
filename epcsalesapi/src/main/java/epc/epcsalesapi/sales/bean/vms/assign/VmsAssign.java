package epc.epcsalesapi.sales.bean.vms.assign;

import java.util.ArrayList;

import epc.epcsalesapi.sales.bean.vms.VmsVoucher;

public class VmsAssign {
    private String custId;
    private String quoteId;
    private ArrayList<VmsVoucher> vouchers;

    public VmsAssign() {}

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
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
