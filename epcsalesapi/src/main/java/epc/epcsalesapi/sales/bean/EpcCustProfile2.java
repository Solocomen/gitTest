package epc.epcsalesapi.sales.bean;

import java.util.HashMap;

public class EpcCustProfile2 {
    private String caseId;
    private String itemId;
    private String custNum;
    private String subrNum;
    private String subrKey;
    private String effectiveDate;
    private String activationType;
    private HashMap<String, Object> custInfoMap;
    
    public EpcCustProfile2() {
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

    public String getCustNum() {
        return custNum;
    }

    public void setCustNum(String custNum) {
        this.custNum = custNum;
    }

    public String getSubrNum() {
        return subrNum;
    }

    public void setSubrNum(String subrNum) {
        this.subrNum = subrNum;
    }

    public String getSubrKey() {
        return subrKey;
    }

    public void setSubrKey(String subrKey) {
        this.subrKey = subrKey;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getActivationType() {
        return activationType;
    }

    public void setActivationType(String activationType) {
        this.activationType = activationType;
    }

    public HashMap<String, Object> getCustInfoMap() {
        return custInfoMap;
    }

    public void setCustInfoMap(HashMap<String, Object> custInfoMap) {
        this.custInfoMap = custInfoMap;
    }

    
}
