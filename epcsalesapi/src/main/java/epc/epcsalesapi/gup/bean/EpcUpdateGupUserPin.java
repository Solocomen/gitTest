package epc.epcsalesapi.gup.bean;

public class EpcUpdateGupUserPin implements EpcGupInput {
    
    private String actionType;
    private String actionUsername;
    private String actionSystem;
    @EpcGupField(fieldName="ref",fieldType="operationElement")
    private String orderId;
    @EpcGupField(fieldName="smcUserName",fieldType="gupfield")
    @EpcGupEncryptField
    private String userName;
    @EpcGupField(fieldName="smcAccount",fieldType="gupfield")
    private String custNum;
    @EpcGupField(fieldName="smcOwner",fieldType="gupfield")
    private String owner;
    @EpcGupField(fieldName="smcCommLang",fieldType="gupfield")
    private String commLang;
    @EpcGupField(fieldName="smcLoginNowEmail",fieldType="gupfield")
    @EpcGupEncryptField
    private String loginNowEmail;
    @EpcGupField(fieldName="smcLoginNowSMS",fieldType="gupfield")
    private String loginNowSMS;
    @EpcGupField(fieldName="smcLoginFailCounter",fieldType="gupfield")
    private String loginFailCounter;
    @EpcGupField(fieldName="smcLoginFailAPP",fieldType="gupfield")
    private String loginFailAPP;
    @EpcGupField(fieldName="smcLoginFailDate",fieldType="gupfield")
    private String loginFailDate;
    @EpcGupField(fieldName="smcResetPIN",fieldType="gupfield")
    private String resetPIN;
    @EpcGupField(fieldName="smcAuthorized",fieldType="gupfield")
    private String authorized;
    @EpcGupField(fieldName="smcDateOfBirth",fieldType="gupfield")
    @EpcGupEncryptField
    private String dateOfBirth;
    @EpcGupField(fieldName="smcUserType",fieldType="gupfield")
    private String userType;
    @EpcGupField(fieldName="smcUserStatus",fieldType="gupfield")
    private String userStatus;
    @EpcGupField(fieldName="smcSQFailCounter",fieldType="gupfield")
    private String sqFailCounter;
    @EpcGupField(fieldName="smcSQFailDate",fieldType="gupfield")
    private String sqFailDate;
    @EpcGupField(fieldName="smcUserNameUpdateDate",fieldType="gupfield")
    private String userNameUpdateDate;
    @EpcGupField(fieldName="smcUserRemindTimes",fieldType="gupfield")
    private String userRemindTimes;
    @EpcGupField(fieldName="smcResetSimplePIN",fieldType="gupfield")
    private String resetSimplePin;
    @EpcGupField(fieldName="smcSimpleLoginFailCounter",fieldType="gupfield")
    private String simpleLoginFailCounter;
    @EpcGupField(fieldName="smcSimpleLoginFailAPP",fieldType="gupfield")
    private String simpleLoginFailAPP;
    @EpcGupField(fieldName="smcSimpleLoginFailAPP",fieldType="gupfield")
    private String simpleLoginFailDate;
    @EpcGupField(fieldName="smcUserRecNo",fieldType="gupfield")
    private String userRecNo;
    @EpcGupField(fieldName="smcResetOnlinePIN",fieldType="gupfield")
    private String resetOnlinePin;
    @EpcGupField(fieldName="smcOnlineLoginFailCounter",fieldType="gupfield")
    private String onlineLoginFailCounter;
    @EpcGupField(fieldName="smcOnlineLoginFailAPP",fieldType="gupfield")
    private String onlineLoginFailAPP;
    @EpcGupField(fieldName="smcOnlineLoginFailDate",fieldType="gupfield")
    private String onlineLoginFailDate;
    @EpcGupField(fieldName="smcAPPID",fieldType="gupfield")
    private String appId;
    @EpcGupField(fieldName="smcRem1",fieldType="gupfield")
    @EpcGupEncryptField
    private String rem1;

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionUsername() {
        return actionUsername;
    }

    public void setActionUsername(String actionUsername) {
        this.actionUsername = actionUsername;
    }

    public String getActionSystem() {
        return actionSystem;
    }

    public void setActionSystem(String actionSystem) {
        this.actionSystem = actionSystem;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCustNum() {
        return custNum;
    }

    public void setCustNum(String custNum) {
        this.custNum = custNum;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCommLang() {
        return commLang;
    }

    public void setCommLang(String commLang) {
        this.commLang = commLang;
    }

    public String getLoginNowEmail() {
        return loginNowEmail;
    }

    public void setLoginNowEmail(String loginNowEmail) {
        this.loginNowEmail = loginNowEmail;
    }

    public String getLoginNowSMS() {
        return loginNowSMS;
    }

    public void setLoginNowSMS(String loginNowSMS) {
        this.loginNowSMS = loginNowSMS;
    }

    public String getLoginFailCounter() {
        return loginFailCounter;
    }

    public void setLoginFailCounter(String loginFailCounter) {
        this.loginFailCounter = loginFailCounter;
    }

    public String getLoginFailAPP() {
        return loginFailAPP;
    }

    public void setLoginFailAPP(String loginFailAPP) {
        this.loginFailAPP = loginFailAPP;
    }

    public String getLoginFailDate() {
        return loginFailDate;
    }

    public void setLoginFailDate(String loginFailDate) {
        this.loginFailDate = loginFailDate;
    }

    public String getResetPIN() {
        return resetPIN;
    }

    public void setResetPIN(String resetPIN) {
        this.resetPIN = resetPIN;
    }

    public String getAuthorized() {
        return authorized;
    }

    public void setAuthorized(String authorized) {
        this.authorized = authorized;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getSqFailCounter() {
        return sqFailCounter;
    }

    public void setSqFailCounter(String sqFailCounter) {
        this.sqFailCounter = sqFailCounter;
    }

    public String getSqFailDate() {
        return sqFailDate;
    }

    public void setSqFailDate(String sqFailDate) {
        this.sqFailDate = sqFailDate;
    }

    public String getUserNameUpdateDate() {
        return userNameUpdateDate;
    }

    public void setUserNameUpdateDate(String userNameUpdateDate) {
        this.userNameUpdateDate = userNameUpdateDate;
    }

    public String getUserRemindTimes() {
        return userRemindTimes;
    }

    public void setUserRemindTimes(String userRemindTimes) {
        this.userRemindTimes = userRemindTimes;
    }

    public String getResetSimplePin() {
        return resetSimplePin;
    }

    public void setResetSimplePin(String resetSimplePin) {
        this.resetSimplePin = resetSimplePin;
    }

    public String getSimpleLoginFailCounter() {
        return simpleLoginFailCounter;
    }

    public void setSimpleLoginFailCounter(String simpleLoginFailCounter) {
        this.simpleLoginFailCounter = simpleLoginFailCounter;
    }

    public String getSimpleLoginFailAPP() {
        return simpleLoginFailAPP;
    }

    public void setSimpleLoginFailAPP(String simpleLoginFailAPP) {
        this.simpleLoginFailAPP = simpleLoginFailAPP;
    }

    public String getSimpleLoginFailDate() {
        return simpleLoginFailDate;
    }

    public void setSimpleLoginFailDate(String simpleLoginFailDate) {
        this.simpleLoginFailDate = simpleLoginFailDate;
    }

    public String getUserRecNo() {
        return userRecNo;
    }

    public void setUserRecNo(String userRecNo) {
        this.userRecNo = userRecNo;
    }

    public String getResetOnlinePin() {
        return resetOnlinePin;
    }

    public void setResetOnlinePin(String resetOnlinePin) {
        this.resetOnlinePin = resetOnlinePin;
    }

    public String getOnlineLoginFailCounter() {
        return onlineLoginFailCounter;
    }

    public void setOnlineLoginFailCounter(String onlineLoginFailCounter) {
        this.onlineLoginFailCounter = onlineLoginFailCounter;
    }

    public String getOnlineLoginFailAPP() {
        return onlineLoginFailAPP;
    }

    public void setOnlineLoginFailAPP(String onlineLoginFailAPP) {
        this.onlineLoginFailAPP = onlineLoginFailAPP;
    }

    public String getOnlineLoginFailDate() {
        return onlineLoginFailDate;
    }

    public void setOnlineLoginFailDate(String onlineLoginFailDate) {
        this.onlineLoginFailDate = onlineLoginFailDate;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getRem1() {
        return rem1;
    }

    public void setRem1(String rem1) {
        this.rem1 = rem1;
    }

}
