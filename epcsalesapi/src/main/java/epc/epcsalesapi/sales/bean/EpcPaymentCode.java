package epc.epcsalesapi.sales.bean;

/**
 *
 * @author williamtam
 */

public class EpcPaymentCode {

    private String paymentCode;
    private String paymentShortKey;
    private String paymentCodeDesc;
    private String paymentCodeDescChi;
    private boolean creditCardType;
    private boolean bankInstallment;
    private boolean mobilePayment;
    private boolean disableReference;

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public String getPaymentShortKey() {
        return paymentShortKey;
    }
    public void setPaymentShortKey(String paymentShortKey) {
        this.paymentShortKey = paymentShortKey;
    }

    public String getPaymentCodeDesc() {
        return paymentCodeDesc;
    }

    public void setPaymentCodeDesc(String paymentCodeDesc) {
        this.paymentCodeDesc = paymentCodeDesc;
    }

    public String getPaymentCodeDescChi() {
        return paymentCodeDescChi;
    }

    public void setPaymentCodeDescChi(String paymentCodeDescChi) {
        this.paymentCodeDescChi = paymentCodeDescChi;
    }

    public boolean isCreditCardType() {
        return creditCardType;
    }

    public void setCreditCardType(boolean creditCardType) {
        this.creditCardType = creditCardType;
    }

    public boolean isBankInstallment() {
        return bankInstallment;
    }

    public void setBankInstallment(boolean bankInstallment) {
        this.bankInstallment = bankInstallment;
    }

    public boolean isMobilePayment() {
        return mobilePayment;
    }

    public void setMobilePayment(boolean mobilePayment) {
        this.mobilePayment = mobilePayment;
    }

    public boolean isDisableReference() {
        return disableReference;
    }

    public void setDisableReference(boolean disableReference) {
        this.disableReference = disableReference;
    }

}
