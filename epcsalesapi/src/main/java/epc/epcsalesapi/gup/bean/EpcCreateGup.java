package epc.epcsalesapi.gup.bean;

public class EpcCreateGup implements EpcGupInput{
    private String actionType;
    private String actionUsername;
    private String actionSystem;
    @EpcGupField(fieldName="ref",fieldType="operationElement")
    private String orderId;
    @EpcGupField(fieldName="smcSubscriberNumber",fieldType="gupfield")
    private String subrNum;
    @EpcGupField(fieldName="smcAccount",fieldType="gupfield")
    private String custNum;
    @EpcGupField(fieldName="smcAMSISDN",fieldType="gupfield")
    private String contactNo1;
    @EpcGupField(fieldName="smcAMSISDN2",fieldType="gupfield")
    private String contactNo2;
    @EpcGupField(fieldName="smcCustomerType",fieldType="gupfield")
    private String customerType;
    @EpcGupField(fieldName="smcStatus",fieldType="gupfield")
    private String status;
    @EpcGupField(fieldName="smcPlanCode",fieldType="gupfield")
    private String planCode;
    @EpcGupField(fieldName="sn;lang-en",fieldType="gupfield")
    private String surNameEng;
    @EpcGupField(fieldName="sn;lang-zh-TW",fieldType="gupfield")
    private String surNameChi;
    @EpcGupField(fieldName="givenName;lang-en",fieldType="gupfield")
    private String givenNameEng;
    @EpcGupField(fieldName="givenName;lang-zh-TW",fieldType="gupfield")
    private String givenNameChi;
    @EpcGupField(fieldName="personalTitle;lang-en",fieldType="gupfield")
    private String titleEng;
    @EpcGupField(fieldName="personalTitle;lang-zh-TW",fieldType="gupfield")
    private String titleChi;
    @EpcGupField(fieldName="smcPreferredLanguageCommunications",fieldType="gupfield")
    private String commLang;
    @EpcGupField(fieldName="smcPreferredLanguageBill",fieldType="gupfield")
    private String billLang;
    @EpcGupField(fieldName="smcCommEmailAddress",fieldType="gupfield")
    @EpcGupEncryptField
    private String commEmailAddress;
    @EpcGupField(fieldName="smcIMSI",fieldType="gupfield")
    private String imsi;
    @EpcGupField(fieldName="smcCorporatePlan",fieldType="gupfield")
    private String idType;
    @EpcGupField(fieldName="smcPlanType",fieldType="gupfield")
    private String planType;
    @EpcGupField(fieldName="smcSwitchOnDate",fieldType="gupfield")
    private String switchOnDate;
    @EpcGupField(fieldName="smcAddOnNo",fieldType="gupfield")
    private String addOnNumber;
    @EpcGupField(fieldName="smcUserName1",fieldType="gupfield")
    @EpcGupEncryptField
    private String userName1;
    @EpcGupField(fieldName="smcPIN-1DN",fieldType="gupfield")
    @EpcGupEncryptField
    private String smcPin1;
    @EpcGupField(fieldName="smcPIN-2DN",fieldType="gupfield")
    @EpcGupEncryptField
    private String smcPin2;
    //private String userName1;
    //@EpcGupField(fieldName="smcVAS3",fieldType="gupfield")
    //private String vas3;
    //@EpcGupField(fieldName="smcUserName1",fieldType="gupfield")
    //private String userName1;
    //@EpcGupField(fieldName="smcSmartInPlan",fieldType="gupfield")
    //private String smartInPlan;
    //@EpcGupField(fieldName="smcRSA",fieldType="gupfield")
    //private String rsa;
    //@EpcGupField(fieldName="smcDataPlan",fieldType="gupfield")
    //private String dataPlan;
    //@EpcGupField(fieldName="smcMktSeg1",fieldType="gupfield")
    //private String mktSeg;
    //@EpcGupField(fieldName="smcPICM",fieldType="gupfield")
    //private String picm;
    //@EpcGupField(fieldName="smcRoamingPlan",fieldType="gupfield")
    //private String roamingPlan;
    //@EpcGupField(fieldName="smcVASbundled",fieldType="gupfield")
    //private String vasBundled;
    //@EpcGupField(fieldName="smcAccessRights",fieldType="gupfield")
    //private String accessRights;


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

    public String getSubrNum() {
        return subrNum;
    }

    public void setSubrNum(String subrNum) {
        this.subrNum = subrNum;
    }

    public String getCustNum() {
        return custNum;
    }

    public void setCustNum(String custNum) {
        this.custNum = custNum;
    }

    public String getContactNo1() {
        return contactNo1;
    }

    public void setContactNo1(String contactNo1) {
        this.contactNo1 = contactNo1;
    }

    public String getContactNo2() {
        return contactNo2;
    }

    public void setContactNo2(String contactNo2) {
        this.contactNo2 = contactNo2;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public String getSurNameEng() {
        return surNameEng;
    }

    public void setSurNameEng(String surNameEng) {
        this.surNameEng = surNameEng;
    }

    public String getSurNameChi() {
        return surNameChi;
    }

    public void setSurNameChi(String surNameChi) {
        this.surNameChi = surNameChi;
    }

    public String getGivenNameEng() {
        return givenNameEng;
    }

    public void setGivenNameEng(String givenNameEng) {
        this.givenNameEng = givenNameEng;
    }

    public String getGivenNameChi() {
        return givenNameChi;
    }

    public void setGivenNameChi(String givenNameChi) {
        this.givenNameChi = givenNameChi;
    }

    public String getTitleEng() {
        return titleEng;
    }

    public void setTitleEng(String titleEng) {
        this.titleEng = titleEng;
    }

    public String getTitleChi() {
        return titleChi;
    }

    public void setTitleChi(String titleChi) {
        this.titleChi = titleChi;
    }

    public String getCommLang() {
        return commLang;
    }

    public void setCommLang(String commLang) {
        this.commLang = commLang;
    }

    public String getBillLang() {
        return billLang;
    }

    public void setBillLang(String billLang) {
        this.billLang = billLang;
    }

    public String getCommEmailAddress() {
        return commEmailAddress;
    }

    public void setCommEmailAddress(String commEmailAddress) {
        this.commEmailAddress = commEmailAddress;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getSwitchOnDate() {
        return switchOnDate;
    }

    public void setSwitchOnDate(String switchOnDate) {
        this.switchOnDate = switchOnDate;
    }
    
    public String getAddOnNumber() {
        return addOnNumber;
    }

    public void setAddOnNumber(String addOnNumber) {
        this.addOnNumber = addOnNumber;
    }
    
    public String getUserName1() {
        return userName1;
    }

    public void setUserName1(String userName1) {
        this.userName1 = userName1;
    }
    
    public String getSmcPin1() {
        return smcPin1;
    }

    public void setSmcPin1(String smcPin1) {
        this.smcPin1 = smcPin1;
    }
    
    public String getSmcPin2() {
        return smcPin2;
    }

    public void setSmcPin2(String smcPin2) {
        this.smcPin2 = smcPin2;
    }

    
    /*
    public String getVas3() {
        return vas3;
    }

    public void setVas3(String vas3) {
        this.vas3 = vas3;
    }

    public String getUserName1() {
        return userName1;
    }

    public void setUserName1(String userName1) {
        this.userName1 = userName1;
    }

    public String getSmartInPlan() {
        return smartInPlan;
    }

    public void setSmartInPlan(String smartInPlan) {
        this.smartInPlan = smartInPlan;
    }

    public String getRsa() {
        return rsa;
    }

    public void setRsa(String rsa) {
        this.rsa = rsa;
    }

    public String getDataPlan() {
        return dataPlan;
    }

    public void setDataPlan(String dataPlan) {
        this.dataPlan = dataPlan;
    }

    public String getMktSeg() {
        return mktSeg;
    }

    public void setMktSeg(String mktSeg) {
        this.mktSeg = mktSeg;
    }

    public String getPicm() {
        return picm;
    }

    public void setPicm(String picm) {
        this.picm = picm;
    }

    public String getRoamingPlan() {
        return roamingPlan;
    }

    public void setRoamingPlan(String roamingPlan) {
        this.roamingPlan = roamingPlan;
    }

    public String getVasBundled() {
        return vasBundled;
    }

    public void setVasBundled(String vasBundled) {
        this.vasBundled = vasBundled;
    }

    public String getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(String accessRights) {
        this.accessRights = accessRights;
    }
    */

}
