package epc.epcsalesapi.stock.bean;

public class EpcUpdateStock {
	
	private String reserveId;
	private String subrNum;
	private String hkidBr;
	private String warehouse;
	private String productCode;
	private int qty;
	private String pickupLoc;
	private String imei;
	private String createUser;
	private String createSalesman;
	private String invoiceNo;
	private String movementComment;
	private String checkBy;
	private String referNo;
	
	public EpcUpdateStock() {}

	public String getReserveId() {
		return reserveId;
	}

	public void setReserveId(String reserveId) {
		this.reserveId = reserveId;
	}

	public String getSubrNum() {
		return subrNum;
	}

	public void setSubrNum(String subrNum) {
		this.subrNum = subrNum;
	}

	public String getHkidBr() {
		return hkidBr;
	}

	public void setHkidBr(String hkidBr) {
		this.hkidBr = hkidBr;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public String getPickupLoc() {
		return pickupLoc;
	}

	public void setPickupLoc(String pickupLoc) {
		this.pickupLoc = pickupLoc;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
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

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getMovementComment() {
		return movementComment;
	}

	public void setMovementComment(String movementComment) {
		this.movementComment = movementComment;
	}

	public String getCheckBy() {
		return checkBy;
	}

	public void setCheckBy(String checkBy) {
		this.checkBy = checkBy;
	}

	public String getReferNo() {
		return referNo;
	}

	public void setReferNo(String referNo) {
		this.referNo = referNo;
	}

	@Override
	public String toString() {
		return "EpcUpdateStock [reserveId=" + reserveId + ", subrNum=" + subrNum + ", warehouse=" + warehouse
				+ ", productCode=" + productCode + ", qty=" + qty + ", pickupLoc=" + pickupLoc + ", imei=" + imei
				+ ", createUser=" + createUser + ", createSalesman=" + createSalesman + ", checkBy=" + checkBy
				+ ", invoiceNo=" + invoiceNo + ", movementComment=" + movementComment + "]";
	}

	

}
