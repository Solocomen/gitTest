package epc.epcsalesapi.change.bean;

import java.util.HashMap;

public class WsChangeSim {
    private String custId;
    private String crmOrderId;
    private String custNum;
    private String masterSubr;
    private String rootPortfolioId;
    private String subrNum;
    private String sim;
    private String imsi;
    private String eSimFlag;
    private String channel;
    private HashMap<String, Object> extraContextData;
    private String requesterId;
    private String dealerCode;
    private String effectiveDate;
    private String directSubmission;
    
    public WsChangeSim() {
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getCrmOrderId() {
        return crmOrderId;
    }

    public void setCrmOrderId(String crmOrderId) {
        this.crmOrderId = crmOrderId;
    }

    public String getCustNum() {
        return custNum;
    }

    public void setCustNum(String custNum) {
        this.custNum = custNum;
    }

    public String getMasterSubr() {
        return masterSubr;
    }

    public void setMasterSubr(String masterSubr) {
        this.masterSubr = masterSubr;
    }

    public String getRootPortfolioId() {
        return rootPortfolioId;
    }

    public void setRootPortfolioId(String rootPortfolioId) {
        this.rootPortfolioId = rootPortfolioId;
    }

    public String getSubrNum() {
        return subrNum;
    }

    public void setSubrNum(String subrNum) {
        this.subrNum = subrNum;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String geteSimFlag() {
        return eSimFlag;
    }

    public void seteSimFlag(String eSimFlag) {
        this.eSimFlag = eSimFlag;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public HashMap<String, Object> getExtraContextData() {
        return extraContextData;
    }

    public void setExtraContextData(HashMap<String, Object> extraContextData) {
        this.extraContextData = extraContextData;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getDirectSubmission() {
        return directSubmission;
    }

    public void setDirectSubmission(String directSubmission) {
        this.directSubmission = directSubmission;
    }

    
}
