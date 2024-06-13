package epc.epcsalesapi.sales.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcVmsVoucherAssignReq {
    private String masterCouponId;
    private String customerId;
    private String customerNo;
    private String subscriberNo;
    private String accountNo;
    private String serialNo;
    private String smcOrderId;
    private String smcOrderReference;

    public EpcVmsVoucherAssignReq() {}

    public String getMasterCouponId() {
        return masterCouponId;
    }

    public void setMasterCouponId(String masterCouponId) {
        this.masterCouponId = masterCouponId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getSubscriberNo() {
        return subscriberNo;
    }

    public void setSubscriberNo(String subscriberNo) {
        this.subscriberNo = subscriberNo;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getSmcOrderId() {
        return smcOrderId;
    }

    public void setSmcOrderId(String smcOrderId) {
        this.smcOrderId = smcOrderId;
    }

    public String getSmcOrderReference() {
        return smcOrderReference;
    }

    public void setSmcOrderReference(String smcOrderReference) {
        this.smcOrderReference = smcOrderReference;
    }

    
}
