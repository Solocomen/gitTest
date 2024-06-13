package epc.epcsalesapi.fes.waiving.bean;

import java.math.BigDecimal;

public class EpcCheckWaive {
    private int loginUser;
    private int waiveUserid;
    private String formCode;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private String approveUsername;
    private String approveDwp;
    private String checkDwp;
    
    public EpcCheckWaive() {}

    public int getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(int loginUser) {
        this.loginUser = loginUser;
    }

    public int getWaiveUserid() {
        return waiveUserid;
    }

    public void setWaiveUserid(int waiveUserid) {
        this.waiveUserid = waiveUserid;
    }

    public String getFormCode() {
        return formCode;
    }

    public void setFormCode(String formCode) {
        this.formCode = formCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getApproveUsername() {
        return approveUsername;
    }

    public void setApproveUsername(String approveUsername) {
        this.approveUsername = approveUsername;
    }

    public String getApproveDwp() {
        return approveDwp;
    }

    public void setApproveDwp(String approveDwp) {
        this.approveDwp = approveDwp;
    }

    public String getCheckDwp() {
        return checkDwp;
    }

    public void setCheckDwp(String checkDwp) {
        this.checkDwp = checkDwp;
    }

}
