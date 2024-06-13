package epc.epcsalesapi.sales.bean.vms.redeem;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import epc.epcsalesapi.sales.bean.vms.VmsDiscount;
import epc.epcsalesapi.sales.bean.vms.VmsProductDetail;
import epc.epcsalesapi.sales.bean.vms.VmsVoucher2;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VmsRedeem2 {
    private int statusCode;
    private String statusDesc;
    private String location;
    private String channel;
    private ArrayList<VmsProductDetail> productDetails;
    private ArrayList<VmsVoucher2> redeemed;
    private ArrayList<VmsVoucher2> vouchers;
    private ArrayList<VmsDiscount> v_discounts;
    private BigDecimal total;
    private boolean forceRemove;
    private boolean assignCancel;
    private boolean removeTransactionLog;

    public VmsRedeem2() {}

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public ArrayList<VmsProductDetail> getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(ArrayList<VmsProductDetail> productDetails) {
        this.productDetails = productDetails;
    }

    public ArrayList<VmsVoucher2> getRedeemed() {
        return redeemed;
    }

    public void setRedeemed(ArrayList<VmsVoucher2> redeemed) {
        this.redeemed = redeemed;
    }

    public ArrayList<VmsVoucher2> getVouchers() {
        return vouchers;
    }

    public void setVouchers(ArrayList<VmsVoucher2> vouchers) {
        this.vouchers = vouchers;
    }

    public ArrayList<VmsDiscount> getV_discounts() {
        return v_discounts;
    }

    public void setV_discounts(ArrayList<VmsDiscount> v_discounts) {
        this.v_discounts = v_discounts;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public boolean isForceRemove() {
        return forceRemove;
    }

    public void setForceRemove(boolean forceRemove) {
        this.forceRemove = forceRemove;
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

    
}
