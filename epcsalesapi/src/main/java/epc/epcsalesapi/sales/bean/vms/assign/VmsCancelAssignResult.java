package epc.epcsalesapi.sales.bean.vms.assign;

import java.util.ArrayList;

import epc.epcsalesapi.sales.bean.vms.VmsVoucher;

public class VmsCancelAssignResult {
    private int statusCode;
    private String statusDesc;
    private ArrayList<VmsVoucher> vouchers;

    public VmsCancelAssignResult() {}

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

    public ArrayList<VmsVoucher> getVouchers() {
        return vouchers;
    }

    public void setVouchers(ArrayList<VmsVoucher> vouchers) {
        this.vouchers = vouchers;
    }

    
}
