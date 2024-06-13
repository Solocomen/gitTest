package epc.epcsalesapi.sales.bean.refundReport;

import lombok.Data;
import org.apache.commons.lang.StringUtils;


import java.io.Serializable;

@Data
public class EpcRefundReportDO implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8330791642457958033L;
	private String cancelOrderDate;
    private String orderReference;
    private String customerNum;
    private String subscriberNum;
    private String receiptStoreCode;
    private String fesReceiptNum;
    private String receiptChargeType;
    private String orderReceiptDate;
    private String orderAmount;
    private String paymentAmount;
    private String paymentCode;
    private String creditCardNum;
    private String midNum;
    private String approvalCodeForCreditCard;
    private String paymentGatewayReferenceNo;
    private String settlementReceipt;
    private String interfacedtoLedgerAmount;
    private String refundMethod;
    private String refundNo;
    private String refundReceipt;
    private String refundReceiptDate;
    private String refundReceiptAmount;
    private String approver;
    private String refundAmount;
    private String reconciliation;
    //Success-Y / Failed-N
    private String autoRefund;
    //(completed date / blank if incomplete)
    private String manualRefund ;
    private String refundStoreCode;
    private String cashOutDate;
    private String CHQRefund;
    private String payeeName;
    private String refundPostaladdrs;
    private String isDone;
    private String modifyDate;


    public String getPaymentCode(){
		return StringUtils.isNotBlank(this.paymentCode)?this.paymentCode:"N/A";
    	
    }
    public String getCreditCardNum() {
    	return StringUtils.isNotBlank(this.creditCardNum)?this.creditCardNum:"N/A";
    }
    
    public String getMidNum() {
    	return StringUtils.isNotBlank(this.midNum)?this.midNum:"N/A";
    }
    
    public String getApprovalCodeForCreditCard() {
    	return StringUtils.isNotBlank(this.approvalCodeForCreditCard)?this.approvalCodeForCreditCard:"N/A";
    }
    
    public String getPaymentGatewayReferenceNo() {
    	return StringUtils.isNotBlank(this.paymentGatewayReferenceNo)?this.paymentGatewayReferenceNo:"N/A";
    }


    public String getCustomerNum() {
        return StringUtils.isEmpty(this.customerNum) ? "N/A" : this.customerNum;
    }

    public String getSubscriberNum() {
        return StringUtils.isEmpty(this.subscriberNum) ? "N/A" : this.subscriberNum;
    }

    public String getReceiptStoreCode() {
        return StringUtils.isEmpty(this.receiptStoreCode) ? "N/A" : this.receiptStoreCode;
    }

    public String getFesReceiptNum() {
        return StringUtils.isEmpty(this.fesReceiptNum) ? "N/A" : this.fesReceiptNum;
    }

    public String getReceiptChargeType() {
        return StringUtils.isEmpty(this.receiptChargeType) ? "N/A" : this.receiptChargeType;
    }

    public String getOrderReceiptDate() {
        return StringUtils.isEmpty(this.orderReceiptDate) ? "N/A" : this.orderReceiptDate;
    }

    public String getOrderReceiptAmount() {
        return StringUtils.isEmpty(this.orderAmount) ? "N/A" : this.orderAmount;
    }

    public String getSettlementReceipt() {
        return StringUtils.isEmpty(this.settlementReceipt) ? "N/A" : this.settlementReceipt;
    }

    public String getInterfacedtoLedgerAmount() {
        return StringUtils.isEmpty(this.interfacedtoLedgerAmount) ? "N/A" : this.interfacedtoLedgerAmount;
    }

    public String getRefundMethod() {
        return StringUtils.isEmpty(this.refundMethod) ? "N/A" : this.refundMethod;
    }

    public String getRefundNo() {
        return StringUtils.isEmpty(this.refundNo) ? "N/A" : this.refundNo;
    }

    public String getRefundReceipt() {
        return StringUtils.isEmpty(this.refundReceipt) ? "N/A" : this.refundReceipt;
    }

    public String getRefundReceiptDate() {
        return StringUtils.isEmpty(this.refundReceiptDate) ? "N/A" : this.refundReceiptDate;
    }

    public String getRefundReceiptAmount() {
        return StringUtils.isEmpty(this.refundReceiptAmount) ? "N/A" : this.refundReceiptAmount;
    }

    public String getApprover() {
        return StringUtils.isEmpty(this.approver) ? "N/A" : this.approver;
    }

    public String getRefundAmount() {
        return StringUtils.isEmpty(this.refundAmount) ? "N/A" : this.refundAmount;
    }

    public String getReconciliation() {
        return StringUtils.isEmpty(this.reconciliation) ? "N/A" : this.reconciliation;
    }

    public String getAutoRefund() {
        return StringUtils.isEmpty(this.autoRefund) ? "N" : this.autoRefund;
    }

    public String getManualRefund() {
        return StringUtils.isEmpty(this.manualRefund) ? "N" : this.manualRefund;
    }

    public String getRefundStoreCode() {
        return StringUtils.isEmpty(this.refundStoreCode) ? "N/A" : this.refundStoreCode;
    }

    public String getCashOutDate() {
        return StringUtils.isEmpty(this.cashOutDate) ? "N" : this.cashOutDate;
    }

    public String getCHQRefund() {
        return StringUtils.isEmpty(this.CHQRefund) ? "N/A" : this.CHQRefund;
    }

    public String getPayeeName() {
        return StringUtils.isEmpty(this.payeeName) ? "N/A" : this.payeeName;
    }

    public String getRefundPostaladdrs() {
        return StringUtils.isBlank(this.refundPostaladdrs) && "  ".equals(this.refundPostaladdrs)? "N/A" : this.refundPostaladdrs;
    }
    
    public String getIsDone() {
    	return "Y".equals(this.isDone)?"completed":"R".equals(this.isDone)?"rejected":"pending";
	}
}
