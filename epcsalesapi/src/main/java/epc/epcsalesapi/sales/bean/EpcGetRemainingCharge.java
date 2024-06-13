package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;
import java.util.ArrayList;

public class EpcGetRemainingCharge {
    private int orderId;
    private ArrayList<EpcCharge> items;
    private BigDecimal remainingCharge;
    private String createUser;
    private String createSalesman;
    private String createChannel;
    private String createLocation;
    private String result;
    private String errMsg;

    public EpcGetRemainingCharge() {
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public ArrayList<EpcCharge> getItems() {
        return items;
    }

    public void setItems(ArrayList<EpcCharge> items) {
        this.items = items;
    }

    public BigDecimal getRemainingCharge() {
        return remainingCharge;
    }

    public void setRemainingCharge(BigDecimal remainingCharge) {
        this.remainingCharge = remainingCharge;
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
