package epc.epcsalesapi.sales.bean.cancelOrder;

import java.math.BigDecimal;
import java.util.ArrayList;

public class EpcCancelAmount {

    private int orderId;
    private ArrayList<String> caseIdList;
    private BigDecimal cancelAmount;
    private BigDecimal voucherAmount;
    private BigDecimal courierFee;
    private String result;
    private String errMsg;

    public EpcCancelAmount() {}

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

    public BigDecimal getCancelAmount() {
        return cancelAmount;
    }

    public void setCancelAmount(BigDecimal cancelAmount) {
        this.cancelAmount = cancelAmount;
    }
    
    public final BigDecimal getVoucherAmount() {
		return voucherAmount;
	}

	public final void setVoucherAmount(BigDecimal voucherAmount) {
		this.voucherAmount = voucherAmount;
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

	public final BigDecimal getCourierFee() {
		return courierFee;
	}

	public final void setCourierFee(BigDecimal courierFee) {
		this.courierFee = courierFee;
	}

    
}
