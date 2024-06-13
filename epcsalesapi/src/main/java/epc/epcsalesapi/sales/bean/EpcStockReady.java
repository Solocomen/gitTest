package epc.epcsalesapi.sales.bean;

public class EpcStockReady {
    private String orderReference;
    private String itemId;
    private String reserveId;
    private String result;
    private String errMsg;
    public EpcStockReady() {
    }
    public String getOrderReference() {
        return orderReference;
    }
    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }
    public String getItemId() {
        return itemId;
    }
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public String getReserveId() {
        return reserveId;
    }
    public void setReserveId(String reserveId) {
        this.reserveId = reserveId;
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
