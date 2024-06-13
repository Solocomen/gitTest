package epc.epcsalesapi.sales.bean.cancelOrder;

import java.math.BigDecimal;
import java.util.ArrayList;

public class EpcCancelOrder {

    private String custId;
    private int orderId;
    private ArrayList<String> caseIdList;
    private String cancelUser;
    private String cancelSalesman;
    private String cancelChannel;
    private String cancelLocation;
    private BigDecimal cancelAmount;
    private String result;
    private String errMsg;
    private String approveBy;
    private String waiveFormCode;
    private String doaLocation;
    private ArrayList<String> transferNotes;

    public EpcCancelOrder() {}

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

    public ArrayList<String> getCaseIdList() {
        return caseIdList;
    }

    public void setCaseIdList(ArrayList<String> caseIdList) {
        this.caseIdList = caseIdList;
    }

    public String getCancelUser() {
        return cancelUser;
    }

    public void setCancelUser(String cancelUser) {
        this.cancelUser = cancelUser;
    }

    public String getCancelSalesman() {
        return cancelSalesman;
    }

    public void setCancelSalesman(String cancelSalesman) {
        this.cancelSalesman = cancelSalesman;
    }

    public String getCancelChannel() {
        return cancelChannel;
    }

    public void setCancelChannel(String cancelChannel) {
        this.cancelChannel = cancelChannel;
    }

    public String getCancelLocation() {
        return cancelLocation;
    }

    public void setCancelLocation(String cancelLocation) {
        this.cancelLocation = cancelLocation;
    }

    public BigDecimal getCancelAmount() {
        return cancelAmount;
    }

    public void setCancelAmount(BigDecimal cancelAmount) {
        this.cancelAmount = cancelAmount;
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
