/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;
import epc.epcsalesapi.helper.StringHelper;

/**
 *
 * @author KerryTsang
 */
public class EpcPayment {
    private int paymentId; 
    private String paymentCode;
    private BigDecimal paymentAmount;
    private String reference1;
    private String reference2;
    private String currencyCode;
    private BigDecimal exchangeRate;
    private BigDecimal currencyAmount;
    private String caseId;
    private String paymentTo;
    private String ccNoMasked;
    private String ccNameMasked;
    private String ccExpiryMasked;
    private String ecrNo;
    private String approvalCode;
    private String txNo;
    private boolean creditCard;
    private BigDecimal balance;
	private boolean newRecord;
    
    public EpcPayment() {
    }
    
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getReference1() {
        return reference1;
    }

    public void setReference1(String reference1) {
        this.reference1 = reference1;
    }

    public String getReference2() {
        return reference2;
    }

    public void setReference2(String reference2) {
        this.reference2 = reference2;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getCurrencyAmount() {
        return currencyAmount;
    }

    public void setCurrencyAmount(BigDecimal currencyAmount) {
        this.currencyAmount = currencyAmount;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getPaymentTo() {
        return paymentTo;
    }

    public void setPaymentTo(String paymentTo) {
        this.paymentTo = paymentTo;
    }

    public String getCcNoMasked() {
        return ccNoMasked;
    }

    public void setCcNoMasked(String ccNoMasked) {
        this.ccNoMasked = ccNoMasked;
    }

    public String getCcNameMasked() {
        return ccNameMasked;
    }

    public void setCcNameMasked(String ccNameMasked) {
        this.ccNameMasked = ccNameMasked;
    }

    public String getCcExpiryMasked() {
        return ccExpiryMasked;
    }

    public void setCcExpiryMasked(String ccExpiryMasked) {
        this.ccExpiryMasked = ccExpiryMasked;
    }

    public String getEcrNo() {
        return ecrNo;
    }

    public void setEcrNo(String ecrNo) {
        this.ecrNo = ecrNo;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    public String getTxNo() {
        return txNo;
    }

    public void setTxNo(String txNo) {
        this.txNo = txNo;
    }
    
    public boolean isCreditCard() {
		return creditCard;
	}

	public void setCreditCard(boolean creditCard) {
		this.creditCard = creditCard;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	@Override
    public boolean equals (Object object) {
        
        if (object instanceof EpcPayment) {
            if (this == object) {
                return true;
            }
            
            EpcPayment epcPayment = (EpcPayment) object;
            
            if (this.getPaymentId() > 0 && epcPayment.getPaymentId() > 0) {
                return this.getPaymentId() == epcPayment.getPaymentId();
            } else {
                if (!"".equals(StringHelper.trim(this.getCaseId())) && !"".equals(StringHelper.trim(epcPayment.getCaseId()))) {
                    return (StringHelper.trim(this.getCaseId()).equals(StringHelper.trim(epcPayment.getCaseId())) && this.getPaymentCode().equals(epcPayment.getPaymentCode()) && this.getPaymentAmount().compareTo(epcPayment.getPaymentAmount()) == 0);
                } else if ("".equals(StringHelper.trim(this.getCaseId())) && "".equals(StringHelper.trim(epcPayment.getCaseId()))) {
                    return (this.getPaymentCode().equals(epcPayment.getPaymentCode()) && this.getPaymentAmount().compareTo(epcPayment.getPaymentAmount()) == 0);
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
        
    }

    @Override
    public String toString() {
        return "EpcPayment [paymentId=" + paymentId + ", paymentCode=" + paymentCode + ", paymentAmount="
                + paymentAmount + ", reference1=" + reference1 + ", reference2=" + reference2 + ", currencyCode="
                + currencyCode + ", exchangeRate=" + exchangeRate + ", currencyAmount=" + currencyAmount + ", caseId="
                + caseId + ", paymentTo=" + paymentTo + ", ccNoMasked=" + ccNoMasked + ", ccNameMasked=" + ccNameMasked
                + ", ccExpiryMasked=" + ccExpiryMasked + ", ecrNo=" + ecrNo + ", approvalCode=" + approvalCode
                + ", txNo=" + txNo + ", newRecord = " + newRecord + "]";
    }

}
