package epc.epcsalesapi.sales.bean;

public class EpcConfirmOrderItem {
    
    private String CustID;
    private String OrderID;
    private String CustNum;
    private String SubrNum;
    private String AcctNum;
    private String SubrStatus;
    private String TVBActCode;

    
    public EpcConfirmOrderItem() {}

    public void setCustID(String CustID) {
        this.CustID = CustID;
    }

    public String getCustID() {
        return CustID;
    }

    public void setOrderID(String OrderID) {
        this.OrderID = OrderID;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setCustNum(String CustNum) {
        this.CustNum = CustNum;
    }

    public String getCustNum() {
        return CustNum;
    }

    public void setSubrNum(String SubrNum) {
        this.SubrNum = SubrNum;
    }

    public String getSubrNum() {
        return SubrNum;
    }

    public void setAcctNum(String AcctNum) {
        this.AcctNum = AcctNum;
    }

    public String getAcctNum() {
        return AcctNum;
    }

    public void setSubrStatus(String SubrStatus) {
        this.SubrStatus = SubrStatus;
    }

    public String getSubrStatus() {
        return SubrStatus;
    }

    public void setTVBActCode(String TVBActCode) {
        this.TVBActCode = TVBActCode;
    }

    public String getTVBActCode() {
        return TVBActCode;
    }
}