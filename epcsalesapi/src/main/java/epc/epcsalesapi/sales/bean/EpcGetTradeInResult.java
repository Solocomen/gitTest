package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;

public class EpcGetTradeInResult {
    
    private String result;
    private String errorCode;
    private String errorMessage;
    private String referenceNo;
    private int level;
    private String productCode;
    private BigDecimal tradeInValue;

    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public String getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public String getReferenceNo() {
        return referenceNo;
    }
    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public String getProductCode() {
        return productCode;
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    public BigDecimal getTradeInValue() {
        return tradeInValue;
    }
    public void setTradeInValue(BigDecimal tradeInValue) {
        this.tradeInValue = tradeInValue;
    }
}
