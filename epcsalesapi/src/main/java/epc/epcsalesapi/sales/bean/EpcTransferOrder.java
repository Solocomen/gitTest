package epc.epcsalesapi.sales.bean;

/**
 *
 * @author KerryTsang
 */
public class EpcTransferOrder {

    private String custId;
    private int tmpOrderId;
    private int tmpQuoteId;
    private int orderId;
    private String orderChannel;
    private String orderUser;
    private String orderSalesman;
    private String orderLocation;
    private String result;
    private String errMsg;

    public EpcTransferOrder() {
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public int getTmpOrderId() {
        return tmpOrderId;
    }

    public void setTmpOrderId(int tmpOrderId) {
        this.tmpOrderId = tmpOrderId;
    }

    public int getTmpQuoteId() {
        return tmpQuoteId;
    }

    public void setTmpQuoteId(int tmpQuoteId) {
        this.tmpQuoteId = tmpQuoteId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderChannel() {
        return orderChannel;
    }

    public void setOrderChannel(String orderChannel) {
        this.orderChannel = orderChannel;
    }

    public String getOrderUser() {
        return orderUser;
    }

    public void setOrderUser(String orderUser) {
        this.orderUser = orderUser;
    }

    public String getOrderSalesman() {
        return orderSalesman;
    }

    public void setOrderSalesman(String orderSalesman) {
        this.orderSalesman = orderSalesman;
    }

    public String getOrderLocation() {
        return orderLocation;
    }

    public void setOrderLocation(String orderLocation) {
        this.orderLocation = orderLocation;
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
