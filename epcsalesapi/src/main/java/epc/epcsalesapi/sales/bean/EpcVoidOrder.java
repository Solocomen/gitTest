package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcVoidOrder {
    private String custId;
    private int orderId;
    private String voidUser;
    private String voidSalesman;
    private String voidLocation;
    private String voidChannel;
    private String result;
    private String errMsg;
    private String approveBy;
    private String waiveFormCode;
    private String doaLocation;
    private ArrayList<String> transferNotes;
    
    public EpcVoidOrder() {
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

    public String getVoidUser() {
        return voidUser;
    }

    public void setVoidUser(String voidUser) {
        this.voidUser = voidUser;
    }

    public String getVoidSalesman() {
        return voidSalesman;
    }

    public void setVoidSalesman(String voidSalesman) {
        this.voidSalesman = voidSalesman;
    }

    public String getVoidLocation() {
        return voidLocation;
    }

    public void setVoidLocation(String voidLocation) {
        this.voidLocation = voidLocation;
    }

    public String getVoidChannel() {
        return voidChannel;
    }

    public void setVoidChannel(String voidChannel) {
        this.voidChannel = voidChannel;
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

    public String getApproveBy() {
        return approveBy;
    }

    public void setApproveBy(String approveBy) {
        this.approveBy = approveBy;
    }

    public String getWaiveFormCode() {
        return waiveFormCode;
    }

    public void setWaiveFormCode(String waiveFormCode) {
        this.waiveFormCode = waiveFormCode;
    }

    public String getDoaLocation() {
        return doaLocation;
    }

    public void setDoaLocation(String doaLocation) {
        this.doaLocation = doaLocation;
    }

    public ArrayList<String> getTransferNotes() {
        return transferNotes;
    }

    public void setTransferNotes(ArrayList<String> transferNotes) {
        this.transferNotes = transferNotes;
    }

    
}
