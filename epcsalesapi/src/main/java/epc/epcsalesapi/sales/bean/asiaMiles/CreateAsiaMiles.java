package epc.epcsalesapi.sales.bean.asiaMiles;

public class CreateAsiaMiles {
    
    private String orderReference;
    private String quoteItemGuid;
    private String custNum;
    private String subrNum;
    private int asiaMilesRuleId;
    private String acknowledgeMobile;
    private int userid;
    private int salesmanId;
    private String result;
    private String errMsg;

    public CreateAsiaMiles() {}

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    public String getQuoteItemGuid() {
        return quoteItemGuid;
    }

    public void setQuoteItemGuid(String quoteItemGuid) {
        this.quoteItemGuid = quoteItemGuid;
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

    public int getAsiaMilesRuleId() {
        return asiaMilesRuleId;
    }

    public void setAsiaMilesRuleId(int asiaMilesRuleId) {
        this.asiaMilesRuleId = asiaMilesRuleId;
    }

    public String getAcknowledgeMobile() {
        return acknowledgeMobile;
    }

    public void setAcknowledgeMobile(String acknowledgeMobile) {
        this.acknowledgeMobile = acknowledgeMobile;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(int salesmanId) {
        this.salesmanId = salesmanId;
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

    

}
