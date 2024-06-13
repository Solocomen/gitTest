package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;
import java.util.ArrayList;

public class EpcVmsCaseInfoInCart {
    
    private String caseId;
    private String offerGuid;
    private String quoteItemGuid;
    private String quoteGuid;
    private int quoteId;
    private BigDecimal totalCharge;
    private boolean validToRedeem;
    private ArrayList<EpcCompiledSpecEntity> entityList;
    
    public EpcVmsCaseInfoInCart() {}

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getOfferGuid() {
        return offerGuid;
    }

    public void setOfferGuid(String offerGuid) {
        this.offerGuid = offerGuid;
    }

    public String getQuoteItemGuid() {
        return quoteItemGuid;
    }

    public void setQuoteItemGuid(String quoteItemGuid) {
        this.quoteItemGuid = quoteItemGuid;
    }

    public BigDecimal getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(BigDecimal totalCharge) {
        this.totalCharge = totalCharge;
    }

    public String getQuoteGuid() {
        return quoteGuid;
    }

    public void setQuoteGuid(String quoteGuid) {
        this.quoteGuid = quoteGuid;
    }

    public boolean isValidToRedeem() {
        return validToRedeem;
    }

    public void setValidToRedeem(boolean validToRedeem) {
        this.validToRedeem = validToRedeem;
    }

    public ArrayList<EpcCompiledSpecEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(ArrayList<EpcCompiledSpecEntity> entityList) {
        this.entityList = entityList;
    }

    public int getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(int quoteId) {
        this.quoteId = quoteId;
    }

}
