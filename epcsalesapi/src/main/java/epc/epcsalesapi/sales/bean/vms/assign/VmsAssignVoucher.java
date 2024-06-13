package epc.epcsalesapi.sales.bean.vms.assign;

import epc.epcsalesapi.sales.bean.EpcVmsVoucherListResponse;

public class VmsAssignVoucher {
    private int orderId;
    private String caseId;
    private String itemId;
    private String voucherGuid;
    private String voucherMasterId;
    private String transactionId;
    private String voucherCode;
    private String assignId;
    private boolean isDone;
    private String quoteGuid;
    private String custId;
    private String desc;
    private String descChi;

    private EpcVmsVoucherListResponse vmsListResponse;
    
    public VmsAssignVoucher() {}

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getVoucherGuid() {
        return voucherGuid;
    }

    public void setVoucherGuid(String voucherGuid) {
        this.voucherGuid = voucherGuid;
    }

    public String getVoucherMasterId() {
        return voucherMasterId;
    }

    public void setVoucherMasterId(String voucherMasterId) {
        this.voucherMasterId = voucherMasterId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getAssignId() {
        return assignId;
    }

    public void setAssignId(String assignId) {
        this.assignId = assignId;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    public String getQuoteGuid() {
        return quoteGuid;
    }

    public void setQuoteGuid(String quoteGuid) {
        this.quoteGuid = quoteGuid;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

	public EpcVmsVoucherListResponse getVmsListResponse() {
		return vmsListResponse;
	}

	public void setVmsListResponse(EpcVmsVoucherListResponse vmsListResponse) {
		this.vmsListResponse = vmsListResponse;
	}

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDescChi() {
        return descChi;
    }

    public void setDescChi(String descChi) {
        this.descChi = descChi;
    }
    
    
}
