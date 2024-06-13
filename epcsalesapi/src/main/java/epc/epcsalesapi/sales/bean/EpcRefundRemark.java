package epc.epcsalesapi.sales.bean;

public class EpcRefundRemark {

	private String rejectType ;
    private String message;
    private String createDate;
    private String createUser;
    private String refundReferenceNo;
	
	public String getRejectType() {
		return rejectType;
	}
	public String getMessage() {
		return message;
	}
	public String getCreateDate() {
		return createDate;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setRejectType(String rejectType) {
		this.rejectType = rejectType;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getRefundReferenceNo() {
		return refundReferenceNo;
	}
	public void setRefundReferenceNo(String refundReferenceNo) {
		this.refundReferenceNo = refundReferenceNo;
	}
    
    
}
