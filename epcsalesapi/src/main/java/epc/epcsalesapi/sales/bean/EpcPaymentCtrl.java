package epc.epcsalesapi.sales.bean;

import java.util.Objects;

public class EpcPaymentCtrl {

    private int recId;
    private String paymentCode;
    private boolean disableReference;
    private boolean defaultPayment;
    private boolean calDefaultPaymentAmount;
    private boolean defaultRef1Mandatory;
    private String defaultRef1Message;
    private boolean defaultRef2Mandatory;
    private String defaultRef2Message;
    private String defaultPaymentAmountMessage;
    private boolean maximumOnePayment;
    private boolean readOnly;

    @Override
    public String toString() {
        return "EpcPaymentCtrl [recId=" + recId + ", paymentCode=" + paymentCode + ", disableReference="
                + disableReference + ", defaultPayment=" + defaultPayment + ", calDefaultPaymentAmount="
                + calDefaultPaymentAmount + ", defaultRef1Mandatory=" + defaultRef1Mandatory + ", defaultRef1Message="
                + defaultRef1Message + ", defaultRef2Mandatory=" + defaultRef2Mandatory + ", defaultRef2Message="
                + defaultRef2Message + ", defaultPaymentAmountMessage=" + defaultPaymentAmountMessage
                + ", maximumOnePayment=" + maximumOnePayment + ", readOnly=" + readOnly + "]";
    }

    public EpcPaymentCtrl(String paymentCode) {
        super();
        this.paymentCode = paymentCode;
    }

    public EpcPaymentCtrl() {
        super();
    }

    public int getRecId() {
        return recId;
    }

    public void setRecId(int recId) {
        this.recId = recId;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public boolean isDisableReference() {
        return disableReference;
    }

    public void setDisableReference(boolean disableReference) {
        this.disableReference = disableReference;
    }

    public boolean isDefaultPayment() {
        return defaultPayment;
    }

    public void setDefaultPayment(boolean defaultPayment) {
        this.defaultPayment = defaultPayment;
    }

    public boolean isCalDefaultPaymentAmount() {
        return calDefaultPaymentAmount;
    }

    public void setCalDefaultPaymentAmount(boolean calDefaultPaymentAmount) {
        this.calDefaultPaymentAmount = calDefaultPaymentAmount;
    }

    public boolean isDefaultRef1Mandatory() {
        return defaultRef1Mandatory;
    }

    public void setDefaultRef1Mandatory(boolean defaultRef1Mandatory) {
        this.defaultRef1Mandatory = defaultRef1Mandatory;
    }

    public String getDefaultRef1Message() {
        return defaultRef1Message;
    }

    public void setDefaultRef1Message(String defaultRef1Message) {
        this.defaultRef1Message = defaultRef1Message;
    }

    public boolean isDefaultRef2Mandatory() {
        return defaultRef2Mandatory;
    }

    public void setDefaultRef2Mandatory(boolean defaultRef2Mandatory) {
        this.defaultRef2Mandatory = defaultRef2Mandatory;
    }

    public String getDefaultRef2Message() {
        return defaultRef2Message;
    }

    public void setDefaultRef2Message(String defaultRef2Message) {
        this.defaultRef2Message = defaultRef2Message;
    }

    public String getDefaultPaymentAmountMessage() {
        return defaultPaymentAmountMessage;
    }

    public void setDefaultPaymentAmountMessage(String defaultPaymentAmountMessage) {
        this.defaultPaymentAmountMessage = defaultPaymentAmountMessage;
    }

    public boolean isMaximumOnePayment() {
        return maximumOnePayment;
    }

    public void setMaximumOnePayment(boolean maximumOnePayment) {
        this.maximumOnePayment = maximumOnePayment;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentCode);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EpcPaymentCtrl other = (EpcPaymentCtrl) obj;
        return Objects.equals(paymentCode, other.paymentCode);
    }

}
