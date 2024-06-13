package epc.epcsalesapi.sales.bean;

public class EpcInitPayment {
    
    private int orderId;
    private String salesActionType;
    private String resultCode;
    private String errorMessage;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getSalesActionType() {
        return salesActionType;
    }

    public void setSalesActionType(String salesActionType) {
        this.salesActionType = salesActionType;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
