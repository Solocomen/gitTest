package epc.epcsalesapi.sales.bean;

import java.io.Serializable;
import java.util.List;


public class RefundRequest implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -3934697420011219676L;
    private String custId;
    private String location;
    private String createUser;
    private String salesman;
    private String approveBy;
    private String waiveFormCode;
    private Integer cancelOrderId;
    private String receiptNo;
    private List<RefundOrderRequest> orderList;
    

    public List<RefundOrderRequest> getOrderList() {
        return orderList;
    }
    public void setOrderList(List<RefundOrderRequest> orderList) {
        this.orderList = orderList;
    }
    public String getCustId() {
        return custId;
    }
    public void setCustId(String custId) {
        this.custId = custId;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getCreateUser() {
        return createUser;
    }
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
    public String getSalesman() {
        return salesman;
    }
    public void setSalesman(String salesman) {
        this.salesman = salesman;
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
    
	public Integer getCancelOrderId() {
		return cancelOrderId;
	}
	
	public void setCancelOrderId(Integer cancelOrderId) {
		this.cancelOrderId = cancelOrderId;
	}
	
	public String getReceiptNo() {
		return receiptNo;
	}
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}


}
