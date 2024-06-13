package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcVmsVoucherListResponseDetail {
    private int statusCode;
    private String statusDesc;
    private EpcVmsVoucherListResponseDetailCustomer customer;
    private ArrayList<EpcVmsVoucherListResponseDetailCoupon> coupon;
    
    public EpcVmsVoucherListResponseDetail(){}

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

    public EpcVmsVoucherListResponseDetailCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(EpcVmsVoucherListResponseDetailCustomer customer) {
        this.customer = customer;
    }

    public ArrayList<EpcVmsVoucherListResponseDetailCoupon> getCoupon() {
        return coupon;
    }

    public void setCoupon(ArrayList<EpcVmsVoucherListResponseDetailCoupon> coupon) {
        this.coupon = coupon;
    }

    
}
