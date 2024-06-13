package epc.epcsalesapi.sales.bean.vms.assign;

import java.util.ArrayList;

import epc.epcsalesapi.sales.bean.vms.VmsVoucher2;

public class VmsAssign2 {
    private int statusCode;
    private String statusDesc;
    private ArrayList<VmsVoucher2> vouchers;

    public VmsAssign2() {}

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

    public ArrayList<VmsVoucher2> getVouchers() {
        return vouchers;
    }

    public void setVouchers(ArrayList<VmsVoucher2> vouchers) {
        this.vouchers = vouchers;
    }

    
    
}
