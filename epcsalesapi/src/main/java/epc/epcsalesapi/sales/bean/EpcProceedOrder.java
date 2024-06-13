package epc.epcsalesapi.sales.bean;

public class EpcProceedOrder {
    private int orderId;
    private String proceedBy;
    private String result;
    private String errMsg;
    private String previousProceedBy;
    private String previousProceedDate; // yyyymmddhh24miss
    private String action;
    
    public EpcProceedOrder() {}

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getProceedBy() {
        return proceedBy;
    }

    public void setProceedBy(String proceedBy) {
        this.proceedBy = proceedBy;
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

    public String getPreviousProceedBy() {
        return previousProceedBy;
    }

    public void setPreviousProceedBy(String previousProceedBy) {
        this.previousProceedBy = previousProceedBy;
    }

    public String getPreviousProceedDate() {
        return previousProceedDate;
    }

    public void setPreviousProceedDate(String previousProceedDate) {
        this.previousProceedDate = previousProceedDate;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    
}
