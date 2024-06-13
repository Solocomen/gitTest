package epc.epcsalesapi.sales.bean.vms.redeem;

import java.util.ArrayList;

import epc.epcsalesapi.sales.bean.vms.VmsVoucher;

public class VmsCancelRedeem {
    private String quoteId;
    private boolean assignCancel;
    private boolean removeTransactionLog;
    private ArrayList<VmsVoucher> vouchers;

    public VmsCancelRedeem() {}

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public boolean isAssignCancel() {
        return assignCancel;
    }

    public void setAssignCancel(boolean assignCancel) {
        this.assignCancel = assignCancel;
    }

    public boolean isRemoveTransactionLog() {
        return removeTransactionLog;
    }

    public void setRemoveTransactionLog(boolean removeTransactionLog) {
        this.removeTransactionLog = removeTransactionLog;
    }

    public ArrayList<VmsVoucher> getVouchers() {
        return vouchers;
    }

    public void setVouchers(ArrayList<VmsVoucher> vouchers) {
        this.vouchers = vouchers;
    }

    

    
}
