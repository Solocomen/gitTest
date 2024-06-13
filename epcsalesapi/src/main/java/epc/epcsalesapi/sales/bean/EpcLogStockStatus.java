package epc.epcsalesapi.sales.bean;

public class EpcLogStockStatus {
    
    private int orderId;
    private String itemId;
    private String oldStockStatus;
    private String newStockStatus;
    private String result;
    private String errMsg;

    public EpcLogStockStatus() {}

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getOldStockStatus() {
        return oldStockStatus;
    }

    public void setOldStockStatus(String oldStockStatus) {
        this.oldStockStatus = oldStockStatus;
    }

    public String getNewStockStatus() {
        return newStockStatus;
    }

    public void setNewStockStatus(String newStockStatus) {
        this.newStockStatus = newStockStatus;
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
