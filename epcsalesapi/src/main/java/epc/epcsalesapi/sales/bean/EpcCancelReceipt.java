package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;
import java.util.List;

public class EpcCancelReceipt {
    private String receiptNo;
    private String receiptDate;
    private BigDecimal cancelAmount;
    private int cancelOrderId;
    private String orderNo;
    private String subrNum;
    private String custNum;
    private String location;
    private String placeOrderDate;
    private String placeOrderChannel;
    private String remarkDate;
    private String remarkUser;
    private String remarkMessage;
    private List<EpcPayment> paymentList;
    
    public EpcCancelReceipt() {}

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

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

    public int getCancelOrderId() {
        return cancelOrderId;
    }

    public void setCancelOrderId(int cancelOrderId) {
        this.cancelOrderId = cancelOrderId;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public List<EpcPayment> getPaymentList() {
		return paymentList;
	}

	public void setPaymentList(List<EpcPayment> paymentList) {
		this.paymentList = paymentList;
	}

	public String getPlaceOrderDate() {
		return placeOrderDate;
	}

	public void setPlaceOrderDate(String placeOrderDate) {
		this.placeOrderDate = placeOrderDate;
	}

	public String getRemarkDate() {
		return remarkDate;
	}

	public String getRemarkUser() {
		return remarkUser;
	}

	public String getRemarkMessage() {
		return remarkMessage;
	}

	public void setRemarkDate(String remarkDate) {
		this.remarkDate = remarkDate;
	}

	public void setRemarkUser(String remarkUser) {
		this.remarkUser = remarkUser;
	}

	public void setRemarkMessage(String remarkMessage) {
		this.remarkMessage = remarkMessage;
	}

	public String getPlaceOrderChannel() {
		return placeOrderChannel;
	}

	public void setPlaceOrderChannel(String placeOrderChannel) {
		this.placeOrderChannel = placeOrderChannel;
	}

	
    
}
