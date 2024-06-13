package epc.epcsalesapi.gup.bean;

public class EpcUpdateGup implements EpcGupInput {
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
    @EpcGupField(fieldName="smcAMSISDN3",fieldType="gupfield")
    private String contactNo3;
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
    @EpcGupField(fieldName="smcSmartInPlan",fieldType="gupfield")
    private String smartInPlan;
    @EpcGupField(fieldName="smcRSA",fieldType="gupfield")
    private String rsa;
    @EpcGupField(fieldName="smcDataPlan",fieldType="gupfield")
    private String dataPlan;
    @EpcGupField(fieldName="smcMktSeg1",fieldType="gupfield")
    private String mktSeg;
    @EpcGupField(fieldName="smcPICM",fieldType="gupfield")
    private String picm;
    @EpcGupField(fieldName="smcRoamingPlan",fieldType="gupfield")
    private String roamingPlan;
    @EpcGupField(fieldName="smcVASbundled",fieldType="gupfield")
    private String vasBundled;
    @EpcGupField(fieldName="smcAccessRights",fieldType="gupfield")
    private String accessRights;
    @EpcGupField(fieldName="smcPIN-1DN",fieldType="gupfield")
    @EpcGupEncryptField
    private String smcPin1;
    @EpcGupField(fieldName="smcDuoBB",fieldType="gupfield")
    private String smcDuoBB;
    @EpcGupField(fieldName="smcNation",fieldType="gupfield")
    private String smcNation;
    @EpcGupField(fieldName="smcCallGuard",fieldType="gupfield")
    private String callGuard;
    @EpcGupField(fieldName="smcPRBT",fieldType="gupfield")
    private String connectingTone;
    @EpcGupField(fieldName="smcTethering",fieldType="gupfield")
    private String tethering;
    @EpcGupField(fieldName="smcOpOutTopUp",fieldType="gupfield")
    private String optOutTopUp;
    @EpcGupField(fieldName="smcDRDPwebapp",fieldType="gupfield")
    private String optInOptOutWebapp;
    @EpcGupField(fieldName="smcVVM",fieldType="gupfield")
    private String visualVoiceMail;
    @EpcGupField(fieldName="smcServiceIndicators",fieldType="gupfield")
    private String serviceIndicators;
    @EpcGupField(fieldName="smcServiceAccessCheck",fieldType="gupfield")
    private String serviceAccessCheck;
    @EpcGupField(fieldName="smcInterest",fieldType="gupfield") 
    private String interest;
    @EpcGupField(fieldName="smcCallBoost",fieldType="gupfield")
    private String callBoost;
    @EpcGupField(fieldName="smcVoLTE",fieldType="gupfield")
    private String voLTE;
    @EpcGupField(fieldName="smcDegradeVideo",fieldType="gupfield")
    private String degradeVideo;
    @EpcGupField(fieldName="smcVAS1",fieldType="gupfield")
    private String vas1;
    @EpcGupField(fieldName="smcVAS2",fieldType="gupfield")
    private String vas2;
    @EpcGupField(fieldName="smcVAS3",fieldType="gupfield")
    private String vas3;
    @EpcGupField(fieldName="smcVAS4",fieldType="gupfield")
    private String vas4;
    @EpcGupField(fieldName="smcVAS5",fieldType="gupfield")
    private String vas5;
    @EpcGupField(fieldName="smcVAS6",fieldType="gupfield")
    private String vas6;
    @EpcGupField(fieldName="smcVAS7",fieldType="gupfield")
    private String vas7;
    @EpcGupField(fieldName="smcVAS8",fieldType="gupfield")
    private String vas8;
    @EpcGupField(fieldName="smcVAS9",fieldType="gupfield")
    private String vas9;
    @EpcGupField(fieldName="smcVAS10",fieldType="gupfield")
    private String vas10;
    @EpcGupField(fieldName="smciCloudChild",fieldType="gupfield")
    private String iCloudChild;
    @EpcGupField(fieldName="smcSMTPIN",fieldType="gupfield")
    @EpcGupEncryptField
    private String smartonePin;
    @EpcGupField(fieldName="smcStatusBarDate",fieldType="gupfield")
    private String statusBarDate;
    @EpcGupField(fieldName="smcStatusDisconnectDate",fieldType="gupfield")
    private String statusDisconnectDate;
    @EpcGupField(fieldName="smcMVRService",fieldType="gupfield")
    private String mvrService;
    @EpcGupField(fieldName="smcISMSBlockedService",fieldType="gupfield")
    private String internationalSMS;
    @EpcGupField(fieldName="smcMVNO",fieldType="gupfield")
    private String mvno;

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

    public String getContactNo3() {
        return contactNo3;
    }

    public void setContactNo3(String contactNo3) {
        this.contactNo3 = contactNo3;
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

    public String getSmcPin1() {
        return smcPin1;
    }

    public void setSmcPin1(String smcPin1) {
        this.smcPin1 = smcPin1;
    }

    public String getSmcDuoBB() {
        return smcDuoBB;
    }

    public void setSmcDuoBB(String smcDuoBB) {
        this.smcDuoBB = smcDuoBB;
    }

    public String getSmcNation() {
        return smcNation;
    }

    public void setSmcNation(String smcNation) {
        this.smcNation = smcNation;
    }

    public String getCallGuard() {
        return callGuard;
    }

    public void setCallGuard(String callGuard) {
        this.callGuard = callGuard;
    }

    public String getConnectingTone() {
        return connectingTone;
    }

    public void setConnectingTone(String connectingTone) {
        this.connectingTone = connectingTone;
    }

    public String getTethering() {
        return tethering;
    }

    public void setTethering(String tethering) {
        this.tethering = tethering;
    }

    public String getOptOutTopUp() {
        return optOutTopUp;
    }

    public void setOptOutTopUp(String optOutTopUp) {
        this.optOutTopUp = optOutTopUp;
    }

    public String getOptInOptOutWebapp() {
        return optInOptOutWebapp;
    }

    public void setOptInOptOutWebapp(String optInOptOutWebapp) {
        this.optInOptOutWebapp = optInOptOutWebapp;
    }

    public String getVisualVoiceMail() {
        return visualVoiceMail;
    }

    public void setVisualVoiceMail(String visualVoiceMail) {
        this.visualVoiceMail = visualVoiceMail;
    }

    public String getServiceIndicators() {
        return serviceIndicators;
    }

    public void setServiceIndicators(String serviceIndicators) {
        this.serviceIndicators = serviceIndicators;
    }

    public String getServiceAccessCheck() {
        return serviceAccessCheck;
    }

    public void setServiceAccessCheck(String serviceAccessCheck) {
        this.serviceAccessCheck = serviceAccessCheck;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getCallBoost() {
        return callBoost;
    }

    public void setCallBoost(String callBoost) {
        this.callBoost = callBoost;
    }

    public String getVoLTE() {
        return voLTE;
    }

    public void setVoLTE(String voLTE) {
        this.voLTE = voLTE;
    }

    public String getDegradeVideo() {
        return degradeVideo;
    }

    public void setDegradeVideo(String degradeVideo) {
        this.degradeVideo = degradeVideo;
    }

    public String getVas1() {
        return vas1;
    }

    public void setVas1(String vas1) {
        this.vas1 = vas1;
    }

    public String getVas2() {
        return vas2;
    }

    public void setVas2(String vas2) {
        this.vas2 = vas2;
    }

    public String getVas3() {
        return vas3;
    }

    public void setVas3(String vas3) {
        this.vas3 = vas3;
    }

    public String getVas4() {
        return vas4;
    }

    public void setVas4(String vas4) {
        this.vas4 = vas4;
    }

    public String getVas5() {
        return vas5;
    }

    public void setVas5(String vas5) {
        this.vas5 = vas5;
    }

    public String getVas6() {
        return vas6;
    }

    public void setVas6(String vas6) {
        this.vas6 = vas6;
    }

    public String getVas7() {
        return vas7;
    }

    public void setVas7(String vas7) {
        this.vas7 = vas7;
    }

    public String getVas8() {
        return vas8;
    }

    public void setVas8(String vas8) {
        this.vas8 = vas8;
    }

    public String getVas9() {
        return vas9;
    }

    public void setVas9(String vas9) {
        this.vas9 = vas9;
    }

    public String getVas10() {
        return vas10;
    }

    public void setVas10(String vas10) {
        this.vas10 = vas10;
    }

    public String getiCloudChild() {
        return iCloudChild;
    }

    public void setiCloudChild(String iCloudChild) {
        this.iCloudChild = iCloudChild;
    }

    public String getSmartonePin() {
        return smartonePin;
    }

    public void setSmartonePin(String smartonePin) {
        this.smartonePin = smartonePin;
    }

    public String getStatusBarDate() {
        return statusBarDate;
    }

    public void setStatusBarDate(String statusBarDate) {
        this.statusBarDate = statusBarDate;
    }

    public String getStatusDisconnectDate() {
        return statusDisconnectDate;
    }

    public void setStatusDisconnectDate(String statusDisconnectDate) {
        this.statusDisconnectDate = statusDisconnectDate;
    }

    public String getMvrService() {
        return mvrService;
    }

    public void setMvrService(String mvrService) {
        this.mvrService = mvrService;
    }

    public String getInternationalSMS() {
        return internationalSMS;
    }

    public void setInternationalSMS(String internationalSMS) {
        this.internationalSMS = internationalSMS;
    }

    public String getMvno() {
        return mvno;
    }

    public void setMvno(String mvno) {
        this.mvno = mvno;
    }
    
    
}

