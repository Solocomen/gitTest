package epc.epcsalesapi.sales.bean.vms.assign;

import java.util.ArrayList;

import epc.epcsalesapi.sales.bean.vms.VmsVoucher2;

public class VmsAutoAssign {
    private String custId;
    private int orderId;
    private String result;
    private String errMsg;
    private ArrayList<VmsVoucher2> failVouchers;

    public VmsAutoAssign() {}

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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

    public ArrayList<VmsVoucher2> getFailVouchers() {
        return failVouchers;
    }

    public void setFailVouchers(ArrayList<VmsVoucher2> failVouchers) {
        this.failVouchers = failVouchers;
    }

    
}
