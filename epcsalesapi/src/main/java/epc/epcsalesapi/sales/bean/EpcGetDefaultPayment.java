package epc.epcsalesapi.sales.bean;

public class EpcGetDefaultPayment {

    private String orderId;
    private String caseId;
    private String salesActionType;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getSalesActionType() {
        return salesActionType;
    }

    public void setSalesActionType(String salesActionType) {
        this.salesActionType = salesActionType;
    }

}
