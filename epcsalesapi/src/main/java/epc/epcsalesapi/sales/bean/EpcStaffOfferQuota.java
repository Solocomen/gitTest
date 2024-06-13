package epc.epcsalesapi.sales.bean;

public class EpcStaffOfferQuota {
    private int orderId;
    private String offerName;
    private String quotaGroup;
    private int quotaCap;
    private int quotaConsumed;
    private String canEnjoy;
    private String company;
    private String staffId;
    private String result;
    private String errMsg;

    public EpcStaffOfferQuota() {}

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getQuotaGroup() {
        return quotaGroup;
    }

    public void setQuotaGroup(String quotaGroup) {
        this.quotaGroup = quotaGroup;
    }

    public int getQuotaCap() {
        return quotaCap;
    }

    public void setQuotaCap(int quotaCap) {
        this.quotaCap = quotaCap;
    }

    public int getQuotaConsumed() {
        return quotaConsumed;
    }

    public void setQuotaConsumed(int quotaConsumed) {
        this.quotaConsumed = quotaConsumed;
    }

    public String getCanEnjoy() {
        return canEnjoy;
    }

    public void setCanEnjoy(String canEnjoy) {
        this.canEnjoy = canEnjoy;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    
}
