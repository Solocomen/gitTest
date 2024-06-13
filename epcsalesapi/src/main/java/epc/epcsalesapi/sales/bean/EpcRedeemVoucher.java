package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;
import java.util.ArrayList;

import epc.epcsalesapi.sales.bean.vms.order.VmsOrderVoucher;

public class EpcRedeemVoucher {
    private String custId;
    private int orderId;
    private String orderReference;
    private String masterVoucherId;
    private String voucherCode;
    private BigDecimal voucherAmount;
    private String action;
    private String result;
    private String errMsg;
    private String loginChannel;
    private String loginLocation;
    private ArrayList<VmsOrderVoucher> failVouchers;

    public EpcRedeemVoucher() {}

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

    public String getMasterVoucherId() {
        return masterVoucherId;
    }

    public void setMasterVoucherId(String masterVoucherId) {
        this.masterVoucherId = masterVoucherId;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public BigDecimal getVoucherAmount() {
        return voucherAmount;
    }

    public void setVoucherAmount(BigDecimal voucherAmount) {
        this.voucherAmount = voucherAmount;
    }

    public String getLoginChannel() {
        return loginChannel;
    }

    public void setLoginChannel(String loginChannel) {
        this.loginChannel = loginChannel;
    }

    public String getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    public ArrayList<VmsOrderVoucher> getFailVouchers() {
        return failVouchers;
    }

    public void setFailVouchers(ArrayList<VmsOrderVoucher> failVouchers) {
        this.failVouchers = failVouchers;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    
}
