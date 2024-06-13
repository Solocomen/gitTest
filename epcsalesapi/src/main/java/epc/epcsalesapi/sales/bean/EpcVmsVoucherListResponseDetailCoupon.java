package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcVmsVoucherListResponseDetailCoupon {
    private String couponId;
    private String serialNo;
    private String status;
    private BigDecimal redeemAmount;
    private BigDecimal remainingAmount;
    private String startDate;
    private String endDate;
    private EpcVmsVoucherListResponseDetailMasterCoupon masterCoupon;
    private EpcVmsVoucherListResponseDetailCustomer customer;
    private ArrayList<EpcVmsVoucherListResponseDetailAssignTransaction> assignmentTransactions;

    public EpcVmsVoucherListResponseDetailCoupon() {
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getRedeemAmount() {
        return redeemAmount;
    }

    public void setRedeemAmount(BigDecimal redeemAmount) {
        this.redeemAmount = redeemAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public EpcVmsVoucherListResponseDetailMasterCoupon getMasterCoupon() {
        return masterCoupon;
    }

    public void setMasterCoupon(EpcVmsVoucherListResponseDetailMasterCoupon masterCoupon) {
        this.masterCoupon = masterCoupon;
    }

    public EpcVmsVoucherListResponseDetailCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(EpcVmsVoucherListResponseDetailCustomer customer) {
        this.customer = customer;
    }

    public ArrayList<EpcVmsVoucherListResponseDetailAssignTransaction> getAssignmentTransactions() {
        return assignmentTransactions;
    }

    public void setAssignmentTransactions(
            ArrayList<EpcVmsVoucherListResponseDetailAssignTransaction> assignmentTransactions) {
        this.assignmentTransactions = assignmentTransactions;
    }
    
}
