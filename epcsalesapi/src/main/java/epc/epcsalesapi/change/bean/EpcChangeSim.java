package epc.epcsalesapi.change.bean;

public class EpcChangeSim {
    private String custId;
    private String rootPortfolioId; // as case id in epc table
    private String custNum;
    private String subrNum;
    private String dealerCode;
    private String newSimNo;
    private String newImsi;
    private String modifyUser;
    private String modifySalesman;
    private String modifyChannel;
    private String modifyLocation;
    private String result;
    private String errMsg;

    public EpcChangeSim() {
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getRootPortfolioId() {
        return rootPortfolioId;
    }

    public void setRootPortfolioId(String rootPortfolioId) {
        this.rootPortfolioId = rootPortfolioId;
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

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    public String getNewSimNo() {
        return newSimNo;
    }

    public void setNewSimNo(String newSimNo) {
        this.newSimNo = newSimNo;
    }

    public String getNewImsi() {
        return newImsi;
    }

    public void setNewImsi(String newImsi) {
        this.newImsi = newImsi;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getModifySalesman() {
        return modifySalesman;
    }

    public void setModifySalesman(String modifySalesman) {
        this.modifySalesman = modifySalesman;
    }

    public String getModifyChannel() {
        return modifyChannel;
    }

    public void setModifyChannel(String modifyChannel) {
        this.modifyChannel = modifyChannel;
    }

    public String getModifyLocation() {
        return modifyLocation;
    }

    public void setModifyLocation(String modifyLocation) {
        this.modifyLocation = modifyLocation;
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
