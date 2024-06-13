package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcSettleExtensionFee {
    private String custId;
    private int orderId;
    private ArrayList<EpcCharge> extensionChargeList;
    private ArrayList<EpcPayment> paymentList;
    private String createUser;
    private String createSalesman;
    private String createChannel;
    private String createLocation;
    private String result;
    private String errMsg;

    public EpcSettleExtensionFee() {
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public ArrayList<EpcCharge> getExtensionChargeList() {
        return extensionChargeList;
    }

    public void setExtensionChargeList(ArrayList<EpcCharge> extensionChargeList) {
        this.extensionChargeList = extensionChargeList;
    }

    public ArrayList<EpcPayment> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(ArrayList<EpcPayment> paymentList) {
        this.paymentList = paymentList;
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
