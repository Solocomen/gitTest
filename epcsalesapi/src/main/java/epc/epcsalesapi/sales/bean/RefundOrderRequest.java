package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;
import java.util.Date;

public class RefundOrderRequest {

    
    private BigDecimal cancelAmount;
    private String receiptDate;
    private String subrNum;
    private String custNum;
    private String selectedName;
    private String payeeName;
    private String postalAddress1;
    private String postalAddress2;
    private String postalDistrict;
    private String postalArea;
    private Boolean manual;
    private Date cashOutDate;
    private String refundReferenceNo;
    private Integer paymentId;


    public BigDecimal getCancelAmount() {
        return cancelAmount;
    }

    public void setCancelAmount(BigDecimal cancelAmount) {
        this.cancelAmount = cancelAmount;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getSubrNum() {
        return subrNum;
    }

    public void setSubrNum(String subrNum) {
        this.subrNum = subrNum;
    }

    public String getCustNum() {
        return custNum;
    }

    public void setCustNum(String custNum) {
        this.custNum = custNum;
    }

	public String getSelectedName() {
		return selectedName;
	}

	public void setSelectedName(String selectedName) {
		this.selectedName = selectedName;
	}

	public String getPayeeName() {
		return payeeName;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	public String getPostalAddress1() {
		return postalAddress1;
	}

	public void setPostalAddress1(String postalAddress1) {
		this.postalAddress1 = postalAddress1;
	}

	public String getPostalAddress2() {
		return postalAddress2;
	}

	public void setPostalAddress2(String postalAddress2) {
		this.postalAddress2 = postalAddress2;
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

	public Boolean getManual() {
		return manual;
	}

	public void setManual(Boolean manual) {
		this.manual = manual;
	}

	public Date getCashOutDate() {
		return cashOutDate;
	}

	public void setCashOutDate(Date cashOutDate) {
		this.cashOutDate = cashOutDate;
	}

	public String getRefundReferenceNo() {
		return refundReferenceNo;
	}

	public void setRefundReferenceNo(String refundReferenceNo) {
		this.refundReferenceNo = refundReferenceNo;
	}

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

    
}
