package epc.epcsalesapi.change.bean;

public class WsChangeSimResult {
    private String orderId; // hansen om id
    private int resultCode;
    private String resultMsg;
    
    public WsChangeSimResult() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    
}
