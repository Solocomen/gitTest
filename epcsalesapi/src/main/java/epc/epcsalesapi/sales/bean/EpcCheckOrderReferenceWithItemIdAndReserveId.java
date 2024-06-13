package epc.epcsalesapi.sales.bean;

public class EpcCheckOrderReferenceWithItemIdAndReserveId {
    private String result;
    private String errMsg;
    private int orderId;
    private String custId;
    private String productDesc;
    private String productDescChi;
    private String premium;

    public EpcCheckOrderReferenceWithItemIdAndReserveId() {
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
    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    public String getCustId() {
        return custId;
    }
    public void setCustId(String custId) {
        this.custId = custId;
    }
    public String getProductDesc() {
        return productDesc;
    }
    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }
    public String getProductDescChi() {
        return productDescChi;
    }
    public void setProductDescChi(String productDescChi) {
        this.productDescChi = productDescChi;
    }
    public String getPremium() {
        return premium;
    }
    public void setPremium(String premium) {
        this.premium = premium;
    }

    
}
