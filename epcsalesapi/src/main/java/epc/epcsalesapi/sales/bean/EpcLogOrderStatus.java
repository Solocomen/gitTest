package epc.epcsalesapi.sales.bean;

public class EpcLogOrderStatus {
    
    private int orderId;
    private String oldOrderStatus;
    private String newOrderStatus;
    private String createUser;
    private String createSalesman;
    private String createChannel;
    private String createLocation;
    private String result;
    private String errMsg;

    public EpcLogOrderStatus() {}
    
    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    public String getOldOrderStatus() {
        return oldOrderStatus;
    }
    public void setOldOrderStatus(String oldOrderStatus) {
        this.oldOrderStatus = oldOrderStatus;
    }
    public String getNewOrderStatus() {
        return newOrderStatus;
    }
    public void setNewOrderStatus(String newOrderStatus) {
        this.newOrderStatus = newOrderStatus;
    }
    public String getCreateUser() {
        return createUser;
    }
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
    public String getCreateSalesman() {
        return createSalesman;
    }
    public void setCreateSalesman(String createSalesman) {
        this.createSalesman = createSalesman;
    }
    public String getCreateChannel() {
        return createChannel;
    }
    public void setCreateChannel(String createChannel) {
        this.createChannel = createChannel;
    }
    public String getCreateLocation() {
        return createLocation;
    }
    public void setCreateLocation(String createLocation) {
        this.createLocation = createLocation;
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

    
}
