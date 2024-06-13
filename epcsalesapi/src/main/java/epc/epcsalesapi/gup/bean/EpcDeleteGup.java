package epc.epcsalesapi.gup.bean;

public class EpcDeleteGup implements EpcGupInput {
    private String actionType;
    private String actionUsername;
    private String actionSystem;
    @EpcGupField(fieldName="ref",fieldType="operationElement")
    private String orderId;
    @EpcGupField(fieldName="smcSubscriberNumber",fieldType="gupfield")
    private String subrNum;
    
    
    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionUsername() {
        return actionUsername;
    }

    public void setActionUsername(String actionUsername) {
        this.actionUsername = actionUsername;
    }

    public String getActionSystem() {
        return actionSystem;
    }

    public void setActionSystem(String actionSystem) {
        this.actionSystem = actionSystem;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSubrNum() {
        return subrNum;
    }

    public void setSubrNum(String subrNum) {
        this.subrNum = subrNum;
    }
}
