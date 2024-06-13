package epc.epcsalesapi.sales.bean.vms.assign;

import java.util.ArrayList;

import epc.epcsalesapi.sales.bean.vms.VmsVoucher;

public class VmsCancelAssign {
    private String custId;
    private String quoteId;
    private boolean assignCancel;
    private boolean removeTransactionLog;
    private ArrayList<VmsVoucher> vouchers;

    public VmsCancelAssign() {}

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
