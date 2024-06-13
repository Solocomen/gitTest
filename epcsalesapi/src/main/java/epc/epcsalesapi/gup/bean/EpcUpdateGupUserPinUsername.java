package epc.epcsalesapi.gup.bean;

public class EpcUpdateGupUserPinUsername implements EpcGupInput {
    
    private String actionType;
    private String actionUsername;
    private String actionSystem;
    @EpcGupField(fieldName="ref",fieldType="operationElement")
    private String orderId;
    @EpcGupField(fieldName="smcUserName",fieldType="gupfield")
    @EpcGupEncryptField
    private String userName;
    @EpcGupEncryptField
    private String originalUserName;

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
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getOriginalUserName() {
        return originalUserName;
    }
    public void setOriginalUserName(String originalUserName) {
        this.originalUserName = originalUserName;
    }

}
