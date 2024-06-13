package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;
import java.util.List;

public class EpcRefundRecord {
    private int recId;
    private BigDecimal refundAmount;
    private String refundReceipt;
    private String refundMethod;
    private String createUser;
    private String createSalesman;
    private String createDate;
    private String createLocation;
    private String createChannel;
    private String payeeName;
    private String postalAddr1;
    private String postalAddr2;
    private String postalDistrict;
    private String postalArea;
    private String manualRefund;
    private String cashOutDate;
    private String refundReferenceNo;
    private String custId;
    private String isDone;
    private String modifyUser;
    private String modifyDate;
    private String result;
    private String errMsg;
    private String rejectType ;
    private String message;
    private String userName;
    
    private List<EpcRefundRemark> remarks;
    
    public EpcRefundRecord() {
    }

    public int getRecId() {
        return recId;
    }

    public void setRecId(int recId) {
        this.recId = recId;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundReceipt() {
        return refundReceipt;
    }

    public void setRefundReceipt(String refundReceipt) {
        this.refundReceipt = refundReceipt;
    }

    public String getRefundMethod() {
        return refundMethod;
    }

    public void setRefundMethod(String refundMethod) {
        this.refundMethod = refundMethod;
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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getPostalAddr1() {
        return postalAddr1;
    }

    public void setPostalAddr1(String postalAddr1) {
        this.postalAddr1 = postalAddr1;
    }

    public String getPostalAddr2() {
        return postalAddr2;
    }

    public void setPostalAddr2(String postalAddr2) {
        this.postalAddr2 = postalAddr2;
    }

    public String getPostalDistrict() {
        return postalDistrict;
    }

    public void setPostalDistrict(String postalDistrict) {
        this.postalDistrict = postalDistrict;
    }

    public String getPostalArea() {
        return postalArea;
    }

    public void setPostalArea(String postalArea) {
        this.postalArea = postalArea;
    }

    public String getManualRefund() {
        return manualRefund;
    }

    public void setManualRefund(String manualRefund) {
        this.manualRefund = manualRefund;
    }

    public String getCashOutDate() {
        return cashOutDate;
    }

    public void setCashOutDate(String cashOutDate) {
        this.cashOutDate = cashOutDate;
    }

    public List<EpcRefundRemark> getRemarks() {
		return remarks;
	}

	public void setRemarks(List<EpcRefundRemark> remarks) {
		this.remarks = remarks;
	}

	public String getRefundReferenceNo() {
        return refundReferenceNo;
    }

    public void setRefundReferenceNo(String refundReferenceNo) {
        this.refundReferenceNo = refundReferenceNo;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getIsDone() {
        return isDone;
    }

    public void setIsDone(String isDone) {
        this.isDone = isDone;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
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

	public String getRejectType() {
		return rejectType;
	}

	public String getMessage() {
		return message;
	}

	public void setRejectType(String rejectType) {
		this.rejectType = rejectType;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCreateLocation() {
		return createLocation;
	}

	public void setCreateLocation(String createLocation) {
		this.createLocation = createLocation;
	}

	public String getCreateChannel() {
		return createChannel;
	}

	public void setCreateChannel(String createChannel) {
		this.createChannel = createChannel;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
    
	@Override
	public String toString() {
		return "EpcRefundRecord [recId=" + recId + ", refundAmount=" + refundAmount + ", refundReceipt=" + refundReceipt
				+ ", refundMethod=" + refundMethod + ", createUser=" + createUser + ", createSalesman=" + createSalesman
				+ ", createDate=" + createDate + ", createLocation=" + createLocation + ", createChannel="
				+ createChannel + ", payeeName=" + payeeName + ", postalAddr1=" + postalAddr1 + ", postalAddr2="
				+ postalAddr2 + ", postalDistrict=" + postalDistrict + ", postalArea=" + postalArea + ", manualRefund="
				+ manualRefund + ", cashOutDate=" + cashOutDate + ", refundReferenceNo=" + refundReferenceNo
				+ ", custId=" + custId + ", isDone=" + isDone + ", modifyUser=" + modifyUser + ", modifyDate="
				+ modifyDate + ", result=" + result + ", errMsg=" + errMsg + ", rejectType=" + rejectType + ", message="
				+ message + ", userName=" + userName + "]";
	}
}
