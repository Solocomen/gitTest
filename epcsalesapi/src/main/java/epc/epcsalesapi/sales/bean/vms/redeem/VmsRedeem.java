package epc.epcsalesapi.sales.bean.vms.redeem;

import java.util.ArrayList;

import epc.epcsalesapi.sales.bean.vms.VmsVoucher;

public class VmsRedeem {
    private String custId;
    private String quoteId;
    private ArrayList<VmsVoucher> redeemed;
    private ArrayList<VmsVoucher> vouchers;

    public VmsRedeem() {}

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

    public ArrayList<VmsVoucher> getRedeemed() {
        return redeemed;
    }

    public void setRedeemed(ArrayList<VmsVoucher> redeemed) {
        this.redeemed = redeemed;
    }

    public ArrayList<VmsVoucher> getVouchers() {
        return vouchers;
    }

    public void setVouchers(ArrayList<VmsVoucher> vouchers) {
        this.vouchers = vouchers;
    }

}
